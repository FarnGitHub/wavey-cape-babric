package dev.tr7zw.waveycapes.renderer;
import dev.tr7zw.waveycapes.WaveyCapes;
import dev.tr7zw.waveycapes.renderer.cape.BlockyCapeRender;
import dev.tr7zw.waveycapes.renderer.cape.CapeRenderer;
import dev.tr7zw.waveycapes.renderer.cape.SmoothCapeRenderer;
import dev.tr7zw.waveycapes.util.Mth;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("SameParameterValue")
public final class CapeRendererImpl {
    public static final int partCount = 16;
    private static CapeRenderer capeRenderer;

    static {
        setCapeRenderer(WaveyCapes.config.blockyCape);
    }

    public static void setCapeRenderer(boolean blocky) {
        capeRenderer = blocky ? new BlockyCapeRender() : new SmoothCapeRenderer();
    }

    public static void render(PlayerEntity player, float delta, boolean undoTranslation) {
        if (undoTranslation) {
            undoVanillaCloakRotations(player, delta);
        }
        capeRenderer.render(player, delta, undoTranslation);
    }

    private static void undoVanillaCloakRotations(PlayerEntity player, float delta) {
        double d = Mth.lerp(delta, player.prevCapeX, player.capeX)
                - Mth.lerp(delta, player.prevX, player.x);
        double e = Mth.lerp(delta, player.prevCapeY, player.capeY)
                - Mth.lerp(delta, player.prevY, player.y);
        double m = Mth.lerp(delta, player.prevCapeZ, player.capeZ)
                - Mth.lerp(delta, player.prevZ, player.z);
        float n = player.lastBodyYaw + player.bodyYaw - player.lastBodyYaw;
        double o = Math.sin(n * 0.017453292F);
        double p = -Math.cos(n * 0.017453292F);
        float height = (float) e * 10.0F;
        height = Mth.clamp(height, -6.0F, 32.0F);
        float swing = (float) (d * o + m * p) * 100.0F;
        swing = Mth.clamp(swing, 0.0F, 150.0F);
        float sidewaysRotationOffset = (float) (d * p - m * o) * 100.0F;
        sidewaysRotationOffset = Mth.clamp(sidewaysRotationOffset, -20.0F, 20.0F);
        float t = Mth.lerp(delta, player.prevStepBobbingAmount, player.stepBobbingAmount);
        height += (float)Math.sin(Mth.lerp(delta, player.prevHorizontalSpeed, player.horizontalSpeed) * 6.0F) * 32.0F * t;
        if (player.isSneaking()) {
            height += 25.0F;
        }

        GL11.glRotatef(-180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(sidewaysRotationOffset / 2.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-sidewaysRotationOffset / 2.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-(6.0F + swing / 2.0F + height), 1.0F, 0.0F, 0.0F);
    }

}
