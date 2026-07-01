package net.alex.guzhenren.item.gu;

import net.alex.guzhenren.item.ItemUseResult;
import net.minecraft.server.level.ServerPlayer;

/** 蛊虫使用效果. functional interface, 实现 apply 方法返回效果结果 */
@FunctionalInterface
public interface GuEffect {

    /**
     * 蛊虫使用时的效果接口
     * @return ItemUseResult, success 字段表示是否成功, messageArgs 为提示参数 (传给 i18n %s)
     */
    ItemUseResult apply(ServerPlayer player);
}