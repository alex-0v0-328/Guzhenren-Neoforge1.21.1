package net.alex.guzhenren.event;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.network.sync.*;
import net.alex.guzhenren.registry.ModAttachment;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Guzhenren.MOD_ID)
public class ModPlayerEvents {

    private static final int ESSENCE_SYNC_INTERVAL = 20;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        ModPlayerData data = sp.getData(ModAttachment.PLAYER_DATA.get());

        data.essence().naturalRecoveryPerTick();

        if (data.cultivation().isDirty()) {
            PacketDistributor.sendToPlayer(sp, new CoreSyncPayload(data.cultivation()));
            data.cultivation().clearDirty();
        }
        if (data.status().isDirty()) {
            PacketDistributor.sendToPlayer(sp, new StatusSyncPayload(data.status()));
            data.status().clearDirty();
        }
        if (data.path().isDirty()) {
            PacketDistributor.sendToPlayer(sp, new PathSyncPayload(data.path()));
            data.path().clearDirty();
        }

        if (sp.tickCount % ESSENCE_SYNC_INTERVAL == 0) {
            PacketDistributor.sendToPlayer(sp, new EssenceSyncPayload(data.essence()));
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        ModPlayerData data = sp.getData(ModAttachment.PLAYER_DATA.get());
        PacketDistributor.sendToPlayer(sp, new ModPlayerSyncPayload(data));
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        ModPlayerData data = sp.getData(ModAttachment.PLAYER_DATA.get());
        PacketDistributor.sendToPlayer(sp, new ModPlayerSyncPayload(data));
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer newPlayer)) return;
        if (!(event.getOriginal() instanceof ServerPlayer oldPlayer)) return;

        if (event.isWasDeath()) {
            boolean keep = newPlayer.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
            if (!keep) return;
        }

        ModPlayerData oldData = oldPlayer.getData(ModAttachment.PLAYER_DATA.get());
        ModPlayerData newData = newPlayer.getData(ModAttachment.PLAYER_DATA.get());
        newData.copyFrom(oldData);
    }
}
