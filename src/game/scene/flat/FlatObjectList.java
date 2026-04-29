package game.scene.flat;

import game.scene.flat.primitive.FlatObject;
import java.util.List;
import java.util.Map;

public class FlatObjectList {

    private List<FlatObject> objects;

    public FlatObjectList(List<FlatObject> objects) {
        this.objects = objects;
    }

    public List<FlatObject> getList() {
        return objects;
    }

    public FlatObject getById(String id) {
        return objects
            .stream()
            .filter(o -> id.equals(o.getId()))
            .findFirst()
            .get();
    }

    public List<FlatObject> getByType(String type) {
        return objects
            .stream()
            .filter(o -> type.equals(o.getType()))
            .toList();
    }

    public static FlatObjectList deserialize(List<Map<String, Object>> list) {
        return new FlatObjectList(
            list.stream().map(FlatObjectRegistry::deserialize).toList()
        );
    }
}
