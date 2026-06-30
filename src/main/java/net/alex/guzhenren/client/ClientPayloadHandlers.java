package net.alex.guzhenren.client;

import net.alex.guzhenren.network.sync.CoreSyncPayload;
import net.alex.guzhenren.network.sync.EssenceSyncPayload;
import net.alex.guzhenren.network.sync.LifespanSyncPayload;
import net.alex.guzhenren.network.sync.ModPlayerSyncPayload;
import net.alex.guzhenren.network.sync.PathDeltaSyncPayload;
import net.alex.guzhenren.network.sync.PathSyncPayload;
import net.alex.guzhenren.network.sync.SoulSyncPayload;
import net.alex.guzhenren.network.sync.StatusSyncPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** 客户端 payload handler: 接收 server 端 sync, 写入 ClientPlayerData */
public final class ClientPayloadHandlers {

    private ClientPayloadHandlers() {}

    public static void handlePlayerFull(ModPlayerSyncPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientPlayerData.setAll(payload.data()));
    }

    public static void handleCore(CoreSyncPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientPlayerData.setCore(payload.data()));
    }

    public static void handleEssence(EssenceSyncPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientPlayerData.setEssence(payload.data()));
    }

    public static void handleStatus(StatusSyncPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientPlayerData.setStatus(payload.data()));
    }

    public static void handlePath(PathSyncPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientPlayerData.setPath(payload.data()));
    }

    public static void handlePathDelta(PathDeltaSyncPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientPlayerData.applyPathDeltas(payload.deltas()));
    }

    public static void handleLifespan(LifespanSyncPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientPlayerData.setLifespan(payload.data()));
    }

    public static void handleSoul(SoulSyncPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientPlayerData.setSoul(payload.data()));
    }
}
