package game.scene;

import game.primitive.Tickable;

public interface Scene extends Tickable {
    void onStart();
    void onEnd();
    void draw();
}
