package net.alex.guzhenren.item.gu;

public record GuEffectResult(boolean success, String overrideMessageKey, Object[] messageArgs) {

    public static GuEffectResult succeed() { return new GuEffectResult(true, null, new Object[0]); }
    public static GuEffectResult succeed(Object... args) { return new GuEffectResult(true, null, args); }
    public static GuEffectResult fail() { return new GuEffectResult(false, null, new Object[0]); }
    public static GuEffectResult fail(String overrideMessageKey) {
        return new GuEffectResult(false, overrideMessageKey, new Object[0]);
    }
}
