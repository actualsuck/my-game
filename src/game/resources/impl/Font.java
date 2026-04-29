package game.resources.impl;

import com.raylib.Raylib;
import game.resources.Resource;
import lombok.Getter;

public class Font extends Raylib.Font {

    @Getter
    private final Resource<Raylib.Font> resource;

    public Font(Resource<Raylib.Font> resource) {
        super(resource.getObject().getPointer());
        this.resource = resource;
    }
}
