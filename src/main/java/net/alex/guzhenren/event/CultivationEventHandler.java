package net.alex.guzhenren.event;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.cultivation.AttachmentTypes;
import net.alex.guzhenren.cultivation.CultivationManager;
import net.alex.guzhenren.cultivation.attachments.BasicAttachments;
import net.alex.guzhenren.cultivation.attachments.EssenceAttachment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Guzhenren.MOD_ID)
public final class CultivationEventHandler {
    private CultivationEventHandler() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        CultivationManager.tickRegen(player);
    }

    @SubscribeEvent
    public static void onWakeUp(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        CultivationManager.restoreFull(player);
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer newPlayer)) return;
        Player oldPlayer = event.getOriginal();

        copyAttachments(oldPlayer, newPlayer);

        if (event.isWasDeath()) {
            boolean keepInventory = newPlayer.level()
                    .getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
            CultivationManager.onDeath(newPlayer, keepInventory);
        }
    }

    private static void copyAttachments(Player from, Player to) {
        BasicAttachments fromB = from.getData(AttachmentTypes.REALM);
        BasicAttachments toB   = to.getData(AttachmentTypes.REALM);
        toB.setAwaken(fromB.isAwaken());
        toB.setRank(fromB.getRank());
        toB.setStage(fromB.getStage());
        toB.setAptitude(fromB.getAptitude());
        toB.setAptitudeRoll(fromB.getAptitudeRoll());

        EssenceAttachment fromE = from.getData(AttachmentTypes.ESSENCE);
        EssenceAttachment toE   = to.getData(AttachmentTypes.ESSENCE);
        toE.setMaxEssence(fromE.getMaxEssence());
        toE.setCurrentEssence(fromE.getCurrentEssence());
    }
}
