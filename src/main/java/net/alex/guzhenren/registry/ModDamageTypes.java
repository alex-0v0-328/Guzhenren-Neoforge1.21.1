package net.alex.guzhenren.registry;

import net.alex.guzhenren.Guzhenren;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

public class ModDamageTypes {

    public static final ResourceKey<DamageType> LIFESPAN_DEPLETED = ResourceKey.create(Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Guzhenren.MOD_ID, "lifespan_depleted"));

    public static final ResourceKey<DamageType> SOUL_COLLAPSED = ResourceKey.create(Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Guzhenren.MOD_ID, "soul_collapsed"));

    public static DamageSource lifespanDepleted(ServerLevel level) {
        return level.damageSources().source(LIFESPAN_DEPLETED);
    }

    public static DamageSource soulCollapsed(ServerLevel level) { return level.damageSources().source(SOUL_COLLAPSED); }
}
