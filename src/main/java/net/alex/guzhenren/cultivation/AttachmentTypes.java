package net.alex.guzhenren.cultivation;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.cultivation.attachments.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AttachmentTypes {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Guzhenren.MOD_ID);

    /** 境界 attachment - 低频变动 */
    public static final Supplier<AttachmentType<BasicAttachments>> REALM =
            ATTACHMENT_TYPES.register("basic", () ->
                    AttachmentType.builder(BasicAttachments::new)
                            .serialize(BasicAttachments.CODEC)
                            .build());

    /** 真元 attachment - 高频变动 */
    public static final Supplier<AttachmentType<EssenceAttachment>> ESSENCE =
            ATTACHMENT_TYPES.register("essence", () ->
                    AttachmentType.builder(EssenceAttachment::new)
                            .serialize(EssenceAttachment.CODEC)
                            .build());

    /** 在主类构造函数里调用一次 */
    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
