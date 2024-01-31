package year.exp.forge.util;

public class StringUtil {
    public static boolean notEmpty(String value) {
        if (value == null) {
            return false;
        }
        return !value.isEmpty();
    }
}
