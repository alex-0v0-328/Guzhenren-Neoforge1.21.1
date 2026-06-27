package net.alex.guzhenren.client.hud;

import net.alex.guzhenren.client.ClientPlayerData;
import net.alex.guzhenren.gameplay.data.CoreComponent;
import net.alex.guzhenren.gameplay.data.EssenceComponent;
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
        boolean awakened = ClientPlayerData.getStatus().isApertureAwakened();

        // 第一行: 修为 (Rank + Stage), 始终显示
        Component rankStage = Component.translatable(core.getPlayerRank().getTranslationKey())
                .append(Component.literal(" "))
                .append(Component.translatable(core.getPlayerStage().getTranslationKey()));
        gui.drawString(mc.font, rankStage, MARGIN_X, MARGIN_Y, TEXT_COLOR);

        // 第二行: 真元条, 未开窍时不显示
        if (!awakened) return;

        int barY = MARGIN_Y + LINE_HEIGHT;
        long max = essence.getMaxEssence();
        float cur = essence.getCurrentEssence();
        float ratio = max > 0 ? Math.min(1f, cur / (float) max) : 0f;
        int fillWidth = (int) (BAR_WIDTH * ratio);

        gui.fill(MARGIN_X, barY, MARGIN_X + BAR_WIDTH, barY + BAR_HEIGHT, BAR_BG_COLOR);
        gui.fill(MARGIN_X, barY, MARGIN_X + fillWidth, barY + BAR_HEIGHT, BAR_FG_COLOR);

        // 第三行: 真元数值
        String text = String.format("%d / %d", (long) cur, max);
        gui.drawString(mc.font, text, MARGIN_X, barY + BAR_HEIGHT + 2, TEXT_COLOR);
    }
}
