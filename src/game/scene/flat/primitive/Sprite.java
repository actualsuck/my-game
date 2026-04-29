package game.scene.flat.primitive;

import static com.raylib.Raylib.DrawTexturePro;
import static com.raylib.Raylib.DrawTextureRec;
import static com.raylib.Raylib.ImageFlipHorizontal;
import static com.raylib.Raylib.LoadTextureFromImage;
import static game.util.TextureUtils.SafeUnloadTexture;

import com.raylib.Raylib.Image;
import com.raylib.Raylib.Texture;
import game.util.geometry.Rect;
import game.util.geometry.Vec2f;
import game.util.geometry.Vec4f;
import lombok.Getter;

@Getter
public class Sprite {

    private final int count;
    private final Texture texture;
    private final Texture flippedTexture;
    private final int width;
    private final int height;

    public Sprite(Image image, int count) {
        this.texture = LoadTextureFromImage(image);
        ImageFlipHorizontal(image);
        this.flippedTexture = LoadTextureFromImage(image);

        this.count = count;
        this.width = texture.width();
        this.height = texture.height() / count;
    }

    public void unload() {
        SafeUnloadTexture(texture);
        SafeUnloadTexture(flippedTexture);
    }

    public Rect getRect() {
        return new Rect(0, 0, width, height);
    }

    public void drawV(int index, boolean flip, Vec2f dest, Vec4f tint) {
        DrawTextureRec(
            flip ? flippedTexture : texture,
            new Rect(0, height * index, width, height).toRectangle(),
            dest.toVector2(),
            tint.toColor()
        );
    }

    public void drawPro(
        int index,
        boolean flip,
        Rect src,
        Rect dest,
        Vec2f origin,
        float rotation,
        Vec4f tint
    ) {
        DrawTexturePro(
            flip ? flippedTexture : texture,
            new Rect(
                src.x(),
                height * index + src.y(),
                src.width(),
                src.height()
            ).toRectangle(),
            dest.toRectangle(),
            origin.toVector2(),
            rotation,
            tint.toColor()
        );
    }
}
