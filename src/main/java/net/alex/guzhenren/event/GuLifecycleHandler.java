package net.alex.guzhenren.event;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.item.ModDataComponents;
import net.alex.guzhenren.item.gu.GuItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Guzhenren.MOD_ID)
public final class GuLifecycleHandler {

    private GuLifecycleHandler() {}

    /** 扫描背包间隔 (ticks) */
    private static final int SCAN_INTERVAL = 200;

    /** 每多少 ticks 衰减 1 vitality
     *  3 天 = 72000 ticks, 100 vitality → 720 ticks/vitality */
    private static final long DECAY_TICKS_PER_POINT = 720L;

    /** 每次喂食回复的 vitality 量 (即 damage 减少量) */
    private static final int FEED_HEAL = 10;

    //region Tick Scan
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (player.tickCount % SCAN_INTERVAL != 0) return;
        scanInventory(player);
    }

    /** 玩家睡觉跳天 → 立刻补扫 (避免离线天数漏检) */
    @SubscribeEvent
    public static void onSleepFinished(SleepFinishedTimeEvent event) {
        if (!(event.getLevel() instanceof ServerLevel server)) return;
        for (ServerPlayer player : server.players()) {
            scanInventory(player);
        }
    }

    /**
     * 扫描玩家背包，按 elapsed 累积衰减各蛊虫
     * 跳过 hungerless 蛊
     */
    private static void scanInventory(Player player) {
        long now = player.level().getGameTime();
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (!(stack.getItem() instanceof GuItem gu)) continue;
            if (gu.isHungerless()) continue;

            Long last = stack.get(ModDataComponents.LAST_DECAY_TIME.get());
            if (last == null) {
                // 第一次见到 → 初始化时间戳，不衰减
                stack.set(ModDataComponents.LAST_DECAY_TIME.get(), now);
                continue;
            }

            long elapsed = now - last;
            if (elapsed < DECAY_TICKS_PER_POINT) continue;

            int decayCount = (int) Math.min(stack.getMaxDamage(), elapsed / DECAY_TICKS_PER_POINT);
            int newDamage = Math.min(stack.getMaxDamage(), stack.getDamageValue() + decayCount);
            stack.setDamageValue(newDamage);
            stack.set(ModDataComponents.LAST_DECAY_TIME.get(),
                    last + (long) decayCount * DECAY_TICKS_PER_POINT);
        }
    }
    //endregion

    //region Feed (双向右键拦截)
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        // 只处理主手 hand 的 fire (避免双 fire 重复处理)
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        Player player = event.getEntity();
        ItemStack main = player.getMainHandItem();
        ItemStack off  = player.getOffhandItem();

        // 识别 "蛊虫 + 食物" 组合 (双向)
        ItemStack guStack = null;
        ItemStack foodStack = null;
        GuItem gu = null;

        if (main.getItem() instanceof GuItem g && g.isFoodMatch(off)) {
            gu = g; guStack = main; foodStack = off;
        } else if (off.getItem() instanceof GuItem g && g.isFoodMatch(main)) {
            gu = g; guStack = off; foodStack = main;
        }

        if (gu == null) return;

        // 取消 vanilla 默认 use (双端 cancel 避免食物被吃)
        event.setCanceled(true);

        // 只在服务端实际改数据
        if (!player.level().isClientSide()) {
            feed(player, guStack, foodStack);
        }
    }

    /** 执行喂食：减 damage (回 vitality)，食物 shrink 1，更新衰减时间戳 */
    private static void feed(Player player, ItemStack guStack, ItemStack foodStack) {
        int newDamage = Math.max(0, guStack.getDamageValue() - FEED_HEAL);
        guStack.setDamageValue(newDamage);
        // 重置衰减计时 (从现在开始重新计 720)
        guStack.set(ModDataComponents.LAST_DECAY_TIME.get(), player.level().getGameTime());
        if (!player.getAbilities().instabuild) {
            foodStack.shrink(1);
        }
        player.sendSystemMessage(Component.translatable("guzhenren.lang.message.gu_fed"));
    }
    //endregion
}
