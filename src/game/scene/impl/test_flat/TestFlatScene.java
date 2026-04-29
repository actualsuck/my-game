package game.scene.impl.test_flat;

import static com.raylib.Raylib.PlayMusicStream;
import static com.raylib.Raylib.StopMusicStream;
import static com.raylib.Raylib.UpdateMusicStream;

import com.raylib.Raylib.Music;
import game.Game;
import game.resources.Resource;
import game.scene.flat.FlatScene;

public class TestFlatScene extends FlatScene {

    Resource<Music> music;

    public TestFlatScene() {
        super("scenes/flat/test.yml");
        music = Game.getInstance()
            .getResourceLoader()
            .loadWavMusic("assets/audio/music-tako-luka.wav")
            .getResource();
    }

    public void onStart() {
        super.onStart();
        PlayMusicStream(music.getObject());
    }

    public void onEnd() {
        super.onEnd();
        StopMusicStream(music.getObject());
    }

    public void tick() {
        super.tick();
        UpdateMusicStream(music.getObject());
    }

    float zoomStep = 10;

    @Override
    public void onMouseScroll(float delta) {
        zoomStep = Math.max(1, delta + zoomStep);

        getCamera().zoom(zoomStep / 10);
    }
}
