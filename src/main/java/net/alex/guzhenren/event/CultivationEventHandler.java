package net.alex.guzhenren.event;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.cultivation.AttachmentTypes;
import net.alex.guzhenren.cultivation.CultivationManager;
import net.alex.guzhenren.cultivation.EssenceManager;
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

    //region Tick - 每 tick 回血
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        EssenceManager.tickRegen(player);
    }
    //endregion

    //region Wake Up - 起床立刻满
    @SubscribeEvent
    public static void onWakeUp(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        EssenceManager.restoreFull(player);
    }
    //endregion

    //region Clone - 死亡复活 / 跨维度迁移
    /**
     * Player 实例克隆事件
     *   触发场景:
     *     - 死亡复活: event.isWasDeath() == true
     *     - 跨维度 / End 返回: event.isWasDeath() == false
     *   处理策略:
     *     1. 默认: 全字段从 old 复制到 new (兜底保留)
     *     2. 若死亡 + !keepInventory: 调 CultivationManager.onDeath 清零
     *   sync 由 PlayerSyncEventHandler.onRespawn / onChangeDimension 在事件链尾推送
     */
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer newPlayer)) return;
        Player oldPlayer = event.getOriginal();

        // Step 1: 默认全字段迁移
        copyAttachments(oldPlayer, newPlayer);

        // Step 2: 死亡场景按 keepInventory 决定是否清零
        if (event.isWasDeath()) {
            boolean keepInventory = newPlayer.level()
                    .getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
            CultivationManager.onDeath(newPlayer, keepInventory);
        }
    }

    /** 将 BasicAttachments + EssenceAttachment 从 from 复制到 to */
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
    //endregion
}
