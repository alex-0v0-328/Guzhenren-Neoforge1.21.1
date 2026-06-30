package net.alex.guzhenren.network.sync;

import java.util.List;
import net.alex.guzhenren.Guzhenren;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/** Server → Client: 同步 path 增量变化 (仅 dirty paths) */
public record PathDeltaSyncPayload(List<PathDelta> deltas) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PathDeltaSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Guzhenren.MOD_ID, "sync_path_delta"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PathDeltaSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.fromCodecWithRegistries(PathDelta.CODEC.listOf()), PathDeltaSyncPayload::deltas,
                    PathDeltaSyncPayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}
