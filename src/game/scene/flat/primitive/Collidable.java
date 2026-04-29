package game.scene.flat.primitive;

import game.util.Pair;
import game.util.geometry.Rect;
import game.util.geometry.Vec2f;
import java.util.Set;

public interface Collidable {
    Rect getRect();
    Pair<Vec2f, Set<Collidable>> onCollide(Vec2f vel, Collidable collidable); // collide -> get possible moving
    void move(Vec2f vel); // force move
}
