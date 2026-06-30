package net.alex.guzhenren.gameplay.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.alex.guzhenren.enums.core.Rank;
import net.alex.guzhenren.enums.core.Stage;
import net.alex.guzhenren.enums.core.Talent;
import net.alex.guzhenren.enums.core.TenExtreme;

public class CoreComponent {

    public static final Codec<CoreComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
            Rank.CODEC.fieldOf("rank").forGetter(CoreComponent::getPlayerRank),
            Stage.CODEC.fieldOf("stage").forGetter(CoreComponent::getPlayerStage),
            Talent.CODEC.fieldOf("talent").forGetter(CoreComponent::getPlayerTalent),
            TenExtreme.CODEC.fieldOf("extreme_physique").forGetter(CoreComponent::getPlayerExtremePhysique),
            Codec.INT.fieldOf("base_essence").forGetter(CoreComponent::getPlayerBaseEssence)
    ).apply(i, CoreComponent::new));

    private Rank playerRank;
    private Stage playerStage;
    private Talent playerTalent;
    private TenExtreme playerExtremePhysique;
    private int playerBaseEssence;
    private transient boolean dirty = false;

    public CoreComponent() {
        this.playerRank = Rank.MORTAL;
        this.playerStage = Stage.NONE;
        this.playerTalent = Talent.NONE;
        this.playerExtremePhysique = TenExtreme.NONE;
        this.playerBaseEssence = 0;
    }

    /** Codec 反序列化专用. 外部代码请用无参构造 + setter */
    public CoreComponent(Rank rank, Stage stage, Talent talent, TenExtreme physique, int baseEssence) {
        this.playerRank = rank;
        this.playerStage = stage;
        this.playerTalent = talent;
        this.playerExtremePhysique = physique;
        this.playerBaseEssence = baseEssence;
    }

//region GETTER
    public Rank getPlayerRank() { return playerRank; }
    public Stage getPlayerStage() { return playerStage; }
    public Talent getPlayerTalent() { return playerTalent; }
    public TenExtreme getPlayerExtremePhysique() { return playerExtremePhysique; }
    public int getPlayerBaseEssence() { return playerBaseEssence; }
//endregion

//region SETTER
    public void setPlayerRank(Rank rank) {
        this.playerRank = rank;
        this.dirty = true;
    }

    public void setPlayerStage(Stage stage) {
        this.playerStage = stage;
        this.dirty = true;
    }

    public void setPlayerTalent(Talent talent) {
        this.playerTalent = talent;
        if (talent != Talent.TEN_EXTREME) {
            this.playerExtremePhysique = TenExtreme.NONE;
        }
        this.dirty = true;
    }

    public void setPlayerExtremePhysique(TenExtreme physique) {
        this.playerExtremePhysique = physique;
        this.dirty = true;
    }

    public void setPlayerBaseEssence(int value) {
        int clamped = Math.clamp(value, 0, 100);
        this.playerBaseEssence = clamped;
        Talent inferred = Talent.fromPercent(clamped);
        if (inferred != this.playerTalent) {
            this.playerTalent = inferred;
            if (this.playerTalent != Talent.TEN_EXTREME) {
                this.playerExtremePhysique = TenExtreme.NONE;
            }
        }
        this.dirty = true;
    }
//endregion

//region BASE ESSENCE
    public void addPlayerBaseEssence(int amount) {
        if (amount <= 0) return;
        setPlayerBaseEssence(this.playerBaseEssence + amount);
    }

    public void subPlayerBaseEssence(int amount) {
        if (amount <= 0) return;
        setPlayerBaseEssence(this.playerBaseEssence - amount);
    }
//endregion

//region RESET
    /** 重置到初始状态 (未开窍凡人) */
    public void reset() {
        this.playerRank = Rank.MORTAL;
        this.playerStage = Stage.NONE;
        this.playerTalent = Talent.NONE;
        this.playerExtremePhysique = TenExtreme.NONE;
        this.playerBaseEssence = 0;
        this.dirty = true;
    }
//endregion

//region DIRTY
    public boolean isDirty() { return dirty; }
    public void clearDirty() { this.dirty = false; }
//endregion
}
