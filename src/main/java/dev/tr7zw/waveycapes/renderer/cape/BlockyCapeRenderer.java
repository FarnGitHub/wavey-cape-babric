package dev.tr7zw.waveycapes.renderer.cape;

import dev.tr7zw.waveycapes.sim.Simulation;
import dev.tr7zw.waveycapes.util.Mth;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.GL11;

public class BlockyCapeRenderer implements CapeRenderer {
    private final ModelPart[] customCape = new ModelPart[partCount];

    public BlockyCapeRenderer() {
        for (int v = 0; v < customCape.length; v++) {
            ModelPart base = new ModelPart(0, v);
            base.addCuboid(-5.0F, (float)v, -1.0F, 10, 1, 1);
            this.customCape[v] = base;
        }
    }

    @Override
    public void render(PlayerEntity player, float delta, float scale, boolean undoTranslation) {
        if(undoTranslation) {
            CapeRenderer.undoVanillaCloakRotations(player, delta);
        }

        for (int part = 0; part < customCape.length; part++) {
            ModelPart model = customCape[part];
            GL11.glPushMatrix();
            modifyPoseStack(player, delta, part, undoTranslation);
            model.render(scale);
            GL11.glPopMatrix();
        }
    }

    private void modifyPoseStack(PlayerEntity player, float delta, int part, boolean undoTranslation) {
        if(player.waveycape_getSimulation() != null) {
            modifyPoseStackSimulation(player, delta, part, undoTranslation);
            return;
        }
        modifyPoseStackVanilla(player, delta, part, undoTranslation);
    }

    private void modifyPoseStackSimulation(PlayerEntity player, float delta, int part, boolean undoTranslation) {
        Simulation simulation = player.waveycape_getSimulation();
        if (!undoTranslation) {
            GL11.glTranslated(0.0D, 0.0D, 0.125D);
        }

        float z = simulation.getPoints().get(part).getLerpX(delta) - simulation.getPoints().get(0).getLerpX(delta);
        if(z > 0) {
            z = 0;
        }
        float y = simulation.getPoints().get(0).getLerpY(delta) - part - simulation.getPoints().get(part).getLerpY(delta);
        float x = simulation.getPoints().get(part).getLerpZ(delta) - simulation.getPoints().get(0).getLerpZ(delta);

        float sidewaysRotationOffset = 0;
        float partRotation = (float) -Math.atan2(y, z);
        partRotation = Math.max(partRotation, 0);
        if(partRotation != 0)
            partRotation = (float) (Math.PI-partRotation);
        partRotation *= 114.5916F;

        float height = 0;
        if (player.isSneaking()) {
            height += 25.0F;
            if (!undoTranslation) {
                GL11.glTranslatef(0.0F, 0.15F, 0.0F);
            }
        }

        height += CapeRenderer.getNaturalWindSwing(part);
        float offset = 0.48f / 16f;
        float partOffset = part * 1f/customCape.length;
        GL11.glRotatef(6.0F + height, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(sidewaysRotationOffset / 2.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-sidewaysRotationOffset / 2.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslated(x/customCape.length, y/customCape.length, z/customCape.length); // movement from the simulation
        GL11.glTranslated(0, offset , -offset);
        GL11.glTranslated(0, partOffset, 0);
        GL11.glRotatef(-partRotation, 1.0F, 0.0F, 0.0F);
        GL11.glTranslated(0, -partOffset, 0);
        GL11.glTranslated(0, -offset, offset);
    }

    private void modifyPoseStackVanilla(PlayerEntity abstractClientPlayer, float h, int part, boolean atVanillaCloakMatrix) {
        if (!atVanillaCloakMatrix) {
            GL11.glTranslated(0.0D, 0.0D, 0.125D);
        }
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
        float swing = (float) (d * o + m * p) * CapeRenderer.easeOutSine(1.0F/customCape.length*part)*100;
        swing = Mth.clamp(swing, 0.0F, 150.0F * CapeRenderer.easeOutSine(1F/customCape.length*part));
        float sidewaysRotationOffset = (float) (d * p - m * o) * 100.0F;
        sidewaysRotationOffset = Mth.clamp(sidewaysRotationOffset, -20.0F, 20.0F);
        float t = Mth.lerp(h, abstractClientPlayer.prevStepBobbingAmount, abstractClientPlayer.stepBobbingAmount);
        height += (float)Math.sin(Mth.lerp(h, abstractClientPlayer.prevHorizontalSpeed, abstractClientPlayer.horizontalSpeed) * 6.0F) * 32.0F * t;
        if (abstractClientPlayer.isSneaking()) {
            height += 25.0F;
            if (!atVanillaCloakMatrix) {
                GL11.glTranslatef(0.0F, 0.15F, 0.0F);
            }
        }

        float naturalWindSwing = CapeRenderer.getNaturalWindSwing(part);

        GL11.glRotatef(6.0F + swing / 2.0F + height + naturalWindSwing, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(sidewaysRotationOffset / 2.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-sidewaysRotationOffset / 2.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
    }
}
