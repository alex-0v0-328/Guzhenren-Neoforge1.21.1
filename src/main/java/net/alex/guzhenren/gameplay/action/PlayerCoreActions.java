package net.alex.guzhenren.gameplay.action;

import java.util.List;
import net.alex.guzhenren.enums.core.Rank;
import net.alex.guzhenren.enums.core.Stage;
import net.alex.guzhenren.enums.core.Talent;
import net.alex.guzhenren.enums.core.TenExtreme;
import net.alex.guzhenren.enums.path.Attainment;
import net.alex.guzhenren.enums.path.Path;
import net.alex.guzhenren.gameplay.data.CoreComponent;
import net.alex.guzhenren.gameplay.data.ModPlayerData;
import net.alex.guzhenren.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;

public class PlayerCoreActions {

    private static final List<Rank> RANK_ORDER = List.of(Rank.ONE, Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE);
    private static final List<Stage> STAGE_ORDER = List.of(Stage.INIT, Stage.MIDDLE, Stage.UPPER, Stage.PEAK);

    //region PRECONDITION
    public static boolean canAwaken(ModPlayerData data) {
        return !data.status().isApertureAwakened()
                && data.core().getPlayerTalent() == Talent.NONE
                && data.core().getPlayerRank() == Rank.MORTAL
                && data.core().getPlayerStage() == Stage.NONE
                && data.core().getPlayerExtremePhysique() == TenExtreme.NONE;
    }

    public static boolean isAwakened(ModPlayerData data) {
        return data.status().isApertureAwakened();
    }
//endregion

    //region AWAKEN
    public static void awaken(ServerPlayer player) {
        Talent t = Talent.randomNonNone();
        doAwaken(player, t, Talent.randomMaxEssence(t));
    }

    public static void awaken(ServerPlayer player, Talent talent) {
        doAwaken(player, talent, Talent.randomMaxEssence(talent));
    }

    public static void awaken(ServerPlayer player, Talent talent, int percent) {
        doAwaken(player, talent, percent);
    }

    private static void doAwaken(ServerPlayer player, Talent talent, int percent) {
        ModPlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        CoreComponent c = data.core();
        c.setPlayerTalent(talent);
        c.setPlayerBaseEssence(percent);
        c.setPlayerRank(Rank.ONE);
        c.setPlayerStage(Stage.INIT);
        c.setPlayerExtremePhysique(talent == Talent.TEN_EXTREME ? TenExtreme.randomNonNone() : TenExtreme.NONE);
        data.status().setApertureAwakened(true);
        recomputeMax(data);
        data.essence().refillCurrentEssence();
    }
//endregion

    //region RANK / STAGE
    public static boolean rankUp(ServerPlayer player) {
        ModPlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        int idx = RANK_ORDER.indexOf(data.core().getPlayerRank());
        if (idx == -1 || idx >= RANK_ORDER.size() - 1) return false;
        setRank(player, RANK_ORDER.get(idx + 1));
        return true;
    }

    public static boolean rankDown(ServerPlayer player) {
        ModPlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        int idx = RANK_ORDER.indexOf(data.core().getPlayerRank());
        if (idx <= 0) return false;
        setRank(player, RANK_ORDER.get(idx - 1));
        return true;
    }

    public static void setRank(ServerPlayer player, Rank rank) {
        ModPlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        data.core().setPlayerRank(rank);
        recomputeMax(data);
    }

    public static boolean stageUp(ServerPlayer player) {
        ModPlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        int idx = STAGE_ORDER.indexOf(data.core().getPlayerStage());
        if (idx == -1 || idx >= STAGE_ORDER.size() - 1) return false;
        setStage(player, STAGE_ORDER.get(idx + 1));
        return true;
    }

    public static boolean stageDown(ServerPlayer player) {
        ModPlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        int idx = STAGE_ORDER.indexOf(data.core().getPlayerStage());
        if (idx <= 0) return false;
        setStage(player, STAGE_ORDER.get(idx - 1));
        return true;
    }

    public static void setStage(ServerPlayer player, Stage stage) {
        ModPlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        data.core().setPlayerStage(stage);
        recomputeMax(data);
    }
//endregion

    //region TALENT / BASE / PHYSIQUE
    public static void setTalent(ServerPlayer player, Talent talent) {
        ModPlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        CoreComponent c = data.core();
        c.setPlayerTalent(talent);
        c.setPlayerBaseEssence(Talent.randomMaxEssence(talent));
        if (talent == Talent.TEN_EXTREME) c.setPlayerExtremePhysique(TenExtreme.randomNonNone());
        recomputeMax(data);
    }

    public static void addBaseEssence(ServerPlayer player, int amount) {
        ModPlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        data.core().addPlayerBaseEssence(amount);
        ensurePhysiqueIfTenExtreme(data);
        recomputeMax(data);
    }

    public static void subBaseEssence(ServerPlayer player, int amount) {
        ModPlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        data.core().subPlayerBaseEssence(amount);
        ensurePhysiqueIfTenExtreme(data);
        recomputeMax(data);
    }

    public static void setPhysique(ServerPlayer player, TenExtreme physique) {
        ModPlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        CoreComponent c = data.core();
        c.setPlayerTalent(Talent.TEN_EXTREME);
        c.setPlayerBaseEssence(100);
        c.setPlayerExtremePhysique(physique);
        recomputeMax(data);
    }
//endregion

    //region RESET
    public static void reset(ServerPlayer player) {
        ModPlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        CoreComponent c = data.core();
        c.setPlayerRank(Rank.MORTAL);
        c.setPlayerStage(Stage.NONE);
        c.setPlayerTalent(Talent.NONE);
        c.setPlayerExtremePhysique(TenExtreme.NONE);
        c.setPlayerBaseEssence(0);
        data.essence().recomputeMaxEssence(0, Rank.MORTAL, Stage.NONE);
        data.status().setApertureAwakened(false);
        for (Path p : Path.values()) {
            data.path().setAttainment(p, Attainment.ORDINARY);
            data.path().setMarks(p, 0L);
        }
        data.path().markFullSync();
    }
//endregion

    //region HELPERS
    private static void recomputeMax(ModPlayerData data) {
        data.essence().recomputeMaxEssence(
                data.core().getPlayerBaseEssence(),
                data.core().getPlayerRank(),
                data.core().getPlayerStage()
        );
    }

    private static void ensurePhysiqueIfTenExtreme(ModPlayerData data) {
        CoreComponent c = data.core();
        if (c.getPlayerTalent() == Talent.TEN_EXTREME
                && c.getPlayerExtremePhysique() == TenExtreme.NONE) {
            c.setPlayerExtremePhysique(TenExtreme.randomNonNone());
        }
    }
//endregion
}
