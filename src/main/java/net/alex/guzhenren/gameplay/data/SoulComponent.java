package net.alex.guzhenren.gameplay.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SoulComponent {

    public static final Codec<SoulComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.LONG.fieldOf("soul").forGetter(SoulComponent::getSoul)
    ).apply(i, SoulComponent::new));
    private static final long DEFAULT_SOUL = 100L;

    private long soul;
    private transient boolean actionDirty = false;

    public SoulComponent() {
        this(DEFAULT_SOUL);
    }

    public SoulComponent(long soul) {
        this.soul = soul;
    }

    //region GETTER
    public long getSoul() { return soul; }
//endregion

    //region SETTER
    public void setSoul(long value) {
        this.soul = Math.max(0L, value);
        actionDirty = true;
    }
//endregion

    //region SOUL
    public void addSoul(long amount) {
        if (amount <= 0L) return;
        setSoul(this.soul + amount);
    }

    public void subSoul(long amount) {
        if (amount <= 0L) return;
        setSoul(this.soul - amount);
    }

    public void reset() {
        this.soul = DEFAULT_SOUL;
        actionDirty = true;
    }
//endregion

    //region DIRTY
    public boolean isActionDirty() { return actionDirty; }
    public void clearActionDirty() { this.actionDirty = false; }
//endregion
}
