package game.scene.flat.impl;

import static com.raylib.Raylib.DrawRectangleRec;
import static game.scene.flat.FlatObjectRegistry.deserializeColor;
import static game.scene.flat.FlatObjectRegistry.deserializeVec2f;

import game.scene.flat.FlatObjectRegistry;
import game.scene.flat.primitive.BoxCollider;
import game.scene.flat.primitive.Collidable;
import game.util.geometry.Rect;
import game.util.geometry.Vec2f;
import game.util.geometry.Vec4f;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolidBox extends BoxCollider {

    private Vec4f color;
    private int weight;

    public SolidBox(
        String id,
        Vec2f position,
        Vec2f size,
        Vec4f color,
        int weight
    ) {
        super("solid_box", id, position, size);
        this.weight = weight;
        this.color = color;
    }

    static {
        FlatObjectRegistry.addRegistry("solid_box", data -> {
            String id = (String) data.get("id");
            Vec2f pos = deserializeVec2f(data.get("pos"));
            Vec2f size = deserializeVec2f(data.get("size"));
            Vec4f color = deserializeColor(data.get("color"));
            int weight = ((Number) data.get("weight")).intValue();

            return new SolidBox(id, pos, size, color, weight);
        });
    }

    public Rect getRect() {
        return new Rect(position, size);
    }

    public void draw() {
        DrawRectangleRec(getRect().toRectangle(), color.toColor());
    }

    @Override
    public void onEnd() {}

    @Override
    public Vec2f onCollideWeight(Vec2f vel, Collidable collidable) {
        // System.out.println("weight " + weight);
        // System.out.println("weigth == 0 " + (weight == 0));
        if (weight == 0) return new Vec2f();
        return new Vec2f(vel.x() / weight, vel.y() / weight);
    }

    public void tick() {}

    @Override
    public void move(Vec2f vel) {
        position.x(position.x() + vel.x());
        position.y(position.y() + vel.y());
    }
}
