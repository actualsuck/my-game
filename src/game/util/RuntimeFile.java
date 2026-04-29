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
}
