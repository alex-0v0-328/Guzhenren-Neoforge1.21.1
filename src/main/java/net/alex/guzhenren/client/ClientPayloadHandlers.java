package net.alex.guzhenren.client;

import net.alex.guzhenren.network.sync.*;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandlers {

    public static void handleFullSync(ModPlayerSyncPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientPlayerData.setAll(payload.data()));
    }

    public static void handleCultivation(CoreSyncPayload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientPlayerData.setCultivation(payload.data()));
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
