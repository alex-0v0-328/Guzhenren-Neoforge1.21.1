package net.alex.guzhenren.network.sync;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ModPlayerSyncPayload(ModPlayerData data) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ModPlayerSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Guzhenren.MOD_ID, "sync_player_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ModPlayerSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.fromCodecWithRegistries(ModPlayerData.CODEC), ModPlayerSyncPayload::data,
                    ModPlayerSyncPayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
