package net.alex.guzhenren.network.handler;

import net.alex.guzhenren.cultivation.AttachmentTypes;
import net.alex.guzhenren.cultivation.attachments.BasicAttachments;
import net.alex.guzhenren.cultivation.attachments.EssenceAttachment;
import net.alex.guzhenren.network.payload.SyncBasicPayload;
import net.alex.guzhenren.network.payload.SyncEssencePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@OnlyIn(Dist.CLIENT)
public class ClientPayloadHandler {

    private ClientPayloadHandler() {}

    /** 写入客户端本地 BasicAttachments */
    public static void handleSyncBasic(SyncBasicPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            BasicAttachments b = player.getData(AttachmentTypes.REALM);
            b.setAwaken(payload.isAwaken());
            b.setRank(payload.rank());
            b.setStage(payload.stage());
            b.setAptitude(payload.aptitude());
            b.setAptitudeRoll(payload.aptitudeRoll());
        });
    }

    /** 写入客户端本地 EssenceAttachment */
    public static void handleSyncEssence(SyncEssencePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            EssenceAttachment e = player.getData(AttachmentTypes.ESSENCE);
            e.setMaxEssence(payload.maxEssence());
            e.setCurrentEssence(payload.currentEssence());
        });
    }
}
