package net.alex.guzhenren.event;

import java.util.ArrayList;
import java.util.List;
import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.enums.path.Path;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.gameplay.data.PathComponent;
import net.alex.guzhenren.network.sync.CoreSyncPayload;
import net.alex.guzhenren.network.sync.EssenceSyncPayload;
import net.alex.guzhenren.network.sync.ModPlayerSyncPayload;
import net.alex.guzhenren.network.sync.PathDelta;
import net.alex.guzhenren.network.sync.PathDeltaSyncPayload;
import net.alex.guzhenren.network.sync.PathSyncPayload;
import net.alex.guzhenren.network.sync.StatusSyncPayload;
import net.alex.guzhenren.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Guzhenren.MOD_ID)
public class ModPlayerEvents {

    private static final int ESSENCE_NATURAL_SYNC_INTERVAL = 20;
    private static final int CORE_PATH_SYNC_INTERVAL = 20;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        ModPlayerData data = sp.getData(ModAttachments.PLAYER_DATA.get());

        int offset = sp.getUUID().hashCode() & 0x1F;
        int adjustedTick = sp.tickCount + offset;

        // 1. 自然恢复 (每 tick)
        data.essence().naturalRecoveryPerTick();

        // 2. Status 立即同步
        if (data.status().isDirty()) {
            PacketDistributor.sendToPlayer(sp, new StatusSyncPayload(data.status()));
            data.status().clearDirty();
        }

        // 3. Essence 主动改动立即同步
        if (data.essence().isActionDirty()) {
            PacketDistributor.sendToPlayer(sp, new EssenceSyncPayload(data.essence()));
            data.essence().clearActionDirty();
        }

        // 4. Essence 自然恢复周期同步 (20 tick + 错开)
        if (adjustedTick % ESSENCE_NATURAL_SYNC_INTERVAL == 0) {
            PacketDistributor.sendToPlayer(sp, new EssenceSyncPayload(data.essence()));
        }

        // 5. Core/Path 节流同步 (20 tick + 错开)
        if (adjustedTick % CORE_PATH_SYNC_INTERVAL == 0) {
            if (data.core().isDirty()) {
                PacketDistributor.sendToPlayer(sp, new CoreSyncPayload(data.core()));
                data.core().clearDirty();
            }
            syncPath(sp, data);
        }
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

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        ModPlayerData data = sp.getData(ModAttachments.PLAYER_DATA.get());
        PacketDistributor.sendToPlayer(sp, new ModPlayerSyncPayload(data));
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        ModPlayerData data = sp.getData(ModAttachments.PLAYER_DATA.get());
        PacketDistributor.sendToPlayer(sp, new ModPlayerSyncPayload(data));
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer newPlayer)) return;
        if (!(event.getOriginal() instanceof ServerPlayer oldPlayer)) return;

        if (event.isWasDeath()) {
            boolean keep = newPlayer.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
            if (!keep) return;
        }

        ModPlayerData oldData = oldPlayer.getData(ModAttachments.PLAYER_DATA.get());
        ModPlayerData newData = newPlayer.getData(ModAttachments.PLAYER_DATA.get());
        newData.copyFrom(oldData);
    }
}
