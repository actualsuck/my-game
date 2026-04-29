package game.scene.impl.test;

import static com.raylib.Raylib.*;
import static com.raylib.Raylib.EndMode3D;
import static game.util.ModelUtils.DrawGridEx;
import static game.util.geometry.Lerp.lerp;

import com.raylib.Colors;
import game.Game;
import game.animation.Toast;
import game.event.Listener;
import game.primitive.SchedulerTask;
import game.scene.Scene;
import game.scene.impl.main_menu.MainMenuScene;
import game.scene.impl.test_flat.TestFlatScene;
import game.util.geometry.Rect;
import game.util.geometry.Vec2f;
import game.util.geometry.Vec3f;
import game.util.geometry.Vec4f;

public class TestScene implements Scene, Listener {

    Model model;
    Camera3D camera;

    float fovy;
    float jump;
    boolean isJumping;

    Color red;
    Color purple;
    Color dark_purple;
    Color white;

    float size = 4;
    int no_the_toast = 0;

    Texture[] keyTexture;

    @Override
    public void onStart() {
        fovy = 100;

        camera = new Camera3D()
            ._position(new Vector3().x(10).y(10).z(2))
            .target(new Vector3().x(-5).y(11).z(1))
            .up(new Vector3().x(0).y(1).z(0))
            .fovy(fovy)
            .projection(CAMERA_PERSPECTIVE);

        model = LoadModel(
            Game.getInstance()
                .getResourceLoader()
                .loadResourceFile("assets/models/Untitled.glb")
                .getPath()
        );

        Game.getInstance().getListenerExecutor().subscribeListener(this);

        red = Colors.RED;
        purple = Colors.PURPLE;
        dark_purple = Colors.DARKPURPLE;
        white = Colors.WHITE;

        Image keyImage = Game.getInstance()
            .getResourceLoader()
            .loadPngImage("assets/images/HappyKey.png");
        keyTexture = new Texture[] { LoadTextureFromImage(keyImage) };
        UnloadImage(keyImage);

        HideCursor();
        DisableCursor();
        SetMouseOffset(GetRenderWidth() / 2, GetRenderHeight() / 2);
    }

    @Override
    public void onEnd() {
        Game.getInstance().getListenerExecutor().unsubscribeListener(this);

        UnloadModel(model);
        ShowCursor();
        EnableCursor();
        SetMouseOffset(0, 0);

        UnloadTexture(keyTexture[0]);
    }

    public void tick() {
        if (isJumping) {
            jump /= 1.1;

            if (jump < 0.5 && jump > 0) {
                jump = -2 + jump;
            }

            jump = Math.max(jump, 10 - camera._position().y());

            if (jump == 0) {
                isJumping = false;
            }
        } else {
            jump = 0;
        }

        camera._position().y(camera._position().y() + jump);
        camera.target().y(camera.target().y() + jump);

        camera.fovy(lerp(camera.fovy(), fovy, 0.2f));
        UpdateCameraPro(
            camera,
            new Vec3f(
                (IsKeyDown(KEY_W) || IsKeyDown(KEY_UP) ? 0.5f : 0) - // Move forward-backward
                    (IsKeyDown(KEY_S) || IsKeyDown(KEY_DOWN) ? 0.5f : 0),
                (IsKeyDown(KEY_D) || IsKeyDown(KEY_RIGHT) ? 0.5f : 0) - // Move right-left
                    (IsKeyDown(KEY_A) || IsKeyDown(KEY_LEFT) ? 0.5f : 0),
                0 // Move up-down
            ).toVector3(),
            new Vec3f(
                GetMouseDelta().x() * 0.5f, // Rotation: yaw
                GetMouseDelta().y() * 0.5f, // Rotation: pitch
                0.0f // Rotation: roll
            ).toVector3(),
            0f
            // GetMouseWheelMove() * 2.0f
        ); // Move to target (zoom)
    }

    public void draw() {
        ClearBackground(dark_purple);

        BeginMode3D(camera);
        DrawGridEx(30, 2.0f, purple, purple, new Vec3f(0, 0, 0));
        // DrawCubeWires(camera.target(), 1, 1, 1, red);
        DrawModel(model, new Vec3f(0, 0, 0).toVector3(), size, white);
        if (Game.getStorageData().isHasKey1() && !alreadyMoving) {
            DrawBillboard(
                camera,
                keyTexture[0],
                new Vec3f(50, 10, 50).toVector3(),
                10.0f,
                white
            );
        }
        EndMode3D();

        if (keyNearby() && !alreadyMoving) {
            DrawFPS(10, 10);
            if (no_the_toast == 0) Toast.drawToast(
                "wanna use the key? click a button IDUNNO",
                new Vec4f(Colors.DARKBLUE),
                new Vec4f(Colors.BLUE).a((byte) 64)
            );
            camera.target(new Vec3f(50, 10, 50).toVector3());

            Rect rect = new Rect(0, 0, GetScreenWidth(), GetScreenHeight());

            Vec2f crosshire = rect.center();

            DrawLineEx(
                crosshire.add(0, -5).toVector2(),
                crosshire.add(0, 5).toVector2(),
                2,
                Colors.RED
            );
            DrawLineEx(
                crosshire.add(-5, 0).toVector2(),
                crosshire.add(5, 0).toVector2(),
                2,
                Colors.RED
            );
        }
    }

    boolean alreadyMoving = false;

    @Override
    public void onKeyDown(int keyCode, char key) {
        if (keyCode == KEY_C) {
            fovy = 20;
        }

        if (keyCode == KEY_ESCAPE && !alreadyMoving) {
            Game.getInstance().switchScene(new MainMenuScene());
            // moving to your godass scene
        }

        if (keyCode == KEY_SPACE && !isJumping) {
            jump = 2;
            isJumping = true;
        }
    }

    @Override
    public void onKeyUp(int keyCode, char key) {
        if (keyCode == KEY_C) {
            fovy = 100;
        }
    }

    public boolean keyNearby() {
        if (Game.getStorageData().isHasKey1()) {
            Vec3f howmuch = new Vec3f(50, 10, 50).sub(
                new Vec3f(camera._position())
            );
            double distance = Math.sqrt(
                Math.abs(howmuch.x()) +
                    Math.abs(howmuch.y()) +
                    Math.abs(howmuch.z())
            );
            if (distance < 5) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMouseDown(int button) {
        if (keyNearby() && !alreadyMoving) {
            no_the_toast = 1;
            alreadyMoving = true;
            Game.getInstance().addAnimation(
                new Toast("ok moving you into The SPACE ...\n...")
            );
            Game.getInstance()
                .getScheduler()
                .scheduleTask(
                    5 * 60,
                    0,
                    1,
                    new SchedulerTask() {
                        public void run() {
                            Game.getInstance().switchScene(new TestFlatScene()); // switch to flat scene
                        }
                    }
                );
        }
    }
}
