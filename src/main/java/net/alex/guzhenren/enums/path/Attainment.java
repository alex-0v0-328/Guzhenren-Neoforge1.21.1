package net.alex.guzhenren.enums.path;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum Attainment implements StringRepresentable {

    ORDINARY                  (1, false),
    QUASI_MASTER              (2, true),
    MASTER                    (3, false),
    QUASI_GRANDMASTER         (4, true),
    GRANDMASTER               (5, false),
    QUASI_GREAT_GRANDMASTER   (6, true),
    GREAT_GRANDMASTER         (7, false),
    QUASI_SUPREME_GRANDMASTER (8, true),
    SUPREME_GRANDMASTER       (9, false);

    public static final Codec<Attainment> CODEC = StringRepresentable.fromEnum(Attainment::values);

    private final int level;
    private final boolean quasi;

    Attainment(int level, boolean quasi) {
        this.level = level;
        this.quasi = quasi;
    }

    public int getLevel() { return level; }
    public boolean isQuasi() { return quasi; }

    @Override
    public @NotNull String getSerializedName() { return name().toLowerCase(); }

    public String getTranslationKey() { return "guzhenren.enum.path.attainment." + name().toLowerCase(); }
}
