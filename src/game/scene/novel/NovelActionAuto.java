package game.scene.novel;

public interface NovelActionAuto {
    void runActionAuto();

    default void afterAction(NovelScene novel) {
        novel.waitClick();
    }
}
