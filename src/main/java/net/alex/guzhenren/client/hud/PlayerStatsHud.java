package net.alex.guzhenren.client.hud;

import net.alex.guzhenren.client.ClientPlayerData;
import net.alex.guzhenren.enums.core.SoulLevel;
import net.alex.guzhenren.enums.core.TenExtreme;
import net.alex.guzhenren.gameplay.data.CoreComponent;
import net.alex.guzhenren.gameplay.data.EssenceComponent;
import net.alex.guzhenren.gameplay.data.LifespanComponent;
import net.alex.guzhenren.gameplay.data.SoulComponent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

/**
 * 玩家修为 / 真元 / 寿元 / 魂魄 HUD
 * 显示位置: 屏幕左上角 (over hotbar layer)
 */
public class PlayerStatsHud implements LayeredDraw.Layer {

    private static final int MARGIN_X = 4;
    private static final int MARGIN_Y = 4;
    private static final int LINE_HEIGHT = 10;
    private static final int BAR_WIDTH = 80;
    private static final int BAR_HEIGHT = 6;
    private static final int BAR_TEXT_GAP = 2;
    private static final int BAR_BG_COLOR = 0xFF555555;  // 灰色背景
    private static final int BAR_FG_COLOR = 0xFF40C4FF;  // 青色填充 (essence 主题色)
    private static final int TEXT_COLOR   = 0xFFFFFFFF;  // 白色文字

    @Override
    public void render(@NotNull GuiGraphics gui, @NotNull DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        CoreComponent core = ClientPlayerData.getCore();
        EssenceComponent essence = ClientPlayerData.getEssence();
        LifespanComponent lifespan = ClientPlayerData.getLifespan();
        SoulComponent soul = ClientPlayerData.getSoul();
        boolean awakened = ClientPlayerData.getStatus().isApertureAwakened();

        int y = MARGIN_Y;

        // 修为 (rank + stage)
        Component rankStage = Component.translatable(core.getPlayerRank().getTranslationKey())
                .append(Component.literal(" "))
                .append(Component.translatable(core.getPlayerStage().getTranslationKey()));
        gui.drawString(mc.font, rankStage, MARGIN_X, y, TEXT_COLOR);
        y += LINE_HEIGHT;

        // 资质 (talent + percent + physique)
        MutableComponent talentMsg = Component.translatable(core.getPlayerTalent().getTranslationKey())
                .append(Component.literal(" " + core.getPlayerBaseEssence() + "%"));
        TenExtreme physique = core.getPlayerExtremePhysique();
        if (physique != TenExtreme.NONE) {
            talentMsg.append(Component.literal(" ["))
                    .append(Component.translatable(physique.getTranslationKey()))
                    .append(Component.literal("]"));
        }
        gui.drawString(mc.font, Component.translatable("guzhenren.hud.talent", talentMsg), MARGIN_X, y, TEXT_COLOR);
        y += LINE_HEIGHT;

        // 真元条 + 数值 (仅开窍后)
        if (awakened) {
            long max = essence.getMaxEssence();
            long cur = essence.getCurrentEssence();
            float ratio = max > 0 ? Math.min(1f, (float) cur / max) : 0f;
            int fillWidth = (int) (BAR_WIDTH * ratio);

            gui.fill(MARGIN_X, y, MARGIN_X + BAR_WIDTH, y + BAR_HEIGHT, BAR_BG_COLOR);
            gui.fill(MARGIN_X, y, MARGIN_X + fillWidth, y + BAR_HEIGHT, BAR_FG_COLOR);
            y += BAR_HEIGHT + BAR_TEXT_GAP;

            gui.drawString(mc.font,
                    Component.translatable("guzhenren.hud.essence", cur, max),
                    MARGIN_X, y, TEXT_COLOR);
            y += LINE_HEIGHT;
        }

        // 寿元 (年龄 + 剩余年数)
        gui.drawString(mc.font,
                Component.translatable("guzhenren.hud.lifespan", lifespan.getAge(), lifespan.getRemainingYears()),
                MARGIN_X, y, TEXT_COLOR);
        y += LINE_HEIGHT;

        // 魂魄 (level + 数值)
        SoulLevel soulLevel = SoulLevel.fromSoulValue(soul.getSoul());
        gui.drawString(mc.font,
                Component.translatable("guzhenren.hud.soul",
                        Component.translatable(soulLevel.getTranslationKey()), soul.getSoul()),
                MARGIN_X, y, TEXT_COLOR);
    }
}
