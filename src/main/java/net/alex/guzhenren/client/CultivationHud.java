package net.alex.guzhenren.client;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.cultivation.AttachmentTypes;
import net.alex.guzhenren.cultivation.attachments.BasicAttachments;
import net.alex.guzhenren.cultivation.attachments.EssenceAttachment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Guzhenren.MOD_ID, value = Dist.CLIENT)
public class CultivationHud {
    private CultivationHud() {}

    /** Layer 资源 ID */
    private static final ResourceLocation LAYER_ID =
            ResourceLocation.fromNamespaceAndPath(Guzhenren.MOD_ID, "cultivation_hud");

    /** 距离屏幕边缘的内边距 (px) */
    private static final int PADDING = 4;

    // 颜色 (ARGB 格式，alpha=0xFF 必须显式提供，否则透明)
    private static final int COLOR_MORTAL  = 0xFF808080; // 灰 - 未开窍
    private static final int COLOR_HEADER  = 0xFFFFD700; // 金 - 境界行
    private static final int COLOR_ESSENCE = 0xFF55FFFF; // 青 - 真元行

    //region Layer 注册
    @SubscribeEvent
    public static void onRegisterLayers(RegisterGuiLayersEvent event) {
        // registerAboveAll: 在所有原版 HUD 之上 (聊天框打开时不会被覆盖)
        event.registerAboveAll(LAYER_ID, CultivationHud::render);
    }
    //endregion

    //region 渲染
    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) return; // F1 跟随隐藏 (双保险，框架已自动处理)

        LocalPlayer player = mc.player;
        if (player == null) return;

        BasicAttachments b = player.getData(AttachmentTypes.REALM);
        EssenceAttachment e = player.getData(AttachmentTypes.ESSENCE);
        Font font = mc.font;
        int screenWidth = graphics.guiWidth();
        int y = PADDING;

        if (!b.isAwaken()) {
            // ---- 未开窍：单行 ----
            MutableComponent line = Component.translatable(Guzhenren.LANG_PREFIX + "hud.unawakened");
            drawRight(graphics, font, line, screenWidth, y, COLOR_MORTAL);
        } else {
            // ---- 已开窍：双行 ----
            // Line 1: "一转·巅峰 (甲等 89)"
            MutableComponent header = b.getRank().getDisplayName()
                    .append("·")
                    .append(b.getStage().getDisplayName())
                    .append(" (")
                    .append(b.getAptitude().getDisplayName())
                    .append(" " + b.getAptitudeRoll() + ")");
            drawRight(graphics, font, header, screenWidth, y, COLOR_HEADER);
            y += font.lineHeight + 1;

            // Line 2: "真元: 1234 / 5678"
            MutableComponent essence = Component.translatable(Guzhenren.LANG_PREFIX + "hud.essence_label")
                    .append(": " + e.getCurrentEssence() + " / " + e.getMaxEssence());
            drawRight(graphics, font, essence, screenWidth, y, COLOR_ESSENCE);
        }
    }

    /** 右对齐绘制文字 (right-aligned draw) */
    private static void drawRight(GuiGraphics g, Font font, Component text,
                                  int screenWidth, int y, int color) {
        int textWidth = font.width(text);
        // dropShadow=true 让深色背景下也可读
        g.drawString(font, text, screenWidth - textWidth - PADDING, y, color, true);
    }
    //endregion
}
