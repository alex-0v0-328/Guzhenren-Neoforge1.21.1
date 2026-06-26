package net.alex.guzhenren.enums.core;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum Stage implements StringRepresentable {
    NONE  (0),
    INIT  (1),
    MIDDLE(2),
    UPPER (4),
    PEAK  (8);

    private final int essenceMultiplier;

    public static final Codec<Stage> CODEC =
            StringRepresentable.fromEnum(Stage::values);

    Stage(int essenceMultiplier) {
        this.essenceMultiplier = essenceMultiplier;
    }

    public int getEssenceMultiplier() { return essenceMultiplier; }

    @Override
    public @NotNull String getSerializedName() { return name().toLowerCase(); }

    public String getTranslationKey() { return "guzhenren.enum.cultivation.stage." + name().toLowerCase(); }
}
