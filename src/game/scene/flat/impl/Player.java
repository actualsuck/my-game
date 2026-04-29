package game.scene.flat.impl;

import static com.raylib.Raylib.IsKeyDown;
import static com.raylib.Raylib.KEY_A;
import static com.raylib.Raylib.KEY_D;
import static com.raylib.Raylib.KEY_S;
import static com.raylib.Raylib.KEY_W;
import static com.raylib.Raylib.LoadImageFromMemory;
import static game.scene.flat.FlatObjectRegistry.deserializeVec2f;
import static game.util.geometry.Lerp.lerp;

import com.raylib.Colors;
import com.raylib.Raylib.Image;
import game.Game;
import game.primitive.SchedulerTask;
import game.scene.flat.FlatObjectRegistry;
import game.scene.flat.FlatScene;
import game.scene.flat.primitive.BoxCollider;
import game.scene.flat.primitive.Collidable;
import game.scene.flat.primitive.FlatObject;
import game.scene.flat.primitive.Sprite;
import game.util.Pair;
import game.util.geometry.Rect;
import game.util.geometry.Vec2f;
import game.util.geometry.Vec4f;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player extends BoxCollider {

    private Sprite idle;
    private Sprite walk;
    private int frameInterval;

    private int idleFrame;
    private int walkFrame;

    private boolean walkFlip;

    private enum FrameType {
        IDLE,
        WALK,
    }

    private FrameType currentFrame;

    public Player(
        Vec2f position,
        Vec2f size,
        Sprite idle,
        Sprite walk,
        int frameInterval
    ) {
        super("player", "player", position, size);
        this.idle = idle;
        this.walk = walk;
        this.frameInterval = frameInterval;
    }

    private static Sprite loadSprite(String resource, int count) {
        Pair<ByteBuffer, Integer> data = Game.getInstance()
            .getResourceLoader()
            .loadResourceData(resource);
        Image image = LoadImageFromMemory(
            ".png",
            data.getFirst(),
            data.getSecond()
        );
        return new Sprite(image, count);
    }

    static {
        FlatObjectRegistry.addRegistry("player", data -> {
            Vec2f pos = deserializeVec2f(data.get("pos"));
            Vec2f size = deserializeVec2f(data.get("size"));

            int frame_interval = (Integer) data.get("frame_interval");
            String idle_sprite = (String) data.get("idle_sprite");
            int idle_count = (Integer) data.get("idle_count");
            String walk_sprite = (String) data.get("walk_sprite");
            int walk_count = (Integer) data.get("walk_count");

            return new Player(
                pos,
                size,
                loadSprite(idle_sprite, idle_count),
                loadSprite(walk_sprite, walk_count),
                frame_interval
            );
        });
    }

    @Override
    public Vec2f onCollideWeight(Vec2f vel, Collidable collidable) {
        return new Vec2f();
    }

    SchedulerTask spriteTask;

    public void onStart(FlatScene scene) {
        this.scene = scene;

        spriteTask = new SchedulerTask() {
            public void run() {
                spriteTick();
            }
        };

        Game.getInstance()
            .getScheduler()
            .scheduleTask(0, frameInterval, -1, spriteTask);
    }

    public void onEnd() {
        spriteTask.setTimes(0);
        walk.unload();
        idle.unload();
    }

    public void draw() {
        // DrawRectangleLinesEx(getRect().toRectangle(), 2, Colors.BLACK);

        switch (currentFrame) {
            case FrameType.IDLE:
                idle.drawPro(
                    idleFrame,
                    walkFlip,
                    idle.getRect(),
                    getRect(),
                    new Vec2f(),
                    0,
                    new Vec4f(Colors.WHITE)
                );
                break;
            case FrameType.WALK:
                walk.drawPro(
                    walkFrame,
                    walkFlip,
                    walk.getRect(),
                    getRect(),
                    new Vec2f(),
                    0,
                    new Vec4f(Colors.WHITE)
                );
                break;
        }
    }

    public void spriteTick() {
        idleFrame++;
        if (idleFrame == idle.getCount()) idleFrame = 0;
        walkFrame++;
        if (walkFrame == walk.getCount()) walkFrame = 0;
    }

    public void tick() {
        scene
            .getCamera()
            .target(
                lerp(
                    new Vec2f(scene.getCamera().target()),
                    getRect().center(),
                    0.125f
                ).toVector2()
            );

        float x = 0,
            y = 0;

        if (IsKeyDown(KEY_W)) y -= 10;
        if (IsKeyDown(KEY_S)) y += 10;
        if (IsKeyDown(KEY_A)) x -= 10;
        if (IsKeyDown(KEY_D)) x += 10;

        move(new Vec2f(x, y));
    }

    public void move(Vec2f vel) {
        if (vel.y() == 0 && vel.x() == 0) currentFrame = FrameType.IDLE;
        else {
            currentFrame = FrameType.WALK;
            if (vel.x() > 0) walkFlip = true;
            else if (vel.x() < 0) walkFlip = false;
        }

        Rect future = new Rect(position.add(vel), size);
        Vec2f min_vel = vel;

        Set<Collidable> move_then = new HashSet<>();

        for (FlatObject object : scene.getObjects().getList()) {
            if (object == this) continue;
            if (object instanceof Collidable collidable) {
                if (collidable.getRect().collide(future)) {
                    Pair<Vec2f, Set<Collidable>> res = collidable.onCollide(
                        vel,
                        this
                    );
                    Vec2f possible_vel = res.getFirst();
                    // System.out.println(
                    //     "possible vel: " +
                    //         possible_vel.x() +
                    //         " " +
                    //         possible_vel.y()
                    // );
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

        for (Collidable object : move_then) {
            object.move(min_vel);
        }

        position.x(position.x() + min_vel.x());
        position.y(position.y() + min_vel.y());
    }
}
