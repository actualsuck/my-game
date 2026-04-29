package game.scene.impl.test_novel;

import static com.raylib.Raylib.*;
import static game.scene.novel.NovelChoice.containsRect;
import static game.scene.novel.NovelScene.dumpTextToLines;
import static game.util.geometry.Lerp.lerp;

import com.raylib.Colors;
import com.raylib.Raylib.Font;
import game.Game;
import game.primitive.SharedData;
import game.primitive.Tickable;
import game.resources.ResourceLoader;
import game.resources.impl.Sound;
import game.scene.impl.main_menu.MainMenuScene;
import game.scene.novel.NovelScene;
import game.util.Pair;
import game.util.geometry.Rect;
import game.util.geometry.Vec2f;
import game.util.geometry.Vec4f;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NovelPauseMenu implements Tickable {

    private boolean paused;
    private float rel;

    private boolean showHistory;
    private float historyRel;
    private List<Pair<String, String>> history;
    private float scrollHistoryX;
    private float scrollHistory;

    private boolean showSaves;
    private float savesRel;
    private Rect savesRect;

    private Rect rect;
    private Rect historyRect;

    private Font titleFont;
    private Font textFont;

    private float titleFontSize;
    private float textFontSize;
    private float buttonFontSize;

    private List<Pair<String, Runnable>> buttons;

    private Sound quack;
    private Texture kitty;

    private NovelScene scene;

    public NovelPauseMenu(
        List<Pair<String, String>> history,
        NovelScene scene
    ) {
        this.scene = scene;
        this.paused = false;
        this.rel = 0;

        this.showHistory = false;
        this.historyRel = 1;
        this.history = history;

        this.savesRel = 1;

        this.scrollHistory = 0;
        this.scrollHistoryX = 0;

        this.titleFont = SharedData.getInstance().getBoldFont();
        this.textFont = SharedData.getInstance().getRegularFont();

        this.titleFontSize = 30;
        this.textFontSize = 25;
        this.buttonFontSize = 40;

        ResourceLoader resourceLoader = Game.getInstance().getResourceLoader();

        this.quack = resourceLoader.loadWavSound("assets/audio/mac-quack.wav");
        this.kitty = LoadTextureFromImage(
            resourceLoader.loadJpgImage("assets/images/KittyHelpless.jpg")
        );

        this.buttons = new ArrayList<>();
        this.buttons.add(
            new Pair<>("leave to menu", () -> {
                Game.getInstance().switchScene(new MainMenuScene());
            })
        );
        this.buttons.add(
            new Pair<>("leave to game", () -> {
                paused = false;
                showHistory = false;
                showSaves = false;
            })
        );
        this.buttons.add(
            new Pair<>("show history", () -> {
                showHistory = !showHistory;
                showSaves = false;
            })
        );
        this.buttons.add(
            new Pair<>("show saves", () -> {
                showSaves = !showSaves;
                showHistory = false;
            })
        );
        this.buttons.add(
            new Pair<>("duck", () -> {
                PlaySound(quack);
            })
        );
    }

    public void onMouseScroll(float delta) {
        Vec2f pos = new Vec2f(GetMousePosition());

        if (showHistory && containsRect(historyRect, pos)) {
            scrollHistoryX = Math.max(0, scrollHistoryX + delta * 30);
        }
    }

    public void onMouseDown(int button) {
        Vec2f pos = new Vec2f(GetMousePosition());

        if (containsRect(rect, pos)) {
            for (int i = 0; i < buttons.size(); i++) {
                Rect rect = indexToButtonRect(i);

                if (containsRect(rect, pos)) {
                    buttons.get(i).getSecond().run();
                    break;
                }
            }
        } else if (showHistory && containsRect(historyRect, pos)) {
            scrollHistoryX = 0;
        } else {
            paused = false;
            showHistory = false;
            showSaves = false;
        }
    }

    public Rect indexToButtonRect(int index) {
        return indexTextToButtonRect(index, buttons.get(index).getFirst());
    }

    public Rect indexTextToButtonRect(int index, String text) {
        Vector2 size = MeasureTextEx(titleFont, text, buttonFontSize, 2);
        return new Rect(
            rect.x() + 20,
            rect.y() + 20 + (buttonFontSize + 5) * index,
            size.x(),
            size.y()
        );
    }

    public void drawMenu() {
        DrawRectangleRounded(rect.toRectangle(), 0.1f, 10, Colors.DARKPURPLE);
        DrawRectangleRoundedLinesEx(
            rect.toRectangle(),
            0.1f,
            10,
            2,
            Colors.PURPLE
        );

        if (rel > 0.1) {
            Vec2f pos = new Vec2f(GetMousePosition());

            int index = 0;
            for (Pair<String, Runnable> pair : buttons) {
                String title = pair.getFirst();
                Rect rect = indexTextToButtonRect(index, title);
                DrawTextEx(
                    titleFont,
                    title,
                    rect.pos().toVector2(),
                    buttonFontSize,
                    2,
                    containsRect(rect, pos) ? Colors.LIGHTGRAY : Colors.WHITE
                );
                index++;
            }
        }
    }

    public void drawHistory() {
        DrawRectangleRounded(
            historyRect.toRectangle(),
            0.1f,
            10,
            Colors.DARKPURPLE
        );
        DrawRectangleRoundedLinesEx(
            historyRect.toRectangle(),
            0.1f,
            10,
            2,
            Colors.PURPLE
        );

        if (historyRel < 0.9) {
            BeginScissorMode(
                (int) historyRect.x(),
                (int) historyRect.y(),
                (int) historyRect.width(),
                (int) historyRect.height()
            );

            scrollHistory = lerp(scrollHistory, scrollHistoryX, 0.125f);
            float y = historyRect.bottom() - 20 + scrollHistory;

            for (int i = history.size() - 1; i >= 0 && y >= 0; i--) {
                Vec2f textPos = new Vec2f(
                    historyRect.left() + 20 + 150,
                    y - titleFontSize
                );

                Pair<String, String> pair = history.get(i);
                String title = pair.getFirst();
                String text = pair.getSecond();

                float maxWidth = historyRect.right() - 20 - textPos.x();
                List<String> lines = dumpTextToLines(
                    text,
                    textFont,
                    textFontSize,
                    maxWidth
                );

                for (String line : lines.reversed()) {
                    DrawTextEx(
                        textFont,
                        line,
                        textPos.toVector2(),
                        textFontSize,
                        2,
                        Colors.WHITE
                    );
                    textPos = textPos.sub(0, textFontSize + 4);
                }

                y -= (lines.size() - 1) * (textFontSize + 4);

                Vec2f titlePos = new Vec2f(
                    historyRect.left() + 20,
                    y - titleFontSize
                );
                if (
                    i == 0 || history.get(i - 1).getFirst() != title
                ) DrawTextEx(
                    titleFont,
                    title,
                    titlePos.toVector2(),
                    titleFontSize,
                    2,
                    Colors.WHITE
                );

                y -= titleFontSize + 5;
            }

            EndScissorMode();
        }
    }

    public void drawSaves() {
        DrawRectangleRounded(
            savesRect.toRectangle(),
            0.1f,
            10,
            Colors.DARKPURPLE
        );
        DrawRectangleRoundedLinesEx(
            savesRect.toRectangle(),
            0.1f,
            10,
            2,
            Colors.PURPLE
        );

        if (savesRel < 0.9) {
            BeginScissorMode(
                (int) savesRect.x(),
                (int) savesRect.y(),
                (int) savesRect.width(),
                (int) savesRect.height()
            );
            Rect kittyRect = new Rect(0, 0, kitty.width(), kitty.height());
            Rect kittyDestRect = new Rect(
                0,
                0,
                savesRect.width() - 40,
                savesRect.height() - 40
            );
            Rect kittyPos = new Rect(
                savesRect.x() + 20,
                savesRect.y() + 20,
                savesRect.width() - 40,
                savesRect.height() - 40
            );
            kittyPos.pos(kittyPos.pos().add(kittyDestRect.center()));
            DrawTexturePro(
                kitty,
                kittyRect.toRectangle(),
                kittyPos.toRectangle(),
                kittyDestRect.center().toVector2(),
                (float) GetTime() * 45,
                Colors.WHITE
            );
            DrawTextEx(
                titleFont,
                "мне лениво",
                kittyPos.pos().toVector2(),
                titleFontSize,
                5,
                Colors.RED
            );
            EndScissorMode();
        }
    }

    @Override
    public void tick() {
        float screen_width = (float) GetScreenWidth();
        float screen_height = (float) GetScreenHeight();

        rel = lerp(rel, paused ? 1 : 0, 0.125f);
        historyRel = lerp(historyRel, showHistory ? 0 : 1, 0.125f);
        savesRel = lerp(savesRel, showSaves ? 0 : 1, 0.125f);

        rect = new Rect(
            (screen_width / 2 + 60) * rel - 50 - screen_width / 2,
            10,
            screen_width / 2 - 20,
            screen_height / 2
        );
        historyRect = new Rect(
            screen_width / 2 + historyRel * (screen_width / 2 + 50),
            10,
            screen_width / 2 - 10,
            screen_height - 20
        );
        savesRect = new Rect(
            screen_width / 2 + savesRel * (screen_width / 2 + 50),
            10,
            screen_width / 2 - 10,
            screen_height - 20
        );

        DrawRectangle(
            0,
            0,
            GetScreenWidth(),
            GetScreenHeight(),
            new Vec4f(Colors.BLACK).a((byte) (128f * rel)).toColor()
        );

        drawMenu();
        drawHistory();
        drawSaves();
    }
}
