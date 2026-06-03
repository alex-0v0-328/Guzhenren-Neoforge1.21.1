package net.alex.guzhenren.cultivation.enums;

import net.alex.guzhenren.Guzhenren;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;

public enum Aptitude {

    FIRST   (99,  80,  0.15, "first"),    // 甲等
    SECOND  (79,  60,  0.20, "second"),   // 乙等
    THIRD   (59,  40,  0.25, "third"),    // 丙等
    FOURTH  (39,  20,  0.30, "fourth"),   // 丁等
    EXTREME (100, 100, 0.10, "extreme"),  // 十绝
    ORDINARY(0,   0,   0.00, "ordinary"); // 凡人 (未开窍占位)

    private final int maxVal;
    private final int minVal;
    private final double rollChance;
    private final String aptitudeKey;

    Aptitude(int maxVal, int minVal, double rollChance, String aptitudeKey) {
        this.maxVal = maxVal;
        this.minVal = minVal;
        this.rollChance = rollChance;
        this.aptitudeKey = aptitudeKey;
    }

    public int getMaxVal() { return maxVal; }
    public int getMinVal() { return minVal; }
    public double getRollChance() { return rollChance; }
    public String getAptitudeKey() { return aptitudeKey; }

    public String getTranslationKey() {
        return Guzhenren.LANG_PREFIX + "aptitude." + aptitudeKey;
    }

    public MutableComponent getDisplayName() {
        return Component.translatable(getTranslationKey());
    }

    public int rollValue(RandomSource random) {
        if (minVal == maxVal) return minVal;
        return random.nextIntBetweenInclusive(minVal, maxVal);
    }

    public static Aptitude rollAptitude(RandomSource random) {
        double r = random.nextDouble();
        double cumulative = 0.0;
        for (Aptitude a : values()) {
            if (a == ORDINARY) continue;
            cumulative += a.rollChance;
            if (r < cumulative) return a;
        }
        return FOURTH; // 浮点兜底 (fallback for rounding edge case)
    }
}
