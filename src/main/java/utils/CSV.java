package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by edoardo on 05/05/2019.
 */
public class CSV {
    public static List<String[]> read(String filePath, String separator) throws IOException {
        List<String[]> lines = new ArrayList<String[]>();

        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        br.readLine(); // Removing the header
        while ((line = br.readLine()) != null) {
            String[] lineArray = line.split(separator);
            lines.add(lineArray);
        }

        return lines;
    }
}
