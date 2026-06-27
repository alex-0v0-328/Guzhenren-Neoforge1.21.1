package net.alex.guzhenren;

import com.mojang.logging.LogUtils;
import net.alex.guzhenren.registry.ModAttachments;
import net.alex.guzhenren.registry.ModCreativeTabs;
import net.alex.guzhenren.registry.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(Guzhenren.MOD_ID)
public class Guzhenren {
    public static final String MOD_ID = "guzhenren";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Guzhenren(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::commonSetup);

        ModAttachments.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // 通用启动逻辑 (client + server 共用)
    }
}
