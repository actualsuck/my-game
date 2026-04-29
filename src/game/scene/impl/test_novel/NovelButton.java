package game.scene.impl.test_novel;

import static com.raylib.Raylib.*;
import static game.scene.novel.NovelChoice.containsRect;

import com.raylib.Colors;
import game.Game;
import game.primitive.Tickable;
import game.resources.ResourceLoader;
import game.util.geometry.Rect;
import game.util.geometry.Vec2f;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NovelButton implements Tickable {

    Texture[] texture;
    Texture[] textureHover;
    Rect rect;

    public static NovelButton createButton(
        String buttonPath,
        String hoverButtonPath
    ) {
        ResourceLoader resourceLoader = Game.getInstance().getResourceLoader();
        return new NovelButton(
            new Texture[] {
                LoadTextureFromImage(resourceLoader.loadPngImage(buttonPath)),
            },
            new Texture[] {
                LoadTextureFromImage(
                    resourceLoader.loadPngImage(hoverButtonPath)
                ),
            },
            new Rect()
        );
    }

    public boolean isHover() {
        return containsRect(rect, new Vec2f(GetMousePosition()));
    }

    public void tick() {
        Texture finalTexture = isHover() ? textureHover[0] : texture[0];

        DrawTexturePro(
            finalTexture,
            new Rect(
                0,
                0,
                finalTexture.width(),
                finalTexture.height()
            ).toRectangle(),
            rect.toRectangle(),
            new Vec2f().toVector2(),
            0,
            Colors.WHITE
        );
    }
}
