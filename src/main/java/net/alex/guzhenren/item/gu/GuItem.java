package net.alex.guzhenren.item.gu;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class GuItem extends Item {

    public GuItem(Properties properties) {
        super(properties);
    }

    //region 子类可覆写的配置
    /** 一次性蛊：使用完直接消耗物品 (希望蛊 = true) */
    protected boolean isOneUse() { return false; }

    /** 不需要喂食：不会饿死、不显示活力条 (希望蛊 = true)；public 供外部 lifecycle 扫描读取 */
    public boolean isHungerless() { return false; }

    /** 使用冷却 (ticks) */
    protected int getCooldownTicks() { return 0; }

    /** 长期蛊每次使用消耗的耐久 (一次性蛊用 isOneUse=true，此值忽略) */
    protected int getDurabilityCost() { return 0; }

    /** 该蛊虫能吃的食物 tag；返回 null 表示不接受任何食物 (hungerless 一般如此) */
    protected TagKey<Item> getDietTag() { return null; }

    /** 客户端/服务端预检 — 双端调用，false 时静默 fail (无提示无消耗) */
    protected boolean canUse(Level level, Player player, ItemStack stack) { return true; }

    /**
     * 实际效果 — 仅服务端调用
     * @return true = 成功 (进入消耗/扣耐久 + cooldown)；false = 失败 (不消耗不 cooldown)
     */
    protected abstract boolean useEffect(Level level, Player player, ItemStack stack);
    //endregion

    //region 左键使用入口 (由 ServerPayloadHandler 调用)
    /**
     * 蛊虫使用主入口 — 仅服务端调用
     * 流程:
     *   1. canUse 预检 → 失败静默
     *   2. 检查是否处于饿死状态 (damage >= maxDamage)
     *   3. 调 useEffect
     *   4. 成功后:
     *      - 饿死状态: 强制消耗物品 + 提示 (绝唱)
     *      - 正常: oneUse → shrink；否则扣 durabilityCost；触发 cooldown
     */
    public void onLeftClick(Level level, Player player, ItemStack stack) {
        if (level.isClientSide()) return;

        // 1. 预检
        if (!canUse(level, player, stack)) return;

        // 2. 是否饿死状态 (仅非 hungerless 蛊可能进入此状态)
        boolean starving = !isHungerless() && stack.getDamageValue() >= stack.getMaxDamage();

        // 3. 执行效果
        boolean ok = useEffect(level, player, stack);
        if (!ok) return;

        // 4. 后处理
        if (starving) {
            // 饿死绝唱：强制消耗物品 (即使长期蛊) + 提示
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.sendSystemMessage(Component.translatable("guzhenren.lang.message.gu_starved"));
            return; // 不进 cooldown
        }

        // 正常路径
        if (isOneUse()) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        } else if (getDurabilityCost() > 0) {
            int newDamage = Math.min(stack.getMaxDamage(), stack.getDamageValue() + getDurabilityCost());
            stack.setDamageValue(newDamage);
        }
        if (getCooldownTicks() > 0) {
            player.getCooldowns().addCooldown(this, getCooldownTicks());
        }
    }
    //endregion

    //region 喂食相关
    /** 判断给定食物 ItemStack 是否能喂这只蛊 */
    public boolean isFoodMatch(ItemStack food) {
        if (isHungerless()) return false; // 不需要喂食
        TagKey<Item> tag = getDietTag();
        if (tag == null) return false;
        return food.is(tag);
    }
    //endregion
}
