package net.alex.guzhenren.item.gu;

import net.minecraft.server.level.ServerPlayer;

public interface GuEffect {

    /**
     * 蛊虫使用时的效果接口
     * @return GuEffectResult, success 字段表示是否成功, messageArgs 为提示参数 (传给 i18n %s)
     */
    GuEffectResult apply(ServerPlayer player);
}
