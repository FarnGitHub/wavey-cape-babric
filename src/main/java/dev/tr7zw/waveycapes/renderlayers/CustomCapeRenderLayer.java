package dev.tr7zw.waveycapes.renderlayers;

import dev.tr7zw.waveycapes.CapeHolder;
import dev.tr7zw.waveycapes.CapeMovement;
import dev.tr7zw.waveycapes.CapeStyle;
import dev.tr7zw.waveycapes.WaveyCapesBase;
import dev.tr7zw.waveycapes.WindMode;
import dev.tr7zw.waveycapes.sim.StickSimulation;
import dev.tr7zw.waveycapes.util.Mth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.GL11;

public class CustomCapeRenderLayer {
    
    static final int partCount = 16;
    private ModelPart[] customCape = new ModelPart[partCount];
    private final PlayerEntityRenderer playerRenderer;
    private SmoothCapeRenderer smoothCapeRenderer = new SmoothCapeRenderer();
    
    public CustomCapeRenderLayer(PlayerEntityRenderer playerRenderer, EntityModel model) {
        this.playerRenderer = playerRenderer;
        buildMesh(model);
    }
    
    private void buildMesh(EntityModel model) {
        customCape = new ModelPart[partCount];
        for (int i = 0; i < partCount; i++) {
            ModelPart base = new ModelPart(0, i);
            base.addCuboid(-5.0F, (float)i, -1.0F, 10, 1, 1);
            this.customCape[i] = base;
        }
    }

    public void doRenderLayer(PlayerEntity abstractClientPlayer, float paramFloat1, float paramFloat2, float deltaTick,
                              float animationTick, float paramFloat5, float paramFloat6, float paramFloat7) {
        doRenderLayer(abstractClientPlayer, paramFloat1, paramFloat2, deltaTick, animationTick, paramFloat5, paramFloat6,
                paramFloat7, false);
    }

    public void doRenderLayer(PlayerEntity abstractClientPlayer, float paramFloat1, float paramFloat2, float deltaTick,
                              float animationTick, float paramFloat5, float paramFloat6, float paramFloat7, boolean atVanillaCloakMatrix) {
        
        if(WaveyCapesBase.config.capeMovement == CapeMovement.BASIC_SIMULATION) {
            CapeHolder holder = (CapeHolder) abstractClientPlayer;
            holder.updateSimulation(abstractClientPlayer, partCount);
        }
        int textureId = Minecraft.INSTANCE.textureManager.downloadTexture(abstractClientPlayer.capeUrl, null);
        if(textureId == -1) return;

        Minecraft.INSTANCE.textureManager.bindTexture(textureId);

        if (atVanillaCloakMatrix) {
            undoVanillaCloakRotations(abstractClientPlayer, deltaTick);
        }

        if (WaveyCapesBase.config.capeStyle == CapeStyle.SMOOTH) {
            smoothCapeRenderer.renderSmoothCape(this, abstractClientPlayer, deltaTick, atVanillaCloakMatrix);
        } else {
            ModelPart[] parts = customCape;
            for (int part = 0; part < partCount; part++) {
                ModelPart model = parts[part];
                GL11.glPushMatrix();
                modifyPoseStack(abstractClientPlayer, deltaTick, part, atVanillaCloakMatrix);
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
        height += Math.sin(Mth.lerp(h, abstractClientPlayer.prevHorizontalSpeed, abstractClientPlayer.horizontalSpeed) * 6.0F) * 32.0F * t;
        if (abstractClientPlayer.isSneaking()) {
            height += 25.0F;
        }

        GL11.glRotatef(-180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(sidewaysRotationOffset / 2.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-sidewaysRotationOffset / 2.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-(6.0F + swing / 2.0F + height), 1.0F, 0.0F, 0.0F);
    }
    
    private void modifyPoseStack(PlayerEntity abstractClientPlayer, float h, int part) {
        modifyPoseStack(abstractClientPlayer, h, part, false);
    }

    private void modifyPoseStack(PlayerEntity abstractClientPlayer, float h, int part, boolean atVanillaCloakMatrix) {
        if(WaveyCapesBase.config.capeMovement == CapeMovement.BASIC_SIMULATION) {
            modifyPoseStackSimulation(abstractClientPlayer, h, part, atVanillaCloakMatrix);
            return;
        }
        modifyPoseStackVanilla(abstractClientPlayer, h, part, atVanillaCloakMatrix);
    }
    
    private void modifyPoseStackSimulation(PlayerEntity abstractClientPlayer, float delta, int part,
            boolean atVanillaCloakMatrix) {
        StickSimulation simulation = ((CapeHolder)abstractClientPlayer).waveycape_getSimulation();
        if (!atVanillaCloakMatrix) {
            GL11.glTranslated(0.0D, 0.0D, 0.125D);
        }
        
        float z = simulation.points.get(part).getLerpX(delta) - simulation.points.get(0).getLerpX(delta);
        if(z > 0) {
            z = 0;
        }
        float y = simulation.points.get(0).getLerpY(delta) - part - simulation.points.get(part).getLerpY(delta);
        
        float sidewaysRotationOffset = 0;
        float partRotation = (float) -Math.atan2(y, z);
        partRotation = Math.max(partRotation, 0);
        if(partRotation != 0)
            partRotation = (float) (Math.PI-partRotation);
        partRotation *= 57.2958;
        partRotation *= 2;
        
        float height = 0;
        if (abstractClientPlayer.isSneaking()) {
            height += 25.0F;
            if (!atVanillaCloakMatrix) {
                GL11.glTranslatef(0.0F, 0.15F, 0.0F);
            }
        }

        float naturalWindSwing = getNatrualWindSwing(part);

        
        // vanilla rotating and wind
        GL11.glRotatef(6.0F + height + naturalWindSwing, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(sidewaysRotationOffset / 2.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-sidewaysRotationOffset / 2.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslated(0, y/partCount, z/partCount); // movement from the simulation
        //offsetting so the rotation is on the cape part
        //float offset = (float) (part * (16 / partCount))/16; // to fold the entire cape into one position for debugging
        GL11.glTranslated(0, /*-offset*/ + (0.48/16) , - (0.48/16)); // (0.48/16)
        GL11.glTranslated(0, part * 1f/partCount, part * (0)/partCount);
        //GL11.glRotatef(-partRotation, 1.0F, 0.0F, 0.0F);
        // undoing the rotation
        GL11.glTranslated(0, -part * 1f/partCount, -part * (0)/partCount);
        GL11.glTranslated(0, -(0.48/16), (0.48/16));
        
    }
    
    void modifyPoseStackVanilla(PlayerEntity abstractClientPlayer, float h, int part) {
        modifyPoseStackVanilla(abstractClientPlayer, h, part, false);
    }

    void modifyPoseStackVanilla(PlayerEntity abstractClientPlayer, float h, int part, boolean atVanillaCloakMatrix) {
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
        float swing = (float) (d * o + m * p) * easeOutSine(1.0F/partCount*part)*100;
        swing = Mth.clamp(swing, 0.0F, 150.0F * easeOutSine(1F/partCount*part));
        float sidewaysRotationOffset = (float) (d * p - m * o) * 100.0F;
        sidewaysRotationOffset = Mth.clamp(sidewaysRotationOffset, -20.0F, 20.0F);
        float t = Mth.lerp(h, abstractClientPlayer.prevStepBobbingAmount, abstractClientPlayer.stepBobbingAmount);
        height += Math.sin(Mth.lerp(h, abstractClientPlayer.prevHorizontalSpeed, abstractClientPlayer.horizontalSpeed) * 6.0F) * 32.0F * t;
        if (abstractClientPlayer.isSneaking()) {
            height += 25.0F;
            if (!atVanillaCloakMatrix) {
                GL11.glTranslatef(0.0F, 0.15F, 0.0F);
            }
        }

        float naturalWindSwing = getNatrualWindSwing(part);
        
        GL11.glRotatef(6.0F + swing / 2.0F + height + naturalWindSwing, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(sidewaysRotationOffset / 2.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-sidewaysRotationOffset / 2.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
    }
    
    float getNatrualWindSwing(int part) {
        if (WaveyCapesBase.config.windMode == WindMode.WAVES) {
            long highlightedPart = (System.currentTimeMillis() / 3) % 360;
            float relativePart = (float) (part + 1) / partCount;
            return (float) (Math.sin(Math.toRadians((relativePart) * 360 - (highlightedPart))) * 3);
        }
//        if (WaveyCapesBase.config.windMode == WindMode.SLIGHT) {
//            return getWind(60);
//        }
        return 0;
    }

    
    /**
     * https://easings.net/#easeOutSine
     * 
     * @param x
     * @return
     */
    private static float easeOutSine(float x) {
        return (float) Math.sin((x * Math.PI) / 2f);
    }

    public boolean shouldCombineTextures() {
        return false;
    }
    
}
