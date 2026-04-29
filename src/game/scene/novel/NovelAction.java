package game.scene.novel;

public interface NovelAction {
    String runAction(); // return next action name

    default void afterAction(NovelScene novel) {
        novel.waitClick();
    }
}
