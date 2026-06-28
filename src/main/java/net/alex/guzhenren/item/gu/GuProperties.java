package net.alex.guzhenren.item.gu;

import net.alex.guzhenren.enums.core.Rank;
import net.alex.guzhenren.enums.gu.GuType;
import net.alex.guzhenren.enums.path.Path;

public class GuProperties {

    private final Path path;
    private final Rank rank;
    private final GuType type;
    private final boolean needFeed;
    private final boolean needRefine;
    private final int totalRefineProgress;
    private final long maxRefineEssencePerOp;

    private GuProperties(Builder b) {
        this.path = b.path;
        this.rank = b.rank;
        this.type = b.type;
        this.needFeed = b.needFeed;
        this.needRefine = b.needRefine;
        this.totalRefineProgress = b.totalRefineProgress;
        this.maxRefineEssencePerOp = b.maxRefineEssencePerOp;
    }

    //region GETTER
    public Path getPath() { return path; }
    public Rank getRank() { return rank; }
    public GuType getType() { return type; }
    public boolean isNeedFeed() { return needFeed; }
    public boolean isNeedRefine() { return needRefine; }
    public int getTotalRefineProgress() { return totalRefineProgress; }
    public long getMaxRefineEssencePerOp() { return maxRefineEssencePerOp; }
//endregion

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Path path = Path.HUMAN;
        private Rank rank = Rank.ONE;
        private GuType type = GuType.ONE_TIME;
        private boolean needFeed = false;
        private boolean needRefine = false;
        private int totalRefineProgress = 0;
        private long maxRefineEssencePerOp = 0L;

        public Builder path(Path path) { this.path = path; return this; }
        public Builder rank(Rank rank) { this.rank = rank; return this; }
        public Builder type(GuType type) { this.type = type; return this; }
        public Builder needFeed(boolean v) { this.needFeed = v; return this; }
        public Builder needRefine(boolean v) { this.needRefine = v; return this; }
        public Builder refineConfig(int totalProgress, long maxEssencePerOp) {
            this.needRefine = true;
            this.totalRefineProgress = totalProgress;
            this.maxRefineEssencePerOp = maxEssencePerOp;
            return this;
        }
        public GuProperties build() { return new GuProperties(this); }
    }
}
