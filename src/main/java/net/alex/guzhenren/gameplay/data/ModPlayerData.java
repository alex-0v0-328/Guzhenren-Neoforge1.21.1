package net.alex.guzhenren.gameplay.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.alex.guzhenren.enums.path.Path;
import net.alex.guzhenren.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;

public record ModPlayerData(CoreComponent core, EssenceComponent essence, StatusComponent status,
                            PathComponent path, LifespanComponent lifespan, SoulComponent soul) {

    public static final Codec<ModPlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CoreComponent.CODEC.fieldOf("core").forGetter(ModPlayerData::core),
            EssenceComponent.CODEC.fieldOf("essence").forGetter(ModPlayerData::essence),
            StatusComponent.CODEC.fieldOf("status").forGetter(ModPlayerData::status),
            PathComponent.CODEC.fieldOf("path").forGetter(ModPlayerData::path),
            LifespanComponent.CODEC.fieldOf("lifespan").forGetter(ModPlayerData::lifespan),
            SoulComponent.CODEC.fieldOf("soul").forGetter(ModPlayerData::soul)
    ).apply(instance, ModPlayerData::new));

    public ModPlayerData() {
        this(new CoreComponent(), new EssenceComponent(), new StatusComponent(),
                new PathComponent(), new LifespanComponent(), new SoulComponent());
    }

    /** 静态入口: 从 ServerPlayer 获取 attachment 数据 */
    public static ModPlayerData of(ServerPlayer player) {
        return player.getData(ModAttachments.PLAYER_DATA.get());
    }

    /**
     * 把 src 的所有状态复制到当前实例.
     * 用于 PlayerEvent.Clone 死亡 keepInventory 场景 / 维度切换.
     * 完成后清掉所有 component 的 dirty 标记 (由 respawn 触发的 full sync 接管).
     */
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
        this.essence.setCurrentEssenceRaw(src.essence.getCurrentEssence());

        this.status.setApertureAwakened(src.status.isApertureAwakened());

        for (Path p : Path.values()) {
            this.path.setAttainment(p, src.path.getAttainment(p));
            this.path.setMarks(p, src.path.getMarks(p));
        }

        this.lifespan.setMaxLifespan(src.lifespan.getMaxLifespan());
        this.lifespan.setAge(src.lifespan.getAge());
        this.lifespan.setTickInCurrentYear(src.lifespan.getTickInCurrentYear());

        this.soul.setSoul(src.soul.getSoul());

        clearAllDirty();
    }

    /** 重置所有 component 到初始状态. Step 2 #3 引入 */
    public void reset() {
        core.reset();
        essence.reset();
        status.reset();
        path.reset();
        lifespan.reset();
        soul.reset();
    }

    private void clearAllDirty() {
        core.clearDirty();
        essence.clearDirty();
        status.clearDirty();
        path.clearDirty();
        lifespan.clearDirty();
        soul.clearDirty();
    }
}
