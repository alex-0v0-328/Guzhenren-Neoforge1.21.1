package net.alex.guzhenren.event;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.item.gu.GuItem;
import net.alex.guzhenren.network.payload.GuLeftClickPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Guzhenren.MOD_ID, value = Dist.CLIENT)
public final class GuClientEventHandler {

    private GuClientEventHandler() {}

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof GuItem) {
            // 客户端发包到服务端，由 ServerPayloadHandler 处理
            PacketDistributor.sendToServer(new GuLeftClickPayload());
        }
    }
}
