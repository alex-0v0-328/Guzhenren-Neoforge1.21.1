package net.alex.guzhenren.gameplay.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class LifespanComponent {

    public static final Codec<LifespanComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("max_lifespan").forGetter(LifespanComponent::getMaxLifespan),
            Codec.INT.fieldOf("age").forGetter(LifespanComponent::getAge),
            Codec.INT.fieldOf("tick_in_current_year").forGetter(LifespanComponent::getTickInCurrentYear)
    ).apply(i, LifespanComponent::new));
    private static final int TICKS_PER_YEAR = 24000;
    private static final int DEFAULT_MAX_LIFESPAN = 100;
    private static final int DEFAULT_AGE = 14;

    private int maxLifespan;
    private int age;
    private int tickInCurrentYear;
    private transient boolean actionDirty = false;

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
    public void setMaxLifespan(int value) {
        this.maxLifespan = Math.max(0, value);
        actionDirty = true;
    }

    public void setAge(int value) {
        this.age = Math.max(0, value);
        actionDirty = true;
    }

    public void setTickInCurrentYear(int value) {
        this.tickInCurrentYear = Math.max(0, value);
        actionDirty = true;
    }

    /**
     * 推进当前年的 tick 计数, 满 TICKS_PER_YEAR 自动结算成 age + 1
     * 返回 true 表示玩家已死 (age >= max)
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
        actionDirty = true;
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
        setMaxLifespan(Math.max(this.age, this.maxLifespan - amount));
    }
//endregion

//region NATURAL AGING
    /** 每 tick 累加, 满 24000 tick 长一岁; 返回 true 表示玩家已死(age >= max) */
    public boolean naturalAgingPerTick() {
        tickInCurrentYear++;
        if (tickInCurrentYear < TICKS_PER_YEAR) return false;
        tickInCurrentYear = 0;
        age++;
        actionDirty = true;
        return age >= maxLifespan;
    }

    public void reset() {
        this.maxLifespan = DEFAULT_MAX_LIFESPAN;
        this.age = DEFAULT_AGE;
        this.tickInCurrentYear = 0;
        actionDirty = true;
    }
//endregion

    //region DIRTY
    public boolean isActionDirty() { return actionDirty; }
    public void clearActionDirty() { this.actionDirty = false; }
//endregion
}
