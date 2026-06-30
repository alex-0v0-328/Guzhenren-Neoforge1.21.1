package net.alex.guzhenren.network;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.client.ClientPayloadHandlers;
import net.alex.guzhenren.network.sync.CoreSyncPayload;
import net.alex.guzhenren.network.sync.EssenceSyncPayload;
import net.alex.guzhenren.network.sync.LifespanSyncPayload;
import net.alex.guzhenren.network.sync.ModPlayerSyncPayload;
import net.alex.guzhenren.network.sync.PathDeltaSyncPayload;
import net.alex.guzhenren.network.sync.PathSyncPayload;
import net.alex.guzhenren.network.sync.SoulSyncPayload;
import net.alex.guzhenren.network.sync.StatusSyncPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** Network payload 注册中心. 所有 sync payload 为 server → client 单向 */
@EventBusSubscriber(modid = Guzhenren.MOD_ID)
public final class ModPayloads {

    private ModPayloads() {}

    private static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar reg = event.registrar(PROTOCOL_VERSION);

        reg.playToClient(ModPlayerSyncPayload.TYPE, ModPlayerSyncPayload.STREAM_CODEC,
                ClientPayloadHandlers::handlePlayerFull);
        reg.playToClient(CoreSyncPayload.TYPE, CoreSyncPayload.STREAM_CODEC,
                ClientPayloadHandlers::handleCore);
        reg.playToClient(EssenceSyncPayload.TYPE, EssenceSyncPayload.STREAM_CODEC,
                ClientPayloadHandlers::handleEssence);
        reg.playToClient(StatusSyncPayload.TYPE, StatusSyncPayload.STREAM_CODEC,
                ClientPayloadHandlers::handleStatus);
        reg.playToClient(PathSyncPayload.TYPE, PathSyncPayload.STREAM_CODEC,
                ClientPayloadHandlers::handlePath);
        reg.playToClient(PathDeltaSyncPayload.TYPE, PathDeltaSyncPayload.STREAM_CODEC,
                ClientPayloadHandlers::handlePathDelta);
        reg.playToClient(LifespanSyncPayload.TYPE, LifespanSyncPayload.STREAM_CODEC,
                ClientPayloadHandlers::handleLifespan);
        reg.playToClient(SoulSyncPayload.TYPE, SoulSyncPayload.STREAM_CODEC,
                ClientPayloadHandlers::handleSoul);
    }
}
