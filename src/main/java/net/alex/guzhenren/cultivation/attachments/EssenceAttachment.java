package net.alex.guzhenren.cultivation.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class EssenceAttachment {

    public static final Codec<EssenceAttachment> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.LONG.fieldOf("max_essence").forGetter(EssenceAttachment::getMaxEssence),
                    Codec.LONG.fieldOf("current_essence").forGetter(EssenceAttachment::getCurrentEssence)
            ).apply(instance, EssenceAttachment::new));

    private long maxEssence;       // 最大真元
    private long currentEssence;   // 当前真元

    private transient double regenBuffer;

    public EssenceAttachment() {
        this(0L, 0L);
    }

    public EssenceAttachment(long maxEssence, long currentEssence) {
        this.maxEssence = maxEssence;
        this.currentEssence = currentEssence;
        this.regenBuffer = 0.0;
    }

    // --- getter ---
    public long getMaxEssence() { return maxEssence; }
    public long getCurrentEssence() { return currentEssence; }
    public double getRegenBuffer() { return regenBuffer; }

    // --- setter ---
    public void setMaxEssence(long maxEssence) { this.maxEssence = maxEssence; }
    public void setCurrentEssence(long currentEssence) { this.currentEssence = currentEssence; }
    public void setRegenBuffer(double regenBuffer) { this.regenBuffer = regenBuffer; }
}
