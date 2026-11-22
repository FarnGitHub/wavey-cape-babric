package dev.tr7zw.waveycapes.util.cape;

import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

import com.google.common.math.Quantiles;
import dev.tr7zw.waveycapes.util.Mth;
import dev.tr7zw.waveycapes.util.matrix.Matrix3;
import dev.tr7zw.waveycapes.util.matrix.Matrix4;
import dev.tr7zw.waveycapes.util.vector.Quaternion;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("unused")
public class PoseStack {
    private final Deque<Pose> poseStack;

    public PoseStack() {
        this.poseStack = new ArrayDeque<>();
        Matrix4 matrix4f = new Matrix4();
        matrix4f.setIdentity();
        Matrix3 matrix3f = new Matrix3();
        matrix3f.setIdentity();
        poseStack.add(new Pose(matrix4f, matrix3f));
    }

    public static PoseStack fromGlModelView() {
        PoseStack poseStack = new PoseStack();
        poseStack.poseStack.clear();
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buffer);
        Matrix4 matrix4f = new Matrix4();
        matrix4f.load(buffer);
        Matrix3 matrix3f = new Matrix3();
        matrix3f.setIdentity();
        poseStack.poseStack.add(new Pose(matrix4f, matrix3f));
        return poseStack;
    }

    public void translate(double x, double y, double z) {
        Pose pose = this.poseStack.getLast();
        pose.pose.multiplyWithTranslation((float) x, (float) y, (float) z);
    }

    public Pose scale(float x, float y, float z) {
        Pose pose = this.poseStack.getLast();
        pose.pose.multiply(Matrix4.createScaleMatrix(x, y, z));
        if (x == y && y == z) {
            if (x > 0.0F)
                return pose;
            pose.normal.mul(-1.0F);
        }
        float xP = 1.0F / x;
        float yP = 1.0F / y;
        float zP = 1.0F / z;
        float cb = Mth.fastInvCubeRoot(xP * yP * zP);
        pose.normal.mul(Matrix3.createScaleMatrix(cb * xP, cb * yP, cb * zP));
        return pose;
    }

    public void mulPose(Quaternion quaternion) {
        Pose pose = this.poseStack.getLast();
        pose.pose.multiply(quaternion);
        pose.normal.mul(quaternion);
    }

    public void pushPose() {
        Pose pose = this.poseStack.getLast();
        this.poseStack.addLast(new Pose(pose.pose.copy(), pose.normal.copy()));
    }

    public void popPose() {
        this.poseStack.removeLast();
    }

    public Pose last() {
        return this.poseStack.getLast();
    }

    public boolean clear() {
        return (this.poseStack.size() == 1);
    }

    public void setIdentity() {
        Pose pose = this.poseStack.getLast();
        pose.pose.setIdentity();
        pose.normal.setIdentity();
    }

    public void mulPoseMatrix(Matrix4 matrix4f) {
        this.poseStack.getLast().pose.multiply(matrix4f);
    }

    public static final class Pose {
        final Matrix4 pose;

        final Matrix3 normal;

        Pose(Matrix4 matrix4f, Matrix3 matrix3f) {
            this.pose = matrix4f;
            this.normal = matrix3f;
        }

        public Matrix4 pose() {
            return this.pose;
        }

        public Matrix3 normal() {
            return this.normal;
        }
    }
}