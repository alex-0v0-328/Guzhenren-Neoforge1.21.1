package net.alex.guzhenren.event;

import java.util.ArrayList;
import java.util.List;
import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.enums.path.Path;
import net.alex.guzhenren.gameplay.action.PlayerLifespanActions;
import net.alex.guzhenren.gameplay.action.PlayerSoulActions;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.gameplay.data.PathComponent;
import net.alex.guzhenren.network.sync.CoreSyncPayload;
import net.alex.guzhenren.network.sync.EssenceSyncPayload;
import net.alex.guzhenren.network.sync.LifespanSyncPayload;
import net.alex.guzhenren.network.sync.ModPlayerSyncPayload;
import net.alex.guzhenren.network.sync.PathDelta;
import net.alex.guzhenren.network.sync.PathDeltaSyncPayload;
import net.alex.guzhenren.network.sync.PathSyncPayload;
import net.alex.guzhenren.network.sync.SoulSyncPayload;
import net.alex.guzhenren.network.sync.StatusSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 玩家生命周期事件 handler:
 * - tick: 自然恢复 + dirty sync + 死亡检测
 * - login / respawn: full sync
 * - clone: 死亡迁移 (按 keepInventory gamerule 决定 copy / reset)
 * - wakeUp: 睡眠推进寿元
 */
@EventBusSubscriber(modid = Guzhenren.MOD_ID)
public final class ModPlayerEvents {

    private ModPlayerEvents() {}

    private static final int ESSENCE_NATURAL_SYNC_INTERVAL = 20;
    private static final int CORE_PATH_SYNC_INTERVAL = 20;
    /** 玩家睡眠完整夜晚的 tick 数 (fallback, ServerPlayer.getSleepStartTime() 在 1.21.1 不可用) */
    private static final int FALLBACK_SLEEP_TICKS = 12000;

//region TICK
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        ModPlayerData data = ModPlayerData.of(sp);
        int adjustedTick = sp.tickCount + Math.floorMod(sp.getUUID().hashCode(), CORE_PATH_SYNC_INTERVAL);

        tickRegeneration(data);
        syncImmediateChanges(sp, data);
        if (checkDeath(sp)) return;
        syncPeriodicChanges(sp, data, adjustedTick);
    }

    private static void tickRegeneration(ModPlayerData data) {
        data.essence().naturalRecoveryPerTick();
        data.lifespan().advanceTicks(1);
    }

    private static void syncImmediateChanges(ServerPlayer sp, ModPlayerData data) {
        if (data.status().isDirty()) {
            PacketDistributor.sendToPlayer(sp, new StatusSyncPayload(data.status()));
            data.status().clearDirty();
        }
        if (data.essence().isDirty()) {
            PacketDistributor.sendToPlayer(sp, new EssenceSyncPayload(data.essence()));
            data.essence().clearDirty();
        }
        if (data.lifespan().isDirty()) {
            PacketDistributor.sendToPlayer(sp, new LifespanSyncPayload(data.lifespan()));
            data.lifespan().clearDirty();
        }
        if (data.soul().isDirty()) {
            PacketDistributor.sendToPlayer(sp, new SoulSyncPayload(data.soul()));
            data.soul().clearDirty();
        }
    }

    /** 死亡检测放在 sync 之后, 让玩家死前能看到数值归零 */
    private static boolean checkDeath(ServerPlayer sp) {
        if (PlayerLifespanActions.checkAndKillIfDepleted(sp)) return true;
        return PlayerSoulActions.checkAndKillIfCollapsed(sp);
    }

    private static void syncPeriodicChanges(ServerPlayer sp, ModPlayerData data, int adjustedTick) {
        if (adjustedTick % ESSENCE_NATURAL_SYNC_INTERVAL == 0) {
            PacketDistributor.sendToPlayer(sp, new EssenceSyncPayload(data.essence()));
        }
        if (adjustedTick % CORE_PATH_SYNC_INTERVAL == 0) {
            syncCore(sp, data);
            syncPath(sp, data);
        }
    }

    private static void syncCore(ServerPlayer sp, ModPlayerData data) {
        if (!data.core().isDirty()) return;
        PacketDistributor.sendToPlayer(sp, new CoreSyncPayload(data.core()));
        data.core().clearDirty();
    }

    private static void syncPath(ServerPlayer sp, ModPlayerData data) {
        PathComponent path = data.path();
        if (path.isForceFullSync()) {
            PacketDistributor.sendToPlayer(sp, new PathSyncPayload(path));
            path.clearDirty();
            return;
        }
        if (path.getDirtyPaths().isEmpty()) return;

        List<PathDelta> deltas = new ArrayList<>(path.getDirtyPaths().size());
        for (Path p : path.getDirtyPaths()) {
            deltas.add(new PathDelta(p, path.getAttainment(p), path.getMarks(p)));
        }
        PacketDistributor.sendToPlayer(sp, new PathDeltaSyncPayload(deltas));
        path.clearDirty();
    }
//endregion

//region LIFECYCLE
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) sendFullSync(sp);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) sendFullSync(sp);
    }

    /**
     * 死亡迁移逻辑:
     * - wasDeath = false (维度切换等): 总是 copy
     * - wasDeath = true + keepInventory = true: copy (保留修为)
     * - wasDeath = true + keepInventory = false: 早返回, 让 attachment 默认 reset 为 default 实例
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer newPlayer)) return;
        if (!(event.getOriginal() instanceof ServerPlayer oldPlayer)) return;

        if (event.isWasDeath()) {
            boolean keep = newPlayer.serverLevel().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
            if (!keep) return;
        }

        ModPlayerData oldData = ModPlayerData.of(oldPlayer);
        ModPlayerData newData = ModPlayerData.of(newPlayer);
        newData.copyFrom(oldData);
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        if (event.wakeImmediately()) return;

        ModPlayerData data = ModPlayerData.of(sp);
        boolean depleted = data.lifespan().advanceTicks(FALLBACK_SLEEP_TICKS);
        if (depleted) PlayerLifespanActions.checkAndKillIfDepleted(sp);
    }

    private static void sendFullSync(ServerPlayer sp) {
        ModPlayerData data = ModPlayerData.of(sp);
        PacketDistributor.sendToPlayer(sp, new ModPlayerSyncPayload(data));
    }
//endregion
}
