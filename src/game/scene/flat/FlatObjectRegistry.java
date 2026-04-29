package game.scene.flat;

import game.scene.flat.primitive.FlatObject;
import game.util.geometry.Vec2f;
import game.util.geometry.Vec4f;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

// registry of scene objects

public class FlatObjectRegistry {

    private static Map<
        String,
        Function<Map<String, Object>, FlatObject>
    > registries = new HashMap<>();

    // classes to load before deserialization
    private static List<String> objectImpls = List.of(
        "Player",
        "Text",
        "DrawCanvas",
        "SolidBox"
    );

    public static void addRegistry(
        String type,
        Function<Map<String, Object>, FlatObject> mapper
    ) {
        registries.put(type, mapper);
    }

    public static FlatObject deserialize(Map<String, Object> data) {
        String type = (String) data.get("type");
        return registries.get(type).apply(data);
    }

    public static Vec2f deserializeVec2f(List<Float> list) {
        return new Vec2f(list.get(0), list.get(1));
    }

    public static Vec2f deserializeVec2f(Object object) {
        return deserializeVec2f(
            ((List<Number>) object).stream().map(Number::floatValue).toList()
        );
    }

    public static Vec4f deserializeVec4f(List<Float> list) {
        return new Vec4f(list.get(0), list.get(1), list.get(2), list.get(3));
    }

    public static Vec4f deserializeVec4f(Object object) {
        return deserializeVec4f(
            ((List<Number>) object).stream().map(Number::floatValue).toList()
        );
    }

    public static Vec4f deserializeColor(List<Byte> list) {
        return new Vec4f(list.get(0), list.get(1), list.get(2), list.get(3));
    }

    public static Vec4f deserializeColor(Object object) {
        return deserializeVec4f(
            ((List<Number>) object).stream().map(Number::byteValue).toList()
        );
    }

    static {
        for (String className : objectImpls) {
            try {
                Class.forName("game.scene.flat.impl." + className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
