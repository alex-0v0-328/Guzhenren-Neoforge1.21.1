package net.alex.guzhenren.item;

import com.mojang.serialization.Codec;
import net.alex.guzhenren.Guzhenren;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {

    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Guzhenren.MOD_ID);

    public static final Supplier<DataComponentType<Long>> LAST_DECAY_TIME =
            COMPONENTS.registerComponentType("last_decay_time",
                    builder -> builder
                            .persistent(Codec.LONG)
                            .networkSynchronized(ByteBufCodecs.VAR_LONG));

    /** 主类构造函数中调用一次 */
    public static void register(IEventBus modEventBus) {
        COMPONENTS.register(modEventBus);
    }
}
