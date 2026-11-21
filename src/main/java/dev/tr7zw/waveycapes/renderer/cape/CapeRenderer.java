package dev.tr7zw.waveycapes.renderer.cape;

import dev.tr7zw.waveycapes.WaveyCapes;
import dev.tr7zw.waveycapes.renderer.CapeRendererImpl;
import net.minecraft.entity.player.PlayerEntity;

public interface CapeRenderer {

    void render(PlayerEntity player, float delta, boolean undoTranslation);

    static float easeOutSine(float x) {
        return (float) Math.sin((x * Math.PI) / 2f);
    }

    static float getNaturalWindSwing(int part) {
        if (WaveyCapes.config.wavyCape) {
            long highlightedPart = (System.currentTimeMillis() / 3) % 360;
            float relativePart = (float) (part + 1) / CapeRendererImpl.partCount;
            return (float) (Math.sin(Math.toRadians((relativePart) * 360 - (highlightedPart))) * 3);
        }
        return 0;
    }
}
