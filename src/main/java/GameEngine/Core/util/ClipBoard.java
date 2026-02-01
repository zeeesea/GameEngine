package GameEngine.Core.util;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

public final class ClipBoard {
    private ClipBoard(){};
    public static void copy(String text) {
        StringSelection selection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    }

}
