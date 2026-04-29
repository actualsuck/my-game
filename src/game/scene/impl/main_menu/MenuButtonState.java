package game.scene.impl.main_menu;

import static com.raylib.Raylib.MeasureTextEx;

import com.raylib.Raylib;
import game.util.geometry.Vec2f;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MenuButtonState {

    float fontSize;
    Vec2f pos;

    public MenuButtonState patchPos(Raylib.Font font, String text) {
        Vec2f size = new Vec2f(MeasureTextEx(font, text, fontSize, 2));
        pos = pos.sub(size.x() / 2, size.y() / 2);
        return this;
    }

    public MenuButtonState patchPosY(Raylib.Font font, String text) {
        Vec2f size = new Vec2f(MeasureTextEx(font, text, fontSize, 2));
        pos = pos.sub(0, size.y() / 2);
        return this;
    }
}
