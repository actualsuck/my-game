package game.animation;

import static com.raylib.Raylib.DrawTexturePro;
import static com.raylib.Raylib.GetScreenHeight;
import static com.raylib.Raylib.GetScreenWidth;
import static com.raylib.Raylib.GetTime;
import static com.raylib.Raylib.LoadTextureFromImage;
import static com.raylib.Raylib.UnloadImage;
import static game.util.TextureUtils.SafeUnloadTexture;

import com.raylib.Colors;
import com.raylib.Raylib.Texture;
import game.Game;
import game.primitive.Animation;
import game.resources.impl.Image;
import game.util.geometry.Rect;
import game.util.geometry.Vec2f;

public class KeyAnimation implements Animation {

    private Texture texture;
    private Image image;

    private double animationStart;
    private double animationLength;

    public KeyAnimation(String resource) {
        image = Game.getInstance().getResourceLoader().loadPngImage(resource);
        animationStart = 0;
        animationLength = 3; // seconds
    }

    public void onStart() {
        animationStart = GetTime();
        texture = LoadTextureFromImage(image);
    }

    public void onEnd() {
        SafeUnloadTexture(texture);
        UnloadImage(image);
    }

    public boolean draw() {
        if (texture == null) return true;

        Rect screen = new Rect(0, 0, GetScreenWidth(), GetScreenHeight());

        float x = (float) (Math.min(
            animationLength,
            GetTime() - animationStart
        ) / animationLength);

        double rotation = x * (1.5f - 1.5f * x + x * x);
        double scale;

        x *= 2;
        if (x > 1) {
            x = 2 - x;
            scale = 1 - Math.pow(1 - x, 5);
        } else {
            scale = 1 - Math.pow(1 - x, 5);
        }

        rotation += 0.5;

        Rect source = new Rect(0, 0, texture.width(), texture.height());
        Rect destination = source.clone();

        destination.size(destination.size().mul((float) scale));
        Vec2f origin = destination.center();
        destination.pos(screen.center());

        DrawTexturePro(
            texture,
            source.toRectangle(),
            destination.toRectangle(),
            origin.toVector2(),
            (float) rotation * 360,
            Colors.WHITE
        );

        return GetTime() < animationStart + animationLength;
    }
}
