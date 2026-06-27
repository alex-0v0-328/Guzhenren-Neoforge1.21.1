package net.alex.guzhenren.enums.core;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.alex.guzhenren.enums.path.Path;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum TenExtreme implements StringRepresentable {

    VERDANT_GREAT_SUN(Path.SPACE),
    DESOLATE_ANCIENT_MOON(Path.TIME),
    NORTHERN_DARK_ICE_SOUL(Path.WATER, Path.ICE_SNOW),
    BOUNDLESS_FOREST_SAMSARA(Path.WOOD),
    BLAZING_GLORY_LIGHTNING_BRILLIANCE(Path.FIRE, Path.LIGHTNING),
    MYRIAD_GOLD_WONDROUS_ESSENCE(Path.METAL),
    GREAT_STRENGTH_TRUE_MARTIAL(Path.STRENGTH),
    CAREFREE_WISDOM_HEART(Path.WISDOM),
    PROFOUND_EARTH_ORIGIN(Path.EARTH),
    UNIVERSE_GREAT_DERIVATION(Path.RULE),
    PURE_DREAM_REALITY_SEEKER(Path.DREAM),
    NONE;

    public static final Codec<TenExtreme> CODEC = StringRepresentable.fromEnum(TenExtreme::values);

    private final List<Path> talentPaths;

    TenExtreme(Path... talentPaths) {
        this.talentPaths = List.of(talentPaths);
    }

    public List<Path> getTalentPaths() {
        return talentPaths;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase();
    }

    public String getTranslationKey() {
        return "guzhenren.enum.core.ten_extreme." + name().toLowerCase();
    }

    public static TenExtreme randomNonNone() {
        TenExtreme[] vals = values();
        TenExtreme t;
        do {
            t = vals[ThreadLocalRandom.current().nextInt(vals.length)];
        } while (t == NONE);
        return t;
    }
}
