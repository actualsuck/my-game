package game.scene.novel;

import static com.raylib.Raylib.*;

import com.raylib.Colors;
import com.raylib.Raylib;
import game.primitive.Tickable;
import game.util.Pair;
import game.util.geometry.Rect;
import game.util.geometry.Vec2f;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class NovelChoice implements Tickable {

    final String title;
    final List<String> variants;

    protected Raylib.Font titleFont;
    protected Raylib.Font textFont;

    protected float titleFontSize;
    protected float textFontSize;

    protected List<Rect> variantsRects;
    protected List<Pair<String, Vec2f>> titleLines;
    protected List<Pair<String, Vec2f>> variantsLines;
    protected Rect boxRect;

    int hoveringVariant = -1;

    public void calcAnchors(int windowWidth, int windowHeight) {
        titleFontSize = 30;
        textFontSize = 25;

        variantsLines = new ArrayList<>();
        variantsRects = new ArrayList<>();
        titleLines = new ArrayList<>();

        float y = 0;
        float max_width = 0;

        for (String line : title.split("\n")) {
            Raylib.Vector2 line_size = MeasureTextEx(
                titleFont,
                line,
                titleFontSize,
                2
            );

            max_width = Math.max(max_width, line_size.x());

            titleLines.add(
                new Pair<>(
                    line,
                    new Vec2f((float) windowWidth / 2 - line_size.x() / 2, y)
                )
            );

            y += line_size.y();
            y += 2;
        }
        y -= 2;

        y += 5;

        for (String variant : variants) {
            Rect rect = new Rect();
            rect.y(y);

            for (String line : variant.split("\n")) {
                Raylib.Vector2 line_size = MeasureTextEx(
                    textFont,
                    line,
                    textFontSize,
                    2
                );

                rect.width(Math.max(line_size.x(), rect.width()));

                variantsLines.add(
                    new Pair<>(
                        line,
                        new Vec2f(
                            (float) windowWidth / 2 - line_size.x() / 2,
                            y
                        )
                    )
                );

                max_width = Math.max(max_width, line_size.x());
                y += line_size.y() + 2;
            }

            y -= 2;
            rect.height(y - rect.y());
            rect.x((float) windowWidth / 2 - rect.width() / 2);

            variantsRects.add(rect);

            y += 5;
        }

        y -= 5;

        for (Rect rect : variantsRects) {
            rect.y((float) windowHeight / 2 - y / 2 + rect.y());
        }
        for (Pair<String, Vec2f> line : variantsLines) {
            line.getSecond().y(
                (float) windowHeight / 2 - y / 2 + line.getSecond().y()
            );
        }
        for (Pair<String, Vec2f> line : titleLines) {
            line.getSecond().y(
                (float) windowHeight / 2 - y / 2 + line.getSecond().y()
            );
        }

        boxRect = new Rect()
            .x((float) windowWidth / 2 - max_width / 2 - 10)
            .y((float) windowHeight / 2 - y / 2 - 10)
            .width(max_width + 20)
            .height(y + 20);
    }

    public void drawBox() {
        DrawRectangleRounded(boxRect.toRectangle(), 0.2f, 10, Colors.MAROON);
    }

    public static boolean containsRect(Rect rect, Vec2f point) {
        return CheckCollisionPointRec(point.toVector2(), rect.toRectangle());
    }

    public void drawTitleLine(String line, Vec2f pos) {
        DrawTextEx(
            titleFont,
            line,
            pos.toVector2(),
            titleFontSize,
            2,
            Colors.WHITE
        );
    }

    public void drawVariantsLine(String line, Vec2f pos, boolean hovering) {
        DrawTextEx(
            textFont,
            line,
            pos.toVector2(),
            textFontSize,
            2,
            hovering ? Colors.WHITE : Colors.GRAY
        );
    }

    public void drawTitle() {
        for (Pair<String, Vec2f> line : titleLines) {
            drawTitleLine(line.getFirst(), line.getSecond());
        }
    }

    public void drawVariants() {
        for (Pair<String, Vec2f> line : variantsLines) {
            boolean hovering =
                hoveringVariant != -1 &&
                containsRect(
                    variantsRects.get(hoveringVariant),
                    line.getSecond().add(1, 1)
                );
            drawVariantsLine(line.getFirst(), line.getSecond(), hovering);
        }
    }

    public void checkHovering() {
        Vec2f mousePos = new Vec2f(GetMousePosition());
        hoveringVariant = -1;
        int index = 0;
        for (Rect variantsRect : variantsRects) {
            if (containsRect(variantsRect, mousePos)) {
                hoveringVariant = index;
                break;
            }
            index++;
        }
    }

    public void tick() {
        checkHovering();
        drawBox();
        drawTitle();
        drawVariants();
    }
}
