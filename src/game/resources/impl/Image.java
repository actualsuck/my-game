package game.resources.impl;

import com.raylib.Raylib;
import game.resources.Resource;
import lombok.Getter;

public class Image extends Raylib.Image {

    @Getter
    private final Resource<Raylib.Image> resource;

    public Image(Resource<Raylib.Image> resource) {
        super(resource.getObject().getPointer());
        this.resource = resource;
    }
}
