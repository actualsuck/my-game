package game.resources.impl;

import com.raylib.Raylib;
import game.resources.Resource;
import lombok.Getter;

public class Sound extends Raylib.Sound {

    @Getter
    private final Resource<Raylib.Sound> resource;

    public Sound(Resource<Raylib.Sound> resource) {
        super(resource.getObject().getPointer());
        this.resource = resource;
    }
}
