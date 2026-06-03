package net.alex.guzhenren.network.payload;

import io.netty.buffer.ByteBuf;
import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.cultivation.attachments.BasicAttachments;
import net.alex.guzhenren.cultivation.enums.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SyncBasicPayload (
        boolean isAwaken,
        Rank rank,
        Stage stage,
        Aptitude aptitude,
        int aptitudeRoll
) implements CustomPacketPayload {

    public static final Type<SyncBasicPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Guzhenren.MOD_ID, "sync_basic")
    );

    // enum 序列化使用 ordinal (VAR_INT)
    private static final StreamCodec<ByteBuf, Rank> RANK_CODEC =
            ByteBufCodecs.VAR_INT.map(i -> Rank.values()[i], Enum::ordinal);
    private static final StreamCodec<ByteBuf, Stage> STAGE_CODEC =
            ByteBufCodecs.VAR_INT.map(i -> Stage.values()[i], Enum::ordinal);
    private static final StreamCodec<ByteBuf, Aptitude> APTITUDE_CODEC =
            ByteBufCodecs.VAR_INT.map(i -> Aptitude.values()[i], Enum::ordinal);

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBasicPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,    SyncBasicPayload::isAwaken,
                    RANK_CODEC,            SyncBasicPayload::rank,
                    STAGE_CODEC,           SyncBasicPayload::stage,
                    APTITUDE_CODEC,        SyncBasicPayload::aptitude,
                    ByteBufCodecs.VAR_INT, SyncBasicPayload::aptitudeRoll,
                    SyncBasicPayload::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /** 从 BasicAttachments 构造 payload */
    public static SyncBasicPayload from(BasicAttachments b) {
        return new SyncBasicPayload(
                b.isAwaken(), b.getRank(), b.getStage(), b.getAptitude(), b.getAptitudeRoll()
        );
    }
}
