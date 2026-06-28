package net.alex.guzhenren.item.gu;

import net.minecraft.server.level.ServerPlayer;

public interface GuEffect {

    /**
     * 蛊虫使用时的效果接口
     * @return true 表示效果成功施加, false 表示失败 (玩家条件不满足)
     */
    boolean apply(ServerPlayer player);
}
