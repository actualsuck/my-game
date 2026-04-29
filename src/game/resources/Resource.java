package game.resources;

import java.nio.ByteBuffer;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Resource<T> {

    private final ByteBuffer source;

    @Getter
    private final T object;

    public <R> Resource<R> transform(Function<T, R> transformer) {
        return new Resource<>(source, transformer.apply(object));
    }
}
