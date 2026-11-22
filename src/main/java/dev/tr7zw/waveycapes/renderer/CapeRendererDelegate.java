package dev.tr7zw.waveycapes.renderer;

import dev.tr7zw.waveycapes.WaveyCapes;
import dev.tr7zw.waveycapes.renderer.cape.BlockyCapeRenderer;
import dev.tr7zw.waveycapes.renderer.cape.CapeRenderer;
import dev.tr7zw.waveycapes.renderer.cape.SmoothCapeRenderer;
import net.minecraft.entity.player.PlayerEntity;

public class CapeRendererDelegate implements CapeRenderer {
    private CapeRenderer renderer;
    private boolean blockyRender;

    public void render(PlayerEntity player, float delta, float scale,  boolean undoTranslation) {
        if(renderer == null || blockyRender != WaveyCapes.config.blockyCape) {
            blockyRender = WaveyCapes.config.blockyCape;
            renderer = blockyRender ? new BlockyCapeRenderer() : new SmoothCapeRenderer();
        }
        renderer.render(player, delta, scale, undoTranslation);
    }

}
