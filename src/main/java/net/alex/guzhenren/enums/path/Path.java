package net.alex.guzhenren.enums.path;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * 流派 (Path) — 26 个流派
 * 每个 path 玩家可独立修炼 attainment 境界 + 累计 marks 道痕
 * 编号顺序按 Reverend Insanity 小说创作时间, 非严格分类; 空行分组仅为视觉
 */
public enum Path implements StringRepresentable {

    HEAVEN, RULE, SPACE, TIME, HUMAN,

    METAL, WOOD, WATER, FIRE, EARTH, ICE_SNOW, LIGHTNING,
    QI, SOUND, LIGHT, DARK,

    STRENGTH, DREAM, REFINEMENT, WISDOM, THEFT,
    LUCK, KILLING, BLOOD, SOUL, ENSLAVEMENT;

    public static final Codec<Path> CODEC = StringRepresentable.fromEnum(Path::values);

    @Override
    public @NotNull String getSerializedName() { return name().toLowerCase(); }

    public String getTranslationKey() { return "guzhenren.enum.path.path." + name().toLowerCase(); }
}
