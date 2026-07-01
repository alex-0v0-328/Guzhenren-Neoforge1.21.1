package net.alex.guzhenren.util;

import java.util.concurrent.ThreadLocalRandom;

/** Enum 相关通用工具方法 */
public final class EnumUtils {

    private EnumUtils() {}

    /**
     * 从 values 中随机选一个非 excluded 的值
     * @param values   候选 enum 值数组 (通常传 EnumClass.values())
     * @param excluded 需排除的值
     * @return 随机选中的非 excluded enum 值
     * @throws IllegalArgumentException 若 values 仅含 excluded
     */
    public static <E extends Enum<E>> E randomExcept(E[] values, E excluded) {
        if (values.length == 0) {
            throw new IllegalArgumentException("values is empty");
        }
        if (values.length == 1 && values[0] == excluded) {
            throw new IllegalArgumentException("values contains only the excluded value");
        }
        E picked;
        do {
            picked = values[ThreadLocalRandom.current().nextInt(values.length)];
        } while (picked == excluded);
        return picked;
    }
}