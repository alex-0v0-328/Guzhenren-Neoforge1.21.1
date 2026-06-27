package net.alex.guzhenren.client;

import net.alex.guzhenren.network.sync.CoreSyncPayload;
import net.alex.guzhenren.network.sync.EssenceSyncPayload;
import net.alex.guzhenren.network.sync.ModPlayerSyncPayload;
import net.alex.guzhenren.network.sync.PathSyncPayload;
import net.alex.guzhenren.network.sync.StatusSyncPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandlers {

    public static void handleFullSync(ModPlayerSyncPayload payload, IPayloadContext ctx) {
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
}
