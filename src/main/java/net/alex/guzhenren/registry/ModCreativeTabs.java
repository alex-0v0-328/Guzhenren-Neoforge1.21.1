package net.alex.guzhenren.registry;

import java.util.function.Supplier;
import net.alex.guzhenren.Guzhenren;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Guzhenren.MOD_ID);

    public static final Supplier<CreativeModeTab> GU_MATERIALS = CREATIVE_MODE_TABS.register("gu_materials",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("guzhenren.itemGroup.gu_materials"))
                    .icon(() -> new ItemStack(ModItems.ESSENCE_STONE.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.ESSENCE_STONE.get());
                    })
                    .build());

    public static final Supplier<CreativeModeTab> MORTAL_GU = CREATIVE_MODE_TABS.register("mortal_gu",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("guzhenren.itemGroup.mortal_gu"))
                    .icon(() -> new ItemStack(ModItems.HOPE_GU.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.HOPE_GU.get());
                        output.accept(ModItems.LIFESPAN_GU.get());
                        output.accept(ModItems.TEN_YEARS_LIFESPAN_GU.get());
                        output.accept(ModItems.COPPER_RELICS_GU.get());
                        output.accept(ModItems.STEEL_RELICS_GU.get());
                        output.accept(ModItems.SILVER_RELICS_GU.get());
                        output.accept(ModItems.GOLD_RELICS_GU.get());
                        output.accept(ModItems.CRYSTAL_RELICS_GU.get());
                    })
                    .build());

    public static void register(IEventBus modEventBus) { CREATIVE_MODE_TABS.register(modEventBus); }
}
