package net.alex.guzhenren.enums.cultivation;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public enum Talent implements StringRepresentable {

    TEN_EXTREME(100, 100),
    FIRST      (80, 99),
    SECOND     (60, 79),
    THIRD      (40, 59),
    FOURTH     (20, 39),
    NONE       (0, 0);

    private final int minEssence;
    private final int maxEssence;

    public static final Codec<Talent> CODEC =
            StringRepresentable.fromEnum(Talent::values);

    Talent(int minVal, int maxVal) {
        this.minEssence = minVal;
        this.maxEssence = maxVal;
    }

    public int getMinEssence() { return minEssence; }
    public int getMaxEssence() { return maxEssence; }

    @Override
    public @NotNull String getSerializedName() { return name().toLowerCase(); }

    public static int randomMaxEssence(Talent talent) {
        if (talent.minEssence == talent.maxEssence) return talent.minEssence;
        return ThreadLocalRandom.current().nextInt(talent.minEssence, talent.maxEssence + 1);
    }

    public static Talent fromEssenceMax(int essenceMax) {
        for (Talent t : values()) {
            if (essenceMax >= t.minEssence && essenceMax <= t.maxEssence) return t;
        }
        return NONE;
    }

    public String getTranslationKey() {
        return "guzhenren.enum.cultivation.talent." + name().toLowerCase();
    }
}
