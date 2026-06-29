package net.alex.guzhenren.datagen;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.enums.core.Rank;
import net.alex.guzhenren.enums.core.SoulLevel;
import net.alex.guzhenren.enums.core.Stage;
import net.alex.guzhenren.enums.core.Talent;
import net.alex.guzhenren.enums.core.TenExtreme;
import net.alex.guzhenren.enums.path.Attainment;
import net.alex.guzhenren.enums.path.Path;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class EnUsLanguageProvider extends LanguageProvider {

    public EnUsLanguageProvider(PackOutput output) {
        super(output, Guzhenren.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addEnumKeys();
        addHudKeys();
        addCreativeTabKeys();
        addItemKeys();
        addItemMessageKeys();
        addGuTooltipKeys();
        addCommandKeys();
        addDeathMessageKeys();
    }

    //region ENUM
    private void addEnumKeys() {
        addRank();
        addStage();
        addTalent();
        addTenExtreme();
        addPath();
        addAttainment();
        addSoulLevel();
    }

    private void addRank() {
        add(Rank.MORTAL.getTranslationKey(), "Mortal");
        add(Rank.ONE.getTranslationKey(),    "Rank I");
        add(Rank.TWO.getTranslationKey(),    "Rank II");
        add(Rank.THREE.getTranslationKey(),  "Rank III");
        add(Rank.FOUR.getTranslationKey(),   "Rank IV");
        add(Rank.FIVE.getTranslationKey(),   "Rank V");
    }

    private void addStage() {
        // 故意留空: 未开窍时不显示
        add(Stage.NONE.getTranslationKey(),   "");
        add(Stage.INIT.getTranslationKey(),   "Initial");
        add(Stage.MIDDLE.getTranslationKey(), "Middle");
        add(Stage.UPPER.getTranslationKey(),  "Upper");
        add(Stage.PEAK.getTranslationKey(),   "Peak");
    }

    private void addTalent() {
        add(Talent.TEN_EXTREME.getTranslationKey(), "Ten Extremes");
        add(Talent.FIRST.getTranslationKey(),       "Tier I");
        add(Talent.SECOND.getTranslationKey(),      "Tier II");
        add(Talent.THIRD.getTranslationKey(),       "Tier III");
        add(Talent.FOURTH.getTranslationKey(),      "Tier IV");
        add(Talent.NONE.getTranslationKey(),        "Unawakened");
    }

    private void addTenExtreme() {
        add(TenExtreme.VERDANT_GREAT_SUN.getTranslationKey(),                  "Verdant Great Sun");
        add(TenExtreme.DESOLATE_ANCIENT_MOON.getTranslationKey(),              "Desolate Ancient Moon");
        add(TenExtreme.NORTHERN_DARK_ICE_SOUL.getTranslationKey(),             "Northern Dark Ice Soul");
        add(TenExtreme.BOUNDLESS_FOREST_SAMSARA.getTranslationKey(),           "Boundless Forest Samsara");
        add(TenExtreme.BLAZING_GLORY_LIGHTNING_BRILLIANCE.getTranslationKey(), "Blazing Glory Lightning Brilliance");
        add(TenExtreme.MYRIAD_GOLD_WONDROUS_ESSENCE.getTranslationKey(),       "Myriad Gold Wondrous Essence");
        add(TenExtreme.GREAT_STRENGTH_TRUE_MARTIAL.getTranslationKey(),        "Great Strength True Martial");
        add(TenExtreme.CAREFREE_WISDOM_HEART.getTranslationKey(),              "Carefree Wisdom Heart");
        add(TenExtreme.PROFOUND_EARTH_ORIGIN.getTranslationKey(),              "Profound Earth Origin");
        add(TenExtreme.UNIVERSE_GREAT_DERIVATION.getTranslationKey(),          "Universe Great Derivation");
        add(TenExtreme.PURE_DREAM_REALITY_SEEKER.getTranslationKey(),          "Pure Dream Reality Seeker");
        // 故意留空: 非十绝体玩家不显示
        add(TenExtreme.NONE.getTranslationKey(),                               "");
    }

    private void addPath() {
        add(Path.HEAVEN.getTranslationKey(),     "Heaven Path");
        add(Path.RULE.getTranslationKey(),       "Rule Path");
        add(Path.SPACE.getTranslationKey(),      "Space Path");
        add(Path.TIME.getTranslationKey(),       "Time Path");
        add(Path.HUMAN.getTranslationKey(),      "Human Path");
        add(Path.METAL.getTranslationKey(),      "Metal Path");
        add(Path.WOOD.getTranslationKey(),       "Wood Path");
        add(Path.WATER.getTranslationKey(),      "Water Path");
        add(Path.FIRE.getTranslationKey(),       "Fire Path");
        add(Path.EARTH.getTranslationKey(),      "Earth Path");
        add(Path.ICE_SNOW.getTranslationKey(),   "Ice-Snow Path");
        add(Path.LIGHTNING.getTranslationKey(),  "Lightning Path");
        add(Path.QI.getTranslationKey(),         "Qi Path");
        add(Path.SOUND.getTranslationKey(),      "Sound Path");
        add(Path.LIGHT.getTranslationKey(),      "Light Path");
        add(Path.DARK.getTranslationKey(),       "Dark Path");
        add(Path.STRENGTH.getTranslationKey(),   "Strength Path");
        add(Path.DREAM.getTranslationKey(),      "Dream Path");
        add(Path.REFINEMENT.getTranslationKey(), "Refinement Path");
        add(Path.WISDOM.getTranslationKey(),     "Wisdom Path");
        add(Path.THEFT.getTranslationKey(),      "Theft Path");
        add(Path.LUCK.getTranslationKey(),       "Luck Path");
        add(Path.KILLING.getTranslationKey(),    "Killing Path");
        add(Path.BLOOD.getTranslationKey(),      "Blood Path");
        add(Path.SOUL.getTranslationKey(),       "Soul Path");
        add(Path.ENSLAVEMENT.getTranslationKey(),"Enslavement Path");
    }

    private void addAttainment() {
        add(Attainment.ORDINARY.getTranslationKey(),                  "Ordinary");
        add(Attainment.QUASI_MASTER.getTranslationKey(),              "Quasi-Master");
        add(Attainment.MASTER.getTranslationKey(),                    "Master");
        add(Attainment.QUASI_GRANDMASTER.getTranslationKey(),         "Quasi-Grandmaster");
        add(Attainment.GRANDMASTER.getTranslationKey(),               "Grandmaster");
        add(Attainment.QUASI_GREAT_GRANDMASTER.getTranslationKey(),   "Quasi-Great Grandmaster");
        add(Attainment.GREAT_GRANDMASTER.getTranslationKey(),         "Great Grandmaster");
        add(Attainment.QUASI_SUPREME_GRANDMASTER.getTranslationKey(), "Quasi-Supreme Grandmaster");
        add(Attainment.SUPREME_GRANDMASTER.getTranslationKey(),       "Supreme Grandmaster");
    }

    private void addSoulLevel() {
        add(SoulLevel.ONE_PERSON.getTranslationKey(),       "One-Person Soul");
        add(SoulLevel.TEN_PEOPLE.getTranslationKey(),       "Ten-People Soul");
        add(SoulLevel.HUNDRED_PEOPLE.getTranslationKey(),   "Hundred-People Soul");
        add(SoulLevel.THOUSAND_PEOPLE.getTranslationKey(),  "Thousand-People Soul");
        add(SoulLevel.TEN_THOUSAND.getTranslationKey(),     "Ten-Thousand Soul");
        add(SoulLevel.HUNDRED_THOUSAND.getTranslationKey(), "Hundred-Thousand Soul");
        add(SoulLevel.MILLION.getTranslationKey(),          "Million Soul");
        add(SoulLevel.TEN_MILLION.getTranslationKey(),      "Ten-Million Soul");
        add(SoulLevel.HUNDRED_MILLION.getTranslationKey(),  "Hundred-Million Soul");
    }
//endregion

    //region HUD
    private void addHudKeys() {
        add("guzhenren.hud.talent",   "Talent: %s");
        add("guzhenren.hud.lifespan", "Age: %s | Lifespan: %s");
        add("guzhenren.hud.soul",     "Soul: %s (%s)");
    }
//endregion

    //region CREATIVE TAB
    private void addCreativeTabKeys() {
        add("guzhenren.itemGroup.gu_materials", "Gu Materials");
        add("guzhenren.itemGroup.mortal_gu",    "Mortal Gu");
    }
//endregion

    //region ITEM
    private void addItemKeys() {
        add("item.guzhenren.essence_stone",          "Essence Stone");
        add("item.guzhenren.hope_gu",                "Hope Gu");
        add("item.guzhenren.lifespan_gu",            "Lifespan Gu");
        add("item.guzhenren.ten_years_lifespan_gu",  "Ten-Years Lifespan Gu");
        add("item.guzhenren.copper_relics_gu",       "Green Copper Relics Gu");
        add("item.guzhenren.steel_relics_gu",        "Red Steel Relics Gu");
        add("item.guzhenren.silver_relics_gu",       "White Silver Relics Gu");
        add("item.guzhenren.gold_relics_gu",         "Yellow Gold Relics Gu");
        add("item.guzhenren.crystal_relics_gu",      "Purple Crystal Relics Gu");
    }

    private void addItemMessageKeys() {
        add("item.guzhenren.essence_stone.use_failed.not_awakened",    "Awaken your aperture first.");
        add("item.guzhenren.essence_stone.use_failed.full",            "Essence is already full.");

        add("item.guzhenren.hope_gu.use_success",                      "Hope Gu opened your aperture.");
        add("item.guzhenren.hope_gu.use_failed",                       "You are already awakened.");

        add("item.guzhenren.lifespan_gu.use_success",                  "Lifespan Gu extended your lifespan by %s years.");

        add("item.guzhenren.relics_gu.use_success",                    "Relics Gu advanced your stage to %s.");
        add("item.guzhenren.relics_gu.use_failed.rank_mismatch",       "Your rank does not match this Gu.");
        add("item.guzhenren.relics_gu.use_failed.stage_peak",          "You are already at peak stage.");
    }

    private void addGuTooltipKeys() {
        add("guzhenren.gu.tooltip.line",            "%s %s %s");
        add("guzhenren.gu.tooltip.category.mortal", "Mortal Gu");
    }
//endregion

    //region COMMAND
    private void addCommandKeys() {
        addCommandSuccessKeys();
        addCommandErrorKeys();
        addCommandInfoKeys();
    }

    private void addCommandSuccessKeys() {
        add("guzhenren.command.success.awakened",         "[GZR] Awakened.");
        add("guzhenren.command.success.rank_set",         "[GZR] Rank set to %s.");
        add("guzhenren.command.success.rank_up",          "[GZR] Rank up: %s.");
        add("guzhenren.command.success.rank_down",        "[GZR] Rank down: %s.");
        add("guzhenren.command.success.stage_set",        "[GZR] Stage set to %s.");
        add("guzhenren.command.success.stage_up",         "[GZR] Stage up: %s.");
        add("guzhenren.command.success.stage_down",       "[GZR] Stage down: %s.");
        add("guzhenren.command.success.talent_set",       "[GZR] Talent set to %s (rerolled).");
        add("guzhenren.command.success.base_added",       "[GZR] Base essence +%s.");
        add("guzhenren.command.success.base_subbed",      "[GZR] Base essence -%s.");
        add("guzhenren.command.success.essence_added",    "[GZR] Essence +%s.");
        add("guzhenren.command.success.essence_subbed",   "[GZR] Essence -%s.");
        add("guzhenren.command.success.essence_refilled", "[GZR] Essence refilled.");
        add("guzhenren.command.success.physique_set",     "[GZR] Physique set to %s (talent & base synced).");
        add("guzhenren.command.success.reset",            "[GZR] All data reset.");

        add("guzhenren.command.success.path_marks_added",     "[GZR] %s marks +%s.");
        add("guzhenren.command.success.path_marks_subbed",    "[GZR] %s marks -%s.");
        add("guzhenren.command.success.path_attainment_set",  "[GZR] %s attainment set to %s.");
        add("guzhenren.command.success.path_attainment_up",   "[GZR] %s attainment up: %s.");
        add("guzhenren.command.success.path_attainment_down", "[GZR] %s attainment down: %s.");

        add("guzhenren.command.success.lifespan_added",  "[GZR] Max lifespan +%s.");
        add("guzhenren.command.success.lifespan_subbed", "[GZR] Max lifespan -%s.");
        add("guzhenren.command.success.age_set",         "[GZR] Age set to %s.");
        add("guzhenren.command.success.lifespan_reset",  "[GZR] Lifespan reset.");

        add("guzhenren.command.success.soul_added",  "[GZR] Soul +%s.");
        add("guzhenren.command.success.soul_subbed", "[GZR] Soul -%s.");
        add("guzhenren.command.success.soul_reset",  "[GZR] Soul reset.");
    }

    private void addCommandErrorKeys() {
        add("guzhenren.command.error.cannot_awaken",        "[GZR] Cannot awaken: preconditions not met.");
        add("guzhenren.command.error.not_awakened",         "[GZR] Aperture not awakened. Run /guzhenren awaken first.");
        add("guzhenren.command.error.invalid_talent_none",  "[GZR] Talent NONE is not a valid choice.");
        add("guzhenren.command.error.percent_out_of_range", "[GZR] Percent must be in range [%s, %s].");
        add("guzhenren.command.error.invalid_rank_mortal",  "[GZR] Rank MORTAL is reserved for unawakened state.");
        add("guzhenren.command.error.invalid_stage_none",   "[GZR] Stage NONE is reserved for unawakened state.");
        add("guzhenren.command.error.invalid_physique_none","[GZR] Cannot set physique to NONE.");
        add("guzhenren.command.error.rank_up_failed",       "[GZR] Rank up failed (already at peak rank).");
        add("guzhenren.command.error.rank_down_failed",     "[GZR] Rank down failed (already at rank 1).");
        add("guzhenren.command.error.stage_up_failed",      "[GZR] Stage up failed (already at peak stage).");
        add("guzhenren.command.error.stage_down_failed",    "[GZR] Stage down failed (already at initial stage).");

        add("guzhenren.command.error.path_attainment_up_failed",   "[GZR] Attainment up failed (already Supreme Grandmaster).");
        add("guzhenren.command.error.path_attainment_down_failed", "[GZR] Attainment down failed (already Ordinary).");
    }

    private void addCommandInfoKeys() {
        add("guzhenren.command.info.core",          "Cultivation: %s %s");
        add("guzhenren.command.info.talent",        "Talent: %s");
        add("guzhenren.command.info.essence",       "Essence: %s / %s");
        add("guzhenren.command.info.not_awakened",  "Essence: (unawakened)");
        add("guzhenren.command.info.path_entry",    "  %s  %s  marks: %s");
        add("guzhenren.command.info.no_paths",      "  (no paths developed)");
        add("guzhenren.command.info.lifespan",      "Age: %s | Lifespan: %s");
        add("guzhenren.command.info.soul",          "Soul: %s (%s)");
    }
//endregion

    //region DEATH MESSAGE
    private void addDeathMessageKeys() {
        add("death.attack.lifespan_depleted", "%1$s ran out of lifespan");
        add("death.attack.soul_collapsed",    "%1$s's soul collapsed");
    }
//endregion
}
