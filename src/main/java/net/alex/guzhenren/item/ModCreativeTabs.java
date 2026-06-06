package net.alex.guzhenren.item;

import net.alex.guzhenren.Guzhenren;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Guzhenren.MOD_ID);

    public static final Supplier<CreativeModeTab> GU_MATERIAL = TABS.register("gu_material",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + Guzhenren.MOD_ID + ".gu_material"))
                    .icon(() -> new ItemStack(ModItems.ESSENCE_STONE.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.ESSENCE_STONE.get());
                    })
                    .build());

    public static final Supplier<CreativeModeTab> MORTAL_GU = TABS.register("mortal_gu",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + Guzhenren.MOD_ID + ".mortal_gu"))
                    .icon(() -> new ItemStack(ModItems.HOPE_GU.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.HOPE_GU.get());
                    })
                    .build());

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
