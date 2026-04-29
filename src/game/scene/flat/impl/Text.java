package game.scene.flat.impl;

import static com.raylib.Raylib.DrawText;
import static com.raylib.Raylib.MeasureText;
import static game.scene.flat.FlatObjectRegistry.deserializeColor;
import static game.scene.flat.FlatObjectRegistry.deserializeVec2f;

import game.scene.flat.FlatObjectRegistry;
import game.scene.flat.FlatScene;
import game.scene.flat.primitive.FlatObject;
import game.util.geometry.Vec2f;
import game.util.geometry.Vec4f;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Text extends FlatObject {

    private Vec2f position;
    private String text;
    private int fontSize;
    private Vec4f color;

    private Vec2f size;

    public Text(
        String id,
        String text,
        Vec2f position,
        int fontSize,
        Vec4f color
    ) {
        super("text", id);
        this.position = position;
        this.text = text;
        this.fontSize = fontSize;
        this.color = color;

        this.size = new Vec2f(MeasureText(text, fontSize), fontSize);
    }

    static {
        FlatObjectRegistry.addRegistry("text", data -> {
            Vec2f pos = deserializeVec2f(data.get("pos"));
            String text = (String) data.get("text");
            int font_size = (Integer) data.get("font_size");
            String id = (String) data.get("id");
            Vec4f color = deserializeColor(data.get("color"));

            return new Text(id, text, pos, font_size, color);
        });
    }

    public void draw() {
        DrawText(
            text,
            (int) (position.x() - size.x() / 2),
            (int) (position.y() - size.y() / 2),
            fontSize,
            color.toColor()
        );
    }

    public void tick() {}

    @Override
    public void onStart(FlatScene scene) {}

    @Override
    public void onEnd() {}
}
