package net.alex.guzhenren.datagen.lang;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.cultivation.enums.Aptitude;
import net.alex.guzhenren.cultivation.enums.Rank;
import net.alex.guzhenren.cultivation.enums.Stage;
import net.alex.guzhenren.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ZhCnLanguageProvider extends LanguageProvider {

    public ZhCnLanguageProvider(PackOutput output) {
        super(output, Guzhenren.MOD_ID, "zh_cn");
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
        add(Guzhenren.LANG_PREFIX + "hud.unawakened",   "未开窍·凡人");
        add(Guzhenren.LANG_PREFIX + "hud.essence_label", "真元");

        add(Guzhenren.LANG_PREFIX + "message.gu_fed",     "蛊虫已喂食");
        add(Guzhenren.LANG_PREFIX + "message.gu_starved", "蛊虫力竭饿死");

        add("itemGroup." + Guzhenren.MOD_ID + ".gu_material", "蛊材");
        add("itemGroup." + Guzhenren.MOD_ID + ".mortal_gu",   "凡蛊");

        add(ModItems.HOPE_GU.get(), "希望蛊");
        add(ModItems.ESSENCE_STONE.get(), "元石");
    }

    private static String aptitudeName(Aptitude a) {
        return switch (a) {
            case FIRST    -> "甲等";
            case SECOND   -> "乙等";
            case THIRD    -> "丙等";
            case FOURTH   -> "丁等";
            case EXTREME  -> "十绝体";
            case ORDINARY -> "凡人";
        };
    }

    private static String rankName(Rank r) {
        return switch (r) {
            case ORDINARY -> "凡人";
            case RANK_1   -> "一转";
            case RANK_2   -> "二转";
            case RANK_3   -> "三转";
            case RANK_4   -> "四转";
            case RANK_5   -> "五转";
        };
    }

    private static String stageName(Stage s) {
        return switch (s) {
            case INITIAL -> "初阶";
            case MIDDLE  -> "中阶";
            case UPPER   -> "高阶";
            case PEAK    -> "巅峰";
        };
    }
}
