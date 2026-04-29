package game.storage;

import java.io.Serializable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class StorageData implements Serializable {

    private boolean hasKey1;

    public static StorageData getDefault() {
        return new StorageData();
    }
}
