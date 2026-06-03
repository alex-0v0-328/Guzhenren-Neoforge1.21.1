package net.alex.guzhenren.cultivation.enums;

import net.alex.guzhenren.Guzhenren;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum Stage {

    INITIAL(1, "init"),
    MIDDLE(2, "mid"),
    UPPER(4, "up"),
    PEAK(8, "peak");

    private final int stageMultiplier;
    private final String stageKey;

    Stage(int multiplier, String key) {
        this.stageMultiplier = multiplier;
        this.stageKey = key;
    }

    public int getStageMultiplier() { return stageMultiplier; }
    public String getStageKey() { return stageKey; }

    public String getTranslationKey() {
        return Guzhenren.LANG_PREFIX + "stage." + stageKey;
    }

    public MutableComponent getDisplayName() {
        return Component.translatable(getTranslationKey());
    }
}
