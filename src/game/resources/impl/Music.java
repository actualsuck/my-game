package game.resources.impl;

import com.raylib.Raylib;
import game.resources.Resource;
import lombok.Getter;

public class Music extends Raylib.Music {

    @Getter
    private final Resource<Raylib.Music> resource;

    public Music(Resource<Raylib.Music> resource) {
        super(resource.getObject().getPointer());
        this.resource = resource;
    }
}
