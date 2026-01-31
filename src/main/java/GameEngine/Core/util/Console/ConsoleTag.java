package GameEngine.Core.util.Console;

public enum ConsoleTag {

    ERROR(ConsoleColor.RED),
    WARNING(ConsoleColor.YELLOW),
    SYSTEM(ConsoleColor.CYAN),
    GRAPHICS(ConsoleColor.BLUE),
    SPRITE(ConsoleColor.PURPLE),
    ANIMATION(ConsoleColor.PURPLE),
    PHYSICS(ConsoleColor.WHITE),
    INPUT(ConsoleColor.CYAN),
    AUDIO(ConsoleColor.PURPLE),
    DEBUG(ConsoleColor.WHITE),
    SCENE(ConsoleColor.GREEN);

    private final ConsoleColor defaultColor;

    ConsoleTag(ConsoleColor defaultColor) {
        this.defaultColor = defaultColor;
    }

    public String tag() {
        return name();
    }

    public ConsoleColor color() {
        return defaultColor;
    }
}
