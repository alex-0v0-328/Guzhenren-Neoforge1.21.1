package net.alex.guzhenren.enums.gu;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum GuType implements StringRepresentable {

    ONE_TIME,
    REUSABLE;

    public static final Codec<GuType> CODEC = StringRepresentable.fromEnum(GuType::values);

    @Override
    public @NotNull String getSerializedName() { return name().toLowerCase(); }

    public String getTranslationKey() { return "guzhenren.enum.gu.type." + name().toLowerCase(); }
}
