package net.alex.guzhenren.network;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.cultivation.AttachmentTypes;
import net.alex.guzhenren.network.handler.ClientPayloadHandler;
import net.alex.guzhenren.network.payload.SyncBasicPayload;
import net.alex.guzhenren.network.payload.SyncEssencePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Guzhenren.MOD_ID)
public final class NetworkHandler {

    private NetworkHandler() {}

    /** Payload 协议版本号 (Network Protocol Version) - 字段或编码变化时升号 */
    public static final String PROTOCOL_VERSION = "1";

    // ============== Payload 注册 ==============

    @SubscribeEvent
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        // playToClient: 仅服务端 → 客户端 方向
        registrar.playToClient(
                SyncBasicPayload.TYPE,
                SyncBasicPayload.STREAM_CODEC,
                ClientPayloadHandler::handleSyncBasic
        );
        registrar.playToClient(
                SyncEssencePayload.TYPE,
                SyncEssencePayload.STREAM_CODEC,
                ClientPayloadHandler::handleSyncEssence
        );
    }

    // ============== 发包工具 (Server → Client) ==============

    /** 全量同步：basic + essence (登录/换维度/复活时使用) */
    public static void syncAll(ServerPlayer player) {
        syncBasic(player);
        syncEssence(player);
    }

    /** 仅同步 basic (境界变动时使用) */
    public static void syncBasic(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, SyncBasicPayload.from(player.getData(AttachmentTypes.REALM)));
    }

    /** 仅同步 essence (真元变动 / 回血时使用) */
    public static void syncEssence(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, SyncEssencePayload.from(player.getData(AttachmentTypes.ESSENCE)));
    }

    // ============== 类型安全的便捷重载 ==============
    // CultivationManager 拿到的是 Player，这些重载内部 instanceof 检查

    /** Player 版本：仅 ServerPlayer 才发包 */
    public static void syncAll(Player player) {
        if (player instanceof ServerPlayer sp) syncAll(sp);
    }

    public static void syncBasic(Player player) {
        if (player instanceof ServerPlayer sp) syncBasic(sp);
    }

    public static void syncEssence(Player player) {
        if (player instanceof ServerPlayer sp) syncEssence(sp);
    }
}
