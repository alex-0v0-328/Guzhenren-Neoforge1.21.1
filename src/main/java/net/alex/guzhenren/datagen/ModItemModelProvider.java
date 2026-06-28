package net.alex.guzhenren.datagen;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Guzhenren.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.ESSENCE_STONE.get());
        basicItem(ModItems.HOPE_GU.get());
        basicItem(ModItems.LIFESPAN_GU.get());
        basicItem(ModItems.TEN_YEARS_LIFESPAN_GU.get());
        basicItem(ModItems.COPPER_RELICS_GU.get());
        basicItem(ModItems.STEEL_RELICS_GU.get());
        basicItem(ModItems.SILVER_RELICS_GU.get());
        basicItem(ModItems.GOLD_RELICS_GU.get());
        basicItem(ModItems.CRYSTAL_RELICS_GU.get());
    }
}
