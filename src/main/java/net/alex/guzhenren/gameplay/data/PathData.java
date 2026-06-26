package net.alex.guzhenren.gameplay.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.alex.guzhenren.enums.path.*;

import java.util.EnumMap;
import java.util.Map;

public class PathData {

    public static final Codec<PathData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.unboundedMap(Path.CODEC, Attainment.CODEC).fieldOf("attainments").forGetter(d -> d.attainments),
            Codec.unboundedMap(Path.CODEC, Codec.LONG).fieldOf("marks").forGetter(d -> d.marks)
    ).apply(i, PathData::fromCodec));

    private final Map<Path, Attainment> attainments;
    private final Map<Path, Long> marks;
    private transient boolean dirty = false;

    public PathData() {
        this.attainments = new EnumMap<>(Path.class);
        this.marks = new EnumMap<>(Path.class);
        for (Path p : Path.values()) {
            this.attainments.put(p, Attainment.ORDINARY);
            this.marks.put(p, 0L);
        }
    }

    private static PathData fromCodec(Map<Path, Attainment> a, Map<Path, Long> m) {
        PathData d = new PathData();
        d.attainments.putAll(a);
        d.marks.putAll(m);
        return d;
    }

    //region GETTER
    public Attainment getAttainment(Path path) { return attainments.get(path); }
    public long getMarks(Path path) { return marks.get(path); }
    //endregion

    //region SETTER
    public void setAttainment(Path path, Attainment attainment) { attainments.put(path, attainment); this.dirty = true; }
    public void setMarks(Path path, long value) { marks.put(path, Math.max(0L, value)); this.dirty = true; }
    //endregion

    //region MARKS
    public void addMarks(Path path, long amount) {
        if (amount <= 0L) return;
        marks.computeIfPresent(path, (k, c) -> c + amount);
        this.dirty = true;
    }

    public void subMarks(Path path, long amount) {
        if (amount <= 0L) return;
        marks.computeIfPresent(path, (k, c) -> Math.max(0L, c - amount));
        this.dirty = true;
    }
    //endregion

    //region DIRTY
    public boolean isDirty() { return dirty; }
    public void clearDirty() { this.dirty = false; }
    //endregion
}
