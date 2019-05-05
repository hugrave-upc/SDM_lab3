package utils;

import java.util.Random;

/**
 * Created by edoardo on 05/05/2019.
 */
public class Utils {

    private static Random r = new Random();

    public static String cleanURI(String id) {
        return id.replaceAll(" ", "_");
    }

    public static int randomInt(int lower, int upper) {
        return (Math.abs(r.nextInt()) % (upper-lower)) + lower;
    }
}
