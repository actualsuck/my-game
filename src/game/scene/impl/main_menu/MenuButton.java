package game.scene.impl.main_menu;

import com.raylib.Raylib;
import game.util.geometry.Vec2f;
import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MenuButton {

    String text;
    int index;
    float fontSize;
    Vec2f pos;
    Raylib.Color color;
    Map<Integer, MenuButtonState> states;
}
