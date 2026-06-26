package net.alex.guzhenren.network;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.client.ClientPayloadHandlers;
import net.alex.guzhenren.network.sync.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Guzhenren.MOD_ID)
public class ModPayload {

    private static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar reg = event.registrar(PROTOCOL_VERSION);

        reg.playToClient(ModPlayerSyncPayload.TYPE,  ModPlayerSyncPayload.STREAM_CODEC,
                ClientPayloadHandlers::handleFullSync);
        reg.playToClient(CoreSyncPayload.TYPE, CoreSyncPayload.STREAM_CODEC,
                ClientPayloadHandlers::handleCultivation);
        reg.playToClient(EssenceSyncPayload.TYPE,     EssenceSyncPayload.STREAM_CODEC,
                ClientPayloadHandlers::handleEssence);
        reg.playToClient(StatusSyncPayload.TYPE,      StatusSyncPayload.STREAM_CODEC,
                ClientPayloadHandlers::handleStatus);
        reg.playToClient(PathSyncPayload.TYPE,        PathSyncPayload.STREAM_CODEC,
                ClientPayloadHandlers::handlePath);
    }
}
