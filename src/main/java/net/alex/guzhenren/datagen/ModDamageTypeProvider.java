package net.alex.guzhenren.datagen;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.registry.ModDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import org.jetbrains.annotations.NotNull;

/** 注册自定义 DamageType JSON (lifespan_depleted / soul_collapsed) */
public class ModDamageTypeProvider extends DatapackBuiltinEntriesProvider {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, ctx -> {
                ctx.register(ModDamageTypes.LIFESPAN_DEPLETED, new DamageType("lifespan_depleted", 0.0F));
                ctx.register(ModDamageTypes.SOUL_COLLAPSED, new DamageType("soul_collapsed", 0.0F));
            });

    public ModDamageTypeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(Guzhenren.MOD_ID));
    }

    @Override
    public @NotNull String getName() { return "Damage Types: " + Guzhenren.MOD_ID; }
}
