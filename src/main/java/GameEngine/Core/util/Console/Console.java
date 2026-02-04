package GameEngine.Core.util.Console;

import GameEngine.Core.gameObject.GameObject;
import GameEngine.Core.util.Vector2;

import java.awt.*;
import java.time.LocalDateTime;

public class Console {

    private Console() {}

    //================ CENTRAL LOGGING =================//

    private static void logInternal(ConsoleTag tag, String msg, ConsoleColor color) {
        StringBuilder sb = new StringBuilder();

        LocalDateTime l = LocalDateTime.now();
        String time = "(" +
                String.format("%02d:%02d:%02d", l.getHour(), l.getMinute(), l.getSecond()) +
                ") ";

        if (tag != null) {
            sb.append("[").append(tag.name()).append("] ");
            if (color == null) {
                color = tag.color();
            }
        }

        sb.append(msg);

        if (color == null) color = ConsoleColor.RESET;

        System.out.println(
                ConsoleColor.DARK_GRAY.code() + time +
                        ConsoleColor.RESET.code() +
                        color.code() + sb +
                        ConsoleColor.RESET.code()
        );
    }

    //================ BASIC =================//

    public static void log(String msg) {
        logInternal(ConsoleTag.DEBUG, msg, null);
    }

    public static void log(String tag, String msg) {
        logInternal(null, "[" + tag + "] " + msg, ConsoleColor.RESET);
    }

    public static void log(String tag, String msg, ConsoleColor color) {
        logInternal(null, "[" + tag + "] " + msg, color);
    }

    //================ TAG =================//

    public static void log(ConsoleTag tag, String msg) {
        logInternal(tag, msg, null);
    }

    public static void log(ConsoleTag tag, String msg, ConsoleColor color) {
        logInternal(tag, msg, color);
    }

    //================ OVERLOADS =================//

    public static void log(int msg) {
        log(String.valueOf(msg));
    }

    public static void log(float msg) {
        log(String.valueOf(msg));
    }

    public static void log(boolean msg) {
        log(String.valueOf(msg));
    }

    public static void log(GameObject obj) {
        log(obj.getClass().getSimpleName());
    }

    public static void log(Vector2 vec) {
        log(vec.toString());
    }

    public static void log (Object o) {
        log(o.toString());
    }

    //================ TAGGED OVERLOADS =================//

    public static void log(ConsoleTag tag, int msg) {
        logInternal(tag, String.valueOf(msg), null);
    }

    public static void log(ConsoleTag tag, float msg) {
        logInternal(tag, String.valueOf(msg), null);
    }

    public static void log(ConsoleTag tag, boolean msg) {
        logInternal(tag, String.valueOf(msg), null);
    }

    public static void log(ConsoleTag tag, GameObject obj) {
        logInternal(tag, obj.getClass().getSimpleName(), null);
    }

    public static void log(ConsoleTag tag, Vector2 vec) {
        logInternal(tag, vec.toString(), null);
    }
}
