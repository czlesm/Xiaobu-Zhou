package byow.InputDemo;

import java.util.Locale;

/**
 * Created by hug.
 */
public class StringInputDevice implements byow.InputDemo.InputSource {
    private String input;
    private int index;

    public StringInputDevice(String s) {
        index = 0;
        input = s.toUpperCase(Locale.ROOT);
    }

    public char getNextKey() {
        char returnChar = input.charAt(index);
        index += 1;
        return returnChar;
    }

    public boolean possibleNextInput() {
        return index < input.length();
    }
}
