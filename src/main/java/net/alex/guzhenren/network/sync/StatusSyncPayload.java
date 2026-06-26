package net.alex.guzhenren.network.sync;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.gameplay.data.StatusData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record StatusSyncPayload(StatusData data) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StatusSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Guzhenren.MOD_ID, "sync_status"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StatusSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.fromCodecWithRegistries(StatusData.CODEC), StatusSyncPayload::data,
                    StatusSyncPayload::new
            );

    @Override public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}
