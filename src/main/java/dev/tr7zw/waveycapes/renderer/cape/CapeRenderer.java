package dev.tr7zw.waveycapes.renderer.cape;

import dev.tr7zw.waveycapes.WaveyCapes;
import dev.tr7zw.waveycapes.util.Mth;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.GL11;

public interface CapeRenderer {

    int partCount = 16;

    void render(PlayerEntity player, float delta, float scale, boolean undoTranslation);

    default float easeOutSine(float x) {
        return (float) Math.sin((x * Math.PI) / 2f);
    }

    default float getNaturalWindSwing(int part) {
        if (WaveyCapes.config.wavyCape) {
            long highlightedPart = (System.currentTimeMillis() / 3) % 360;
            float relativePart = (float) (part + 1) / partCount;
            return (float) (Math.sin(Math.toRadians((relativePart) * 360 - (highlightedPart))) * 3);
        }
        return 0;
    }
}
