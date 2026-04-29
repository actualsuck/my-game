package game.event;

import game.util.geometry.Vec2f;

public interface Listener {
    @Event(priority = 0)
    default void onGameTick() {}

    @Event(priority = 0)
    default void onMouseMove(Vec2f prev_pos, Vec2f new_pos) {}

    @Event(priority = 0)
    default void onMouseDown(int button) {}

    @Event(priority = 0)
    default void onMouseUp(int button) {}

    @Event(priority = 0)
    default void onMouseScroll(float delta) {}

    @Event(priority = 0)
    default void onKeyDown(int keyCode, char key) {}

    @Event(priority = 0)
    default void onKeyUp(int keyCode, char key) {}

    @Event(priority = 0)
    default void onWindowResize(
        int prev_width,
        int prev_height,
        int width,
        int height
    ) {}
}
