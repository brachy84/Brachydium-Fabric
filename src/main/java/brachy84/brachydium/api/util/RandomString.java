package brachy84.brachydium.api.util;

import java.util.Random;

public class RandomString {

    private static final String symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!§$%&/()=?,;.:-_#'+*";

    public static String create(int length) {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < length; i++) {
            s.append(symbols.charAt(new Random().nextInt(symbols.length())));
        }
        return s.toString();
    }
}
