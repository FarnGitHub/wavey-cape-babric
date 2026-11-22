package dev.tr7zw.waveycapes.renderer.cape;

import dev.tr7zw.waveycapes.sim.Simulation;
import dev.tr7zw.waveycapes.util.Mth;
import dev.tr7zw.waveycapes.util.cape.PoseStack;
import dev.tr7zw.waveycapes.util.matrix.Matrix4;
import dev.tr7zw.waveycapes.util.vector.Vector3;
import dev.tr7zw.waveycapes.util.vector.Vector4;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("SameParameterValue")
public class SmoothCapeRenderer implements CapeRenderer{
    @Override
    public void render(PlayerEntity player, float delta, float scale,  boolean undoTranslation) {
        if(undoTranslation) {
            CapeRenderer.undoVanillaCloakRotations(player, delta);
        }
        Tessellator tessellator = Tessellator.INSTANCE;
        tessellator.startQuads();
        PoseStack poseStack = undoTranslation ? PoseStack.fromGlModelView() : new PoseStack();
        poseStack.pushPose();

        Matrix4 oldPositionMatrix = null;
        float s16 = scale * 16F;
        float size96 = (0.96F / partCount);
        for (int part = 0; part < partCount; part++) {
            modifyPoseStack(poseStack, player, delta, part, undoTranslation);

            Matrix4 curPoseMatrix = poseStack.scale(s16, s16, s16).pose();
            if (oldPositionMatrix == null) {
                oldPositionMatrix = curPoseMatrix;
            }

            if (part == 0) {
                addTopVertex(tessellator, curPoseMatrix, oldPositionMatrix,
                        0.3F,
                        0,
                        0F,
                        -0.3F,
                        0,
                        -0.06F,
                        part
                );
            }

            if (part == partCount - 1) {
                addBottomVertex(tessellator, curPoseMatrix, curPoseMatrix,
                        0.3F,
                        (part + 1) * size96,
                        0F,
                        -0.3F,
                        (part + 1) * size96,
                        -0.06F,
                        part
                );
            }

            addLeftVertex(tessellator, curPoseMatrix, oldPositionMatrix,
                    -0.3F,
                    (part + 1) * size96,
                    0F,
                    -0.3F,
                    part * size96,
                    -0.06F,
                    part
            );

            addRightVertex(tessellator, curPoseMatrix, oldPositionMatrix,
                    0.3F,
                    (part + 1) * size96,
                    0F,
                    0.3F,
                    part * size96,
                    -0.06F,
                    part
            );

            addBackVertex(tessellator, curPoseMatrix, oldPositionMatrix,
                    0.3F,
                    (part + 1) * size96,
                    -0.06F,
                    -0.3F,
                    part * size96,
                    -0.06F,
                    part
            );

            addFrontVertex(tessellator, oldPositionMatrix, curPoseMatrix,
                    0.3F,
                    (part + 1) * size96,
                    0F,
                    -0.3F,
                    part * size96,
                    0F,
                    part
            );

            oldPositionMatrix = curPoseMatrix;
            poseStack.popPose();
        }
        // Vertices are baked with the full model view matrix; reset it so draw() does not transform again.
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        tessellator.draw();
        GL11.glPopMatrix();
    }

    void modifyPoseStack(PoseStack stack, PlayerEntity player, float delta, int part, boolean undoTranslation) {
        if(player.waveycape_getSimulation() != null) {
            modifyPoseStackSimulation(stack, player, delta, part, undoTranslation);
            return;
        }
        modifyPoseStackVanilla(stack, player, delta, part, undoTranslation);
    }

    private void modifyPoseStackSimulation(PoseStack poseStack, PlayerEntity player, float delta, int part, boolean undoTranslation) {
        Simulation simulation = player.waveycape_getSimulation();
        poseStack.pushPose();
        if (!undoTranslation) {
            poseStack.translate(0.0D, 0.0D, 0.125D);
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
                poseStack.translate(0, 0.15F, 0);
            }
        }

        float naturalWindSwing = CapeRenderer.getNaturalWindSwing(part);
        float offset = 0.48f / 16f;
        float partOffset = part * 1f/partCount;
        // vanilla rotating and wind
        poseStack.mulPose(Vector3.XP.rotationDegrees(6.0F + height + naturalWindSwing));
        poseStack.mulPose(Vector3.ZP.rotationDegrees(sidewaysRotationOffset / 2.0F));
        poseStack.mulPose(Vector3.YP.rotationDegrees(180.0F - sidewaysRotationOffset / 2.0F));
        poseStack.translate(x/partCount, y/ partCount, z/ partCount); // movement from the simulation
        //offsetting so the rotation is on the cape part
        //float offset = (float) (part * (16 / CustomCapeRenderLayer.partCount))/16; // to fold the entire cape into one position for debugging
        poseStack.translate(0, offset , -offset); // (0.48/16)
        poseStack.translate(0, partOffset, 0);
        poseStack.mulPose(Vector3.XP.rotationDegrees(-partRotation)); // apply actual rotation
        // undoing the rotation
        poseStack.translate(0, -partOffset, 0);
        poseStack.translate(0, -offset, offset);

    }

    private void modifyPoseStackVanilla(PoseStack poseStack, PlayerEntity player, float delta, int part, boolean undoTranslation) {
        poseStack.pushPose();
        if (!undoTranslation) {
            poseStack.translate(0.0D, 0.0D, 0.125D);
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
        float swing = (float) (d * o + m * p) * CapeRenderer.easeOutSine(1F/ partCount*part)*100;
        swing = Mth.clamp(swing, 0.0F, 150.0F * CapeRenderer.easeOutSine(1F/ partCount*part));
        float sidewaysRotationOffset = (float) (d * p - m * o) * 100.0F;
        sidewaysRotationOffset = Mth.clamp(sidewaysRotationOffset, -20.0F, 20.0F);
        float t = Mth.lerp(delta, player.prevStepBobbingAmount, player.stepBobbingAmount);
        height += (float)Math.sin(Mth.lerp(delta, player.prevHorizontalSpeed, player.horizontalSpeed) * 6.0F) * 32.0F * t;
        if (player.isSneaking()) {
            height += 25.0F;
            if (!undoTranslation) {
                poseStack.translate(0, 0.15F, 0);
            }
        }

        float naturalWindSwing = CapeRenderer.getNaturalWindSwing(part);

        poseStack.mulPose(Vector3.XP.rotationDegrees(6.0F + swing / 2.0F + height + naturalWindSwing));
        poseStack.mulPose(Vector3.ZP.rotationDegrees(sidewaysRotationOffset / 2.0F));
        poseStack.mulPose(Vector3.YP.rotationDegrees(180.0F - sidewaysRotationOffset / 2.0F));
    }

    private static void addBackVertex(Tessellator tess, Matrix4 matrix, Matrix4 oldMatrix, float x1, float y1, float z1, float x2, float y2, float z2, int part) {
        float i;
        Matrix4 k;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;

            k = matrix;
            matrix = oldMatrix;
            oldMatrix = k;
        }

        float minU = .015625F;
        float maxU = .171875F;

        float minV = .03125F;
        float maxV = .53125F;

        float deltaV = maxV - minV;
        float vPerPart = deltaV / partCount;
        maxV = minV + (vPerPart * (part + 1));
        minV = minV + (vPerPart * part);

        //oldMatrix
        vertexUVNormal(tess, oldMatrix, x1, y2, z1, maxU, minV, 1, 0, 0);
        vertexUVNormal(tess, oldMatrix, x2, y2, z1, minU, minV, 1, 0, 0);
        //matrix
        vertexUVNormal(tess, matrix, x2, y1, z2, minU, maxV, 1, 0, 0);
        vertexUVNormal(tess, matrix, x1, y1, z2, maxU, maxV, 1, 0, 0);

    }

    private static void addFrontVertex(Tessellator tess, Matrix4 matrix, Matrix4 oldMatrix, float x1, float y1, float z1, float x2, float y2, float z2, int part) {
        float i;
        Matrix4 k;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;

            k = matrix;
            matrix = oldMatrix;
            oldMatrix = k;
        }

        float minU = .1875F;
        float maxU = .34375F;

        float minV = .03125F;
        float maxV = .53125F;

        float deltaV = maxV - minV;
        float vPerPart = deltaV / partCount;
        maxV = minV + (vPerPart * (part + 1));
        minV = minV + (vPerPart * part);

        //oldMatrix
        vertexUVNormal(tess, oldMatrix, x1, y1, z1, maxU, maxV, 1, 0, 0);
        vertexUVNormal(tess, oldMatrix, x2, y1, z1, minU, maxV, 1, 0, 0);
        //matrix
        vertexUVNormal(tess, matrix, x2, y2, z2, minU, minV, 1, 0, 0);
        vertexUVNormal(tess, matrix, x1, y2, z2, maxU, minV, 1, 0, 0);

    }

    private static void addLeftVertex(Tessellator tess, Matrix4 matrix, Matrix4 oldMatrix, float x1, float y1, float z1, float x2, float y2, float z2, int part) {
        float i;
        if (x1 < x2) {
            i = x1;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }

        float minU = 0;
        float maxU = .015625F;

        float minV = .03125F;
        float maxV = .53125F;

        float deltaV = maxV - minV;
        float vPerPart = deltaV / partCount;
        maxV = minV + (vPerPart * (part + 1));
        minV = minV + (vPerPart * part);

        //matrix
        vertexUVNormal(tess, matrix, x2, y1, z1, maxU, maxV, 1, 0, 0);
        vertexUVNormal(tess, matrix, x2, y1, z2, minU, maxV, 1, 0, 0);
        //oldMatrix
        vertexUVNormal(tess, oldMatrix, x2, y2, z2, minU, minV, 1, 0, 0);
        vertexUVNormal(tess, oldMatrix, x2, y2, z1, maxU, minV, 1, 0, 0);

    }

    private static void addRightVertex(Tessellator tess, Matrix4 matrix, Matrix4 oldMatrix, float x1, float y1, float z1, float x2, float y2, float z2, int part) {
        float i;
        if (x1 < x2) {
            i = x1;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }

        float minU = .171875F;
        float maxU = .1875F;

        float minV = .03125F;
        float maxV = .53125F;

        float deltaV = maxV - minV;
        float vPerPart = deltaV / partCount;
        maxV = minV + (vPerPart * (part + 1));
        minV = minV + (vPerPart * part);

        //matrix
        vertexUVNormal(tess, matrix, x2, y1, z2, minU, maxV, 1, 0, 0);
        vertexUVNormal(tess, matrix, x2, y1, z1, maxU, maxV, 1, 0, 0);
        //oldMatrix
        vertexUVNormal(tess, oldMatrix, x2, y2, z1, maxU, minV, 1, 0, 0);
        vertexUVNormal(tess, oldMatrix, x2, y2, z2, minU, minV, 1, 0, 0);

    }

    private static void addBottomVertex(Tessellator tess, Matrix4 matrix, Matrix4 oldMatrix, float x1, float y1, float z1, float x2, float y2, float z2, int part) {
        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }

        float minU = .171875F;
        float maxU = .328125F;

        float minV = 0;
        float maxV = .03125F;

        float deltaV = maxV - minV;
        float vPerPart = deltaV / partCount;
        maxV = minV + (vPerPart * (part + 1));
        minV = minV + (vPerPart * part);

        //oldMatrix
        vertexUVNormal(tess, oldMatrix, x1, y2, z2, maxU, minV, 1, 0, 0);
        vertexUVNormal(tess, oldMatrix, x2, y2, z2, minU, minV, 1, 0, 0);
        //newMatrix
        vertexUVNormal(tess, matrix, x2, y1, z1, minU, maxV, 1, 0, 0);
        vertexUVNormal(tess, matrix, x1, y1, z1, maxU, maxV, 1, 0, 0);

    }

    private static void vertexUVNormal(Tessellator tess, Matrix4 matrix4f, float f, float g, float h, float u, float v, float nx, float ny, float nz) {
        Vector4 vector4f = new Vector4(f, g, h, 1F);
        vector4f.transform(matrix4f);
        tess.normal(nx, ny, nz);
        tess.vertex(vector4f.x(), vector4f.y(), vector4f.z(), u, v);
    }

    private static void addTopVertex(Tessellator tess, Matrix4 matrix, Matrix4 oldMatrix, float x1, float y1, float z1, float x2, float y2, float z2, int part) {
        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }

        float minU = .015625F;
        float maxU = .171875F;

        float minV = 0;
        float maxV = .03125F;

        float deltaV = maxV - minV;
        float vPerPart = deltaV / partCount;
        maxV = minV + (vPerPart * (part + 1));
        minV = minV + (vPerPart * part);

        //oldMatrix
        vertexUVNormal(tess, oldMatrix, x1, y2, z1, maxU, maxV, 0, 1, 0);
        vertexUVNormal(tess, oldMatrix, x2, y2, z1, minU, maxV, 0, 1, 0);
        //newMatrix
        vertexUVNormal(tess, matrix, x2, y1, z2, minU, minV, 0, 1, 0);
        vertexUVNormal(tess, matrix, x1, y1, z2, maxU, minV, 0, 1, 0);
    }
}
