package game.scene.impl.main_menu;

import static com.raylib.Raylib.*;
import static game.util.ModelUtils.DrawCubeWires;
import static game.util.ModelUtils.DrawGridEx;
import static game.util.geometry.Lerp.lerp;

import com.raylib.Colors;
import game.Game;
import game.event.Listener;
import game.primitive.SharedData;
import game.scene.Scene;
import game.scene.impl.test.TestScene;
import game.scene.impl.test_novel.TestNovelScene;
import game.util.geometry.Vec2f;
import game.util.geometry.Vec3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

public class MainMenuScene implements Scene, Listener {

    @Getter
    Camera3D camera;

    Map<Integer, CameraState> cameraStates;

    Font font;

    int selected;
    List<MenuButton> buttons;

    Vec2f wobbling;

    boolean catched;

    Vec3f rotation;

    List<SillyCube> sillyCubes;

    public SillyCube createSillyCube() {
        SillyCube sillyCube = new SillyCube(
            new Vec3f(),
            new Vec3f(),
            new Vec3f()
        );
        randomSillyCube(sillyCube);
        return sillyCube;
    }

    public void moveSillyCube(SillyCube sillyCube) {
        if (
            sillyCube.vel.x() + sillyCube.vel.y() + sillyCube.vel.z() < 0.1 ||
            sillyCube.pos.x() > 30 ||
            sillyCube.pos.y() > 20 ||
            sillyCube.pos.z() > 30
        ) {
            randomSillyCube(sillyCube);
        }
        sillyCube.pos.x(
            lerp(
                sillyCube.pos.x(),
                sillyCube.pos.x() + sillyCube.vel.x(),
                0.125f
            )
        );
        sillyCube.pos.y(
            lerp(
                sillyCube.pos.y(),
                sillyCube.pos.y() + sillyCube.vel.y(),
                0.125f
            )
        );
        sillyCube.pos.z(
            lerp(
                sillyCube.pos.z(),
                sillyCube.pos.z() + sillyCube.vel.z(),
                0.125f
            )
        );
        sillyCube.rot.x(
            lerp(
                sillyCube.rot.x(),
                sillyCube.rot.x() + sillyCube.vel.x() * 15,
                0.2f
            )
        );
        sillyCube.rot.y(
            lerp(
                sillyCube.rot.y(),
                sillyCube.rot.y() + sillyCube.vel.y() * 15,
                0.2f
            )
        );
        sillyCube.rot.z(
            lerp(
                sillyCube.rot.z(),
                sillyCube.rot.z() + sillyCube.vel.z() * 15,
                0.2f
            )
        );
        sillyCube.vel.x(lerp(sillyCube.vel.x(), 0, 0.05f));
        sillyCube.vel.y(lerp(sillyCube.vel.y(), 0, 0.05f));
        sillyCube.vel.z(lerp(sillyCube.vel.z(), 0, 0.05f));
    }

    public void randomSillyCube(SillyCube sillyCube) {
        sillyCube.pos.x((float) Math.random() * 60 - 30);
        sillyCube.pos.y((float) Math.random() * 10 - 5);
        sillyCube.pos.z((float) Math.random() * 60 - 30);
        sillyCube.rot.x((float) Math.random() * 2 - 1);
        sillyCube.rot.y((float) Math.random() * 2 - 1);
        sillyCube.rot.z((float) Math.random() * 2 - 1);
        sillyCube.vel.x((float) Math.random() * 10 - 5);
        sillyCube.vel.y((float) Math.random() * 10 - 5);
        sillyCube.vel.z((float) Math.random() * 10 - 5);
    }

    public void onStart() {
        font = SharedData.getInstance().getMenuFont();
        camera = new Camera3D()
            ._position(new Vector3().x(10).y(2).z(2))
            .target(new Vector3().x(-5).y(3).z(1))
            .up(new Vector3().x(0).y(1).z(0))
            .fovy(100)
            .projection(CAMERA_PERSPECTIVE);

        rotation = new Vec3f();
        sillyCubes = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            sillyCubes.add(createSillyCube());
        }

        wobbling = new Vec2f();

        cameraStates = new HashMap<>();
        cameraStates.put(
            -1,
            new CameraState(new Vec3f(-5f, 3, 1), new Vec3f(10f, 2, 2), 100)
        );

        selected = -1;

        catched = false;

        buttons = new ArrayList<>();

        Vec2f window_size = new Vec2f(GetScreenWidth(), GetScreenHeight());

        List<String> texts = List.of(
            "New game",
            "Load game",
            "Settings",
            "Bye!"
        );

        int i = 0;
        for (String text : texts) {
            Vec2f pos = new Vec2f();
            pos.x(50);
            pos.y(
                (window_size.y() / texts.size()) * i +
                    window_size.y() / texts.size() / 2
            );

            Map<Integer, MenuButtonState> states = new HashMap<>();

            states.put(
                -1,
                new MenuButtonState(60f + i * 5, pos.clone()).patchPosY(
                    font,
                    text
                )
            );

            pos.x(window_size.x() / 2);

            for (int selected = 0; selected < texts.size(); selected++) {
                float fontSize = 70f;
                Vec2f buttonPos = pos.clone();

                if (selected == i) {
                    // if button is selected

                    fontSize += 10;

                    if (i >= texts.size() / 2) {
                        buttonPos.y(
                            buttonPos.y() - 40 * (i - (float) texts.size() / 2)
                        );
                    } else {
                        buttonPos.y(
                            buttonPos.y() + 30 * ((float) texts.size() / 2 - i)
                        );
                    }
                } else if (selected > i) {
                    // if button is higher than selected one

                    fontSize += (selected - i) * 2;
                    buttonPos.y(buttonPos.y() - (selected - i) * 40 - 30);
                } else {
                    // if button is lower than selected one

                    fontSize -= (i - selected) * 5;
                    buttonPos.y(buttonPos.y() + (i - selected) * 40 + 30);
                }

                states.put(
                    selected,
                    new MenuButtonState(fontSize, buttonPos).patchPos(
                        font,
                        text
                    )
                );
            }

            buttons.add(
                new MenuButton(
                    text,
                    i,
                    states.get(-1).fontSize,
                    states.get(-1).pos,
                    Colors.GREEN,
                    states
                )
            );

            cameraStates.put(
                i,
                new CameraState(
                    new Vec3f(
                        -5f,
                        (float) (3 +
                            (float) (texts.size() - i - texts.size() / 2) /
                                1.5),
                        10
                    ),
                    new Vec3f(10f, 2, 2),
                    45
                )
            );

            i++;
        }

        Game.getInstance().getListenerExecutor().subscribeListener(this);
    }

    public void onEnd() {
        Game.getInstance().getListenerExecutor().unsubscribeListener(this);
    }

    public void tick() {
        rotation = lerp(rotation, new Vec3f(), 0.125f).add(
            wobbling.x(),
            wobbling.y(),
            wobbling.x() + wobbling.y()
        );

        CameraState cameraState = cameraStates.get(selected);

        camera._position(
            lerp(
                new Vec3f(camera._position()),
                cameraState.pos,
                0.125f
            ).toVector3()
        );
        camera.fovy(lerp(camera.fovy(), cameraState.fovy, 0.125f));
        camera.target(
            lerp(new Vec3f(camera.target()), cameraState.target, 0.125f)
                .add(-wobbling.x() / 10, wobbling.y() / 20, wobbling.x() / 10)
                .toVector3()
        );
    }

    public void draw() {
        ClearBackground(Colors.DARKPURPLE);

        BeginMode3D(camera);

        for (SillyCube sillyCube : sillyCubes) {
            moveSillyCube(sillyCube);
            DrawCubeWires(
                sillyCube.pos.toVector3(),
                sillyCube.rot.toVector3(),
                new Vec3f(1, 1, 1).toVector3(),
                Colors.PURPLE
            );
        }

        DrawCubeWires(
            camera.target(),
            rotation.toVector3(),
            new Vec3f(1, 1, 1).toVector3(),
            Colors.PURPLE
        );

        DrawGridEx(30, 2.0f, Colors.PURPLE, Colors.PURPLE, new Vec3f(0, 0, 0));

        EndMode3D();

        for (MenuButton button : buttons) {
            MenuButtonState state = button.states.get(selected);

            button.pos = lerp(button.pos, state.pos, 0.125f).add(wobbling);
            button.fontSize = lerp(button.fontSize, state.fontSize, 0.125f);
            button.color = ColorLerp(
                button.color,
                button.index == selected ? Colors.RED : Colors.ORANGE,
                0.125f
            );

            if (wobbling.y() < 0) {
                button.fontSize -=
                    ((buttons.size() - button.index - 1) *
                        Math.abs(wobbling.y())) /
                    10;
            } else {
                button.fontSize -= (button.index * Math.abs(wobbling.y())) / 10;
            }

            DrawTextEx(
                font,
                button.text,
                button.pos
                    .mul(GetScreenWidth(), GetScreenHeight())
                    .div(1280, 720)
                    .toVector2(),
                button.fontSize,
                2,
                button.color
            );
        }
    }

    public void onMouseMove(Vec2f prev, Vec2f now) {
        if (prev == null) return;

        Vec2f window_size = new Vec2f(GetScreenWidth(), GetScreenHeight());

        float fourth_part = window_size.x() / 4;

        if (!catched && now.x() < fourth_part * 1.5) {
            catched = true;
        }

        if (catched) {
            if (now.x() < fourth_part * 3) {
                selected = (int) (now.y() / (window_size.y() / buttons.size()));
                SetMouseCursor(MOUSE_CURSOR_POINTING_HAND);
            } else {
                selected = -1;
                catched = false;
                SetMouseCursor(MOUSE_CURSOR_DEFAULT);
            }
        }

        wobbling = lerp(wobbling, prev.sub(now).normalize().mul(1.2f), 0.125f);

        // System.out.println(
        //     "fuck " +
        //         catched +
        //         " prev " +
        //         prev.x() +
        //         " " +
        //         prev.y() +
        //         " new " +
        //         now.x() +
        //         " " +
        //         now.y()
        // );
    }

    public void clickButton() {
        if (selected == 0) {
            Game.getInstance().switchScene(new TestNovelScene());
        } else if (selected == 1) {
            Game.getInstance().switchScene(new TestScene());
        } else if (selected == 2) {
            IsMouseButtonPressed(0);
            do {
                BeginDrawing();
                GuiMessageBox(
                    new Rectangle().width(128).height(64),
                    "error",
                    "error",
                    "error"
                );
                EndDrawing();
            } while (!IsMouseButtonPressed(0) || WindowShouldClose());
        } else if (selected == 3) {
            CloseWindow();
        }
    }

    public void onMouseDown(int button) {
        SetMouseCursor(MOUSE_CURSOR_DEFAULT);

        if (button == 0) {
            clickButton();
        }
    }

    public void onKeyDown(int keyCode, char key) {
        if (selected == -1) {
            switch (keyCode) {
                case KEY_UP:
                case KEY_DOWN:
                case KEY_LEFT:
                case KEY_TAB:
                case KEY_ENTER:
                case KEY_SPACE:
                    selected = 0;
                    catched = true;
            }
        } else {
            switch (keyCode) {
                case KEY_UP:
                    selected--;
                    if (selected == -1) {
                        catched = false;
                    }
                    break;
                case KEY_TAB:
                case KEY_DOWN:
                    selected++;
                    if (selected == buttons.size()) {
                        selected = -1;
                        catched = false;
                    }
                    break;
                case KEY_RIGHT:
                    selected = -1;
                    catched = false;
                    break;
                case KEY_ENTER:
                case KEY_SPACE:
                    clickButton();
            }
        }
    }
}
