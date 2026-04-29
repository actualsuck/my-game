package game.util;

import static com.raylib.Raylib.IsTextureValid;
import static com.raylib.Raylib.UnloadTexture;

import com.raylib.Raylib.Texture;

public class TextureUtils {

    public static void SafeUnloadTexture(Texture texture) {
        if (texture == null || texture.isNull()) return;
        if (IsTextureValid(texture)) UnloadTexture(texture);
    }
}
