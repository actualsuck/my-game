package game.scene.impl.main_menu;

import game.util.geometry.Vec3f;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CameraState {

    Vec3f target;
    Vec3f pos;
    float fovy;
}
