package net.alex.guzhenren.network.payload;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.cultivation.attachments.EssenceAttachment;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SyncEssencePayload (
        long maxEssence,
        long currentEssence
) implements CustomPacketPayload {

    public static final Type<SyncEssencePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Guzhenren.MOD_ID, "sync_essence")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncEssencePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG, SyncEssencePayload::maxEssence,
                    ByteBufCodecs.VAR_LONG, SyncEssencePayload::currentEssence,
                    SyncEssencePayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /** 从 EssenceAttachment 构造 payload */
    public static SyncEssencePayload from(EssenceAttachment e) {
        return new SyncEssencePayload(e.getMaxEssence(), e.getCurrentEssence());
    }
}
