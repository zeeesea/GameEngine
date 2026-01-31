package GameEngine.Core.util.Timer;

import GameEngine.Core.util.Console.Console;
import GameEngine.Core.util.Console.ConsoleTag;

import java.util.ArrayList;
import java.util.List;

public class TimerSystem {
    private final List<Timer> timers = new ArrayList<>();

    public void addTimer(Timer timer) {
        timers.add(timer);
    }

    public void update(double deltaTime) {
        for (Timer t : new ArrayList<>(timers)) {
            t.update(deltaTime);
            if (!t.isActive()) timers.remove(t);
        }

    }
}
