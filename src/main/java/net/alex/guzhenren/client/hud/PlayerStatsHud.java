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
import org.jetbrains.annotations.NotNull;

public class PlayerStatsHud implements LayeredDraw.Layer {

    private static final int MARGIN_X = 4;
    private static final int MARGIN_Y = 4;
    private static final int LINE_HEIGHT = 10;
    private static final int BAR_WIDTH = 80;
    private static final int BAR_HEIGHT = 6;
    private static final int BAR_BG_COLOR = 0xFF555555;
    private static final int BAR_FG_COLOR = 0xFF40C4FF;
    private static final int TEXT_COLOR = 0xFFFFFFFF;

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

        // Line 1: 修为
        Component rankStage = Component.translatable(core.getPlayerRank().getTranslationKey())
                .append(Component.literal(" "))
                .append(Component.translatable(core.getPlayerStage().getTranslationKey()));
        gui.drawString(mc.font, rankStage, MARGIN_X, y, TEXT_COLOR);
        y += LINE_HEIGHT;

        // Line 2: 资质
        Component talentMsg = Component.translatable(core.getPlayerTalent().getTranslationKey())
                .copy().append(Component.literal(" " + core.getPlayerBaseEssence() + "%"));
        TenExtreme physique = core.getPlayerExtremePhysique();
        if (physique != TenExtreme.NONE) {
            talentMsg = talentMsg.copy().append(Component.literal(" ["))
                    .append(Component.translatable(physique.getTranslationKey()))
                    .append(Component.literal("]"));
        }
        gui.drawString(mc.font, Component.translatable("guzhenren.hud.talent", talentMsg), MARGIN_X, y, TEXT_COLOR);
        y += LINE_HEIGHT;

        // Line 3 + 4: 真元条 + 数值 (仅开窍时)
        if (awakened) {
            long max = essence.getMaxEssence();
            float cur = essence.getCurrentEssence();
            float ratio = max > 0 ? Math.min(1f, cur / (float) max) : 0f;
            int fillWidth = (int) (BAR_WIDTH * ratio);

            gui.fill(MARGIN_X, y, MARGIN_X + BAR_WIDTH, y + BAR_HEIGHT, BAR_BG_COLOR);
            gui.fill(MARGIN_X, y, MARGIN_X + fillWidth, y + BAR_HEIGHT, BAR_FG_COLOR);
            y += BAR_HEIGHT + 2;

            String text = String.format("%d / %d", (long) cur, max);
            gui.drawString(mc.font, text, MARGIN_X, y, TEXT_COLOR);
            y += LINE_HEIGHT;
        }

        // Line 5: 年龄 + 寿元
        gui.drawString(mc.font,
                Component.translatable("guzhenren.hud.lifespan", lifespan.getAge(), lifespan.getRemainingYears()),
                MARGIN_X, y, TEXT_COLOR);
        y += LINE_HEIGHT;

        // Line 6: 魂魄
        SoulLevel soulLevel = SoulLevel.fromSoulValue(soul.getSoul());
        gui.drawString(mc.font,
                Component.translatable("guzhenren.hud.soul",
                        Component.translatable(soulLevel.getTranslationKey()), soul.getSoul()),
                MARGIN_X, y, TEXT_COLOR);
    }
}
