package game.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RuntimeFile {

    private final Path location; // directory
    private final boolean localData;
    private final boolean localResources;
    private final boolean localLibs;

    // can be null
    public Path findResourcesLocation() {
        if (localResources) {
            return Path.of(".", "resources").toAbsolutePath();
        }
        return null;
    }

    // can be null
    public Path findLibsLocation() {
        if (localLibs) {
            return Path.of(".", "libs").toAbsolutePath();
        }
        return null;
    }

    public Path findDataDirectory() {
        Path runtime_location = location;
        if (localData) {
            runtime_location = Path.of(".").toAbsolutePath();
        }
        Path path = runtime_location.resolve("data");
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
        return path;
    }

    public static RuntimeFile createDefault(Path path) throws IOException {
        Files.writeString(
            path,
            writeProperties(
                Map.of(
                    "local_data",
                    "n",
                    "local_resources",
                    "No",
                    "local_libs",
                    "NO FUCKING NO",
                    "your_mom_is_fat",
                    "y"
                )
            )
        );
        return fromFile(path.toFile());
    }

    private static Map<String, String> readProperties(String data) {
        Map<String, String> props = new HashMap<>();
        data.lines()
            .map(o -> o.contains("#") ? o.split("#")[0] : o)
            .map(o -> o.stripTrailing())
            .filter(o -> o.length() > 0)
            .filter(o -> o.contains("="))
            .map(o -> o.split("="))
            .forEach(o -> props.put(o[0], o[1]));
        return props;
    }

    private static String writeProperties(Map<String, String> props) {
        StringBuilder builder = new StringBuilder();
        props.forEach((k, v) -> {
            builder.append(k).append("=").append(v).append("\n");
        });
        return builder.toString();
    }

    public static RuntimeFile fromFile(File file) throws IOException {
        Path location = file.getParentFile().toPath();
        Map<String, String> props = readProperties(
            Files.readString(file.toPath())
        );
        return new RuntimeFile(
            location,
            props.getOrDefault("local-data", "n").equals("y"),
            props.getOrDefault("local-resources", "n").equals("y"),
            props.getOrDefault("local-libs", "n").equals("y")
        );
    }

    public static RuntimeFile findRuntimeFile() {
        try {
            System.out.println("searching for the runtime file. ...");
            if (Files.exists(Path.of(".game-runtime"))) {
                System.out.println("found on local!!");
                return RuntimeFile.fromFile(
                    Path.of(".game-runtime").toAbsolutePath().toFile()
                );
            }

            System.out.println(
                "put me in directory with .game-runtime you dumbass"
            );
            System.out.println("ok i will just use your HOME directory");
            System.out.println(
                "my runtime dir is now your home dir...........................................local/share/leeach-game"
            );
            System.out.println("can start crying ~");

            // return null;

            Path path = Path.of(
                System.getProperty("user.home"),
                ".local",
                "share",
                "leeach-game"
            );

            if (Files.exists(path.getParent())) {
                System.out.println("GOD bless my ass");
                System.out.println(
                    "i finally found the directory to penetrate...."
                );
                Files.createDirectory(path);
                return RuntimeFile.createDefault(path.resolve(".game-runtime"));
            }

            for (String hated : new String[] {
                "family",
                "mom",
                "dad",
                "grandmom",
                "granddad",
                "fatass cat",
                "life",
            }) {
                System.out.println("I hate your " + hated);
            }

            path = Path.of(
                System.getProperty("user.home"),
                ".leeach-game-sorry-to-interrupt-here-dude-but-no-local-share-directory-uhh-nvm-just-fuck-yourself-ok-YOU-forced-me-to-do-that"
            );

            Files.createDirectories(path);
            return RuntimeFile.createDefault(path.resolve(".game-runtime"));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
