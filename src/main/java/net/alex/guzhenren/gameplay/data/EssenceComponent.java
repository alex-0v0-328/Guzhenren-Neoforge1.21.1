package net.alex.guzhenren.gameplay.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.alex.guzhenren.enums.core.Rank;
import net.alex.guzhenren.enums.core.Stage;

public class EssenceComponent {

    public static final Codec<EssenceComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.LONG.fieldOf("max_essence").forGetter(EssenceComponent::getMaxEssence),
            Codec.LONG.fieldOf("current_essence").forGetter(EssenceComponent::getCurrentEssence)
    ).apply(i, EssenceComponent::new));

    /** MC 一个游戏日 = 24000 ticks. essence 设计为每游戏日回满 */
    private static final int TICKS_PER_DAY = 24000;

    private long maxEssence;
    private long currentEssence;
    /** natural recovery 累积小数缓冲. 满 1.0 入账到 currentEssence */
    private transient double accumulatedRecovery = 0.0;
    private transient boolean dirty = false;

    public EssenceComponent() {
        this(0L, 0L);
    }

    public EssenceComponent(long maxEssence, long currentEssence) {
        this.maxEssence = maxEssence;
        this.currentEssence = currentEssence;
    }

//region GETTER
    public long getMaxEssence() { return maxEssence; }
    public long getCurrentEssence() { return currentEssence; }
//endregion

//region CURRENT ESSENCE
    public void addCurrent(long amount) {
        if (amount <= 0L) return;
        currentEssence = Math.min(maxEssence, currentEssence + amount);
        dirty = true;
    }

    public void subCurrent(long amount) {
        if (amount <= 0L) return;
        currentEssence = Math.max(0L, currentEssence - amount);
        dirty = true;
    }

    /** 包私有 raw setter, 仅供 ModPlayerData.copyFrom 使用. 外部禁用 */
    void setCurrentEssenceRaw(long value) {
        this.currentEssence = Math.clamp(value, 0L, maxEssence);
        dirty = true;
    }
//endregion

//region FUNCTIONS
    public void recomputeMaxEssence(int baseEssence, Rank rank, Stage stage) {
        this.maxEssence = (long) baseEssence * rank.getEssenceBase() * stage.getEssenceMultiplier() / 100L;
        if (currentEssence > maxEssence) currentEssence = maxEssence;
        dirty = true;
    }

    /** 内部用 double 累积小数, 满 1.0 才入账, 避免 long division 丢失精度 */
    public void naturalRecoveryPerTick() {
        if (maxEssence <= 0L) return;
        accumulatedRecovery += (double) maxEssence / TICKS_PER_DAY;
        long whole = (long) accumulatedRecovery;
        if (whole > 0L) {
            accumulatedRecovery -= whole;
            currentEssence = Math.min(maxEssence, currentEssence + whole);
        }
        // 不标 dirty: natural recovery 走周期 sync
    }

    public void refillCurrentEssence() {
        this.currentEssence = this.maxEssence;
        dirty = true;
    }

    /** 重置到初始状态. 调 caller 负责后续 recomputeMaxEssence */
    public void reset() {
        this.maxEssence = 0L;
        this.currentEssence = 0L;
        this.accumulatedRecovery = 0.0;
        dirty = true;
    }
//endregion

//region DIRTY
    public boolean isDirty() { return dirty; }
    public void clearDirty() { this.dirty = false; }
//endregion
}
