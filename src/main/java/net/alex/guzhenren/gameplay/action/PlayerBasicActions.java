package net.alex.guzhenren.gameplay.action;

import net.alex.guzhenren.enums.cultivation.*;
import net.alex.guzhenren.enums.path.*;
import net.alex.guzhenren.gameplay.data.PlayerData;
import net.alex.guzhenren.gameplay.data.CultivationData;
import net.alex.guzhenren.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerBasicActions {

    private static final List<Rank> RANK_ORDER  = List.of(Rank.ONE, Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE);
    private static final List<Stage> STAGE_ORDER = List.of(Stage.INIT, Stage.MIDDLE, Stage.UPPER, Stage.PEAK);

    //region PRECONDITION
    public static boolean canAwaken(PlayerData data) {
        return !data.status().isApertureAwakened()
                && data.cultivation().getPlayerTalent() == Talent.NONE
                && data.cultivation().getPlayerRank() == Rank.MORTAL
                && data.cultivation().getPlayerStage() == Stage.NONE
                && data.cultivation().getPlayerExtremePhysique() == TenExtreme.NONE;
    }

    public static boolean isAwakened(PlayerData data) {
        return data.status().isApertureAwakened();
    }
    //endregion

    //region AWAKEN
    public static void awaken(ServerPlayer player) {
        Talent t = randomNonNoneTalent();
        doAwaken(player, t, Talent.randomMaxEssence(t));
    }

    public static void awaken(ServerPlayer player, Talent talent) {
        doAwaken(player, talent, Talent.randomMaxEssence(talent));
    }

    public static void awaken(ServerPlayer player, Talent talent, int percent) {
        doAwaken(player, talent, percent);
    }

    private static void doAwaken(ServerPlayer player, Talent talent, int percent) {
        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        CultivationData c = data.cultivation();
        c.setPlayerTalent(talent);
        c.setPlayerBaseEssence(percent);
        c.setPlayerRank(Rank.ONE);
        c.setPlayerStage(Stage.INIT);
        c.setPlayerExtremePhysique(talent == Talent.TEN_EXTREME ? randomTenExtreme() : TenExtreme.NONE);
        data.status().setApertureAwakened(true);
        recomputeMax(data);
        data.essence().refillCurrentEssence();
    }
    //endregion

    //region RANK / STAGE
    public static boolean rankUp(ServerPlayer player) {
        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        int idx = RANK_ORDER.indexOf(data.cultivation().getPlayerRank());
        if (idx == -1 || idx >= RANK_ORDER.size() - 1) return false;
        setRank(player, RANK_ORDER.get(idx + 1));
        return true;
    }

    public static boolean rankDown(ServerPlayer player) {
        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        int idx = RANK_ORDER.indexOf(data.cultivation().getPlayerRank());
        if (idx <= 0) return false;
        setRank(player, RANK_ORDER.get(idx - 1));
        return true;
    }

    public static void setRank(ServerPlayer player, Rank rank) {
        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        data.cultivation().setPlayerRank(rank);
        recomputeMax(data);
    }

    public static boolean stageUp(ServerPlayer player) {
        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        int idx = STAGE_ORDER.indexOf(data.cultivation().getPlayerStage());
        if (idx == -1 || idx >= STAGE_ORDER.size() - 1) return false;
        setStage(player, STAGE_ORDER.get(idx + 1));
        return true;
    }

    public static boolean stageDown(ServerPlayer player) {
        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        int idx = STAGE_ORDER.indexOf(data.cultivation().getPlayerStage());
        if (idx <= 0) return false;
        setStage(player, STAGE_ORDER.get(idx - 1));
        return true;
    }

    public static void setStage(ServerPlayer player, Stage stage) {
        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        data.cultivation().setPlayerStage(stage);
        recomputeMax(data);
    }
    //endregion

    //region TALENT / BASE / PHYSIQUE
    public static void setTalent(ServerPlayer player, Talent talent) {
        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        CultivationData c = data.cultivation();
        c.setPlayerTalent(talent);
        c.setPlayerBaseEssence(Talent.randomMaxEssence(talent));
        if (talent == Talent.TEN_EXTREME) c.setPlayerExtremePhysique(randomTenExtreme());
        recomputeMax(data);
    }

    /** 加 baseEssence; 若结算后 talent 跨入 TEN_EXTREME 区间且 physique 仍 NONE, 随机一个 physique */
    public static void addBaseEssence(ServerPlayer player, int amount) {
        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        data.cultivation().addPlayerBaseEssence(amount);
        ensurePhysiqueIfTenExtreme(data);
        recomputeMax(data);
    }

    public static void subBaseEssence(ServerPlayer player, int amount) {
        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        data.cultivation().subPlayerBaseEssence(amount);
        ensurePhysiqueIfTenExtreme(data);
        recomputeMax(data);
    }

    public static void setPhysique(ServerPlayer player, TenExtreme physique) {
        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        CultivationData c = data.cultivation();
        c.setPlayerTalent(Talent.TEN_EXTREME);
        c.setPlayerBaseEssence(100);
        c.setPlayerExtremePhysique(physique);
        recomputeMax(data);
    }
    //endregion

    //region RESET
    public static void reset(ServerPlayer player) {
        PlayerData data = player.getData(ModAttachments.PLAYER_DATA.get());
        CultivationData c = data.cultivation();
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
    }
    //endregion

    //region HELPERS
    static void recomputeMax(PlayerData data) {
        data.essence().recomputeMaxEssence(
                data.cultivation().getPlayerBaseEssence(),
                data.cultivation().getPlayerRank(),
                data.cultivation().getPlayerStage()
        );
    }

    /** 跨入 TEN_EXTREME 时若 physique == NONE 自动 roll 一个 */
    private static void ensurePhysiqueIfTenExtreme(PlayerData data) {
        CultivationData c = data.cultivation();
        if (c.getPlayerTalent() == Talent.TEN_EXTREME
                && c.getPlayerExtremePhysique() == TenExtreme.NONE) {
            c.setPlayerExtremePhysique(randomTenExtreme());
        }
    }

    private static Talent randomNonNoneTalent() {
        Talent[] vals = Talent.values();
        Talent t;
        do { t = vals[ThreadLocalRandom.current().nextInt(vals.length)]; } while (t == Talent.NONE);
        return t;
    }

    private static TenExtreme randomTenExtreme() {
        TenExtreme[] vals = TenExtreme.values();
        TenExtreme t;
        do { t = vals[ThreadLocalRandom.current().nextInt(vals.length)]; } while (t == TenExtreme.NONE);
        return t;
    }
    //endregion
}
