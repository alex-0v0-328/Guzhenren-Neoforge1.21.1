package net.alex.guzhenren.gameplay.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import net.alex.guzhenren.enums.path.Attainment;
import net.alex.guzhenren.enums.path.Path;

public class PathComponent {

    public static final Codec<PathComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.unboundedMap(Path.CODEC, Attainment.CODEC).fieldOf("attainments").forGetter(d -> d.attainments),
            Codec.unboundedMap(Path.CODEC, Codec.LONG).fieldOf("marks").forGetter(d -> d.marks)
    ).apply(i, PathComponent::fromCodec));

    private final Map<Path, Attainment> attainments;
    private final Map<Path, Long> marks;
    private final transient EnumSet<Path> dirtyPaths = EnumSet.noneOf(Path.class);
    private transient boolean forceFullSync = false;

    public PathComponent() {
        this.attainments = new EnumMap<>(Path.class);
        this.marks = new EnumMap<>(Path.class);
        for (Path p : Path.values()) {
            this.attainments.put(p, Attainment.ORDINARY);
            this.marks.put(p, 0L);
        }
    }

    private static PathComponent fromCodec(Map<Path, Attainment> a, Map<Path, Long> m) {
        PathComponent d = new PathComponent();
        d.attainments.putAll(a);
        d.marks.putAll(m);
        return d;
    }

    //region GETTER
    public Attainment getAttainment(Path path) { return attainments.get(path); }
    public long getMarks(Path path) { return marks.get(path); }
//endregion

    //region SETTER
    public void setAttainment(Path path, Attainment attainment) {
        attainments.put(path, attainment);
        dirtyPaths.add(path);
    }

    public void setMarks(Path path, long value) {
        marks.put(path, Math.max(0L, value));
        dirtyPaths.add(path);
    }
//endregion

    //region MARKS
    public void addMarks(Path path, long amount) {
        if (amount <= 0L) return;
        marks.computeIfPresent(path, (k, cur) -> cur + amount);
        dirtyPaths.add(path);
    }

    public void subMarks(Path path, long amount) {
        if (amount <= 0L) return;
        marks.computeIfPresent(path, (k, cur) -> Math.max(0L, cur - amount));
        dirtyPaths.add(path);
    }
//endregion

    //region DIRTY
    public EnumSet<Path> getDirtyPaths() { return dirtyPaths; }
    public boolean isForceFullSync() { return forceFullSync; }
    public void markFullSync() { this.forceFullSync = true; }
    public void clearDirty() {
        dirtyPaths.clear();
        forceFullSync = false;
    }
//endregion
}
