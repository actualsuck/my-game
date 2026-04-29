package game;

import static com.raylib.Raylib.*;
import static com.raylib.Raylib.CloseWindow;

import game.event.ListenerExecutor;
import game.primitive.Animation;
import game.primitive.Scheduler;
import game.primitive.SharedData;
import game.primitive.Tickable;
import game.resources.ResourceLoader;
import game.scene.Scene;
import game.scene.impl.main_menu.MainMenuScene;
import game.storage.Storage;
import game.storage.StorageData;
import game.util.RuntimeFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class Game implements Tickable {

    @Getter
    private static Game instance;

    private Scene scene;
    private Scheduler scheduler;
    private SharedData sharedData;
    private ListenerExecutor listenerExecutor;
    private RuntimeFile runtimeFile;
    private Storage storage;
    private ResourceLoader resourceLoader;

    public Game() {
        instance = this;

        this.scene = new MainMenuScene();
        this.runtimeFile = findRuntimeFile();
        this.storage = new Storage(runtimeFile.findDataDirectory());
        this.resourceLoader = new ResourceLoader(
            runtimeFile.findResourcesLocation()
        );
    }

    public void loopInit() {
        this.storage.loadData();
        this.sharedData = new SharedData(this);
        this.scheduler = new Scheduler();
        this.listenerExecutor = new ListenerExecutor();
        this.extraAnimation = new ArrayList<>();
    }

    public void mainLoop() {
        SetWindowState(FLAG_WINDOW_RESIZABLE);
        InitWindow(1280, 720, "hjkhjk!");
        InitAudioDevice();
        SetTargetFPS(60);
        SetExitKey(0);

        loopInit();

        scene.onStart();

        while (!WindowShouldClose()) {
            tick();
        }

        scene.onEnd();

        CloseAudioDevice();

        CloseWindow();
    }

    public void switchScene(Scene newScene) {
        scene.onEnd();
        scene = newScene;
        scene.onStart();
    }

    public void tick() {
        listenerExecutor.tick();
        scheduler.tick();
        scene.tick();

        draw();
    }

    public void draw() {
        BeginDrawing();

        scene.draw();
        extraDraw();

        EndDrawing();
    }

    private List<Animation> extraAnimation;

    public void addAnimation(Animation anim) {
        extraAnimation.add(anim);
        anim.onStart();
    }

    public void extraDraw() {
        extraAnimation.removeIf(anim -> {
            boolean res = anim.draw();
            if (!res) anim.onEnd();
            return !res;
        });
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

    public static StorageData getStorageData() {
        return getInstance().getStorage().getData();
    }
}
