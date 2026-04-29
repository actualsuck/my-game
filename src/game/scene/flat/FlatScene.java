package game.scene.flat;

import static com.raylib.Raylib.BeginMode2D;
import static com.raylib.Raylib.ClearBackground;
import static com.raylib.Raylib.EndMode2D;
import static com.raylib.Raylib.GetScreenHeight;
import static com.raylib.Raylib.GetScreenWidth;

import com.raylib.Colors;
import com.raylib.Raylib.Camera2D;
import game.Game;
import game.event.Listener;
import game.scene.Scene;
import game.scene.flat.impl.Player;
import game.scene.flat.primitive.FlatObject;
import game.util.Pair;
import game.util.geometry.Vec2f;
import java.nio.ByteBuffer;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

@Getter
public class FlatScene implements Scene, Listener {

    private Player player;
    private Camera2D camera;
    private FlatObjectList objects;

    public FlatScene(String resource) {
        Yaml yaml = new Yaml();
        Pair<ByteBuffer, Integer> data = Game.getInstance()
            .getResourceLoader()
            .loadResourceData(resource);
        byte[] bytes = new byte[data.getSecond()];
        data.getFirst().get(bytes);
        objects = FlatObjectList.deserialize(yaml.load(new String(bytes)));
    }

    @Override
    public void onStart() {
        player = (Player) objects.getById("player");
        camera = new Camera2D()
            .target(new Vec2f(0, 0).toVector2())
            .rotation(0)
            .offset(
                new Vec2f(
                    GetScreenWidth() / 2,
                    GetScreenHeight() / 2
                ).toVector2()
            )
            .zoom(1);

        for (FlatObject obj : objects.getList()) {
            obj.onStart(this);
        }

        Game.getInstance().getListenerExecutor().subscribeListener(this);
    }

    @Override
    public void onEnd() {
        for (FlatObject obj : objects.getList()) {
            obj.onEnd();
        }

        Game.getInstance().getListenerExecutor().unsubscribeListener(this);
    }

    @Override
    public void tick() {
        camera.offset(
            new Vec2f(GetScreenWidth() / 2, GetScreenHeight() / 2).toVector2()
        );

        for (FlatObject obj : objects.getList()) {
            obj.tick();
        }
    }

    @Override
    public void draw() {
        BeginMode2D(camera);

        ClearBackground(Colors.RAYWHITE);

        for (FlatObject obj : objects.getList()) {
            obj.draw();
        }

        EndMode2D();
    }
}
