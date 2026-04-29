package game.util.geometry;

import com.raylib.Raylib;

public class Vec4f {

    private float x = 0;
    private float y = 0;
    private float z = 0;
    private float w = 0;

    public Vec4f x(float x) {
        this.x = x;
        return this;
    }

    public Vec4f y(float y) {
        this.y = y;
        return this;
    }

    public Vec4f z(float z) {
        this.z = z;
        return this;
    }

    public Vec4f w(float w) {
        this.w = w;
        return this;
    }

    public Vec4f r(byte r) {
        this.x = (float) r;
        return this;
    }

    public Vec4f g(byte g) {
        this.y = (float) g;
        return this;
    }

    public Vec4f b(byte b) {
        this.z = (float) b;
        return this;
    }

    public Vec4f a(byte a) {
        this.w = (float) a;
        return this;
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

    public float w() {
        return this.w;
    }

    public byte r() {
        return (byte) this.x;
    }

    public byte g() {
        return (byte) this.y;
    }

    public byte b() {
        return (byte) this.z;
    }

    public byte a() {
        return (byte) this.w;
    }

    public Raylib.Vector4 toVector4() {
        return new Raylib.Vector4().x(x).y(y).z(z).w(w);
    }

    public Raylib.Color toColor() {
        return new Raylib.Color()
            .r((byte) x)
            .g((byte) y)
            .b((byte) z)
            .a((byte) w);
    }

    public Vec4f(float x, float y, float z, float w) {
        this.x(x);
        this.y(y);
        this.z(z);
        this.w(w);
    }

    public Vec4f() {}

    public Vec4f(Raylib.Vector4 vector4) {
        this(vector4.x(), vector4.y(), vector4.z(), vector4.w());
    }

    public Vec4f(Raylib.Color color) {
        this(
            (float) color.r(),
            (float) color.g(),
            (float) color.b(),
            (float) color.a()
        );
    }

    public boolean equals(Vec4f other) {
        if (other == null) {
            return false;
        }
        return (
            this.x() == other.x() &&
            this.y() == other.y() &&
            this.z() == other.z() &&
            this.w() == other.w()
        );
    }

    public Vec4f clone() {
        return new Vec4f(this.x(), this.y(), this.z(), this.w());
    }
}
