package game.scene.flat.primitive;

import game.scene.flat.FlatScene;
import game.util.Pair;
import game.util.geometry.Rect;
import game.util.geometry.Vec2f;
import java.util.HashSet;
import java.util.Set;

public abstract class BoxCollider extends FlatObject implements Collidable {

    protected Vec2f position;
    protected Vec2f size;

    public BoxCollider(String type, String id, Vec2f position, Vec2f size) {
        super(type, id);
        this.position = position;
        this.size = size;
    }

    @Override
    public Rect getRect() {
        return new Rect(position, size);
    }

    @Override
    public Pair<Vec2f, Set<Collidable>> onCollide(
        Vec2f vel,
        Collidable initiator
    ) {
        Vec2f weight = onCollideWeight(vel, initiator);
        // System.out.println("weight: " + weight.x() + " " + weight.y());
        Rect future = new Rect(position.add(weight), size);
        Vec2f min_vel = weight;
        Set<Collidable> move_then = new HashSet<>();

        for (FlatObject object : scene.getObjects().getList()) {
            if (object == this) continue;
            if (object instanceof Collidable collidable) {
                if (collidable == initiator) continue;
                if (collidable.getRect().collide(future)) {
                    Pair<Vec2f, Set<Collidable>> res = collidable.onCollide(
                        vel,
                        this
                    );
                    Vec2f possible_vel = res.getFirst();
                    move_then.addAll(res.getSecond());
                    move_then.add(collidable);
                    if (
                        Math.abs(possible_vel.x()) < Math.abs(min_vel.x()) ||
                        Math.abs(possible_vel.y()) < Math.abs(min_vel.y())
                    ) {
                        min_vel = possible_vel;
                    }
                }
            }
        }

        return new Pair<>(min_vel, move_then);
    }

    public abstract Vec2f onCollideWeight(Vec2f vel, Collidable collidable);

    protected FlatScene scene;

    @Override
    public void onStart(FlatScene scene) {
        this.scene = scene;
    }
}
