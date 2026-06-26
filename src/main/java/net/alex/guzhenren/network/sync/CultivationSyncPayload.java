package net.alex.guzhenren.network.sync;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.gameplay.data.CultivationData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record CultivationSyncPayload(CultivationData data) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<CultivationSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Guzhenren.MOD_ID, "sync_cultivation"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CultivationSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.fromCodecWithRegistries(CultivationData.CODEC), CultivationSyncPayload::data,
                    CultivationSyncPayload::new
            );

    @Override public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}
