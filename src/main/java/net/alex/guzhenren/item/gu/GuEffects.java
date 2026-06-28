package net.alex.guzhenren.item.gu;

import net.alex.guzhenren.gameplay.action.PlayerCoreActions;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.registry.ModAttachments;

public class GuEffects {

    public static final GuEffect AWAKEN = player -> {
        ModPlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        if (!PlayerCoreActions.canAwaken(data)) return false;
        PlayerCoreActions.awaken(player);
        return true;
    };
}
