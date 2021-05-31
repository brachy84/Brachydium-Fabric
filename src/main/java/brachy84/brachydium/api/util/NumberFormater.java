package brachy84.brachydium.api.util;

public class NumberFormater {

    /**
     * 1000000 -> 1 000 000
     * @param number to convert
     * @return converted number as string
     */
    public static String space(long number) {
        String num = Long.toString(number);
        StringBuilder newNum = new StringBuilder();
        while (num.length() > 3) {
            newNum.insert(0, " " + num.substring(num.length() - 3));
            num = num.substring(0, num.length() - 4);
        }
        newNum.insert(0, num);
        return newNum.toString();
    }
}
