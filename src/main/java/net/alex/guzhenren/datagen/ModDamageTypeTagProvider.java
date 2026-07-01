package net.alex.guzhenren.datagen;

import java.util.concurrent.CompletableFuture;
import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.registry.ModDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
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
        addBoth(DamageTypeTags.BYPASSES_ARMOR);
        addBoth(DamageTypeTags.BYPASSES_EFFECTS);
        addBoth(DamageTypeTags.BYPASSES_ENCHANTMENTS);
        addBoth(DamageTypeTags.NO_KNOCKBACK);
    }

    /** 把 LIFESPAN_DEPLETED + SOUL_COLLAPSED 同时加入指定 tag */
    private void addBoth(TagKey<DamageType> tag) {
        tag(tag)
                .add(ModDamageTypes.LIFESPAN_DEPLETED)
                .add(ModDamageTypes.SOUL_COLLAPSED);
    }

    @Override
    public @NotNull String getName() { return "Damage Type Tags: " + Guzhenren.MOD_ID; }
}