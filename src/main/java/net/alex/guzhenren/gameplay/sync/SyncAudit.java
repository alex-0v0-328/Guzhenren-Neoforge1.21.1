package net.alex.guzhenren.gameplay.sync;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

/**
 * Sync 顺序 audit helper.
 * 用于验证 "syncImmediate → checkDeath → return" 顺序在死亡场景下正确.
 * 使用方式:
 * 1. 临时改 ENABLED = true
 * 2. 触发死亡 (如 /guzhenren age set 99 逼近 maxLifespan)
 * 3. 查看 log 应显示顺序: syncImmediate → hurt
 * 4. 验证通过后改回 ENABLED = false (生产环境 disable)
 * 未来 Phase 3 会用 GameTest 框架替代 (记账中).
 */
public final class SyncAudit {

    /** 生产环境 disable, 手动调试时改 true */
    private static final boolean ENABLED = false;

    private static final Logger LOGGER = LogUtils.getLogger();

    private SyncAudit() {}

    /** 记录一次 sync 事件. label 示例: "syncImmediate.essence" / "syncCore" / "hurt.lifespan" */
    public static void log(String label, ServerPlayer sp) {
        if (!ENABLED) return;
        LOGGER.info("[SyncAudit] {} player={} tick={}",
                label, sp.getName().getString(), sp.tickCount);
    }
}