package net.alex.guzhenren;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/** Mod 客户端入口. 仅在 client side 实例化, 用于注册 client-only API (如 config screen) */
@Mod(value = Guzhenren.MOD_ID, dist = Dist.CLIENT)
public class GuzhenrenClient {

    public GuzhenrenClient(ModContainer container) {
        // 后期需要 mod config 时在此 register IConfigScreenFactory
    }
}
