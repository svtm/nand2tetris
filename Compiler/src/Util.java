/**
 * Created by enielsen on 02.02.17.
 */
public abstract class Util {

    public static final int ADDRESS_LEN = 16;

    public static String zeroPad(String str, int len) {
        if (str.length() > len) {
            return str;
        }
        String res = "";
        for (int i = 0; i < len - str.length(); i++) {
            res += "0";
        }
        res += str;
        return res;
    }

}
