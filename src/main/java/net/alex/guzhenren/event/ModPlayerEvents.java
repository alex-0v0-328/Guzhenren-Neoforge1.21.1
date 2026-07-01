package net.alex.guzhenren.event;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.gameplay.action.PlayerLifespanActions;
import net.alex.guzhenren.gameplay.action.PlayerSoulActions;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.gameplay.sync.PlayerSyncManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * 玩家生命周期事件 handler:
 * - tick: 自然恢复 + dirty sync + 死亡检测 (sync 逻辑委托 PlayerSyncManager)
 * - login / respawn: full sync
 * - clone: 死亡迁移 (按 keepInventory gamerule 决定 copy / reset)
 * - wakeUp: 睡眠推进寿元
 */
@EventBusSubscriber(modid = Guzhenren.MOD_ID)
public final class ModPlayerEvents {

    private ModPlayerEvents() {}

    /** 玩家睡眠完整夜晚的 tick 数 (fallback, ServerPlayer.getSleepStartTime() 在 1.21.1 不可用) */
    private static final int FALLBACK_SLEEP_TICKS = 12000;

//region TICK
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        ModPlayerData data = ModPlayerData.of(sp);
        int adjustedTick = sp.tickCount + Math.floorMod(sp.getUUID().hashCode(), PlayerSyncManager.CORE_PATH_SYNC_INTERVAL);

        tickRegeneration(data);
        PlayerSyncManager.syncImmediate(sp, data);
        if (checkDeath(sp)) return;

        if (adjustedTick % PlayerSyncManager.ESSENCE_NATURAL_SYNC_INTERVAL == 0) {
            PlayerSyncManager.syncEssenceNatural(sp, data);
        }
        if (adjustedTick % PlayerSyncManager.CORE_PATH_SYNC_INTERVAL == 0) {
            PlayerSyncManager.syncCoreIfDirty(sp, data);
            PlayerSyncManager.syncPathIfDirty(sp, data);
        }
    }

    private static void tickRegeneration(ModPlayerData data) {
        data.essence().naturalRecoveryPerTick();
        data.lifespan().advanceTicks(1);
    }

    /** 死亡检测放在 sync 之后, 让玩家死前能看到数值归零 */
    private static boolean checkDeath(ServerPlayer sp) {
        if (PlayerLifespanActions.checkAndKillIfDepleted(sp)) return true;
        return PlayerSoulActions.checkAndKillIfCollapsed(sp);
    }
//endregion

//region LIFECYCLE
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) PlayerSyncManager.syncFull(sp);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) PlayerSyncManager.syncFull(sp);
    }

    /**
     * 死亡迁移逻辑:
     * - wasDeath = false (维度切换等): 总是 copy
     * - wasDeath = true + keepInventory = true: copy (保留修为)
     *   若死因为寿元/魂魄, 额外 reset lifespan + soul (维持"寿终/魂灭必失"的语义)
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

        if (event.isWasDeath() && oldData.status().isDeathByLifespanOrSoul()) {
            newData.lifespan().reset();
            newData.soul().reset();
        }
        newData.status().clearDeathByLifespanOrSoul();
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        if (event.wakeImmediately()) return;

        ModPlayerData data = ModPlayerData.of(sp);
        boolean depleted = data.lifespan().advanceTicks(FALLBACK_SLEEP_TICKS);
        if (depleted) PlayerLifespanActions.checkAndKillIfDepleted(sp);
    }
//endregion
}
