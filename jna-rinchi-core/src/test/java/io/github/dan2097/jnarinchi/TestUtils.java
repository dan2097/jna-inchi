package io.github.dan2097.jnarinchi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * @author Nikolay Kochev
 * @author Uli Fechner
 */
public class TestUtils {
    public static final String LINE_SEPARATOR_NEWLINE = "\n";

    /**
     * Read a text file into a string.
     *
     * @param fileName name of the text file to read
     * @return file content as a single string
     */
    public static String readTextFromResourceAsString(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(TestUtils.class.getResourceAsStream(fileName)));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append(LINE_SEPARATOR_NEWLINE);
        }

        // clean up
        br.close();

        return sb.toString();
    }

    public static List<String> readTextFromResourceAsList(String filename) throws IOException {
        return Arrays.asList(readTextFromResourceAsString(filename).split(LINE_SEPARATOR_NEWLINE));
    }
}
