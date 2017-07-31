package org.valuereporter.helper;

/**
 * Created by baardl on 31.07.17.
 */
public class StringHelper {
    private StringHelper() {
    }

    public static boolean hasContent(String text) {
        boolean hasContent = false;
        if(text != null && !text.isEmpty()) {
            hasContent = true;
        }

        return hasContent;
    }
}
