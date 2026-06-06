package net.alex.guzhenren.item.material;

import net.alex.guzhenren.cultivation.EssenceManager;
import net.alex.guzhenren.cultivation.attachments.EssenceAttachment;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EssenceStone extends Item {

    private static final int COOLDOWN_TICKS = 10;
    private static final long HEAL_DIVISOR = 20L;

    public EssenceStone(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        EssenceAttachment e = EssenceManager.essence(player);
        long max = e.getMaxEssence();
        if (max <= 0L || e.getCurrentEssence() >= max) {
            return InteractionResultHolder.fail(stack);
        }

        // 服务端实际操作
        if (!level.isClientSide()) {
            long heal = Math.max(1L, max / HEAL_DIVISOR);
            EssenceManager.addEssence(player, heal);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResultHolder.success(stack);
    }
}
