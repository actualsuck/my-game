package game.animation;

import static com.raylib.Raylib.DrawRectangleRounded;
import static com.raylib.Raylib.DrawRectangleRoundedLinesEx;
import static com.raylib.Raylib.DrawTextEx;
import static com.raylib.Raylib.GetScreenHeight;
import static com.raylib.Raylib.GetScreenWidth;
import static com.raylib.Raylib.GetTime;
import static game.scene.novel.NovelScene.dumpTextToLines;

import com.raylib.Colors;
import game.primitive.Animation;
import game.primitive.SharedData;
import game.util.geometry.Rect;
import game.util.geometry.Vec4f;
import java.util.List;

public class Toast implements Animation {

    private String text;
    private double animationStart;
    private double animationLength;

    private Vec4f borderColor;
    private Vec4f backColor;

    public Toast(String text) {
        this.text = text;
        this.animationLength = 5;

        this.borderColor = new Vec4f(Colors.YELLOW);
        this.backColor = new Vec4f(Colors.YELLOW).a((byte) 64);
    }

    public void onStart() {
        this.animationStart = GetTime();
    }

    public void onEnd() {}

    public static void drawToast(
        String text,
        Vec4f borderColor,
        Vec4f backColor
    ) {
        Rect screen = new Rect(0, 0, GetScreenWidth(), GetScreenHeight());

        Rect toast = new Rect(
            screen.width() / 5,
            (screen.height() / 5) * 4,
            (screen.width() / 5) * 3,
            screen.height() / 6
        );

        int padding = 15;
        Rect toast_inner = new Rect(
            toast.x() + padding,
            toast.y() + padding,
            toast.width() - padding * 2,
            toast.height() - padding * 2
        );

        List<String> lines = dumpTextToLines(
            text,
            SharedData.getInstance().getBoldFont(),
            30,
            toast_inner.width()
        );

        DrawRectangleRounded(
            toast.toRectangle(),
            0.2f,
            10,
            backColor.toColor()
        );
        DrawRectangleRoundedLinesEx(
            toast.toRectangle(),
            0.2f,
            10,
            2,
            borderColor.toColor()
        );

        float y = toast_inner.y();

        for (String line : lines) {
            DrawTextEx(
                SharedData.getInstance().getBoldFont(),
                line,
                toast_inner.pos().y(y).toVector2(),
                30,
                2,
                Colors.RAYWHITE
            );
            y += 35;
        }
    }

    public boolean draw() {
        drawToast(text, borderColor, backColor);

        return GetTime() < animationStart + animationLength;
    }
}
