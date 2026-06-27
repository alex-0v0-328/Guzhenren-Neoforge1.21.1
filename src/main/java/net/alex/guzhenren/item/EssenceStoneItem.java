package net.alex.guzhenren.item;

import net.alex.guzhenren.gameplay.data.EssenceComponent;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.registry.ModAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EssenceStoneItem extends Item {

    private static final int COOLDOWN_TICKS = 2;
    private static final long RECOVERY_DIVISOR = 20L;

    public EssenceStoneItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer sp)) return InteractionResultHolder.success(stack);

        ModPlayerData data = sp.getData(ModAttachments.PLAYER_DATA.get());

        if (!data.status().isApertureAwakened()) {
            sp.displayClientMessage(Component.translatable("item.guzhenren.essence_stone.use_failed.not_awakened"), true);
            return InteractionResultHolder.fail(stack);
        }

        EssenceComponent essence = data.essence();
        if (essence.getCurrentEssence() >= essence.getMaxEssence()) {
            sp.displayClientMessage(Component.translatable("item.guzhenren.essence_stone.use_failed.full"), true);
            return InteractionResultHolder.fail(stack);
        }

        float recovery = (float) (essence.getMaxEssence() / RECOVERY_DIVISOR);
        essence.addCurrent(recovery);

        if (!sp.getAbilities().instabuild) {
            stack.shrink(1);
        }
        sp.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        return InteractionResultHolder.consume(stack);
    }
}
