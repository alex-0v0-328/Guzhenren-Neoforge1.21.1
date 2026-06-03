package net.alex.guzhenren.cultivation.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.alex.guzhenren.cultivation.enums.Aptitude;
import net.alex.guzhenren.cultivation.enums.Rank;
import net.alex.guzhenren.cultivation.enums.Stage;

public class BasicAttachments {

    public static final Codec<BasicAttachments> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("isAwaken").forGetter(BasicAttachments::isAwaken),
            Codec.STRING.xmap(Rank::valueOf, Rank::name).fieldOf("rank").forGetter(BasicAttachments::getRank),
            Codec.STRING.xmap(Stage::valueOf, Stage::name).fieldOf("stage").forGetter(BasicAttachments::getStage),
            Codec.STRING.xmap(Aptitude::valueOf, Aptitude::name).fieldOf("aptitude").forGetter(BasicAttachments::getAptitude),
            Codec.INT.fieldOf("aptitudeRoll").forGetter(BasicAttachments::getAptitudeRoll)
    ).apply(instance, BasicAttachments::new));

    private boolean isAwaken;     // 是否已开窍
    private Rank rank;            // 转 (大境界)
    private Stage stage;          // 小境界
    private Aptitude aptitude;    // 天赋等级
    private int aptitudeRoll;     // 天赋占比抽值 (0=未开窍，20-100=有效值)

    public BasicAttachments() {
        this(false, Rank.ORDINARY, Stage.INITIAL, Aptitude.ORDINARY, 0);
    }

    public BasicAttachments(boolean isAwaken, Rank rank, Stage stage, Aptitude aptitude, int aptitudeRoll) {
        this.isAwaken = isAwaken;
        this.rank = rank;
        this.stage = stage;
        this.aptitude = aptitude;
        this.aptitudeRoll = aptitudeRoll;
    }

    // --- getter ---
    public boolean isAwaken() { return isAwaken; }
    public Rank getRank() { return rank; }
    public Stage getStage() { return stage; }
    public Aptitude getAptitude() { return aptitude; }
    public int getAptitudeRoll() { return aptitudeRoll; }

    // --- setter ---
    public void setAwaken(boolean awaken) { this.isAwaken = awaken; }
    public void setRank(Rank rank) { this.rank = rank; }
    public void setStage(Stage stage) { this.stage = stage; }
    public void setAptitude(Aptitude aptitude) { this.aptitude = aptitude; }
    public void setAptitudeRoll(int aptitudeRoll) { this.aptitudeRoll = aptitudeRoll; }
}
