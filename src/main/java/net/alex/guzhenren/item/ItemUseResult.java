package net.alex.guzhenren.item;

/**
 * item 使用结果
 * @param success            是否成功
 * @param overrideMessageKey 失败时覆盖 caller 的 failMessageKey (可 null, 走默认)
 * @param messageArgs        i18n %s 占位参数 (成功 message 用)
 */
public record ItemUseResult(boolean success, String overrideMessageKey, Object[] messageArgs) {

    /** 成功且无 message arg */
    public static ItemUseResult succeed() { return new ItemUseResult(true, null, new Object[0]); }

    /** 成功且带 message arg (用于 i18n %s 占位) */
    public static ItemUseResult succeed(Object... args) { return new ItemUseResult(true, null, args); }

    /** 失败, 由 caller 的 failMessageKey 显示 */
    public static ItemUseResult fail() { return new ItemUseResult(false, null, new Object[0]); }

    /** 失败, 用 overrideMessageKey 覆盖默认 failMessageKey */
    public static ItemUseResult fail(String overrideMessageKey) {
        return new ItemUseResult(false, overrideMessageKey, new Object[0]);
    }
}