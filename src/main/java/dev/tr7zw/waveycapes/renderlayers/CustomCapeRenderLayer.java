package dev.tr7zw.waveycapes.renderlayers;

import dev.tr7zw.waveycapes.impl.PlayerImpl;
import dev.tr7zw.waveycapes.config.enums.CapeStyle;
import dev.tr7zw.waveycapes.WaveyCapes;
import dev.tr7zw.waveycapes.config.enums.WindMode;
import dev.tr7zw.waveycapes.sim.Simulation;
import dev.tr7zw.waveycapes.util.Mth;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class CustomCapeRenderLayer {
    
    static final int partCount = 16;
    private final ModelPart[] customCape;
    private final SmoothCapeRenderer smoothCapeRenderer = new SmoothCapeRenderer();
    
    public CustomCapeRenderLayer() {
        customCape = new ModelPart[partCount];
        for (int i = 0; i < partCount; i++) {
            ModelPart base = new ModelPart(0, i);
            base.addCuboid(-5.0F, (float)i, -1.0F, 10, 1, 1);
            this.customCape[i] = base;
        }
    }

    public void doRenderLayer(PlayerEntity player, float deltaTick, boolean atVanillaCloakMatrix) {

        if (atVanillaCloakMatrix) {
            undoVanillaCloakRotations(player, deltaTick);
        }

        if (WaveyCapes.config.capeStyle == CapeStyle.SMOOTH) {
            smoothCapeRenderer.renderSmoothCape(this, player, deltaTick, atVanillaCloakMatrix);
        } else {
            for (int part = 0; part < partCount; part++) {
                ModelPart model = customCape[part];
                GL11.glPushMatrix();
                modifyPoseStack(player, deltaTick, part, atVanillaCloakMatrix);
                model.render(0.0625F);
                GL11.glPopMatrix();
            }
        }
    }
    
    private void undoVanillaCloakRotations(PlayerEntity abstractClientPlayer, float h) {
        double d = Mth.lerp(h, abstractClientPlayer.prevCapeX, abstractClientPlayer.capeX)
                - Mth.lerp(h, abstractClientPlayer.prevX, abstractClientPlayer.x);
        double e = Mth.lerp(h, abstractClientPlayer.prevCapeY, abstractClientPlayer.capeY)
                - Mth.lerp(h, abstractClientPlayer.prevY, abstractClientPlayer.y);
        double m = Mth.lerp(h, abstractClientPlayer.prevCapeZ, abstractClientPlayer.capeZ)
                - Mth.lerp(h, abstractClientPlayer.prevZ, abstractClientPlayer.z);
        float n = abstractClientPlayer.lastBodyYaw + abstractClientPlayer.bodyYaw - abstractClientPlayer.lastBodyYaw;
        double o = Math.sin(n * 0.017453292F);
        double p = -Math.cos(n * 0.017453292F);
        float height = (float) e * 10.0F;
        height = Mth.clamp(height, -6.0F, 32.0F);
        float swing = (float) (d * o + m * p) * 100.0F;
        swing = Mth.clamp(swing, 0.0F, 150.0F);
        float sidewaysRotationOffset = (float) (d * p - m * o) * 100.0F;
        sidewaysRotationOffset = Mth.clamp(sidewaysRotationOffset, -20.0F, 20.0F);
        float t = Mth.lerp(h, abstractClientPlayer.prevStepBobbingAmount, abstractClientPlayer.stepBobbingAmount);
        height = (float) (height + Math.sin(Mth.lerp(h, abstractClientPlayer.prevHorizontalSpeed, abstractClientPlayer.horizontalSpeed) * 6.0F) * 32.0F * t);
        if (abstractClientPlayer.isSneaking()) {
            height += 25.0F;
        }

        GL11.glRotatef(-180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(sidewaysRotationOffset / 2.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-sidewaysRotationOffset / 2.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-(6.0F + swing / 2.0F + height), 1.0F, 0.0F, 0.0F);
    }

    private void modifyPoseStack(PlayerEntity player, float delta, int part, boolean atVanillaCloakMatrix) {
        if (!atVanillaCloakMatrix) {
            GL11.glTranslated(0.0D, 0.0D, 0.125D);
        }
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
        float swing = (float) (d * o + m * p) * easeOutSine(1.0F/partCount*part)*100;
        swing = Mth.clamp(swing, 0.0F, 150.0F * easeOutSine(1F/partCount*part));
        float sidewaysRotationOffset = (float) (d * p - m * o) * 100.0F;
        sidewaysRotationOffset = Mth.clamp(sidewaysRotationOffset, -20.0F, 20.0F);
        float t = Mth.lerp(delta, player.prevStepBobbingAmount, player.stepBobbingAmount);
        height = (float) (height + Math.sin(Mth.lerp(delta, player.prevHorizontalSpeed, player.horizontalSpeed) * 6.0F) * 32.0F * t);
        if (player.isSneaking()) {
            height += 25.0F;
            if (!atVanillaCloakMatrix) {
                GL11.glTranslatef(0.0F, 0.15F, 0.0F);
            }
        }

        float naturalWindSwing = getNaturalWindSwing(part);
        
        GL11.glRotatef(6.0F + swing / 2.0F + height + naturalWindSwing, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(sidewaysRotationOffset / 2.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-sidewaysRotationOffset / 2.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
    }
    
    float getNaturalWindSwing(int part) {
        if (WaveyCapes.config.windMode == WindMode.WAVES) {
            long highlightedPart = (System.currentTimeMillis() / 3) % 360;
            float relativePart = (float) (part + 1) / partCount;
            return (float) (Math.sin(Math.toRadians((relativePart) * 360 - (highlightedPart))) * 3);
        }
        return 0;
    }

    private static float easeOutSine(float x) {
        return (float) Math.sin((x * Math.PI) / 2f);
    }
    
}
