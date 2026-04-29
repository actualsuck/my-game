package game.scene.novel;

import static com.raylib.Raylib.*;
import static game.util.TextureUtils.SafeUnloadTexture;
import static game.util.geometry.Lerp.lerp;

import com.raylib.Colors;
import game.Game;
import game.event.Listener;
import game.primitive.SharedData;
import game.scene.Scene;
import game.util.geometry.Rect;
import game.util.geometry.Vec2f;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

public abstract class NovelScene implements Scene, Listener {

    private Thread novelThread;

    private String title;
    private String text;

    private int textTyping;
    private long typingLastIncrement;

    protected Font titleFont;
    protected Font textFont;

    public int windowWidth;
    public int windowHeight;

    @Getter
    @Setter
    long typingSpeed;

    Map<String, NovelCharacter> characters;
    List<String> selectedCharacters;

    Map<String, Sound> voiceSounds;
    String currentVoiceSound;

    int madeUpClick = -1;

    NovelChoice choice;

    Texture jumpImage;
    float jumpImageScale;

    private String firstAction = null;
    private NovelAction lastAction = null;
    private String runningAction = null;
    private String nextAction = null;
    private Map<String, NovelAction> actions;

    @Override
    public void onStart() {
        actions = new HashMap<>();

        onActSetup();

        typingSpeed = 50;

        characters = new HashMap<>();
        selectedCharacters = new ArrayList<>();

        voiceSounds = new HashMap<>();
        currentVoiceSound = null;

        titleFont = SharedData.getInstance().getBoldFont();
        textFont = SharedData.getInstance().getRegularFont();

        title = null;
        text = null;
        planner = new LinkedList<>();

        jumpImage = null;

        choice = null;

        calcAnchors(GetScreenWidth(), GetScreenHeight());

        onNovelInit();

        novelThread = Thread.startVirtualThread(this::runNovel);

        Game.getInstance().getListenerExecutor().subscribeListener(this);
    }

    public void restartNovel() {
        novelThread.interrupt();
        novelThread = Thread.startVirtualThread(this::runNovel);
    }

    protected void act(String name, NovelAction action) {
        if (firstAction == null) firstAction = name;
        if (
            lastAction instanceof NovelActionAutoImpl lastActionAuto
        ) lastActionAuto.setNextAction(name);
        actions.put(name, action);
        lastAction = action;
    }

    protected void actNoClick(String name, NovelAction action) {
        act(
            name,
            new NovelAction() {
                @Override
                public String runAction() {
                    return action.runAction();
                }

                @Override
                public void afterAction(NovelScene novel) {}
            }
        );
    }

    protected void actAuto(String name, NovelActionAuto action) {
        act(name, new NovelActionAutoImpl(action, name));
    }

    protected void actAutoNoClick(String name, NovelActionAuto action) {
        actNoClick(name, new NovelActionAutoImpl(action, name));
    }

    protected void act(NovelAction action) {
        act("norm" + actions.size(), action);
    }

    protected void actNoClick(NovelAction action) {
        actNoClick("norm" + actions.size(), action);
    }

    protected void actAuto(NovelActionAuto action) {
        actAuto("auto" + actions.size(), action);
    }

    protected void actAutoNoClick(NovelActionAuto action) {
        actAutoNoClick("auto" + actions.size(), action);
    }

    public void setFirstAct(String name) {
        if (firstAction == null) firstAction = name;
    }

    public String getCurrentAct() {
        return runningAction;
    }

    protected abstract void onActSetup();

    protected void addVoiceSound(String name, Sound sound) {
        voiceSounds.put(name, sound);
    }

    protected void setCurrentVoice(String name) {
        currentVoiceSound = name;
    }

    protected void addChar(NovelCharacter character) {
        characters.put(character.getName(), character);
    }

    protected NovelCharacter getChar(String name) {
        return characters.get(name);
    }

    protected void selectChar(String name) {
        characters.get(name).onSelect();
        selectedCharacters.add(name);
    }

    protected void unselectChar(String name) {
        selectedCharacters.remove(name);
    }

    public abstract void onNovelInit();

    @Override
    public void onEnd() {
        novelThread.interrupt();

        if (jumpImage != null) SafeUnloadTexture(jumpImage);

        for (NovelCharacter character : characters.values())
            SafeUnloadTexture(character.getTexture());

        for (Sound sound : voiceSounds.values()) UnloadSound(sound);

        Game.getInstance().getListenerExecutor().unsubscribeListener(this);
    }

    public Rect boxBorderRect;
    public Rect boxRect;
    public Vec2f titlePos;
    public Vec2f textPos;
    public float titleFontSize;
    public float textFontSize;

    public void calcFontSize(int windowWidth, int windowHeight) {
        titleFontSize = 30;
        textFontSize = 25;
    }

    public void calcAnchors(int windowWidth, int windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;

        boxBorderRect = new Rect()
            .x((float) windowWidth / 20)
            .y(((float) windowHeight / 4) * 3 - 10)
            .width(((float) windowWidth / 20) * 18)
            .height((float) windowHeight / 4);

        boxRect = new Rect()
            .x((float) windowWidth / 20 + 2)
            .y(((float) windowHeight / 4) * 3 - 10 + 2)
            .width(((float) windowWidth / 20) * 18 - 4)
            .height((float) windowHeight / 4 - 4);

        calcFontSize(windowWidth, windowHeight);

        titlePos = new Vec2f(boxRect.x() + 10, boxRect.y() + 10);
        textPos = new Vec2f(
            boxRect.x() + 10,
            boxRect.y() + 10 + titleFontSize + 10
        );

        if (choice != null) choice.calcAnchors(windowWidth, windowHeight);
    }

    public void drawBackground() {
        ClearBackground(Colors.DARKPURPLE);
    }

    public void drawBox() {
        DrawRectangleRounded(
            boxBorderRect.toRectangle(),
            0.2f,
            10,
            Colors.PURPLE
        );
        DrawRectangleRounded(
            boxRect.toRectangle(),
            0.2f,
            10,
            Colors.DARKPURPLE
        );
    }

    public void drawTitle() {
        DrawTextEx(
            titleFont,
            title,
            titlePos.toVector2(),
            titleFontSize,
            2,
            Colors.WHITE
        );
    }

    public void drawTextLine(String line, float y) {
        DrawTextEx(
            textFont,
            line,
            textPos.add(0, y).toVector2(),
            textFontSize,
            2,
            Colors.WHITE
        );
    }

    private static float textWidth(Font font, float fontSize, String text) {
        return MeasureTextEx(font, text, fontSize, 2).x();
    }

    private static float textHeight(Font font, float fontSize, String text) {
        return MeasureTextEx(font, text, fontSize, 2).y();
    }

    public static List<String> dumpTextToLines(
        String text,
        Font font,
        float fontSize,
        float maxWidth
    ) {
        List<String> lines = new ArrayList<>();

        while (!text.isEmpty() && textWidth(font, fontSize, text) > maxWidth) {
            int l = 0;
            int r = text.length();

            while (l + 1 < r) {
                int m = (l + r) / 2;
                float w = textWidth(font, fontSize, text.substring(0, m));
                if (w > maxWidth) r = m;
                else if (w < maxWidth) l = m;
                else break;
            }

            for (int i = l; i > 0 && i >= l - 5; i--) if (
                text.charAt(i) == ' '
            ) {
                l = i;
                break;
            }

            int l2 = l;
            for (; l2 < text.length(); l2++) if (text.charAt(l2) != ' ') break;

            lines.add(text.substring(0, l));
            text = text.substring(l2);
        }

        if (!text.isEmpty()) lines.add(text);

        return lines;
    }

    public void drawText() {
        float maxWidth = boxRect.right() - textPos.x();
        String text = this.text.substring(0, textTyping);
        List<String> lines = dumpTextToLines(
            text,
            textFont,
            textFontSize,
            maxWidth
        );

        float y = 0;
        for (String line : lines) {
            drawTextLine(line, y);
            y += textHeight(textFont, textFontSize, line) + 4;
        }
    }

    public void incrementText() {
        if (
            System.currentTimeMillis() - typingLastIncrement > typingSpeed &&
            textTyping < text.length() &&
            textTyping != madeUpClick
        ) {
            do {
                textTyping += 1;
                if (textTyping == madeUpClick) {
                    madeUpClick = -1;
                    pushClick();
                    if (currentVoiceSound != null) {
                        PlaySound(voiceSounds.get(currentVoiceSound));
                    }
                    return;
                }
            } while (
                textTyping < text.length() && text.charAt(textTyping - 1) == ' '
            );
            typingLastIncrement = System.currentTimeMillis();
            if (currentVoiceSound != null) {
                PlaySound(voiceSounds.get(currentVoiceSound));
            }
        }
    }

    public void drawButtons() {}

    protected void drawChar(NovelCharacter character, Vec2f pos) {
        if (character.isHidden()) unselectChar(character.getName());

        DrawTextureEx(
            character.getTexture(),
            pos.add(windowWidth * character.getRelX(), 0).toVector2(),
            0,
            character.getScale(),
            Colors.WHITE
        );
    }

    public void drawJumpImage() {
        jumpImageScale = lerp(jumpImageScale, 1, 0.125f);
        DrawTextureEx(
            jumpImage,
            new Vec2f(
                windowWidth / 2 - (jumpImage.width() / 2) * jumpImageScale,
                windowHeight / 2 - (jumpImage.height() / 2) * jumpImageScale
            ).toVector2(),
            0,
            jumpImageScale,
            Colors.WHITE
        );
    }

    private LinkedList<Runnable> planner;

    public void drawOverlay() {}

    public void drawCharacters() {
        float total_width = 0;

        for (String name : new ArrayList<>(selectedCharacters)) {
            NovelCharacter character = characters.get(name);
            total_width += character.getRect().width() * character.getScale();
        }

        float x = (float) windowWidth / 2 - total_width / 2;

        for (String name : new ArrayList<>(selectedCharacters)) {
            NovelCharacter character = characters.get(name);
            character.tick();
            character.setX(
                lerp(
                    character.getX(),
                    -(
                        (float) windowWidth / 2 -
                        (character.getRect().width() * character.getScale()) / 2
                    ) + x,
                    0.125f
                )
            );
            Vec2f pos = new Vec2f(
                (float) windowWidth / 2 -
                    (character.getRect().width() * character.getScale()) / 2 -
                    character.getRect().x() * character.getScale() +
                    character.getX(),
                windowHeight -
                    character.getRect().height() * character.getScale() -
                    character.getRect().y() * character.getScale() -
                    character.getYPos()
            );
            drawChar(character, pos);
            x += character.getRect().width() * character.getScale();
        }
    }

    public void tick() {
        // TODO: move ticks in here
    }

    @Override
    public void draw() {
        for (int i = 0; i < planner.size(); i++) {
            planner.pop().run();
        }

        drawBackground();
        drawCharacters();
        drawBox();
        if (title != null) drawTitle();
        if (text != null) {
            drawText();
            incrementText();
        }

        if (choice != null) {
            choice.tick();
        } else {
            drawButtons();
        }

        if (jumpImage != null) drawJumpImage();

        drawOverlay();
    }

    @Override
    public void onMouseDown(int button) {
        if (jumpImage != null) {
            pushClick();
            UnloadTexture(jumpImage);
            jumpImage = null;
            return;
        }
        if (choice != null) {
            int variant = choice.getHoveringVariant();
            System.out.println(variant);
            if (variant >= 0 && variant < choice.getVariants().size()) {
                pushChoice(variant);
            }
            return;
        }
        if (text == null || textTyping >= text.length()) {
            pushClick();
        } else if (madeUpClick == -1) {
            textTyping = text.length();
        }
    }

    @Override
    public void onKeyDown(int keyCode, char key) {
        if (jumpImage != null) {
            if (keyCode == KEY_SPACE || keyCode == KEY_ENTER) {
                pushClick();
                UnloadTexture(jumpImage);
                jumpImage = null;
            }
            return;
        }
        if (keyCode == KEY_SPACE || keyCode == KEY_ENTER) {
            if (text == null || textTyping >= text.length()) {
                pushClick();
            }
        } else if (keyCode == KEY_LEFT_SHIFT || keyCode == KEY_RIGHT_SHIFT) {
            if (text != null && madeUpClick == -1) {
                textTyping = text.length();
            }
        }
    }

    @Override
    public void onWindowResize(
        int prev_width,
        int prev_height,
        int width,
        int height
    ) {
        calcAnchors(width, height);
    }

    private byte[] clickChannel = { 0 };

    protected void pushClick() {
        clickChannel[0] = (byte) (((int) clickChannel[0] + 1) % 256);
    }

    protected void waitClick() {
        byte was = clickChannel[0];
        while (clickChannel[0] == was) WaitTime(0.0001);
    }

    protected void plan(Runnable runnable) {
        planner.add(runnable);
    }

    protected void waitForPlans() {
        while (!planner.isEmpty()) WaitTime(0.0001);
    }

    protected void setMadeUpClick(int on_character) {
        madeUpClick = on_character;
    }

    protected void setText(String title, String text) {
        this.title = title;
        this.text = text;
        this.textTyping = 1;
        this.typingLastIncrement = System.currentTimeMillis();
    }

    private int[] choiceChannel = { -1 };

    protected void pushChoice(int variant) {
        choiceChannel[0] = variant;
    }

    protected int waitChoice() {
        while (choiceChannel[0] == -1) WaitTime(0.0001);
        int answ = choiceChannel[0];
        choiceChannel[0] = -1;
        choice = null;
        return answ;
    }

    protected void setupChoice(NovelChoice choice) {
        choice.setTitleFont(titleFont);
        choice.setTextFont(textFont);
        choice.calcAnchors(windowWidth, windowHeight);
        this.choice = choice;
    }

    protected void showImage(Image image) {
        jumpImage = LoadTextureFromImage(image);
        jumpImageScale = 0.5f;
    }

    protected void runNovel() {
        runningAction = firstAction;
        while (runningAction != null) {
            NovelAction act = actions.get(runningAction);
            nextAction = act.runAction();
            if (nextAction != null) act.afterAction(this);
            runningAction = nextAction;
        }
    }
}
