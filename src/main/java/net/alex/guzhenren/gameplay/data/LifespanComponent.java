package net.alex.guzhenren.gameplay.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class LifespanComponent {

    public static final Codec<LifespanComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("max_lifespan").forGetter(LifespanComponent::getMaxLifespan),
            Codec.INT.fieldOf("age").forGetter(LifespanComponent::getAge),
            Codec.INT.fieldOf("tick_in_current_year").forGetter(LifespanComponent::getTickInCurrentYear)
    ).apply(i, LifespanComponent::new));

    /** MC 一个游戏日 = 24000 ticks. 设计为 1 游戏日 = 角色 1 年 */
    private static final int TICKS_PER_YEAR = 24000;
    private static final int DEFAULT_MAX_LIFESPAN = 100;
    private static final int DEFAULT_AGE = 14;

    private int maxLifespan;
    private int age;
    private int tickInCurrentYear;
    private transient boolean dirty = false;

    public LifespanComponent() {
        this(DEFAULT_MAX_LIFESPAN, DEFAULT_AGE, 0);
    }

    public LifespanComponent(int maxLifespan, int age, int tickInCurrentYear) {
        this.maxLifespan = maxLifespan;
        this.age = age;
        this.tickInCurrentYear = tickInCurrentYear;
    }

//region GETTER
    public int getMaxLifespan() { return maxLifespan; }
    public int getAge() { return age; }
    public int getTickInCurrentYear() { return tickInCurrentYear; }
    public int getRemainingYears() { return Math.max(0, maxLifespan - age); }
//endregion

//region SETTER
    /** 设置 maxLifespan, 内部 clamp 下限为 age (避免 maxLifespan < age 导致"已超寿但活着") */
    public void setMaxLifespan(int value) {
        this.maxLifespan = Math.max(this.age, value);
        dirty = true;
    }

    public void setAge(int value) {
        this.age = Math.max(0, value);
        dirty = true;
    }

    public void setTickInCurrentYear(int value) {
        this.tickInCurrentYear = Math.max(0, value);
        dirty = true;
    }

    /**
     * 推进当前年的 tick 计数, 满 TICKS_PER_YEAR 自动结算成 age + 1
     * @return true 若玩家已死 (age >= max)
     */
    public boolean advanceTicks(int amount) {
        if (amount <= 0) return false;
        tickInCurrentYear += amount;
        boolean depleted = false;
        while (tickInCurrentYear >= TICKS_PER_YEAR) {
            tickInCurrentYear -= TICKS_PER_YEAR;
            age++;
            if (age >= maxLifespan) depleted = true;
        }
        dirty = true;
        return depleted;
    }
//endregion

//region MAX LIFESPAN
    public void addMaxLifespan(int amount) {
        if (amount <= 0) return;
        setMaxLifespan(this.maxLifespan + amount);
    }

    public void subMaxLifespan(int amount) {
        if (amount <= 0) return;
        setMaxLifespan(this.maxLifespan - amount);
    }
//endregion

//region RESET
    public void reset() {
        this.maxLifespan = DEFAULT_MAX_LIFESPAN;
        this.age = DEFAULT_AGE;
        this.tickInCurrentYear = 0;
        dirty = true;
    }
//endregion

//region DIRTY
    public boolean isDirty() { return dirty; }
    public void clearDirty() { this.dirty = false; }
//endregion
}
