package net.alex.guzhenren.gameplay.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.alex.guzhenren.enums.path.Path;

public record ModPlayerData(CoreComponent cultivation, EssenceComponent essence,
                            StatusComponent status, PathComponent path) {

    public static final Codec<ModPlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CoreComponent.CODEC.fieldOf("cultivation").forGetter(ModPlayerData::cultivation),
            EssenceComponent.CODEC.fieldOf("essence").forGetter(ModPlayerData::essence),
            StatusComponent.CODEC.fieldOf("status").forGetter(ModPlayerData::status),
            PathComponent.CODEC.fieldOf("path").forGetter(ModPlayerData::path)
    ).apply(instance, ModPlayerData::new));

    public ModPlayerData() {
        this(new CoreComponent(), new EssenceComponent(), new StatusComponent(), new PathComponent());
    }

    public void copyFrom(ModPlayerData src) {
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
