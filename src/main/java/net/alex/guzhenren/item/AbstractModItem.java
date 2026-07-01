package net.alex.guzhenren.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * mod item 基类. 提供 use 模板:
 * 1. client 端早返回 success
 * 2. server 端调 subclass 的 doUse
 * 3. 按 result 显示 message
 * 4. 成功时 consume + cooldown
 * subclass 只需实现 doUse (业务逻辑) + 可选 override shouldConsume / getCooldownTicks
 */
public abstract class AbstractModItem extends Item {

    protected static final int DEFAULT_COOLDOWN_TICKS = 2;

    protected final String successMessageKey;
    protected final String failMessageKey;

    protected AbstractModItem(Properties properties, String successMessageKey, String failMessageKey) {
        super(properties);
        this.successMessageKey = successMessageKey;
        this.failMessageKey = failMessageKey;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer sp)) return InteractionResultHolder.success(stack);

        ItemUseResult result = doUse(sp, stack);
        sendMessage(sp, result);
        if (!result.success()) return InteractionResultHolder.fail(stack);

        if (shouldConsume(result) && !sp.getAbilities().instabuild) {
            stack.shrink(1);
        }
        sp.getCooldowns().addCooldown(this, getCooldownTicks());
        return InteractionResultHolder.consume(stack);
    }

    /** subclass 实现的业务逻辑 */
    protected abstract ItemUseResult doUse(ServerPlayer sp, ItemStack stack);

    /** 是否消耗物品 (默认 true). subclass 可 override */
    protected boolean shouldConsume(ItemUseResult result) { return true; }

    /** cooldown tick 数 (默认 2). subclass 可 override */
    protected int getCooldownTicks() { return DEFAULT_COOLDOWN_TICKS; }

    private void sendMessage(ServerPlayer sp, ItemUseResult result) {
        if (result.success()) {
            if (successMessageKey != null) {
                sp.displayClientMessage(Component.translatable(successMessageKey, result.messageArgs()), true);
            }
            return;
        }
        String key = result.overrideMessageKey() != null ? result.overrideMessageKey() : failMessageKey;
        if (key != null) sp.displayClientMessage(Component.translatable(key), true);
    }
}