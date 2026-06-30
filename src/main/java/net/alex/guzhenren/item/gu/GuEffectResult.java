package net.alex.guzhenren.item.gu;

public record GuEffectResult(boolean success, String overrideMessageKey, Object[] messageArgs) {

    /** 成功且无 message arg */
    public static GuEffectResult succeed() { return new GuEffectResult(true, null, new Object[0]); }

    /** 成功且带 message arg (用于 i18n %s 占位) */
    public static GuEffectResult succeed(Object... args) { return new GuEffectResult(true, null, args); }

    /** 失败, 由 caller 的 failMessageKey 显示 */
    public static GuEffectResult fail() { return new GuEffectResult(false, null, new Object[0]); }

    /** 失败, 用 overrideMessageKey 覆盖默认 failMessageKey */
    public static GuEffectResult fail(String overrideMessageKey) {
        return new GuEffectResult(false, overrideMessageKey, new Object[0]);
    }
}
