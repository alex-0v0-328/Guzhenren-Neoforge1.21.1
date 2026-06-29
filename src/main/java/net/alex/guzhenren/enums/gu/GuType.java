package net.alex.guzhenren.enums.gu;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum GuType implements StringRepresentable {

    /** 一次性: 使用后消耗 */
    ONE_TIME,
    /** 多次使用: 通过耐久控制次数 */
    REUSABLE;

    public static final Codec<GuType> CODEC = StringRepresentable.fromEnum(GuType::values);

    @Override
    public @NotNull String getSerializedName() { return name().toLowerCase(); }

    public String getTranslationKey() { return "guzhenren.enum.gu.type." + name().toLowerCase(); }
}
