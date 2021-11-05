package brachy84.brachydium.api.util;

import java.util.Random;

public class CrypticNumber {

    private final int length;
    private String current;
    private static final String symbols = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public CrypticNumber(int length) {
        this.length = length;
        this.current = "0".repeat(Math.max(0, length));
    }

    public String next() {
        char[] chars = current.toCharArray();
        for(int i = length-1; i >= 0; i--) {
            if(i == 0 && current.charAt(i) == 'Z') {
                throw new IllegalStateException("Maximum number reached");
            }
            char s = next(chars[i]);
            chars[i] = s;
            if(s != symbols.charAt(0)) {
                break;
            }
        }
        current = new String(chars);
        return current;
    }

    public char next(Character c) {
        int i = symbols.indexOf(c);
        if(i == symbols.length() -1) {
            return symbols.charAt(0);
        }
        return symbols.charAt(i+1);
    }

    public String create(int length) {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < length; i++) {
            s.append(symbols.charAt(new Random().nextInt(symbols.length())));
        }
        return s.toString();
    }
}
