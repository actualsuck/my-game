package game.resources;

import static com.raylib.Raylib.*;

import game.Game;
import game.resources.impl.Font;
import game.resources.impl.Image;
import game.resources.impl.Music;
import game.resources.impl.Sound;
import game.util.Pair;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceLoader {

    private Path resourcesDirectory;
    private boolean direct; // resources are not bundled into jar, instead using runtime directory/resources

    private Map<String, Pair<ByteBuffer, Integer>> resourcesData =
        new HashMap<>();
    private List<String> notDirectFileResources = new ArrayList<>();

    public ResourceLoader(Path resourcesDirectory) {
        if (resourcesDirectory == null) {
            try {
                this.resourcesDirectory = Files.createTempDirectory(
                    "leach-game-cache"
                );
                this.resourcesDirectory.toFile().deleteOnExit();
                direct = false;
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        } else {
            this.resourcesDirectory = resourcesDirectory;
            this.direct = true;
        }
    }

    public void unload() {
        if (!direct) {
            try {
                for (String name : notDirectFileResources) {
                    Path path = resourcesDirectory.resolve(name);
                    Files.delete(path);
                }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
            resourcesDirectory.toFile().delete();
        }
    }

    private InputStream getResourceBundled(String path) {
        return Game.class.getClassLoader().getResourceAsStream(path);
    }

    private InputStream getResource(String path) {
        if (direct) {
            try {
                return new FileInputStream(
                    resourcesDirectory.resolve(path).toFile()
                );
            } catch (FileNotFoundException exception) {
                throw new RuntimeException(exception);
            }
        }
        return getResourceBundled(path);
    }

    public Pair<ByteBuffer, Integer> loadResourceData(String path) {
        Pair<ByteBuffer, Integer> pair;
        if (resourcesData.containsKey(path)) {
            pair = resourcesData.get(path);
        } else {
            byte[] data;
            try {
                data = getResource(path).readAllBytes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ByteBuffer buffer = ByteBuffer.allocateDirect(data.length).put(
                data
            );
            buffer.flip();
            pair = new Pair<>(buffer, data.length);
        }
        return pair;
    }

    public File loadResourceFile(String path) {
        Path resourcePath = resourcesDirectory.resolve(path);
        if (!direct && !notDirectFileResources.contains(path)) {
            try {
                Path parentPath = resourcePath.getParent();
                if (!Files.exists(parentPath)) {
                    Files.createDirectories(parentPath);
                    parentPath.toFile().deleteOnExit();
                }
                FileOutputStream out = new FileOutputStream(
                    resourcePath.toFile()
                );
                getResourceBundled(path).transferTo(out);
                out.close();
                resourcePath.toFile().deleteOnExit();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            notDirectFileResources.add(path);
        }
        return resourcePath.toFile();
    }

    public Font loadTtfFont(String fileName, int fontSize) {
        int[] codepoints = new int[0x7E -
            0x20 +
            1 +
            (0xFF - 0xA0 + 1) +
            (0x04FF - 0x0400 + 1) +
            (0x052F - 0x0500 + 1)];

        int index = 0;
        for (int i = 0x20; i <= 0x7E; i++) codepoints[index++] = i; // ascii
        for (int i = 0xA0; i <= 0xFF; i++) codepoints[index++] = i; // ascii2
        for (int i = 0x0400; i <= 0x04FF; i++) codepoints[index++] = i; // cyrillic
        for (int i = 0x0500; i <= 0x052F; i++) codepoints[index++] = i; // cyrillic2

        Pair<ByteBuffer, Integer> pair = loadResourceData(fileName);

        return new Font(
            new Resource<>(
                pair.getFirst(),
                LoadFontFromMemory(
                    ".ttf",
                    pair.getFirst(),
                    pair.getSecond(),
                    fontSize,
                    IntBuffer.wrap(codepoints),
                    codepoints.length
                )
            )
        );
    }

    public Image loadPngImage(String resource) {
        Pair<ByteBuffer, Integer> pair = loadResourceData(resource);
        return new Image(
            new Resource<>(
                pair.getFirst(),
                LoadImageFromMemory(".png", pair.getFirst(), pair.getSecond())
            )
        );
    }

    public Image loadJpgImage(String resource) {
        Pair<ByteBuffer, Integer> pair = loadResourceData(resource);
        return new Image(
            new Resource<>(
                pair.getFirst(),
                LoadImageFromMemory(".jpg", pair.getFirst(), pair.getSecond())
            )
        );
    }

    public Music loadWavMusic(String resource) {
        Pair<ByteBuffer, Integer> pair = loadResourceData(resource);
        return new Music(
            new Resource<>(
                pair.getFirst(),
                LoadMusicStreamFromMemory(
                    ".wav",
                    pair.getFirst(),
                    pair.getSecond()
                )
            )
        );
    }

    public Sound loadWavSound(String resource) {
        Pair<ByteBuffer, Integer> pair = loadResourceData(resource);
        Wave wave = LoadWaveFromMemory(
            ".wav",
            pair.getFirst(),
            pair.getSecond()
        );
        return new Sound(
            new Resource<>(pair.getFirst(), LoadSoundFromWave(wave))
        );
    }

    public Music loadOggMusic(String resource) {
        Pair<ByteBuffer, Integer> pair = loadResourceData(resource);
        return new Music(
            new Resource<>(
                pair.getFirst(),
                LoadMusicStreamFromMemory(
                    ".ogg",
                    pair.getFirst(),
                    pair.getSecond()
                )
            )
        );
    }

    public Sound loadOggSound(String resource) {
        Pair<ByteBuffer, Integer> pair = loadResourceData(resource);
        Wave wave = LoadWaveFromMemory(
            ".ogg",
            pair.getFirst(),
            pair.getSecond()
        );
        return new Sound(
            new Resource<>(pair.getFirst(), LoadSoundFromWave(wave))
        );
    }
}
