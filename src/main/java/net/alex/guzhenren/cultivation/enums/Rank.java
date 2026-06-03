package net.alex.guzhenren.cultivation.enums;

import net.alex.guzhenren.Guzhenren;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum Rank {

    ORDINARY(0, "ordinary", 0),
    RANK_1(1, "rank_1", 100),
    RANK_2(2, "rank_2", 1000),
    RANK_3(3, "rank_3", 10000),
    RANK_4(4, "rank_4", 100000),
    RANK_5(5, "rank_5", 1000000);

    private final int rankNum;
    private final String rankKey;
    private final int baseEssence;

    Rank(int num, String key, int base) {
        this.rankNum = num;
        this.rankKey = key;
        this.baseEssence = base;
    }

    // getter
    public int getRankNum() { return rankNum; }
    public String getRankKey() { return rankKey; }
    public int getBaseEssence() { return baseEssence; }

    public String getTranslationKey() {
        return Guzhenren.LANG_PREFIX + "rank." + rankKey;
    }

    public MutableComponent getDisplayName() {
        return Component.translatable(getTranslationKey());
    }
}
