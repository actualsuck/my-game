package game.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.Getter;

@Getter
public class Storage {

    private Path dataDirectory;
    private File dataFile;
    private StorageData data;

    public Storage(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.dataFile = dataDirectory
            .resolve("j9iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii.bin")
            .toFile();
    }

    public void loadData() {
        if (this.dataFile.exists()) {
            try {
                ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream(this.dataFile)
                );
                data = (StorageData) in.readObject();
                in.close();
            } catch (IOException | ClassNotFoundException exception) {
                throw new RuntimeException(exception);
            }
        } else {
            data = StorageData.getDefault();
            saveData();
        }
    }

    public void saveData() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(this.dataFile)
            );
            out.writeObject(data);
            out.close();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
