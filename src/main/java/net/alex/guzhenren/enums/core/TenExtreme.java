package net.alex.guzhenren.enums.core;

import com.mojang.serialization.Codec;
import net.alex.guzhenren.enums.path.Path;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum TenExtreme implements StringRepresentable {

    VERDANT_GREAT_SUN                  (Path.SPACE),
    DESOLATE_ANCIENT_MOON              (Path.TIME),
    NORTHERN_DARK_ICE_SOUL             (Path.WATER, Path.ICE_SNOW),
    BOUNDLESS_FOREST_SAMSARA           (Path.WOOD),
    BLAZING_GLORY_LIGHTNING_BRILLIANCE (Path.FIRE, Path.LIGHTNING),
    MYRIAD_GOLD_WONDROUS_ESSENCE       (Path.METAL),
    GREAT_STRENGTH_TRUE_MARTIAL        (Path.STRENGTH),
    CAREFREE_WISDOM_HEART              (Path.WISDOM),
    PROFOUND_EARTH_ORIGIN              (Path.EARTH),
    UNIVERSE_GREAT_DERIVATION          (Path.RULE),
    PURE_DREAM_REALITY_SEEKER          (Path.DREAM),
    NONE                               ();

    private final List<Path> talentPaths;

    public static final Codec<TenExtreme> CODEC =
            StringRepresentable.fromEnum(TenExtreme::values);

    TenExtreme(Path... talentPaths) {
        this.talentPaths = List.of(talentPaths);
    }

    public List<Path> getTalentPaths() { return talentPaths; }

    @Override
    public @NotNull String getSerializedName() { return name().toLowerCase(); }

    public String getTranslationKey() { return "guzhenren.enum.cultivation.ten_extreme." + name().toLowerCase(); }
}
