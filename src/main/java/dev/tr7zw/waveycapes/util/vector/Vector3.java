package dev.tr7zw.waveycapes.util.vector;

import dev.tr7zw.waveycapes.util.Mth;

@SuppressWarnings("unused")
public final class Vector3 {

    public static Vector3 XN = new Vector3(-1.0F, 0.0F, 0.0F);

    public static Vector3 XP = new Vector3(1.0F, 0.0F, 0.0F);

    public static Vector3 YN = new Vector3(0.0F, -1.0F, 0.0F);

    public static Vector3 YP = new Vector3(0.0F, 1.0F, 0.0F);

    public static Vector3 ZN = new Vector3(0.0F, 0.0F, -1.0F);

    public static Vector3 ZP = new Vector3(0.0F, 0.0F, 1.0F);

    public static Vector3 ZERO = new Vector3(0.0F, 0.0F, 0.0F);

    private float x;

    private float y;

    private float z;

    public Vector3() {
    }

    public Vector3(float f, float g, float h) {
        this.x = f;
        this.y = g;
        this.z = h;
    }

    public Vector3(Vector4 vector4f) {
        this(vector4f.x(), vector4f.y(), vector4f.z());
    }

    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        Vector3 vector3f = (Vector3) object;
        if (Float.compare(vector3f.x, this.x) != 0)
            return false;
        if (Float.compare(vector3f.y, this.y) != 0)
            return false;
        return (Float.compare(vector3f.z, this.z) == 0);
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.x);
        i = 31 * i + Float.floatToIntBits(this.y);
        i = 31 * i + Float.floatToIntBits(this.z);
        return i;
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public float z() {
        return this.z;
    }

    public void mul(float f) {
        this.x *= f;
        this.y *= f;
        this.z *= f;
    }

    public void mul(float f, float g, float h) {
        this.x *= f;
        this.y *= g;
        this.z *= h;
    }

    public void clamp(Vector3 vector3f, Vector3 vector3f2) {
        this.x = Mth.clamp(this.x, vector3f.x(), vector3f2.x());
        this.y = Mth.clamp(this.y, vector3f.x(), vector3f2.y());
        this.z = Mth.clamp(this.z, vector3f.z(), vector3f2.z());
    }

    public void clamp(float f, float g) {
        this.x = Mth.clamp(this.x, f, g);
        this.y = Mth.clamp(this.y, f, g);
        this.z = Mth.clamp(this.z, f, g);
    }

    public void set(float f, float g, float h) {
        this.x = f;
        this.y = g;
        this.z = h;
    }

    public void load(Vector3 vector3f) {
        this.x = vector3f.x;
        this.y = vector3f.y;
        this.z = vector3f.z;
    }

    public void add(float f, float g, float h) {
        this.x += f;
        this.y += g;
        this.z += h;
    }

    public void add(Vector3 vector3f) {
        this.x += vector3f.x;
        this.y += vector3f.y;
        this.z += vector3f.z;
    }

    public void sub(Vector3 vector3f) {
        this.x -= vector3f.x;
        this.y -= vector3f.y;
        this.z -= vector3f.z;
    }

    public float dot(Vector3 vector3f) {
        return this.x * vector3f.x + this.y * vector3f.y + this.z * vector3f.z;
    }

    public boolean normalize() {
        float f = this.x * this.x + this.y * this.y + this.z * this.z;
        if (f < 1.0E-5D)
            return false;
        float g = Mth.fastInvSqrt(f);
        this.x *= g;
        this.y *= g;
        this.z *= g;
        return true;
    }

    public void cross(Vector3 vector3f) {
        float f = this.x;
        float g = this.y;
        float h = this.z;
        float i = vector3f.x();
        float j = vector3f.y();
        float k = vector3f.z();
        this.x = g * k - h * j;
        this.y = h * i - f * k;
        this.z = f * j - g * i;
    }

    public void transform(Quaternion quaternion) {
        Quaternion quaternion2 = new Quaternion(quaternion);
        quaternion2.mul(new Quaternion(x(), y(), z(), 0.0F));
        Quaternion quaternion3 = new Quaternion(quaternion);
        quaternion3.conj();
        quaternion2.mul(quaternion3);
        set(quaternion2.i(), quaternion2.j(), quaternion2.k());
    }

    public void lerp(Vector3 vector3f, float f) {
        float g = 1.0F - f;
        this.x = this.x * g + vector3f.x * f;
        this.y = this.y * g + vector3f.y * f;
        this.z = this.z * g + vector3f.z * f;
    }

    public Quaternion rotation(float f) {
        return new Quaternion(this, f, false);
    }

    public Quaternion rotationDegrees(float f) {
        return new Quaternion(this, f, true);
    }

    public Vector3 copy() {
        return new Vector3(this.x, this.y, this.z);
    }

    public String toString() {
        return String.format("[%s, %s, %s]", this.x, this.y, this.z);
    }
}