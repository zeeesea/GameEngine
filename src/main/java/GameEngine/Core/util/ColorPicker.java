package GameEngine.Core.util;

import GameEngine.Core.gameObject.FuncInt.FuncIntOne;

import javax.swing.*;
import java.awt.*;

public final class ColorPicker {
    private ColorPicker() {}
    public static void openColorPicker(FuncIntOne<Color> onPick) {
        Color c = JColorChooser.showDialog(null, "Choose Color", Color.WHITE);
        if (c != null) {
            onPick.call(c);
        }
    }
}
