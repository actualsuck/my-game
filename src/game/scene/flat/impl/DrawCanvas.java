package game.scene.flat.impl;

import static com.raylib.Raylib.DrawLineV;
import static com.raylib.Raylib.GetMousePosition;
import static com.raylib.Raylib.GetScreenHeight;
import static com.raylib.Raylib.GetScreenToWorld2D;
import static com.raylib.Raylib.GetScreenWidth;
import static com.raylib.Raylib.GetWorldToScreen2D;
import static com.raylib.Raylib.IsMouseButtonDown;
import static com.raylib.Raylib.IsMouseButtonReleased;
import static com.raylib.Raylib.MOUSE_BUTTON_LEFT;
import static game.scene.flat.FlatObjectRegistry.deserializeColor;

import game.scene.flat.FlatObjectRegistry;
import game.scene.flat.FlatScene;
import game.scene.flat.primitive.FlatObject;
import game.util.geometry.Vec2f;
import game.util.geometry.Vec4f;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DrawCanvas extends FlatObject {

    private List<Set<Vec2f>> done = new ArrayList<>();
    private Set<Vec2f> points = new LinkedHashSet<>();

    private Vec4f color;

    public DrawCanvas(String id, Vec4f color) {
        super("canvas", id);
        this.color = color;
    }

    static {
        FlatObjectRegistry.addRegistry("canvas", data -> {
            return new DrawCanvas(
                (String) data.get("id"),
                deserializeColor(data.get("color"))
            );
        });
    }

    @Override
    public void onEnd() {}

    FlatScene scene;

    @Override
    public void onStart(FlatScene scene) {
        this.scene = scene;
    }

    public Set<Vec2f> drawOne(Set<Vec2f> set) {
        Iterator<Vec2f> iterator = set.iterator();
        Set<Vec2f> to_remove = new HashSet<>();
        Vec2f prev = null,
            real,
            now;

        while (iterator.hasNext()) {
            now = iterator.next();
            real = new Vec2f(
                GetWorldToScreen2D(now.toVector2(), scene.getCamera())
            );
            if (
                real.x() < 0 ||
                real.x() > GetScreenWidth() ||
                real.y() < 0 ||
                real.y() > GetScreenHeight()
            ) {
                to_remove.add(now);
            } else {
                if (prev != null) DrawLineV(
                    prev.toVector2(),
                    now.toVector2(),
                    color.toColor()
                );
                prev = now;
            }
        }
        return to_remove;
    }

    public void draw() {
        for (Set<Vec2f> set : done) {
            set.removeAll(drawOne(set));
        }
        points.removeAll(drawOne(points));
    }

    public void tick() {
        if (IsMouseButtonDown(MOUSE_BUTTON_LEFT)) {
            points.add(
                new Vec2f(
                    GetScreenToWorld2D(GetMousePosition(), scene.getCamera())
                )
            );
        }
        if (IsMouseButtonReleased(MOUSE_BUTTON_LEFT)) {
            done.add(new HashSet<>(points));
            points = new LinkedHashSet<>();
        }
    }
}
