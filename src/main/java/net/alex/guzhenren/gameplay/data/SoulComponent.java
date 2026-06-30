package net.alex.guzhenren.gameplay.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SoulComponent {

    public static final Codec<SoulComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.LONG.fieldOf("soul").forGetter(SoulComponent::getSoul)
    ).apply(i, SoulComponent::new));

    private static final long DEFAULT_SOUL = 100L;

    private long soul;
    private transient boolean dirty = false;

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
        dirty = true;
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
//endregion

//region RESET
    public void reset() {
        setSoul(DEFAULT_SOUL);
    }
//endregion

//region DIRTY
    public boolean isDirty() { return dirty; }
    public void clearDirty() { this.dirty = false; }
//endregion
}
