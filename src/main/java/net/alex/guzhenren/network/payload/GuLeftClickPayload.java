package net.alex.guzhenren.network.payload;

import net.alex.guzhenren.Guzhenren;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record GuLeftClickPayload() implements CustomPacketPayload {

    public static final Type<GuLeftClickPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Guzhenren.MOD_ID, "guzhenren_left_click")
    );

    /** 单例 codec — payload 无 payload 数据 */
    public static final StreamCodec<RegistryFriendlyByteBuf, GuLeftClickPayload> STREAM_CODEC =
            StreamCodec.unit(new GuLeftClickPayload());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
