package net.alex.guzhenren.registry;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachment {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Guzhenren.MOD_ID);

    public static final Supplier<AttachmentType<ModPlayerData>> PLAYER_DATA =
            ATTACHMENT_TYPES.register("player_data", () ->
                    AttachmentType.builder(ModPlayerData::new)
                            .serialize(ModPlayerData.CODEC)
                            .build()
            );

    public static void register(IEventBus modEventBus) { ATTACHMENT_TYPES.register(modEventBus); }
}
