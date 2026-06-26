package net.alex.guzhenren.network.sync;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.gameplay.data.PlayerData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PlayerDataSyncPayload(PlayerData data) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PlayerDataSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Guzhenren.MOD_ID, "sync_player_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerDataSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.fromCodecWithRegistries(PlayerData.CODEC), PlayerDataSyncPayload::data,
                    PlayerDataSyncPayload::new
            );

    @Override public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}
