package net.alex.guzhenren.item.gu;

import java.util.List;
import net.alex.guzhenren.enums.gu.GuType;
import net.alex.guzhenren.item.AbstractModItem;
import net.alex.guzhenren.item.ItemUseResult;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

/** 蛊虫物品. use 逻辑委托给 GuEffect, tooltip 显示 path/rank/category */
public class GuItem extends AbstractModItem {

    private final GuProperties guProps;
    private final GuEffect effect;

    public GuItem(Properties properties, GuProperties guProps, GuEffect effect,
                  String successMessageKey, String failMessageKey) {
        super(properties, successMessageKey, failMessageKey);
        this.guProps = guProps;
        this.effect = effect;
    }

    public GuProperties getGuProps() { return guProps; }

    @Override
    protected ItemUseResult doUse(ServerPlayer sp, ItemStack stack) {
        // TODO: 喂食检查 (needFeed)
        // TODO: 炼化检查 (needRefine)
        return effect.apply(sp);
    }

    @Override
    protected boolean shouldConsume(ItemUseResult result) {
        return guProps.getType() == GuType.ONE_TIME;
        // TODO: REUSABLE 类型扣耐久 (需 override use 或加更细粒度 hook)
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // TODO: rank 6-10 时切换 category (immortal_gu)
        tooltip.add(Component.translatable("guzhenren.gu.tooltip.line",
                Component.translatable(guProps.getPath().getTranslationKey()),
                Component.translatable(guProps.getRank().getTranslationKey()),
                Component.translatable("guzhenren.gu.tooltip.category.mortal")));
    }
}