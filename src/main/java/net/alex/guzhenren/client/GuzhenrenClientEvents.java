package net.alex.guzhenren.client;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.client.hud.PlayerEssenceHud;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = Guzhenren.MOD_ID, value = Dist.CLIENT)
public class GuzhenrenClientEvents {

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(
                VanillaGuiLayers.HOTBAR,
                ResourceLocation.fromNamespaceAndPath(Guzhenren.MOD_ID, "essence_hud"),
                new PlayerEssenceHud()
        );
    }
}
