package game.scene.novel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class NovelActionAutoImpl implements NovelAction {

    final NovelActionAuto auto;
    final String currentAction;
    String nextAction = null;

    public String runAction() {
        auto.runActionAuto();
        return nextAction;
    }

    @Override
    public void afterAction(NovelScene novel) {
        auto.afterAction(novel);
    }
}
