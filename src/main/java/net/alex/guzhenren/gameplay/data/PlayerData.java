package net.alex.guzhenren.gameplay.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.alex.guzhenren.enums.path.Path;

public record PlayerData(CultivationData cultivation, EssenceData essence,
                         StatusData status, PathData path) {

    public static final Codec<PlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CultivationData.CODEC.fieldOf("cultivation").forGetter(PlayerData::cultivation),
            EssenceData.CODEC.fieldOf("essence").forGetter(PlayerData::essence),
            StatusData.CODEC.fieldOf("status").forGetter(PlayerData::status),
            PathData.CODEC.fieldOf("path").forGetter(PlayerData::path)
    ).apply(instance, PlayerData::new));

    public PlayerData() {
        this(new CultivationData(), new EssenceData(), new StatusData(), new PathData());
    }

    public void copyFrom(PlayerData src) {
        this.cultivation.setPlayerRank(src.cultivation.getPlayerRank());
        this.cultivation.setPlayerStage(src.cultivation.getPlayerStage());
        this.cultivation.setPlayerTalent(src.cultivation.getPlayerTalent());
        this.cultivation.setPlayerExtremePhysique(src.cultivation.getPlayerExtremePhysique());
        this.cultivation.setPlayerBaseEssence(src.cultivation.getPlayerBaseEssence());

        this.essence.recomputeMaxEssence(
                src.cultivation.getPlayerBaseEssence(),
                src.cultivation.getPlayerRank(),
                src.cultivation.getPlayerStage()
        );
        this.essence.addCurrent(src.essence.getCurrentEssence());

        this.status.setApertureAwakened(src.status.isApertureAwakened());

        for (var p : Path.values()) {
            this.path.setAttainment(p, src.path.getAttainment(p));
            this.path.setMarks(p, src.path.getMarks(p));
        }

        this.cultivation.clearDirty();
        this.status.clearDirty();
        this.path.clearDirty();
    }
}
