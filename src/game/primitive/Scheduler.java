package game.primitive;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class Scheduler implements Tickable {

    private long living; // ticks only while tasks exist, otherwise it is zero
    private List<SchedulerTask> tasks;

    public Scheduler() {
        living = 0;
        tasks = new ArrayList<>();
    }

    // -1 times for infinite
    // 1 time for onetime task lol
    // 0 times for no task at all
    // delay and interval are measured in ticks - 60 ticks in second
    public void scheduleTask(
        long delay,
        long interval,
        long times,
        SchedulerTask task
    ) {
        task.setNextTick(living + delay);
        task.setInterval(interval);
        task.setTimes(times);
        tasks.add(task);
    }

    @Override
    public void tick() {
        if (!tasks.isEmpty()) {
            // System.out.println("tasks: " + tasks.size());
            // System.out.println("living:" + living);
            List<SchedulerTask> to_remove = new ArrayList<>();

            for (SchedulerTask task : new ArrayList<>(tasks)) {
                // System.out.println(
                //     task.getInterval() +
                //         " " +
                //         task.getNextTick() +
                //         " " +
                //         task.getTimes()
                // );
                if (task.getTimes() == 0 || task.isCancelled()) {
                    to_remove.add(task);
                    continue;
                }
                if (task.getNextTick() <= living) {
                    task.run();
                    task.setTimes(task.getTimes() - 1);
                    task.setNextTick(living + task.getInterval());
                }
            }

            tasks.removeAll(to_remove);

            living++;
        } else if (living != 0) {
            living = 0;
        }
    }
}
