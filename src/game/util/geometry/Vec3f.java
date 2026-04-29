package game.util.geometry;

import com.raylib.Raylib;

public class Vec3f {

    private float x = 0;
    private float y = 0;
    private float z = 0;

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float z() {
        return z;
    }

    public Vec3f x(float x) {
        this.x = x;
        return this;
    }

    public Vec3f y(float y) {
        this.y = y;
        return this;
    }

    public Vec3f z(float z) {
        this.z = z;
        return this;
    }

    public Raylib.Vector3 toVector3() {
        return new Raylib.Vector3().x(x).y(y).z(z);
    }

    public Vec2f toXY() {
        return new Vec2f(x, y);
    }

    public Vec2f toXZ() {
        return new Vec2f(x, z);
    }

    public Vec2f toYZ() {
        return new Vec2f(y, z);
    }

    public Vec2f toYX() {
        return new Vec2f(y, x);
    }

    public Vec2f toZX() {
        return new Vec2f(z, x);
    }

    public Vec2f toZY() {
        return new Vec2f(z, y);
    }

    public Vec3f(float x, float y, float z) {
        this.x(x);
        this.y(y);
        this.z(z);
    }

    public Vec3f() {}

    public Vec3f(Raylib.Vector3 vector3) {
        this(vector3.x(), vector3.y(), vector3.z());
    }

    public boolean equals(Vec3f other) {
        if (other == null) {
            return false;
        }
        return (
            this.x() == other.x() &&
            this.y() == other.y() &&
            this.z() == other.z()
        );
    }

    public Vec3f clone() {
        return new Vec3f(this.x(), this.y(), this.z());
    }

    public Vec3f add(Vec3f other) {
        return new Vec3f(
            this.x() + other.x(),
            this.y() + other.y(),
            this.z() + other.z()
        );
    }

    public Vec3f add(float x, float y, float z) {
        return new Vec3f(this.x() + x, this.y() + y, this.z() + z);
    }

    public Vec3f add(float a) {
        return new Vec3f(this.x() + a, this.y() + a, this.z() + a);
    }

    public Vec3f sub(Vec3f other) {
        return new Vec3f(
            this.x() - other.x(),
            this.y() - other.y(),
            this.z() - other.z()
        );
    }

    public Vec3f sub(float x, float y, float z) {
        return new Vec3f(this.x() - x, this.y() - y, this.z() - z);
    }

    public Vec3f sub(float a) {
        return new Vec3f(this.x() - a, this.y() - a, this.z() - a);
    }

    public Vec3f mul(Vec3f other) {
        return new Vec3f(
            this.x() * other.x(),
            this.y() * other.y(),
            this.z() * other.z()
        );
    }

    public Vec3f mul(float x, float y, float z) {
        return new Vec3f(this.x() * x, this.y() * y, this.z() * z);
    }

    public Vec3f mul(float a) {
        return new Vec3f(this.x() * a, this.y() * a, this.z() * a);
    }

    public Vec3f div(Vec3f other) {
        return new Vec3f(
            this.x() / other.x(),
            this.y() / other.y(),
            this.z() / other.z()
        );
    }

    public Vec3f div(float x, float y, float z) {
        return new Vec3f(this.x() / x, this.y() / y, this.z() / z);
    }

    public Vec3f div(float a) {
        return new Vec3f(this.x() / a, this.y() / a, this.z() / a);
    }

    public Vec3f mod(float a) {
        return new Vec3f(this.x() % a, this.y() % a, this.z() % a);
    }

    public Vec3f neg() {
        return new Vec3f(-this.x(), -this.y(), -this.z());
    }

    public Vec3f normalize() {
        float length = (float) Math.sqrt(
            this.x() * this.x() + this.y() * this.y() + this.z() * this.z()
        );

        if (length == 0) return new Vec3f(0, 0, 0);

        return new Vec3f(
            this.x() / length,
            this.y() / length,
            this.z() / length
        );
    }
}
