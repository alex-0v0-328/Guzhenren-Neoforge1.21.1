package net.alex.guzhenren.gameplay.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class StatusData {

    public static final Codec<StatusData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.BOOL.fieldOf("aperture_awakened").forGetter(StatusData::isApertureAwakened)
    ).apply(i, StatusData::new));

    private boolean apertureAwakened;
    private transient boolean dirty = false;

    public StatusData() { this.apertureAwakened = false; }
    public StatusData(boolean apertureAwakened) { this.apertureAwakened = apertureAwakened; }

    //GETTER & SETTER
    //region AWAKEN APERTURE
    public boolean isApertureAwakened() { return apertureAwakened; }
    public void setApertureAwakened(boolean v) { this.apertureAwakened = v; this.dirty = true; }
    //endregion

    //region DIRTY
    public boolean isDirty() { return dirty; }
    public void clearDirty() { this.dirty = false; }
    //endregion
}
