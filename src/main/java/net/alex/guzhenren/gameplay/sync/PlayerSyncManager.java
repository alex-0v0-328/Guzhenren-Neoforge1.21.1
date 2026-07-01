package net.alex.guzhenren.gameplay.sync;

import java.util.ArrayList;
import java.util.List;
import net.alex.guzhenren.enums.path.Path;
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
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 玩家数据同步管理器.
 * 集中 dirty 检查与 payload 发送逻辑, 与 event 路由层 (ModPlayerEvents) 解耦.
 * - syncImmediate: 每 tick 调, 仅 sync 有 dirty 的 component
 * - syncPeriodic: 周期 sync (节流 dirty 检查), 由 caller 控制调用时机
 * - syncFull: 全量 sync (login / respawn)
 */
public final class PlayerSyncManager {

    /** 真元自然恢复的周期 sync 间隔 (tick) */
    public static final int ESSENCE_NATURAL_SYNC_INTERVAL = 20;
    /** core / path 节流 sync 间隔 (tick) */
    public static final int CORE_PATH_SYNC_INTERVAL = 20;

    private PlayerSyncManager() {}

//region IMMEDIATE
    public static void syncImmediate(ServerPlayer sp, ModPlayerData data) {
        if (data.status().isDirty()) {
            PacketDistributor.sendToPlayer(sp, new StatusSyncPayload(data.status()));
            data.status().clearDirty();
            SyncAudit.log("syncImmediate.status", sp);
        }
        if (data.essence().isDirty()) {
            PacketDistributor.sendToPlayer(sp, new EssenceSyncPayload(data.essence()));
            data.essence().clearDirty();
            SyncAudit.log("syncImmediate.essence", sp);
        }
        if (data.lifespan().isDirty()) {
            PacketDistributor.sendToPlayer(sp, new LifespanSyncPayload(data.lifespan()));
            data.lifespan().clearDirty();
            SyncAudit.log("syncImmediate.lifespan", sp);
        }
        if (data.soul().isDirty()) {
            PacketDistributor.sendToPlayer(sp, new SoulSyncPayload(data.soul()));
            data.soul().clearDirty();
            SyncAudit.log("syncImmediate.soul", sp);
        }
    }
//endregion

//region PERIODIC
    /**
     * 周期同步. 由 caller 控制调用时机 (推荐按 tick % CORE_PATH_SYNC_INTERVAL == 0)
     * - essence 走 natural recovery 周期 sync (无 dirty 检查, 总是发)
     * - core / path 仅 dirty 时发
     */
    public static void syncEssenceNatural(ServerPlayer sp, ModPlayerData data) {
        PacketDistributor.sendToPlayer(sp, new EssenceSyncPayload(data.essence()));
        SyncAudit.log("syncEssenceNatural", sp);
    }

    public static void syncCoreIfDirty(ServerPlayer sp, ModPlayerData data) {
        if (!data.core().isDirty()) return;
        PacketDistributor.sendToPlayer(sp, new CoreSyncPayload(data.core()));
        data.core().clearDirty();
        SyncAudit.log("syncCore", sp);
    }

    public static void syncPathIfDirty(ServerPlayer sp, ModPlayerData data) {
        PathComponent path = data.path();
        if (path.isForceFullSync()) {
            PacketDistributor.sendToPlayer(sp, new PathSyncPayload(path));
            path.clearDirty();
            SyncAudit.log("syncPath.full", sp);
            return;
        }
        if (path.getDirtyPaths().isEmpty()) return;

        List<PathDelta> deltas = new ArrayList<>(path.getDirtyPaths().size());
        for (Path p : path.getDirtyPaths()) {
            deltas.add(new PathDelta(p, path.getAttainment(p), path.getMarks(p)));
        }
        PacketDistributor.sendToPlayer(sp, new PathDeltaSyncPayload(deltas));
        path.clearDirty();
        SyncAudit.log("syncPath.delta", sp);
    }
//endregion

//region FULL
    /** 全量同步. 用于 login / respawn */
    public static void syncFull(ServerPlayer sp) {
        ModPlayerData data = ModPlayerData.of(sp);
        PacketDistributor.sendToPlayer(sp, new ModPlayerSyncPayload(data));
        SyncAudit.log("syncFull", sp);
    }
//endregion
}