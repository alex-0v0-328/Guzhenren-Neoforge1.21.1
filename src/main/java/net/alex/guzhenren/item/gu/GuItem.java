package net.alex.guzhenren.item.gu;

import java.util.List;
import net.alex.guzhenren.enums.gu.GuType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class GuItem extends Item {

    private static final int DEFAULT_COOLDOWN_TICKS = 2;

    private final GuProperties guProps;
    private final GuEffect effect;
    private final String successMessageKey;
    private final String failMessageKey;

    public GuItem(Properties properties, GuProperties guProps, GuEffect effect,
                  String successMessageKey, String failMessageKey) {
        super(properties);
        this.guProps = guProps;
        this.effect = effect;
        this.successMessageKey = successMessageKey;
        this.failMessageKey = failMessageKey;
    }

    public GuProperties getGuProps() { return guProps; }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer sp)) return InteractionResultHolder.success(stack);

        // TODO: 喂食检查 (needFeed)
        // TODO: 炼化检查 (needRefine)

        boolean ok = effect.apply(sp);
        if (!ok) {
            if (failMessageKey != null) sp.displayClientMessage(Component.translatable(failMessageKey), true);
            return InteractionResultHolder.fail(stack);
        }

        if (successMessageKey != null) sp.displayClientMessage(Component.translatable(successMessageKey), true);

        if (guProps.getType() == GuType.ONE_TIME && !sp.getAbilities().instabuild) {
            stack.shrink(1);
        }
        // TODO: REUSABLE 类型扣耐久

        sp.getCooldowns().addCooldown(this, DEFAULT_COOLDOWN_TICKS);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("guzhenren.gu.tooltip.line",
                Component.translatable(guProps.getPath().getTranslationKey()),
                Component.translatable(guProps.getRank().getTranslationKey()),
                Component.translatable("guzhenren.gu.tooltip.category.mortal")));
    }
}
