package game.primitive;

import game.Game;
import game.resources.ResourceLoader;
import game.resources.impl.Font;
import game.storage.Storage;
import lombok.Getter;

@Getter
public class SharedData {

    private final Game game;
    private final Storage storage;
    private final ResourceLoader resourceLoader;

    private Font menuFont;
    private Font regularFont;
    private Font boldFont;
    private Font italicFont;

    public SharedData(Game game) {
        this.game = game;

        this.storage = game.getStorage();
        this.resourceLoader = game.getResourceLoader();

        this.load();
    }

    public void load() {
        loadFonts();
    }

    public void loadFonts() {
        this.italicFont = resourceLoader.loadTtfFont(
            "assets/fonts/MomoSignature-Regular.ttf",
            100
        );
        this.menuFont = resourceLoader.loadTtfFont(
            "assets/fonts/ArchivoBlack-Regular.ttf",
            100
        );
        this.regularFont = resourceLoader.loadTtfFont(
            "assets/fonts/Roboto-Regular.ttf",
            100
        );
        this.boldFont = resourceLoader.loadTtfFont(
            "assets/fonts/Roboto-Bold.ttf",
            100
        );
    }

    public static SharedData getInstance() {
        return Game.getInstance().getSharedData();
    }
}
