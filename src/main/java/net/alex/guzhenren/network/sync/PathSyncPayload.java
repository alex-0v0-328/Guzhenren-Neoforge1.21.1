package net.alex.guzhenren.network.sync;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.gameplay.data.PathData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PathSyncPayload(PathData data) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PathSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Guzhenren.MOD_ID, "sync_path"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PathSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.fromCodecWithRegistries(PathData.CODEC), PathSyncPayload::data,
                    PathSyncPayload::new
            );

    @Override public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}
