package game.util.geometry;

import com.raylib.Raylib;

public class Vec2f {

    private float x = 0;
    private float y = 0;

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public Vec2f x(float x) {
        this.x = x;
        return this;
    }

    public Vec2f y(float y) {
        this.y = y;
        return this;
    }

    public Raylib.Vector2 toVector2() {
        return new Raylib.Vector2().x(x).y(y);
    }

    public Vec2f(float x, float y) {
        super();

        this.x(x);
        this.y(y);
    }

    public Vec2f() {}

    public Vec2f(Raylib.Vector2 vector2) {
        this(vector2.x(), vector2.y());
    }

    public boolean equals(Vec2f other) {
        if (other == null) {
            return false;
        }
        return this.x() == other.x() && this.y() == other.y();
    }

    public Vec2f clone() {
        return new Vec2f(this.x(), this.y());
    }

    public Vec2f add(Vec2f other) {
        return new Vec2f(this.x() + other.x(), this.y() + other.y());
    }

    public Vec2f add(float x, float y) {
        return new Vec2f(this.x() + x, this.y() + y);
    }

    public Vec2f add(float a) {
        return new Vec2f(this.x() + a, this.y() + a);
    }

    public Vec2f sub(Vec2f other) {
        return new Vec2f(this.x() - other.x(), this.y() - other.y());
    }

    public Vec2f sub(float x, float y) {
        return new Vec2f(this.x() - x, this.y() - y);
    }

    public Vec2f sub(float a) {
        return new Vec2f(this.x() - a, this.y() - a);
    }

    public Vec2f mul(Vec2f other) {
        return new Vec2f(this.x() * other.x(), this.y() * other.y());
    }

    public Vec2f mul(float x, float y) {
        return new Vec2f(this.x() * x, this.y() * y);
    }

    public Vec2f mul(float a) {
        return new Vec2f(this.x() * a, this.y() * a);
    }

    public Vec2f div(Vec2f other) {
        return new Vec2f(this.x() / other.x(), this.y() / other.y());
    }

    public Vec2f div(float x, float y) {
        return new Vec2f(this.x() / x, this.y() / y);
    }

    public Vec2f div(float a) {
        return new Vec2f(this.x() / a, this.y() / a);
    }

    public Vec2f neg() {
        return new Vec2f(-this.x(), -this.y());
    }

    public Vec2f normalize() {
        float length = (float) Math.sqrt(
            this.x() * this.x() + this.y() * this.y()
        );
        if (length == 0) return new Vec2f(0, 0);
        return new Vec2f(this.x() / length, this.y() / length);
    }
}
