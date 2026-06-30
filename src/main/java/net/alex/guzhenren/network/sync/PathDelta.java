package net.alex.guzhenren.network.sync;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.alex.guzhenren.enums.path.Attainment;
import net.alex.guzhenren.enums.path.Path;

/** 单 path 的 delta 数据 (用于 PathDeltaSyncPayload 增量同步) */
public record PathDelta(Path path, Attainment attainment, long marks) {

    public static final Codec<PathDelta> CODEC = RecordCodecBuilder.create(i -> i.group(
            Path.CODEC.fieldOf("path").forGetter(PathDelta::path),
            Attainment.CODEC.fieldOf("attainment").forGetter(PathDelta::attainment),
            Codec.LONG.fieldOf("marks").forGetter(PathDelta::marks)
    ).apply(i, PathDelta::new));
}
