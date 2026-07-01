package net.alex.guzhenren.enums.core;

import com.mojang.serialization.Codec;
import java.util.concurrent.ThreadLocalRandom;
import net.alex.guzhenren.util.EnumUtils;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum Talent implements StringRepresentable {

    TEN_EXTREME(100, 100),
    FIRST(80, 99),
    SECOND(60, 79),
    THIRD(40, 59),
    FOURTH(20, 39),
    NONE(0, 0);

    public static final Codec<Talent> CODEC = StringRepresentable.fromEnum(Talent::values);
    private static final String KEY_PREFIX = "guzhenren.enum.core.talent.";

    private final int minPercent;
    private final int maxPercent;

    Talent(int minPercent, int maxPercent) {
        this.minPercent = minPercent;
        this.maxPercent = maxPercent;
    }

    public int getMinPercent() { return minPercent; }
    public int getMaxPercent() { return maxPercent; }

    @Override
    public @NotNull String getSerializedName() { return name().toLowerCase(); }

    public String getTranslationKey() { return KEY_PREFIX + name().toLowerCase(); }

    /** 在 talent 区间内 roll 一个百分比 (0-100) */
    public static int randomPercent(Talent talent) {
        if (talent.minPercent == talent.maxPercent) return talent.minPercent;
        return ThreadLocalRandom.current().nextInt(talent.minPercent, talent.maxPercent + 1);
    }

    /** 根据百分比反查所属 talent tier. 无匹配时返回 NONE */
    public static Talent fromPercent(int percent) {
        for (Talent t : values()) {
            if (percent >= t.minPercent && percent <= t.maxPercent) return t;
        }
        return NONE;
    }

    /** 随机一个非 NONE 的 talent */
    public static Talent randomNonNone() {
        return EnumUtils.randomExcept(values(), NONE);
    }
}
