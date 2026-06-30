package net.alex.guzhenren.datagen;

import java.util.concurrent.CompletableFuture;
import net.alex.guzhenren.Guzhenren;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/** DataGen 入口. 注册 lang / item model / damage type / damage type tag provider */
@EventBusSubscriber(modid = Guzhenren.MOD_ID)
public final class DataGenerators {

    private DataGenerators() {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();

        ModDamageTypeProvider damageTypeProvider = new ModDamageTypeProvider(packOutput, registries);

        generator.addProvider(event.includeClient(), new EnUsLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(), new ZhCnLanguageProvider(packOutput));
        generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeServer(), damageTypeProvider);
        generator.addProvider(event.includeServer(),
                new ModDamageTypeTagProvider(packOutput, damageTypeProvider.getRegistryProvider(), existingFileHelper));
    }
}
