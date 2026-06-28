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

public class ZhCnLanguageProvider extends LanguageProvider {

    public ZhCnLanguageProvider(PackOutput output) {
        super(output, Guzhenren.MOD_ID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        addRank();
        addStage();
        addTalent();
        addTenExtreme();
        addPath();
        addAttainment();
        addCommandKeys();
        addItemKeys();
        addLifespanSoulKeys();
        addGuKeys();
    }

    private void addRank() {
        add(Rank.MORTAL.getTranslationKey(), "凡人");
        add(Rank.ONE.getTranslationKey(), "一转");
        add(Rank.TWO.getTranslationKey(), "二转");
        add(Rank.THREE.getTranslationKey(), "三转");
        add(Rank.FOUR.getTranslationKey(), "四转");
        add(Rank.FIVE.getTranslationKey(), "五转");
    }

    private void addStage() {
        // 故意留空: 未开窍时不显示
        add(Stage.NONE.getTranslationKey(), "");
        add(Stage.INIT.getTranslationKey(), "初阶");
        add(Stage.MIDDLE.getTranslationKey(), "中阶");
        add(Stage.UPPER.getTranslationKey(), "高阶");
        add(Stage.PEAK.getTranslationKey(), "巅峰");
    }

    private void addTalent() {
        add(Talent.TEN_EXTREME.getTranslationKey(), "十绝体");
        add(Talent.FIRST.getTranslationKey(), "甲等");
        add(Talent.SECOND.getTranslationKey(), "乙等");
        add(Talent.THIRD.getTranslationKey(), "丙等");
        add(Talent.FOURTH.getTranslationKey(), "丁等");
        add(Talent.NONE.getTranslationKey(), "未觉醒");
    }

    private void addTenExtreme() {
        add(TenExtreme.VERDANT_GREAT_SUN.getTranslationKey(), "太日阳莽体");
        add(TenExtreme.DESOLATE_ANCIENT_MOON.getTranslationKey(), "古月阴荒体");
        add(TenExtreme.NORTHERN_DARK_ICE_SOUL.getTranslationKey(), "北冥冰魄体");
        add(TenExtreme.BOUNDLESS_FOREST_SAMSARA.getTranslationKey(), "森海轮回体");
        add(TenExtreme.BLAZING_GLORY_LIGHTNING_BRILLIANCE.getTranslationKey(), "炎煌雷泽体");
        add(TenExtreme.MYRIAD_GOLD_WONDROUS_ESSENCE.getTranslationKey(), "万金妙华体");
        add(TenExtreme.GREAT_STRENGTH_TRUE_MARTIAL.getTranslationKey(), "大力真武体");
        add(TenExtreme.CAREFREE_WISDOM_HEART.getTranslationKey(), "逍遥智心体");
        add(TenExtreme.PROFOUND_EARTH_ORIGIN.getTranslationKey(), "厚土元央体");
        add(TenExtreme.UNIVERSE_GREAT_DERIVATION.getTranslationKey(), "宇宙大衍体");
        add(TenExtreme.PURE_DREAM_REALITY_SEEKER.getTranslationKey(), "纯梦求真体");
        // 故意留空: 非十绝体玩家不显示
        add(TenExtreme.NONE.getTranslationKey(), "");
    }

    private void addPath() {
        add(Path.HEAVEN.getTranslationKey(), "天道");
        add(Path.RULE.getTranslationKey(), "律道");
        add(Path.SPACE.getTranslationKey(), "宇道");
        add(Path.TIME.getTranslationKey(), "宙道");
        add(Path.METAL.getTranslationKey(), "金道");
        add(Path.WOOD.getTranslationKey(), "木道");
        add(Path.WATER.getTranslationKey(), "水道");
        add(Path.FIRE.getTranslationKey(), "火道");
        add(Path.EARTH.getTranslationKey(), "土道");
        add(Path.ICE_SNOW.getTranslationKey(), "冰雪道");
        add(Path.LIGHTNING.getTranslationKey(), "雷道");
        add(Path.QI.getTranslationKey(), "气道");
        add(Path.SOUND.getTranslationKey(), "音道");
        add(Path.LIGHT.getTranslationKey(), "光道");
        add(Path.DARK.getTranslationKey(), "暗道");
        add(Path.STRENGTH.getTranslationKey(), "力道");
        add(Path.DREAM.getTranslationKey(), "梦道");
        add(Path.REFINEMENT.getTranslationKey(), "炼道");
        add(Path.WISDOM.getTranslationKey(), "智道");
        add(Path.THEFT.getTranslationKey(), "偷道");
        add(Path.LUCK.getTranslationKey(), "运道");
        add(Path.KILLING.getTranslationKey(), "杀道");
        add(Path.BLOOD.getTranslationKey(), "血道");
        add(Path.SOUL.getTranslationKey(), "魂道");
        add(Path.ENSLAVEMENT.getTranslationKey(), "奴道");
    }

    private void addAttainment() {
        add(Attainment.ORDINARY.getTranslationKey(), "普通");
        add(Attainment.QUASI_MASTER.getTranslationKey(), "准大师");
        add(Attainment.MASTER.getTranslationKey(), "大师");
        add(Attainment.QUASI_GRANDMASTER.getTranslationKey(), "准宗师");
        add(Attainment.GRANDMASTER.getTranslationKey(), "宗师");
        add(Attainment.QUASI_GREAT_GRANDMASTER.getTranslationKey(), "准大宗师");
        add(Attainment.GREAT_GRANDMASTER.getTranslationKey(), "大宗师");
        add(Attainment.QUASI_SUPREME_GRANDMASTER.getTranslationKey(), "准无上大宗师");
        add(Attainment.SUPREME_GRANDMASTER.getTranslationKey(), "无上大宗师");
    }

    private void addCommandKeys() {
        add("guzhenren.command.success.awakened", "[蛊真人] 已开窍。");
        add("guzhenren.command.success.rank_set", "[蛊真人] 大境界设为 %s。");
        add("guzhenren.command.success.rank_up", "[蛊真人] 大境界提升：%s。");
        add("guzhenren.command.success.rank_down", "[蛊真人] 大境界下降：%s。");
        add("guzhenren.command.success.stage_set", "[蛊真人] 小境界设为 %s。");
        add("guzhenren.command.success.stage_up", "[蛊真人] 小境界提升：%s。");
        add("guzhenren.command.success.stage_down", "[蛊真人] 小境界下降：%s。");
        add("guzhenren.command.success.talent_set", "[蛊真人] 资质设为 %s（已重新 roll 百分比）。");
        add("guzhenren.command.success.base_added", "[蛊真人] 真元基础值 +%s。");
        add("guzhenren.command.success.base_subbed", "[蛊真人] 真元基础值 -%s。");
        add("guzhenren.command.success.essence_added", "[蛊真人] 当前真元 +%s。");
        add("guzhenren.command.success.essence_subbed", "[蛊真人] 当前真元 -%s。");
        add("guzhenren.command.success.essence_refilled", "[蛊真人] 真元已回满。");
        add("guzhenren.command.success.physique_set", "[蛊真人] 体质设为 %s（资质与基础值已同步）。");
        add("guzhenren.command.success.reset", "[蛊真人] 所有数据已重置。");

        add("guzhenren.command.error.cannot_awaken", "[蛊真人] 无法开窍：前置条件不满足。");
        add("guzhenren.command.error.not_awakened", "[蛊真人] 尚未开窍，请先执行 /guzhenren awaken。");
        add("guzhenren.command.error.invalid_talent_none", "[蛊真人] 资质 NONE 不可作为参数。");
        add("guzhenren.command.error.percent_out_of_range", "[蛊真人] 百分比必须在 [%s, %s] 区间内。");
        add("guzhenren.command.error.invalid_rank_mortal", "[蛊真人] 大境界 MORTAL 仅用于未开窍状态。");
        add("guzhenren.command.error.invalid_stage_none", "[蛊真人] 小境界 NONE 仅用于未开窍状态。");
        add("guzhenren.command.error.invalid_physique_none", "[蛊真人] 不可将体质设为 NONE。");
        add("guzhenren.command.error.rank_up_failed", "[蛊真人] 提升失败（已达最高大境界）。");
        add("guzhenren.command.error.rank_down_failed", "[蛊真人] 下降失败（已是 1 转）。");
        add("guzhenren.command.error.stage_up_failed", "[蛊真人] 提升失败（已是巅峰小境界）。");
        add("guzhenren.command.error.stage_down_failed", "[蛊真人] 下降失败（已是初阶）。");

        add("guzhenren.command.info.core", "修为：%s %s");
        add("guzhenren.command.info.talent", "资质：%s");
        add("guzhenren.command.info.essence", "真元：%s / %s");
        add("guzhenren.command.info.not_awakened", "真元：（未开窍）");
        add("guzhenren.command.info.path_entry", "  %s  %s  道痕：%s");
        add("guzhenren.command.info.no_paths", "  （未开发任何流派）");

        add("guzhenren.command.success.path_marks_added", "[蛊真人] %s 道痕 +%s。");
        add("guzhenren.command.success.path_marks_subbed", "[蛊真人] %s 道痕 -%s。");
        add("guzhenren.command.success.path_attainment_set", "[蛊真人] %s 流派境界设为 %s。");
        add("guzhenren.command.success.path_attainment_up", "[蛊真人] %s 流派境界提升：%s。");
        add("guzhenren.command.success.path_attainment_down", "[蛊真人] %s 流派境界下降：%s。");
        add("guzhenren.command.error.path_attainment_up_failed", "[蛊真人] 流派境界提升失败（已是无上大宗师）。");
        add("guzhenren.command.error.path_attainment_down_failed", "[蛊真人] 流派境界下降失败（已是普通）。");
    }

    private void addItemKeys() {
        add("guzhenren.itemGroup.gu_materials", "蛊材");
        add("item.guzhenren.essence_stone", "元石");
        add("item.guzhenren.essence_stone.use_failed.not_awakened", "请先开窍。");
        add("item.guzhenren.essence_stone.use_failed.full", "真元已满。");
    }

    private void addLifespanSoulKeys() {
        add(SoulLevel.ONE_PERSON.getTranslationKey(), "一人魂");
        add(SoulLevel.TEN_PEOPLE.getTranslationKey(), "十人魂");
        add(SoulLevel.HUNDRED_PEOPLE.getTranslationKey(), "百人魂");
        add(SoulLevel.THOUSAND_PEOPLE.getTranslationKey(), "千人魂");
        add(SoulLevel.TEN_THOUSAND.getTranslationKey(), "万人魂");
        add(SoulLevel.HUNDRED_THOUSAND.getTranslationKey(), "十万人魂");
        add(SoulLevel.MILLION.getTranslationKey(), "百万人魂");
        add(SoulLevel.TEN_MILLION.getTranslationKey(), "千万人魂");
        add(SoulLevel.HUNDRED_MILLION.getTranslationKey(), "亿人魂");

        add("guzhenren.hud.talent", "资质：%s");
        add("guzhenren.hud.lifespan", "年龄：%s 岁 | 寿元：%s 年");
        add("guzhenren.hud.soul", "魂魄：%s（%s）");

        add("guzhenren.command.info.lifespan", "年龄：%s 岁 | 寿元：%s 年");
        add("guzhenren.command.info.soul", "魂魄：%s（%s）");

        add("guzhenren.command.success.lifespan_added", "[蛊真人] 寿元上限 +%s。");
        add("guzhenren.command.success.lifespan_subbed", "[蛊真人] 寿元上限 -%s。");
        add("guzhenren.command.success.age_set", "[蛊真人] 年龄设为 %s 岁。");
        add("guzhenren.command.success.lifespan_reset", "[蛊真人] 寿元已重置。");

        add("guzhenren.command.success.soul_added", "[蛊真人] 魂魄 +%s。");
        add("guzhenren.command.success.soul_subbed", "[蛊真人] 魂魄 -%s。");
        add("guzhenren.command.success.soul_reset", "[蛊真人] 魂魄已重置。");

        add("death.attack.lifespan_depleted", "%1$s 寿元已尽而亡");
        add("death.attack.soul_collapsed", "%1$s 魂魄衰竭而亡");
    }

    private void addGuKeys() {
        add(Path.HUMAN.getTranslationKey(), "人道");

        add("guzhenren.itemGroup.mortal_gu", "凡蛊");

        add("item.guzhenren.hope_gu", "希望蛊");

        add("guzhenren.gu.tooltip.category.mortal", "凡蛊");
        add("guzhenren.gu.tooltip.line", "%s%s%s");

        add("item.guzhenren.hope_gu.use_success", "希望蛊已助你开窍。");
        add("item.guzhenren.hope_gu.use_failed", "你已开窍，无需此蛊。");

        add("item.guzhenren.lifespan_gu", "寿蛊");
        add("item.guzhenren.ten_years_lifespan_gu", "十年寿蛊");

        add("item.guzhenren.lifespan_gu.use_success", "寿蛊为你增加了 %s 年寿元。");
        add("item.guzhenren.ten_years_lifespan_gu.use_success", "十年寿蛊为你增加了 %s 年寿元。");

        add("item.guzhenren.copper_relics_gu", "青铜舍利蛊");
        add("item.guzhenren.steel_relics_gu", "黑铁舍利蛊");
        add("item.guzhenren.silver_relics_gu", "白银舍利蛊");
        add("item.guzhenren.gold_relics_gu", "黄金舍利蛊");
        add("item.guzhenren.crystal_relics_gu", "紫金舍利蛊");

        add("item.guzhenren.relics_gu.use_success", "舍利蛊提升了你的小境界至 %s。");
        add("item.guzhenren.relics_gu.use_failed.rank_mismatch", "你的大境界与此蛊不匹配。");
        add("item.guzhenren.relics_gu.use_failed.stage_peak", "已是小境界巅峰。");
    }
}
