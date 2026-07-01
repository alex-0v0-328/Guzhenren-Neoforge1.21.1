package net.alex.guzhenren.gameplay.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class StatusComponent {

    public static final Codec<StatusComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.BOOL.fieldOf("aperture_awakened").forGetter(StatusComponent::isApertureAwakened)
    ).apply(i, StatusComponent::new));

    private boolean apertureAwakened;
    private transient boolean dirty = false;
    /** 死因 flag: true 表示上次死亡是寿元耗尽或魂魄衰竭. 不持久化, 仅供 PlayerEvent.Clone 读取 */
    private transient boolean deathByLifespanOrSoul = false;

    public StatusComponent() {
        this.apertureAwakened = false;
    }

    /** Codec 反序列化专用. 外部代码请用无参构造 + setter */
    public StatusComponent(boolean apertureAwakened) {
        this.apertureAwakened = apertureAwakened;
    }

//region AWAKEN APERTURE
    public boolean isApertureAwakened() { return apertureAwakened; }

    public void setApertureAwakened(boolean awakened) {
        this.apertureAwakened = awakened;
        this.dirty = true;
    }
//endregion

//region DEATH FLAG
    public boolean isDeathByLifespanOrSoul() { return deathByLifespanOrSoul; }
    public void markDeathByLifespanOrSoul() { this.deathByLifespanOrSoul = true; }
    public void clearDeathByLifespanOrSoul() { this.deathByLifespanOrSoul = false; }
//endregion

//region RESET
    public void reset() {
        this.apertureAwakened = false;
        this.dirty = true;
    }
//endregion

//region DIRTY
    public boolean isDirty() { return dirty; }
    public void clearDirty() { this.dirty = false; }
//endregion
}
