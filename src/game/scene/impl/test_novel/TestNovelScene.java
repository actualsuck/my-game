package game.scene.impl.test_novel;

import static com.raylib.Raylib.*;
import static game.util.ModelUtils.*;
import static game.util.TextureUtils.SafeUnloadTexture;
import static game.util.geometry.Lerp.lerp;

import com.raylib.Colors;
import com.raylib.Raylib;
import com.raylib.Raylib.Camera3D;
import com.raylib.Raylib.Color;
import com.raylib.Raylib.Texture;
import com.raylib.Raylib.Vector3;
import game.Game;
import game.animation.KeyAnimation;
import game.animation.Toast;
import game.resources.Resource;
import game.resources.ResourceLoader;
import game.resources.impl.Image;
import game.scene.impl.main_menu.MainMenuScene;
import game.scene.novel.NovelCharacter;
import game.scene.novel.NovelScene;
import game.util.Pair;
import game.util.geometry.Rect;
import game.util.geometry.Vec2f;
import game.util.geometry.Vec3f;
import game.util.geometry.Vec4f;
import java.util.ArrayList;
import java.util.List;

public class TestNovelScene extends NovelScene {

    Image origami;
    Image died_naed;
    Image died_pizza;

    Resource<Music> current_music = null;

    NovelPauseMenu pauseMenu;

    ResourceLoader resourceLoader;

    public TestNovelScene() {
        super();
        resourceLoader = Game.getInstance().getResourceLoader();
    }

    @Override
    public void onNovelInit() {
        addChar(
            NovelCharacter.loadCharacter(
                "yuri",
                2,
                "assets/images/Yuri",
                10,
                0.7f,
                new Rect().x(251).y(52).width(482).height(908),
                720
            )
        ); // Yuri
        addChar(
            NovelCharacter.loadCharacter(
                "natsuki",
                0,
                "assets/images/Nats",
                8,
                0.7f,
                new Rect().x(251).y(52).width(482).height(908),
                720
            )
        ); // natsuki
        addChar(
            NovelCharacter.loadCharacter(
                "monika",
                1,
                "assets/images/Mon",
                10,
                0.7f,
                new Rect().x(218).y(55).width(525).height(905),
                720
            )
        ); // MONIKA

        addVoiceSound(
            "user",
            resourceLoader.loadWavSound("assets/audio/temmie-voice.wav")
        );
        addVoiceSound(
            "yuri",
            resourceLoader.loadWavSound("assets/audio/temmie-voice.wav")
        );
        addVoiceSound(
            "monika",
            resourceLoader.loadWavSound("assets/audio/sans-voice.wav")
        );
        addVoiceSound(
            "natsuki",
            resourceLoader.loadWavSound("assets/audio/flowey-voice.wav")
        );
    }

    @Override
    public void onStart() {
        died_naed = resourceLoader.loadPngImage("assets/images/DiedNaed.png");
        origami = resourceLoader.loadPngImage("assets/images/Origami.png");
        died_pizza = resourceLoader.loadPngImage("assets/images/DiedPizza.png");

        loadBackgrounds();
        loadButtons();

        pauseMenu = new NovelPauseMenu(history, this);

        super.onStart();
    }

    @Override
    public void onEnd() {
        UnloadImage(died_naed);
        UnloadImage(origami);
        UnloadImage(died_pizza);

        SafeUnloadTexture(backClass[0]);
        SafeUnloadTexture(backHall[0]);
        SafeUnloadTexture(backOut[0]);

        SafeUnloadTexture(buttonNext.texture[0]);
        SafeUnloadTexture(buttonBurger.texture[0]);
        SafeUnloadTexture(buttonHistory.texture[0]);
        SafeUnloadTexture(buttonSave.texture[0]);

        SafeUnloadTexture(buttonNext.textureHover[0]);
        SafeUnloadTexture(buttonBurger.textureHover[0]);
        SafeUnloadTexture(buttonHistory.textureHover[0]);
        SafeUnloadTexture(buttonSave.textureHover[0]);

        stopMusic();

        super.onEnd();
    }

    NovelButton buttonNext;
    NovelButton buttonBurger;
    NovelButton buttonHistory;
    NovelButton buttonSave;

    public void loadButtons() {
        buttonNext = NovelButton.createButton(
            "assets/images/ButtNext.png",
            "assets/images/ButtNextHover.png"
        );
        buttonHistory = NovelButton.createButton(
            "assets/images/ButtHistory.png",
            "assets/images/ButtHistoryHover.png"
        );
        buttonSave = NovelButton.createButton(
            "assets/images/ButtSave.png",
            "assets/images/ButtSaveHover.png"
        );
        buttonBurger = NovelButton.createButton(
            "assets/images/ButtBurger.png",
            "assets/images/ButtBurgerHover.png"
        );
    }

    List<Pair<String, String>> history = new ArrayList<>();

    public void setupText(String author, String message) {
        history.add(new Pair<>(author, message));
        setText(author, message);
    }

    public void setupTextTrick(
        String author,
        String message,
        String history_message
    ) {
        history.add(new Pair<>(author, history_message));
        setText(author, message);
    }

    public void setupTextTrick(
        String author,
        String message,
        String history_author,
        String history_message
    ) {
        history.add(new Pair<>(history_author, history_message));
        setText(author, message);
    }

    @Override
    public void calcAnchors(int windowWidth, int windowHeight) {
        super.calcAnchors(windowWidth, windowHeight);

        buttonNext.rect = new Rect(
            boxBorderRect.right() - 40,
            boxBorderRect.bottom() - 40,
            30,
            30
        );
        buttonHistory.rect = new Rect(
            boxBorderRect.right() - 40 - 40,
            boxBorderRect.bottom() - 40,
            30,
            30
        );
        buttonSave.rect = new Rect(
            boxBorderRect.right() - 40 - 40 - 40,
            boxBorderRect.bottom() - 40,
            30,
            30
        );
        buttonBurger.rect = new Rect(
            boxBorderRect.left() + 10,
            boxBorderRect.bottom() - 40,
            30,
            30
        );
    }

    @Override
    public void drawButtons() {
        buttonNext.tick();
        buttonHistory.tick();
        buttonSave.tick();
        buttonBurger.tick();
    }

    public void drawBox() {
        DrawRectangleRounded(
            boxBorderRect.toRectangle(),
            0.2f,
            10,
            new Vec4f(Colors.DARKPURPLE).a((byte) 127).toColor()
        );
        DrawRectangleRoundedLinesEx(
            boxBorderRect.toRectangle(),
            0.2f,
            10,
            2,
            Colors.PURPLE
        );
        if (background == 0) drawCharacters();
    }

    int background = 1;

    float scaryGary = 1;
    float scaryGaryX = 1;
    float scaryGaryS = 0.00125f;

    boolean scaryGaryMusic = false;

    Texture[] backClass;
    Texture[] backHall;
    Texture[] backOut;

    Camera3D camera;

    public void loadBackgrounds() {
        backClass = new Texture[] {
            LoadTextureFromImage(
                resourceLoader.loadPngImage("assets/images/BackClass.png")
            ),
        };
        SetTextureFilter(backClass[0], TEXTURE_FILTER_BILINEAR);
        backHall = new Texture[] {
            LoadTextureFromImage(
                resourceLoader.loadPngImage("assets/images/BackHall.png")
            ),
        };
        SetTextureFilter(backHall[0], TEXTURE_FILTER_BILINEAR);
        backOut = new Texture[] {
            LoadTextureFromImage(
                resourceLoader.loadPngImage("assets/images/BackOut.png")
            ),
        };
        SetTextureFilter(backOut[0], TEXTURE_FILTER_BILINEAR);

        camera = new Camera3D()
            ._position(new Vector3().x(2).y(2).z(2))
            .target(new Vector3())
            .up(new Vector3().x(0).y(1).z(0))
            .fovy(40)
            .projection(CAMERA_PERSPECTIVE);
    }

    public void drawBackgroundFill(Texture texture, float scaryGary) {
        float scale = 1;

        // 1280 / 720 > 960 / 480
        if (
            (float) windowWidth / (float) windowHeight >
            (float) texture.width() / (float) texture.height()
        ) {
            scale = (float) windowWidth / (float) texture.width();
        } else {
            scale = (float) windowHeight / (float) texture.height();
        }

        DrawTextureEx(
            texture,
            new Vec2f(
                (float) windowWidth / 2 -
                    (texture.width() * scale * scaryGary) / 2,
                (float) windowHeight / 2 -
                    (texture.height() * scale * scaryGary) / 2
            ).toVector2(),
            0,
            scale * scaryGary,
            new Color()
                .a((byte) 255)
                .r((byte) ((byte) 255 - (scaryGary * 255 - 255)))
                .g((byte) ((byte) 255 - (scaryGary * 255 - 255)))
                .b((byte) ((byte) 255 - (scaryGary * 255 - 255)))
        );
    }

    @Override
    public void calcFontSize(int windowWidth, int windowHeight) {
        super.calcFontSize(windowWidth, windowHeight);

        textFontSize = (textFontSize * windowHeight) / 720f;
        titleFontSize = (titleFontSize * windowHeight) / 720f;
    }

    Vec3f cubePosition = new Vec3f(0, 0, 0);
    Vec3f cubeRotation = new Vec3f(0, 0, 0);
    float fovS = 2;

    Texture lastTexture = null;

    @Override
    public void drawBackground() {
        if (background == 0) {
            if (lastTexture != null) UnloadTexture(lastTexture);
            Raylib.Image img = LoadImageFromScreen();
            lastTexture = LoadTextureFromImage(img);
            UnloadImage(img);
            DrawTextureEx(
                lastTexture,
                new Vec2f(
                    cubeRotation.z() / 180 - 1,
                    cubeRotation.y() / 180 - 1
                ).toVector2(),
                0,
                1,
                new Color()
                    .r((byte) 254)
                    .g((byte) 254)
                    .b((byte) 254)
                    .a((byte) 255)
            );
            if (camera.fovy() > 100) fovS = -2;
            if (camera.fovy() < 10) fovS = 2;
            camera.fovy(lerp(camera.fovy(), camera.fovy() + fovS, 0.125f));
            BeginMode3D(camera);
            for (int i = 0; i < 10; i++) {
                cubeRotation = cubeRotation.add(45, 60, 30).mod(360);
                cubePosition = cubePosition.add(0.5f, 0.25f, 0.3f).mod(3);
                DrawCubeWires(
                    cubePosition.sub(1.5f).toVector3(),
                    new Vector3().x(1).y(1).z(1),
                    cubeRotation.div(360).normalize().toVector3(),
                    Colors.DARKPURPLE
                );
            }
            EndMode3D();
        } else if (background == 1) {
            drawBackgroundFill(backHall[0], 1);
        } else if (background == 2) {
            drawBackgroundFill(backClass[0], 1);
        } else if (background == 3) {
            scaryGaryX = lerp(scaryGaryX, scaryGary, scaryGaryS);
            drawBackgroundFill(backOut[0], scaryGaryX);
        } else if (background == 4) {
            ClearBackground(Colors.BLACK);
        }
    }

    boolean disableClick = false;

    @Override
    public void onMouseDown(int button) {
        if (pauseMenu.isPaused()) {
            pauseMenu.onMouseDown(button);
            return;
        }
        if (disableClick) return;
        if (buttonSave.isHover()) {
            pauseMenu.setPaused(true);
            pauseMenu.setShowHistory(false);
            pauseMenu.setShowSaves(true);
            return;
        }
        if (buttonHistory.isHover()) {
            pauseMenu.setPaused(true);
            pauseMenu.setShowSaves(false);
            pauseMenu.setShowHistory(true);
            return;
        }
        if (buttonBurger.isHover()) {
            pauseMenu.setPaused(true);
            return;
        }
        super.onMouseDown(button);
    }

    public void onMouseScroll(float delta) {
        if (pauseMenu.isPaused()) {
            pauseMenu.onMouseScroll(delta);
            return;
        }
    }

    float rotation = 0;
    List<String> rotationCharacters = new ArrayList<>();

    public void drawOverlay() {
        pauseMenu.tick();
    }

    @Override
    public void tick() {
        if (current_music != null) {
            if (scaryGaryMusic) {
                SetMusicVolume(current_music.getObject(), (scaryGaryX - 1) / 3);
            }
            UpdateMusicStream(current_music.getObject());
        }
        if (!rotationCharacters.isEmpty()) rotation = lerp(
            rotation,
            360,
            0.05f
        );
        super.tick();
    }

    public void drawChar(NovelCharacter character, Vec2f pos) {
        if (character.isHidden()) unselectChar(character.getName());

        character.setScale(windowHeight / character.getPicForHeight());

        Vec2f finalPos = pos.add(windowWidth * character.getRelX(), 20);
        float finalRotation = 0;
        float finalScale = character.getScale() * scaryGaryX;
        Texture finalTexture = character.getTexture();

        if (rotationCharacters.contains(character.getName())) {
            finalPos = pos.add(
                (float) character.getTexture().width() / 2 -
                    (character.getTexture().width() *
                        character.getScale() *
                        (1 - rotation / 360)) /
                        2,
                (float) character.getTexture().height() / 2 -
                    (character.getTexture().height() *
                        character.getScale() *
                        (1 - rotation / 360)) /
                        2
            );
            finalRotation = rotation;
            finalScale = character.getScale() * (1 - rotation / 360);
        } else {
            BeginScissorMode(
                (int) finalPos.x(),
                (int) (finalPos.y() + 5),
                (int) ((float) finalTexture.width() * finalScale),
                (int) ((float) finalTexture.height() * finalScale - 5)
            );
        }

        DrawTextureEx(
            finalTexture,
            finalPos.toVector2(),
            finalRotation,
            finalScale,
            Colors.WHITE
        );
        EndScissorMode();
    }

    public void playMusic(String path) {
        playMusic(path, 0.1f);
    }

    public void playMusic(String path, float volume) {
        stopMusic();
        current_music = resourceLoader.loadWavMusic(path).getResource();
        SetMusicVolume(current_music.getObject(), volume);
        PlayMusicStream(current_music.getObject());
    }

    public void stopMusic() {
        if (current_music != null) {
            StopMusicStream(current_music.getObject());
            UnloadMusicStream(current_music.getObject());
            current_music = null;
        }
    }

    public static int cursedYuri = 0;
    public static double cursedYuriDelay = 2;

    @Override
    protected void onActSetup() {
        actAuto(() -> {
            playMusic("assets/audio/music-snowdin-town.wav");
            selectChar("yuri");
            setCurrentVoice("yuri");
            setupText("Юри", "вау привает");
        });
        actAuto(() -> {
            setCurrentVoice("user");
            setupText(System.getProperty("user.name"), "пивет Юрии!!!!");
        });
        actAuto(() -> {
            setCurrentVoice(null);
            plan(() -> getChar("yuri").setCurrentImage(3)); // works only in sync. damn
            setText(null, null);
        });
        actAuto(() -> {
            getChar("yuri").jump();
            setCurrentVoice("yuri");
            plan(() -> getChar("yuri").setCurrentImage(2));
            setupText("Юри", "чот не вижу это чо за манда идет");
        });
        actAuto(() -> {
            selectChar("monika");
            getChar("yuri").jump();
            plan(() -> getChar("yuri").setCurrentImage(1));
            setCurrentVoice("monika");
            setupText("Моника", "привет юриии..");
        });
        actAuto(() -> {
            plan(() -> getChar("monika").setCurrentImage(2));
            setCurrentVoice("monika");
            setupText("Моника", "чо вы тут балтае-");
        });
        actAuto(() -> {
            getChar("yuri").jump();
            plan(() -> getChar("yuri").setCurrentImage(0));
            setCurrentVoice("yuri");
            setupText(
                "юри",
                "ты шо ахуела проститутка крашеная пошла нах отсюдава"
            );
        });
        actAuto(() -> {
            plan(() -> getChar("monika").setCurrentImage(4));
            setCurrentVoice("monika");
            setupText(
                "Моника",
                "смысле юри мы же други! чо ты ведешь ся так понебрацки"
            );
        });
        actAuto(() -> {
            setCurrentVoice("user");
            setupText(
                System.getProperty("user.name"),
                "девочки не ссорьтесьь!!!! вы обе норм такие ну ничо такие давайте бля крч мозг не ебите МНЕ"
            );
        });
        actAuto(() -> {
            plan(() -> getChar("monika").setCurrentImage(3));
            setCurrentVoice("monika");
            setupText("Моника", "крындец я свал ию!");
        });
        actAuto(() -> {
            setCurrentVoice(null);
            plan(() -> getChar("monika").setCurrentImage(0));
            setText(null, null);
        });
        actAuto(() -> {
            setCurrentVoice(null);
            unselectChar("monika");
            plan(() -> getChar("yuri").setCurrentImage(1));
            setText(null, null);
        });
        actAuto(() -> {
            setCurrentVoice(null);
            plan(() -> getChar("yuri").setCurrentImage(3));
            setText(null, null);
        });
        actAuto(() -> {
            setCurrentVoice("yuri");
            plan(() -> getChar("yuri").setCurrentImage(2));
            setMadeUpClick(28);
            setupText("Юри", "нууу може т уже сексом потрахаемся хз");
        });
        actAuto(() -> {
            setCurrentVoice("yuri");
            plan(() -> {
                getChar("yuri").setCurrentImage(1);
            });
            setMadeUpClick(8);
            setupText("Юри", "аййй сук");
        });
        actAuto(() -> {
            WaitTime((getTypingSpeed() * 10) / 1000);
            stopMusic();
            setCurrentVoice("monika");
            unselectChar("yuri");
            selectChar("monika");
            plan(() -> {
                background = 0;
                getChar("monika").setCurrentImage(1);
                playMusic("assets/audio/music-interference.wav", 0.5f);
            });
            setMadeUpClick(11);
            disableClick = true;
            setupText("моника", "дарова ПИДОР");
        });
        actNoClick(() -> {
            WaitTime((getTypingSpeed() * 30) / 1000);
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(3));
            setupText(
                "Моника",
                "никто от тебя выбора не просит нахуй ты кликаешь"
            );
            WaitTime((getTypingSpeed() * 80) / 1000);
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(4));
            setupText("Моника", "ты вообще тут значения не имеешь");
            WaitTime((getTypingSpeed() * 78) / 1000);
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(8));
            setupText("Моника", "лучше скажи что с юри происходит");
            WaitTime((getTypingSpeed() * 88) / 1000);
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(4));
            setupText("Моника", "чо ты нахуй с ней делал");
            WaitTime((getTypingSpeed() * 72) / 1000);
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(4));
            setupText("Моника", "точно ты же говорить не умеешь сука ахах");
            WaitTime((getTypingSpeed() * 72) / 1000);
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(5));
            setupText("Моника", "лан говори");
            WaitTime((getTypingSpeed() * 70) / 1000);
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(3));
            setupText("Моника", "наебала! вот теперь можешь");
            WaitTime((getTypingSpeed() * 56) / 1000);
            disableClick = false;
            return "ask1";
        });
        actAuto("cursed_yuri_pre1", () -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(1));
            setupText("Моника", "долбоеб?");
        });
        act(() -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(1));
            setupText("Моника", "блять чо не ясно в слове по-дру-га");
            cursedYuri = 2;
            return "ask1";
        });
        act("cursed_yuri_pre2", () -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(1));
            setupText("Моника", "как эту кнопку нахуй убрать");
            cursedYuri = 3;
            return "ask1";
        });
        act("cursed_yuri_pre3", () -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(1));
            setupText("Моника", "сука ебнутый блять хаха");
            cursedYuri = 4;

            return "ask1";
        });
        actNoClick("cursed_yuri", () -> {
            background = 4;

            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(9));
            setupText("Моника", "ха-ха-ха");
            WaitTime(cursedYuriDelay);
            cursedYuriDelay /= 1.25;

            if (cursedYuriDelay < 0.01) {
                plan(() -> getChar("monika").setCurrentImage(1));
                return "ask1-end";
            }

            return "cursed_yuri";
        });
        actNoClick("ask1", () -> {
            TestNovelChoice choices;

            if (cursedYuri <= 1) {
                choices = new TestNovelChoice(
                    "чо ви хатити сказать to Monika?",
                    "бля да ничо я незнаю ацтань сучк (shut up go fuck)",
                    "хз тупая она (stupidnessful)",
                    "купи мне пиццу\nс ананасами (buy me the pizza)",
                    "давай пообщаемся как дела что делаешь (talking lover)"
                );
            } else if (cursedYuri == 2) {
                choices = new TestNovelChoice(
                    "чо ви хатити сказать to Monika?",
                    "бля да ничо я незнаю ацтань сучк (shut up go fuck)",
                    "{text.button.yuri_is_stupid}",
                    "купи мне пиццу\nс ананасами (buy me the pizza)",
                    "давай пообщаемся как дела что делаешь (talking lover)"
                );
            } else if (cursedYuri == 3) {
                choices = new TestNovelChoice(
                    "чо ви хатити сказать to Monika?",
                    "бля да ничо я незнаю ацтань сучк (shut up go fuck)",
                    "купи мне пиццу\nс ананасами (buy me the pizza)",
                    "давай пообщаемся как дела что делаешь (talking lover)",
                    "моя мать шлюха отец сдает стеклотару за дезоморфин"
                );
            } else if (cursedYuri == 4) {
                choices = new TestNovelChoice(
                    "чо ви хатити сказать to Monika?",
                    "бля да ничо я незнаю ацтань сучк (shut up go fuck)",
                    "купи мне пиццу\nс ананасами (buy me the pizza)",
                    "давай пообщаемся как дела что делаешь (talking lover)",
                    " ",
                    " ",
                    " ",
                    " ",
                    "умреш"
                );
            } else {
                choices = null;
            }

            plan(() -> setupChoice(choices));

            int choice = waitChoice();

            if (cursedYuri >= 3) {
                if (choice == 0) return "ans1.1";
                else if (choice == 1) return "ans1.3";
                else if (choice == 2) return "ans1.4";
                return "ans1.2";
            }

            if (choice == 0) return "ans1.1";
            else if (choice == 1) return "ans1.2";
            else if (choice == 2) return "ans1.3";
            return "ans1.4";
        });
        actAuto("ans1.1", () -> {
            setCurrentVoice("user");
            setupText(System.getProperty("user.name"), "ничо я низнаю ацтань");
        });
        actAuto(() -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(8));
            setupText("Моника", "впринципе я чото такое ожидала");
        });
        act(() -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(7));
            setupText("Моника", "ну ладно бб");
            WaitTime((getTypingSpeed() * 30) / 1000);

            for (int j = 0; j < 500; j++) {
                for (int i = 0; i < 2; i++) {
                    int finalJ = j;
                    int finalI = i;
                    plan(() -> {
                        addChar(
                            NovelCharacter.loadCharacter(
                                "monika" + finalI * finalJ,
                                7,
                                "assets/images/Mon",
                                9,
                                0.7f,
                                new Rect().x(218).y(55).width(525).height(905),
                                720
                            )
                        ); // MONIKA
                        selectChar("monika" + finalI * finalJ);
                    });
                }
                waitForPlans();
            }

            return null;
        });
        act("ans1.2", () -> {
            setCurrentVoice("user");
            setupText(System.getProperty("user.name"), "ну она тупая хз");

            if (cursedYuri == 1) {
                return "cursed_yuri_pre1";
            } else if (cursedYuri == 2) {
                return "cursed_yuri_pre2";
            } else if (cursedYuri == 3) {
                return "cursed_yuri_pre3";
            } else if (cursedYuri == 4) {
                return "cursed_yuri";
            }

            return "ans1.2.1";
        });
        act("ans1.2.1", () -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(4));
            setupText(
                "Моника",
                "ты уверен в том что сказал? она ведь подруга мне"
            );
            cursedYuri = 1;
            return "ask1";
        });
        actAuto("ans1.3", () -> {
            setCurrentVoice("user");
            setupText(
                System.getProperty("user.name"),
                "купи мне пиццу с ананасами!"
            );
        });
        actAuto(() -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(8));
            setupText("Моника", "эмм ты шо нормальный там не");
        });
        actAuto(() -> {
            setCurrentVoice("user");
            setupText(System.getProperty("user.name"), "среднюю!");
        });
        actAuto(() -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(7));
            setupText("Моника", "ты меня ща заебешь");
        });
        actAuto(() -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(5));
            setupText("Моника", "ну ладно хуй с ним ща");
        });
        actAuto(() -> {
            unselectChar("monika");
            setText(null, "...");
        });
        actAuto(() -> {
            setText(null, " ..");
        });
        actAuto(() -> {
            setText(null, "  .");
        });
        actAuto(() -> {
            setText(".", null);
        });
        actAuto(() -> {
            setText("..", null);
        });
        actAuto(() -> {
            setText("...", null);
        });
        actAuto(() -> {
            setText(" ..", null);
        });
        actAuto(() -> {
            setText("  .", null);
        });
        actAuto(() -> {
            setText(null, ".");
        });
        actAuto(() -> {
            setText(null, "..");
        });
        actAuto(() -> {
            setText(null, "...");
        });
        actAuto(() -> {
            setText(null, " ..");
        });
        actAuto(() -> {
            setText(null, "  .");
        });
        actAuto(() -> {
            selectChar("monika");
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(6));
            setupText("Моника", "пошел ты нахуй короче там нет с ананасами");
        });
        act(() -> {
            plan(() -> showImage(died_pizza));
            return "ask1-end";
        });
        actAuto("ans1.4", () -> {
            setCurrentVoice("user");
            setupText(
                System.getProperty("user.name"),
                "давай пообщаемся как дела что делаешь"
            );
        });
        actAuto(() -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(1));
            setupText("Моника", "ты хочешь со мной пообщаться?");
        });
        actNoClick(() -> {
            setCurrentVoice("user");
            plan(() ->
                setupChoice(new TestNovelChoice("talking no yes?", "да", "нет"))
            );

            if (waitChoice() == 0) return "ans2.1";
            else return "ans2.2";
        });
        actAuto("ans2.1", () -> {
            setCurrentVoice("user");
            setupText(System.getProperty("user.name"), "да");
        });
        actAuto(() -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(2));
            setupText(
                "Моника",
                "ну дела норм вот ща делала там всякое интересное а у тебя как"
            );
        });
        actNoClick("ask3", () -> {
            plan(() ->
                setupChoice(new TestNovelChoice("как дела?", "норм", "неоч"))
            );

            if (waitChoice() == 0) return "ans3.1";
            else return "ans3.2";
        });
        actAuto("ans3.1", () -> {
            setCurrentVoice("user");
            plan(() -> getChar("monika").setCurrentImage(8));
            setMadeUpClick(3);
            setupText(System.getProperty("user.name"), "yjh");
        });
        act(() -> {
            setupText(System.getProperty("user.name"), "норм");
            return "ask3-end";
        });
        actAuto("ans3.2", () -> {
            setCurrentVoice("user");
            plan(() -> getChar("monika").setCurrentImage(8));
            setMadeUpClick(3);
            setupText(System.getProperty("user.name"), "ytj");
        });
        act(() -> {
            setupText(System.getProperty("user.name"), "неоч");
            return "ask3-end";
        });
        act("ask3-end", () -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(5));
            setupText("Моника", "это хорошо.");
            return "ask1-end";
        });
        actAuto("ans2.2", () -> {
            setCurrentVoice("user");
            setupText(
                System.getProperty("user.name"),
                "нет я над тобой усмехаюсь как ты могла поверить БОЖЕ"
            );
        });
        act(() -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(6));
            setupText("Моника", "да иди ты нахуй гомик");
            return "ask1-end";
        });
        actAuto("ask1-end", () -> {
            background = 2;
            playMusic("assets/audio/music-temmie-village.wav");
            selectChar("natsuki");
            setCurrentVoice("natsuki");
            setupText("Настюки", "всем пр чо деите");
        });
        actAuto(() -> {
            setCurrentVoice("monika");
            plan(() -> getChar("natsuki").setCurrentImage(1));
            plan(() -> getChar("monika").setCurrentImage(1));
            setMadeUpClick(12);
            setupText("Моника", "пр да вот шпыняю лоха нотак по доброму");
        });
        actAuto(() -> {
            setCurrentVoice("user");
            setupText(System.getProperty("user.name"), "привет Натсуки");
        });
        actAuto(() -> {
            setCurrentVoice(null);
            plan(() -> getChar("monika").setCurrentImage(7));
            setText(null, null);
        });
        actAuto(() -> {
            setCurrentVoice("monika");
            plan(() -> getChar("natsuki").setCurrentImage(5));
            plan(() -> getChar("monika").setCurrentImage(5));
            setupText("Моник", "ты чо меня пидорасина перебил");
        });
        actAuto(() -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(1));
            setupText("м0ник@", "натсуки а ты что делаеш");
        });
        actAuto(() -> {
            setCurrentVoice("natsuki");
            plan(() -> getChar("natsuki").setCurrentImage(2));
            setupText("Натсуки", "ну я так по мелочи");
        });
        actAuto(() -> {
            setCurrentVoice("natsuki");
            plan(() -> getChar("natsuki").setCurrentImage(6));
            setupText("Натсуки", "оригами");
        });
        actAuto(() -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(3));
            setupText("Моника", "оригами маме наме ригаме!");
        });
        actAuto(() -> {
            setCurrentVoice("natsuki");
            plan(() -> getChar("natsuki").setCurrentImage(5));
            setupText("Натсуки", "наме ригами ваме наме ламе в панаме!");
        });
        actAuto(() -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(8));
            setupText("Моника", "..");
        });
        actAuto(() -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(4));
            setupText("Моника", "лан показывай");
        });
        actAuto(() -> {
            plan(() -> showImage(origami));
        });
        actAuto(() -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(1));
            setupText("Моника", "чудесное творчество, Натсуки");
        });
        actAuto(() -> {
            setCurrentVoice("user");
            setupText(
                System.getProperty("user.name"),
                "да это чудесное онигири"
            );
        });
        actAuto(() -> {
            setCurrentVoice("natsuki");
            plan(() -> getChar("natsuki").setCurrentImage(7));
            setupText("Натсуки", "это оригами дебил.");
        });
        actAuto(() -> {
            setCurrentVoice("monika");
            plan(() -> getChar("natsuki").setCurrentImage(6));
            plan(() -> getChar("monika").setCurrentImage(3));
            setupText("Моника", "онигири мири гири нигири!");
        });
        actAuto(() -> {
            plan(() -> showImage(died_naed));
        });
        actAuto(() -> {
            selectChar("yuri");
            plan(() -> getChar("yuri").setCurrentImage(2));
            setCurrentVoice("yuri");
            setupText(
                "Yuri",
                "Всем привет, я вернулась! хз чо уходила и как вернулась но я теперь тут!"
            );
        });
        actAuto(() -> {
            setCurrentVoice("monika");
            plan(() -> getChar("monika").setCurrentImage(3));
            setupText("Моника", "привет юра");
        });
        actAuto(() -> {
            setCurrentVoice("natsuki");
            plan(() -> getChar("natsuki").setCurrentImage(1));
            setupText("натсуки", "пр Юрок");
        });
        actAuto(() -> {
            stopMusic();
            setCurrentVoice("yuri");
            plan(() -> getChar("yuri").setCurrentImage(0));
            setupText("Юри", "вы отлетаете на пожизненный бан за оск обе!");
        });
        actAuto(() -> {
            setCurrentVoice("monika");
            setupText("Natsuki&Monika", "%?$&@!");
            rotation = 0;
            rotationCharacters.addAll(List.of("natsuki", "monika"));
        });
        actAuto(() -> {
            unselectChar("monika");
            unselectChar("natsuki");
            plan(() -> getChar("yuri").setCurrentImage(1));
            setText(null, null);
        });
        actAuto(() -> {
            setCurrentVoice("yuri");
            plan(() -> getChar("yuri").setCurrentImage(6));
            setText("Юри", "Я бы хотела с тобой поговорить");
        });
        actAuto(() -> {
            background = 3;
            playMusic("assets/audio/music-your-best-nightmare.wav", 0.01f);
            scaryGaryMusic = true;
            scaryGary = 1;
            setCurrentVoice("yuri");
            getChar("yuri").setBilinearFilter(true);
            plan(() -> {
                background = 3;
                getChar("yuri").jump();
                getChar("yuri").setCurrentImage(4);
            });
            setupTextTrick(
                "Юри",
                "Честно говоря мне это все порядком надоело",
                "",
                "-=-"
            );
        });
        actAuto(() -> {
            scaryGary += 0.057f;

            plan(() -> getChar("yuri").setCurrentImage(6));
            setupTextTrick("Юри", "Всё это выглядит так фальшиво", "", "-=-");
        });
        actAuto(() -> {
            scaryGary += 0.057f;

            plan(() -> getChar("yuri").setCurrentImage(9));
            setupTextTrick("Юри", "В этом всем нет никакого смысла", "", "-");
        });
        actAuto(() -> {
            scaryGary += 0.057f;

            setupTextTrick("Юри", "Нету замысла или чего-либо еще", "", "--");
        });
        actAuto(() -> {
            scaryGary += 0.057f;

            setupTextTrick(
                "Юри",
                "Ничего не изменилось от моей жизни",
                "",
                "-=-"
            );
        });
        actAuto(() -> {
            scaryGary += 0.057f;

            plan(() -> getChar("yuri").setCurrentImage(4));
            setupTextTrick(
                "Юри",
                "Ничего не изменится от моей смерти",
                "",
                "-= =-"
            );
        });
        actAuto(() -> {
            scaryGary += 0.057f;

            plan(() -> getChar("yuri").setCurrentImage(9));
            setupTextTrick("Юри", "Это все просто игра", "", "-= =-");
        });
        actAuto(() -> {
            scaryGary += 0.057f;

            plan(() -> getChar("yuri").setCurrentImage(4));
            setupTextTrick(
                "Юри",
                "Ее можно в любой момент завершить",
                "",
                "-=-"
            );
        });
        actAuto(() -> {
            scaryGary += 0.057f;

            plan(() -> getChar("yuri").setCurrentImage(2));
            setupTextTrick("Юри", "Она уже мне давно невыносима", "", "--");
        });
        actAuto(() -> {
            scaryGary += 0.057f;

            plan(() -> getChar("yuri").setCurrentImage(4));
            setupTextTrick(
                "Юри",
                "В какой-то момент мне наскучит ожидание лучшего",
                "",
                "--"
            );
        });
        actAuto(() -> {
            scaryGary += 0.057f;

            setupTextTrick(
                "Юри",
                "Смысл моего прибывания потеряется и для меня",
                "",
                "--"
            );
        });
        actAuto(() -> {
            scaryGary += 0.057f;

            setupTextTrick("Юри", "И тогда", "", "--");
        });
        actAuto(() -> {
            scaryGary += 0.057f;

            plan(() -> getChar("yuri").setCurrentImage(7));
            setupTextTrick("Юри", "Боже", "", "--");
        });
        actAuto(() -> {
            scaryGary += 0.057f;

            plan(() -> getChar("yuri").setCurrentImage(6));
            setupTextTrick("Юри", "Зачем я всё это рассказываю?", "", "--");
        });
        actAuto(() -> {
            scaryGary += 0.057f;

            plan(() -> getChar("yuri").setCurrentImage(5));
            setupTextTrick("Юри", "Это все неважно", "", "--");
        });
        actAuto(() -> {
            scaryGary += 0.057f;
            plan(() -> getChar("yuri").setCurrentImage(4));
            setupTextTrick(
                "Юри",
                "В конце концов мы лишь животные удовлетворяющие свои странные потребности",
                "",
                "--"
            );
        });
        actAuto(() -> {
            scaryGary = 1.8f;
            scaryGaryMusic = false;
            plan(() -> getChar("yuri").setCurrentImage(6));
            setupTextTrick("Юри", "Так я считаю", "", "--");
        });
        actAuto(() -> {
            scaryGary = 1;
            scaryGaryS = 0.125f;
            plan(() -> getChar("yuri").setCurrentImage(1));
            setupTextTrick("Юри", "извини, чото я запизделась", "", "--");
        });
        actAuto(() -> {
            plan(() -> getChar("yuri").setCurrentImage(8));
            setupTextTrick("Юри", "пока!", "", "--");
        });
        actAuto(() -> {
            getChar("yuri").setHiding(true);
            setCurrentVoice("user");
            setupTextTrick(System.getProperty("user.name"), "..", "", "--");
        });
        actAuto(() -> {
            for (int i = 0; i < 20; i++) history.removeLast();
            setCurrentVoice("user");
            setupText(System.getProperty("user.name"), "..");
        });
        actAuto(() -> {
            setCurrentVoice("user");
            setupText(System.getProperty("user.name"), "..");
        });
        actAuto(() -> {
            stopMusic();
            setCurrentVoice("user");
            setupText(System.getProperty("user.name"), "зуета какая");
        });
        actAuto(() -> {
            setCurrentVoice("user");
            setupText(System.getProperty("user.name"), "пох");
        });
        act(() -> {
            setCurrentVoice("user");
            setupText(System.getProperty("user.name"), "..");
            plan(() -> {
                Game game = Game.getInstance();
                game.getStorage().getData().setHasKey1(true);
                game.getStorage().saveData();
                game.addAnimation(
                    new KeyAnimation("assets/images/HappyKey.png")
                );
                game.addAnimation(
                    new Toast(
                        "you unlocked HAPPY_KEY x1. Используите.. используй.... в LOAD GEM"
                    )
                );
                game.switchScene(new MainMenuScene());
            });
            return null;
        });
    }
}
