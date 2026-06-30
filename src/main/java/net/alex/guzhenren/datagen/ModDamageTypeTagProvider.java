package net.alex.guzhenren.datagen;

import java.util.concurrent.CompletableFuture;
import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.registry.ModDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

/** 注册自定义 DamageType 的 tag (bypass armor / effects / enchantments / no knockback) */
public class ModDamageTypeTagProvider extends DamageTypeTagsProvider {

    public ModDamageTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries,
                                    ExistingFileHelper existingFileHelper) {
        super(output, registries, Guzhenren.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(DamageTypeTags.BYPASSES_ARMOR)
                .add(ModDamageTypes.LIFESPAN_DEPLETED)
                .add(ModDamageTypes.SOUL_COLLAPSED);
        tag(DamageTypeTags.BYPASSES_EFFECTS)
                .add(ModDamageTypes.LIFESPAN_DEPLETED)
                .add(ModDamageTypes.SOUL_COLLAPSED);
        tag(DamageTypeTags.BYPASSES_ENCHANTMENTS)
                .add(ModDamageTypes.LIFESPAN_DEPLETED)
                .add(ModDamageTypes.SOUL_COLLAPSED);
        tag(DamageTypeTags.NO_KNOCKBACK)
                .add(ModDamageTypes.LIFESPAN_DEPLETED)
                .add(ModDamageTypes.SOUL_COLLAPSED);
    }

    @Override
    public @NotNull String getName() { return "Damage Type Tags: " + Guzhenren.MOD_ID; }
}
