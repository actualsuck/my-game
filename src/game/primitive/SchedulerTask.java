package game.primitive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SchedulerTask implements Runnable {

    private long nextTick;
    private long interval;
    private long times;
    private boolean cancelled;
}
