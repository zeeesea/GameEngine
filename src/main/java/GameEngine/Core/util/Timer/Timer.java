package GameEngine.Core.util.Timer;

import GameEngine.Core.gameObject.FuncInt.FuncInt;
import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.util.Console.Console;
import GameEngine.Core.util.Console.ConsoleTag;

import java.awt.*;

public class Timer {
    private final FuncInt callback;
    private final float interval;

    private boolean counting = false;
    private boolean active = true;
    private float time;
    boolean repeating = true;

    private static TimerSystem system;

    private Timer(FuncInt callback, float interval) {
        this.callback = callback;
        this.interval = interval;
    }
    private Timer(FuncInt callback, float interval, boolean repeating) {
        this(callback, interval);
        this.repeating = repeating;
    }

    public static Timer create(FuncInt callback, float interval) {
        Timer t = new Timer(callback, interval);
        if (system != null) system.addTimer(t);
        return t;
    }
    public static Timer create(FuncInt callback, float interval, boolean repeating) {
        Timer t =  new Timer(callback, interval, repeating);
        if (system != null) system.addTimer(t);
        return t;
    }

    public void update(double deltaTime) {
        if (!counting) return;

        time += (float) deltaTime;
        if (time >= interval) {
            callback.call();
            if (repeating) {
                time = 0;
            } else {
                counting = false;
                active = false;
            }
        }
    }

    public Timer start() {
        time = 0;
        counting = true;
        return this;
    }
    public void stop() {
        counting = false;
    }
    public static TimerSystem getTimerSystem() {
        return system;
    }
    public static void setTimerSystem(TimerSystem system) {
        Timer.system = system;
    }
    public boolean isActive() {
        return active;
    }
}
