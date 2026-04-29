package game.scene.impl.test_novel;

import static com.raylib.Raylib.*;

import com.raylib.Colors;
import game.scene.novel.NovelChoice;
import game.util.geometry.Vec2f;
import game.util.geometry.Vec4f;
import java.util.List;

public class TestNovelChoice extends NovelChoice {

    public TestNovelChoice(String title, String... variants) {
        super(title, List.of(variants));
    }

    @Override
    public void drawVariantsLine(String line, Vec2f pos, boolean hovering) {
        DrawTextEx(
            textFont,
            line,
            pos.toVector2(),
            textFontSize,
            2,
            hovering ? Colors.WHITE : Colors.LIGHTGRAY
        );
    }

    @Override
    public void drawTitleLine(String line, Vec2f pos) {
        super.drawTitleLine(line, pos);
    }

    @Override
    public void drawBox() {
        DrawRectangleRounded(
            boxRect.toRectangle(),
            0.2f,
            10,
            new Vec4f(Colors.DARKPURPLE).a((byte) (127 + 64)).toColor()
        );
        DrawRectangleRoundedLinesEx(
            boxRect.toRectangle(),
            0.2f,
            10,
            2,
            Colors.PURPLE
        );
    }
}
