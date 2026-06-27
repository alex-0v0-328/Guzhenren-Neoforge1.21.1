package net.alex.guzhenren.gameplay.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.alex.guzhenren.enums.path.Path;

public record ModPlayerData(CoreComponent core, EssenceComponent essence,
                            StatusComponent status, PathComponent path) {

    public static final Codec<ModPlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CoreComponent.CODEC.fieldOf("core").forGetter(ModPlayerData::core),
            EssenceComponent.CODEC.fieldOf("essence").forGetter(ModPlayerData::essence),
            StatusComponent.CODEC.fieldOf("status").forGetter(ModPlayerData::status),
            PathComponent.CODEC.fieldOf("path").forGetter(ModPlayerData::path)
    ).apply(instance, ModPlayerData::new));

    public ModPlayerData() {
        this(new CoreComponent(), new EssenceComponent(), new StatusComponent(), new PathComponent());
    }

    public void copyFrom(ModPlayerData src) {
        this.core.setPlayerRank(src.core.getPlayerRank());
        this.core.setPlayerStage(src.core.getPlayerStage());
        this.core.setPlayerTalent(src.core.getPlayerTalent());
        this.core.setPlayerExtremePhysique(src.core.getPlayerExtremePhysique());
        this.core.setPlayerBaseEssence(src.core.getPlayerBaseEssence());

        this.essence.recomputeMaxEssence(
                src.core.getPlayerBaseEssence(),
                src.core.getPlayerRank(),
                src.core.getPlayerStage()
        );
        this.essence.addCurrent(src.essence.getCurrentEssence());

        this.status.setApertureAwakened(src.status.isApertureAwakened());

        for (var p : Path.values()) {
            this.path.setAttainment(p, src.path.getAttainment(p));
            this.path.setMarks(p, src.path.getMarks(p));
        }

        this.core.clearDirty();
        this.status.clearDirty();
        this.path.clearDirty();
    }
}
