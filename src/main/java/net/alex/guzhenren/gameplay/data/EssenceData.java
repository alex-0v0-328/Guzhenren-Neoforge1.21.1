package net.alex.guzhenren.gameplay.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.alex.guzhenren.enums.cultivation.*;

public class EssenceData {

    public static final Codec<EssenceData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.LONG.fieldOf("max_essence").forGetter(EssenceData::getMaxEssence),
            Codec.FLOAT.fieldOf("current_essence").forGetter(EssenceData::getCurrentEssence)
    ).apply(i, EssenceData::new));

    private static final int TICKS_PER_DAY = 24000;
    private long maxEssence;
    private float currentEssence;

    public EssenceData() { this(0L, 0f); }

    public EssenceData(long maxEssence, float currentEssence) {
        this.maxEssence = maxEssence;
        this.currentEssence = currentEssence;
    }

    //region GETTER
    public long  getMaxEssence() { return maxEssence; }
    public float getCurrentEssence() { return currentEssence; }
    //endregion

    //region CURRENT ESSENCE
    public void addCurrent(float amount) {
        if (amount <= 0f) return;
        currentEssence = Math.min(maxEssence, currentEssence + amount);
    }

    public void subCurrent(float amount) {
        if (amount <= 0f) return;
        currentEssence = Math.max(0f, currentEssence - amount);
    }
    //endregion

    //region FUNCTIONS
    public void recomputeMaxEssence(int baseEssence, Rank rank, Stage stage) {
        this.maxEssence = (long) baseEssence * rank.getEssenceBase() * stage.getEssenceMultiplier() / 100L;
        if (currentEssence > maxEssence) currentEssence = maxEssence;
    }

    public void naturalRecoveryPerTick() {
        if (maxEssence <= 0L) return;
        float perTick = (float) maxEssence / TICKS_PER_DAY;
        currentEssence = Math.min(maxEssence, currentEssence + perTick);
    }

    public void refillCurrentEssence() { this.currentEssence = this.maxEssence; }
    //endregion
}
