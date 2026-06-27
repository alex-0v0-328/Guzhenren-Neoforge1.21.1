package net.alex.guzhenren.gameplay.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class StatusComponent {

    public static final Codec<StatusComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.BOOL.fieldOf("aperture_awakened").forGetter(StatusComponent::isApertureAwakened)
    ).apply(i, StatusComponent::new));

    private boolean apertureAwakened;
    private transient boolean dirty = false;

    public StatusComponent() {
        this.apertureAwakened = false;
    }

    public StatusComponent(boolean apertureAwakened) {
        this.apertureAwakened = apertureAwakened;
    }

    //region AWAKEN APERTURE
    public boolean isApertureAwakened() {
        return apertureAwakened;
    }

    public void setApertureAwakened(boolean awakened) {
        this.apertureAwakened = awakened;
        this.dirty = true;
    }
//endregion

    //region DIRTY
    public boolean isDirty() {
        return dirty;
    }

    public void clearDirty() {
        this.dirty = false;
    }
//endregion
}
