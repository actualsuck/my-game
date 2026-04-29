package game.util.geometry;

import com.raylib.Raylib;
import java.util.Objects;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Rect {

    Vec2f pos;
    Vec2f size;

    public Rect(float x, float y, float width, float height) {
        this.pos = new Vec2f(x, y);
        this.size = new Vec2f(width, height);
    }

    public Rect() {
        this.pos = new Vec2f();
        this.size = new Vec2f();
    }

    public Vec2f pos() {
        return pos;
    }

    public Vec2f size() {
        return size;
    }

    public Rect pos(Vec2f pos) {
        this.pos = pos;
        return this;
    }

    public Rect size(Vec2f size) {
        this.size = size;
        return this;
    }

    public float right() {
        return pos.x() + size.x();
    }

    public float left() {
        return pos.x();
    }

    public float bottom() {
        return pos.y() + size.y();
    }

    public float top() {
        return pos.y();
    }

    public Vec2f center() {
        return pos.add(size.div(2));
    }

    public float x() {
        return pos.x();
    }

    public float y() {
        return pos.y();
    }

    public float width() {
        return size.x();
    }

    public float height() {
        return size.y();
    }

    public Rect x(float x) {
        this.pos.x(x);
        return this;
    }

    public Rect y(float y) {
        this.pos.y(y);
        return this;
    }

    public Rect width(float width) {
        this.size.x(width);
        return this;
    }

    public Rect height(float height) {
        this.size.y(height);
        return this;
    }

    public Rect clone() {
        return new Rect(pos, size);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Rect rect)) return false;
        return Objects.equals(pos, rect.pos) && Objects.equals(size, rect.size);
    }

    public boolean contains(Vec2f point) {
        return (
            point.x() >= x() &&
            point.x() < x() + width() &&
            point.y() >= y() &&
            point.y() < y() + height()
        );
    }

    public boolean collide(Rect rect) {
        return (
            x() < rect.x() + rect.width() &&
            x() + width() > rect.x() &&
            y() < rect.y() + rect.height() &&
            y() + height() > rect.y()
        );
    }

    public Raylib.Rectangle toRectangle() {
        return new Raylib.Rectangle()
            .x(x())
            .y(y())
            .width(width())
            .height(height());
    }
}
