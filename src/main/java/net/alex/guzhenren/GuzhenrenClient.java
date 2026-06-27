package net.alex.guzhenren;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = Guzhenren.MOD_ID, dist = Dist.CLIENT)
public class GuzhenrenClient {

    public GuzhenrenClient(ModContainer container) {
        // 后期需要 mod config 时在此 register IConfigScreenFactory
    }
}
