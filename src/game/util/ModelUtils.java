package game.util;

import static com.raylib.Raylib.*;

import game.util.geometry.Vec3f;

public class ModelUtils {

    public static void DrawGridEx(
        int slices,
        float spacing,
        Color color1,
        Color color2,
        Vec3f pos
    ) {
        int halfSlices = slices / 2;

        rlBegin(RL_LINES);
        for (int i = -halfSlices; i <= halfSlices; i++) {
            if (i == 0) {
                rlColor4ub(color1.r(), color1.g(), color1.b(), color1.a());
            } else {
                rlColor4ub(color2.r(), color2.g(), color2.b(), color2.a());
            }

            rlVertex3f(
                pos.x() + (float) i * spacing,
                pos.y(),
                pos.z() + (float) -halfSlices * spacing
            );
            rlVertex3f(
                pos.x() + (float) i * spacing,
                pos.y(),
                pos.z() + (float) halfSlices * spacing
            );

            rlVertex3f(
                pos.x() + (float) -halfSlices * spacing,
                pos.y(),
                pos.z() + (float) i * spacing
            );
            rlVertex3f(
                pos.x() + (float) halfSlices * spacing,
                pos.y(),
                pos.z() + (float) i * spacing
            );
        }
        rlEnd();
    }

    // Draw cube wires
    public static void DrawCubeWires(
        Vector3 position,
        Vector3 rotation,
        Vector3 size,
        Color color
    ) {
        float x = 0.0f;
        float y = 0.0f;
        float z = 0.0f;

        float width = size.x();
        float height = size.y();
        float length = size.z();

        rlPushMatrix();
        rlTranslatef(position.x(), position.y(), position.z());

        // Apply rotations (order matters!)
        rlRotatef(rotation.x(), 1.0f, 0.0f, 0.0f); // Rotate around X axis
        rlRotatef(rotation.y(), 0.0f, 1.0f, 0.0f); // Rotate around Y axis
        rlRotatef(rotation.z(), 0.0f, 0.0f, 1.0f); // Rotate around Z axis

        rlBegin(RL_LINES);
        rlColor4ub(color.r(), color.g(), color.b(), color.a());

        // Front face
        //------------------------------------------------------------------
        // Bottom line
        rlVertex3f(x - width / 2, y - height / 2, z + length / 2); // Bottom left
        rlVertex3f(x + width / 2, y - height / 2, z + length / 2); // Bottom right

        // Left line
        rlVertex3f(x + width / 2, y - height / 2, z + length / 2); // Bottom right
        rlVertex3f(x + width / 2, y + height / 2, z + length / 2); // Top right

        // Top line
        rlVertex3f(x + width / 2, y + height / 2, z + length / 2); // Top right
        rlVertex3f(x - width / 2, y + height / 2, z + length / 2); // Top left

        // Right line
        rlVertex3f(x - width / 2, y + height / 2, z + length / 2); // Top left
        rlVertex3f(x - width / 2, y - height / 2, z + length / 2); // Bottom left

        // Back face
        //------------------------------------------------------------------
        // Bottom line
        rlVertex3f(x - width / 2, y - height / 2, z - length / 2); // Bottom left
        rlVertex3f(x + width / 2, y - height / 2, z - length / 2); // Bottom right

        // Left line
        rlVertex3f(x + width / 2, y - height / 2, z - length / 2); // Bottom right
        rlVertex3f(x + width / 2, y + height / 2, z - length / 2); // Top right

        // Top line
        rlVertex3f(x + width / 2, y + height / 2, z - length / 2); // Top right
        rlVertex3f(x - width / 2, y + height / 2, z - length / 2); // Top left

        // Right line
        rlVertex3f(x - width / 2, y + height / 2, z - length / 2); // Top left
        rlVertex3f(x - width / 2, y - height / 2, z - length / 2); // Bottom left

        // Top face
        //------------------------------------------------------------------
        // Left line
        rlVertex3f(x - width / 2, y + height / 2, z + length / 2); // Top left front
        rlVertex3f(x - width / 2, y + height / 2, z - length / 2); // Top left back

        // Right line
        rlVertex3f(x + width / 2, y + height / 2, z + length / 2); // Top right front
        rlVertex3f(x + width / 2, y + height / 2, z - length / 2); // Top right back

        // Bottom face
        //------------------------------------------------------------------
        // Left line
        rlVertex3f(x - width / 2, y - height / 2, z + length / 2); // Top left front
        rlVertex3f(x - width / 2, y - height / 2, z - length / 2); // Top left back

        // Right line
        rlVertex3f(x + width / 2, y - height / 2, z + length / 2); // Top right front
        rlVertex3f(x + width / 2, y - height / 2, z - length / 2); // Top right back
        rlEnd();
        rlPopMatrix();
    }
}
