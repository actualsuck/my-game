package game.scene.flat.primitive;

import game.primitive.Tickable;
import game.scene.flat.FlatScene;
import java.util.Map;
import lombok.Getter;

@Getter
public abstract class FlatObject implements Tickable {

    private final String type;
    private final String id;

    public FlatObject(String type, String id) {
        this.type = type;
        this.id = id;
    }

    public FlatObject(Map<String, Object> data) {
        this.type = (String) data.get("type");
        this.id = (String) data.get("id");
    }

    public abstract void onStart(FlatScene scene);

    public abstract void onEnd();

    public abstract void tick();

    public abstract void draw(); // can draw outside of the rect
}
