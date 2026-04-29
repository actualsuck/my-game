package game.event;

import static com.raylib.Raylib.*;

import com.raylib.Raylib.Vector2;
import game.primitive.Tickable;
import game.util.geometry.Vec2f;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

public class ListenerExecutor implements Tickable {

    private final List<Listener> subscribers;

    public ListenerExecutor() {
        subscribers = new ArrayList<>();
    }

    public void subscribeListener(Listener listener) {
        subscribers.add(listener);
    }

    public void unsubscribeListener(Listener listener) {
        subscribers.remove(listener);
    }

    private void runEvent(
        Consumer<? super Listener> runner,
        ToIntFunction<? super Listener> comparator
    ) {
        subscribers
            .stream()
            .sorted(Comparator.comparingInt(comparator).reversed())
            .forEachOrdered(runner);
    }

    private int getEventPriority(
        Listener listener,
        String name,
        Class<?>... parameterTypes
    ) {
        try {
            return listener
                .getClass()
                .getMethod(name, parameterTypes)
                .getAnnotation(Event.class)
                .priority();
        } catch (NoSuchMethodException | NullPointerException e) {
            return 0;
        }
    }

    private Vec2f mousePosition;

    private void checkMouseMove() {
        Vec2f newMousePosition = new Vec2f(GetMousePosition());
        if (!newMousePosition.equals(mousePosition)) {
            runEvent(
                listener ->
                    listener.onMouseMove(mousePosition, newMousePosition),
                listener ->
                    getEventPriority(
                        listener,
                        "onMouseMove",
                        Vector2.class,
                        Vector2.class
                    )
            );
            mousePosition = newMousePosition;
        }
    }

    private void checkMouseDown() {
        for (int button = 0; button < 7; button++) {
            int finalButton = button;
            if (IsMouseButtonPressed(finalButton)) {
                runEvent(
                    listener -> listener.onMouseDown(finalButton),
                    listener ->
                        getEventPriority(listener, "onMouseDown", Integer.class)
                );
            }
        }
    }

    private void checkMouseScroll() {
        float scrollDelta = GetMouseWheelMove();
        if (scrollDelta != 0) {
            runEvent(
                listener -> listener.onMouseScroll(scrollDelta),
                listener ->
                    getEventPriority(listener, "onMouseScroll", Float.class)
            );
        }
    }

    private void checkMouseUp() {
        for (int button = 0; button < 7; button++) {
            int finalButton = button;
            if (IsMouseButtonReleased(finalButton)) {
                runEvent(
                    listener -> listener.onMouseUp(finalButton),
                    listener ->
                        getEventPriority(listener, "onMouseUp", Integer.class)
                );
            }
        }
    }

    private void checkKeyDown() {
        for (int key = 0; key < KEY_KB_MENU + 1; key++) {
            int finalKey = key;
            if (IsKeyPressed(finalKey)) {
                runEvent(
                    listener -> listener.onKeyDown(finalKey, (char) 0), // FIXME: fix char
                    listener ->
                        getEventPriority(
                            listener,
                            "onKeyDown",
                            Integer.class,
                            Character.class
                        )
                );
            }
        }
    }

    private void checkKeyUp() {
        for (int key = 0; key < KEY_KB_MENU + 1; key++) {
            int finalKey = key;
            if (IsKeyReleased(finalKey)) {
                runEvent(
                    listener -> listener.onKeyUp(finalKey, (char) 0), // FIXME: fix char
                    listener ->
                        getEventPriority(
                            listener,
                            "onKeyUp",
                            Integer.class,
                            Character.class
                        )
                );
            }
        }
    }

    private int windowWidth = 0,
        windowHeight = 0;

    private void checkWindowResize() {
        int now_window_width = GetScreenWidth();
        int now_window_height = GetScreenHeight();

        if (
            now_window_width == windowWidth || now_window_height != windowHeight
        ) {
            runEvent(
                listener ->
                    listener.onWindowResize(
                        windowWidth,
                        windowHeight,
                        now_window_width,
                        now_window_height
                    ),
                listener ->
                    getEventPriority(
                        listener,
                        "onWindowResize",
                        Integer.class,
                        Integer.class,
                        Integer.class,
                        Integer.class
                    )
            );
            windowWidth = now_window_width;
            windowHeight = now_window_height;
        }
    }

    private void checkGameTick() {
        runEvent(
            listener -> listener.onGameTick(),
            listener -> getEventPriority(listener, "onGameTick")
        );
    }

    @Override
    public void tick() {
        checkMouseMove();
        checkMouseDown();
        checkMouseUp();
        checkMouseScroll();
        checkKeyDown();
        checkKeyUp();
        checkWindowResize();
        checkGameTick();
    }
}
