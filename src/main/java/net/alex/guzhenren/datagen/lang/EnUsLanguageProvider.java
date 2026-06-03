package net.alex.guzhenren.datagen.lang;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.cultivation.enums.Aptitude;
import net.alex.guzhenren.cultivation.enums.Rank;
import net.alex.guzhenren.cultivation.enums.Stage;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class EnUsLanguageProvider extends LanguageProvider {

    public EnUsLanguageProvider(PackOutput output) {
        super(output, Guzhenren.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        for (Aptitude a : Aptitude.values()) {
            add(a.getTranslationKey(), aptitudeName(a));
        }
        for (Rank r : Rank.values()) {
            add(r.getTranslationKey(), rankName(r));
        }
        for (Stage s : Stage.values()) {
            add(s.getTranslationKey(), stageName(s));
        }

        // HUD
        add(Guzhenren.LANG_PREFIX + "hud.unawakened",   "Mortal (not awakened)");
        add(Guzhenren.LANG_PREFIX + "hud.essence_label", "Essence");
    }

    private static String aptitudeName(Aptitude a) {
        return switch (a) {
            case FIRST    -> "Grade A";
            case SECOND   -> "Grade B";
            case THIRD    -> "Grade C";
            case FOURTH   -> "Grade D";
            case EXTREME  -> "Ten Extreme";
            case ORDINARY -> "Ordinary";
        };
    }

    private static String rankName(Rank r) {
        return switch (r) {
            case ORDINARY -> "Ordinary";
            case RANK_1   -> "Rank 1";
            case RANK_2   -> "Rank 2";
            case RANK_3   -> "Rank 3";
            case RANK_4   -> "Rank 4";
            case RANK_5   -> "Rank 5";
        };
    }

    private static String stageName(Stage s) {
        return switch (s) {
            case INITIAL -> "Initial";
            case MIDDLE  -> "Middle";
            case UPPER   -> "Upper";
            case PEAK    -> "Peak";
        };
    }
}
