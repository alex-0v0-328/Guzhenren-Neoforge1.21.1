package net.alex.guzhenren.item.gu;

import net.alex.guzhenren.cultivation.CultivationManager;
import net.alex.guzhenren.cultivation.RealmManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HopeGu extends GuItem {

    public HopeGu(Properties properties) {
        super(properties);
    }

    @Override protected boolean isOneUse()  { return true; }
    @Override public    boolean isHungerless() { return true; }

    @Override
    protected boolean canUse(Level level, Player player, ItemStack stack) {
        return !RealmManager.realm(player).isAwaken();
    }

    @Override
    protected boolean useEffect(Level level, Player player, ItemStack stack) {
        return CultivationManager.awaken(player, player.getRandom());
    }
}
