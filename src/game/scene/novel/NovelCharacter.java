package game.scene.novel;

import static com.raylib.Raylib.*;
import static game.util.geometry.Lerp.lerp;

import game.Game;
import game.primitive.Tickable;
import game.util.Pair;
import game.util.geometry.Rect;
import java.nio.ByteBuffer;
import java.util.*;
import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
public class NovelCharacter implements Tickable {

    private @NonNull String name;
    private @NonNull List<Image> images;
    private @NonNull Float scale;
    private @NonNull Rect rect;
    private @NonNull Texture texture;
    private @NonNull Integer currentImage;
    private @NonNull Float picForHeight;

    private float yPos = 0;
    private float yVel = 0;
    private float relX = 0;
    private float x = 0;

    private boolean bilinearFilter = true;
    private boolean hiding = false;
    private boolean hidden = false;

    /// example: "assets/images/Mon", 5, 1
    public static NovelCharacter loadCharacter(
        String name,
        int firstImage,
        String resource,
        int resourcesCount,
        float scale,
        Rect rect,
        float picForHeight
    ) {
        List<Image> images = new ArrayList<>();

        for (int i = 0; i < resourcesCount; i++) {
            Pair<ByteBuffer, Integer> data = Game.getInstance()
                .getResourceLoader()
                .loadResourceData(resource + i + ".png");
            Image image = LoadImageFromMemory(
                ".png",
                data.getFirst(),
                data.getSecond()
            );
            ImageResize(
                image,
                (int) ((float) image.width() * scale),
                (int) ((float) image.height() * scale)
            );
            images.add(image);
        }

        rect.x(rect.x() * scale);
        rect.y(rect.y() * scale);
        rect.width(rect.width() * scale);
        rect.height(rect.height() * scale);

        Texture texture = LoadTextureFromImage(images.get(firstImage));

        return new NovelCharacter(
            name,
            images,
            1.0f, // can be used during runtime to adjust scale if resized window or some other shit
            rect,
            texture,
            firstImage,
            picForHeight
        );
    }

    @Override
    public void tick() {
        if (yVel != 0) {
            yPos = lerp(yPos, yPos + yVel, 0.2f);
            yVel /= 1.1f;
        }

        yPos = lerp(yPos, 0, 0.3f);

        if (hiding) {
            relX = lerp(relX, 2, 0.035f);
            if (relX > 1.9) hidden = true;
        }
    }

    public void setCurrentImage(int index) {
        Texture prev_texture = texture;
        texture = LoadTextureFromImage(images.get(index));
        if (bilinearFilter) SetTextureFilter(texture, TEXTURE_FILTER_BILINEAR);
        UnloadTexture(prev_texture);
        currentImage = index;
    }

    public void onSelect() {
        jump();
    }

    public void jump() {
        yVel += 30;
    }
}
