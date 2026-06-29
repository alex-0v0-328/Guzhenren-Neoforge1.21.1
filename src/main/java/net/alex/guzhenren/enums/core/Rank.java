package net.alex.guzhenren.enums.core;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum Rank implements StringRepresentable {

    // 凡人无真元
    MORTAL(0, 0L),
    ONE(1, 100L),
    TWO(2, 1_000L),
    THREE(3, 10_000L),
    FOUR(4, 100_000L),
    FIVE(5, 1_000_000L);

    public static final Codec<Rank> CODEC = StringRepresentable.fromEnum(Rank::values);

    private final int rankNum;
    private final long essenceBase;

    Rank(int rankNum, long essenceBase) {
        this.rankNum = rankNum;
        this.essenceBase = essenceBase;
    }

    public int getRankNum() { return rankNum; }
    public long getEssenceBase() { return essenceBase; }

    @Override
    public @NotNull String getSerializedName() { return name().toLowerCase(); }

    public String getTranslationKey() { return "guzhenren.enum.core.rank." + name().toLowerCase(); }
}
