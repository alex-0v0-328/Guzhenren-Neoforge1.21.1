package net.alex.guzhenren.enums.core;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum SoulLevel implements StringRepresentable {

    ONE_PERSON       (100L),
    TEN_PEOPLE       (1_000L),
    HUNDRED_PEOPLE   (10_000L),
    THOUSAND_PEOPLE  (100_000L),
    TEN_THOUSAND     (1_000_000L),
    HUNDRED_THOUSAND (10_000_000L),
    MILLION          (100_000_000L),
    TEN_MILLION      (1_000_000_000L),
    HUNDRED_MILLION  (10_000_000_000L);

    public static final Codec<SoulLevel> CODEC = StringRepresentable.fromEnum(SoulLevel::values);

    private final long threshold;

    SoulLevel(long threshold) {
        this.threshold = threshold;
    }

    public long getThreshold() { return threshold; }

    @Override
    public @NotNull String getSerializedName() { return name().toLowerCase(); }

    public String getTranslationKey() { return "guzhenren.enum.core.soul_level." + name().toLowerCase(); }

    /** 向下取整返回 soul 对应的最高 level. 任意 soul >= 0 都返回 ONE_PERSON 或以上 */
    public static SoulLevel fromSoulValue(long soul) {
        SoulLevel result = ONE_PERSON;
        for (SoulLevel level : values()) {
            if (soul >= level.threshold) result = level;
            else break;
        }
        return result;
    }
}
