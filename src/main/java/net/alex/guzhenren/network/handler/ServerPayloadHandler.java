package net.alex.guzhenren.network.handler;

import net.alex.guzhenren.item.gu.GuItem;
import net.alex.guzhenren.network.payload.GuLeftClickPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ServerPayloadHandler {

    private ServerPayloadHandler() {}

    public static void handleGuLeftClick(GuLeftClickPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof GuItem gu) {
                gu.onLeftClick(player.level(), player, stack);
            }
        });
    }
}
