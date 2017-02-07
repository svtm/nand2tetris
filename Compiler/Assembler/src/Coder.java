import java.util.Map;
import java.util.TreeMap;

/**
 * Created by enielsen on 01.02.17.
 */
public abstract class Coder {

    private static Map<String, String> compMap;

    public static final int DEST_LEN = 3;

    public static String codeDest(String dest) {
        int val = 0b000;
        if (dest != null) {
            if (dest.contains("M")) {
                val |= 0b001;
            }
            if (dest.contains("D")) {
                val |= 0b010;
            }
            if (dest.contains("A")) {
                val |= 0b100;
            }
        }
        return Util.zeroPad(Integer.toBinaryString(val), 3);
    }


    public static String codeComp(String comp) {
        if (compMap == null) {
            initCompMap();
        }
        return compMap.get(comp);
    }

    private static void initCompMap() {
        compMap = new TreeMap<>();
        compMap.put("0",        "0101010");
        compMap.put("1",        "0111111");
        compMap.put("-1",       "0111010");
        compMap.put("D",        "0001100");
        compMap.put("A",        "0110000");
        compMap.put("M",        "1110000");
        compMap.put("!D",       "0001111");
        compMap.put("!A",       "0110001");
        compMap.put("!M",       "1110001");
        compMap.put("-D",       "0000000");
        compMap.put("-A",       "0110011");
        compMap.put("-M",       "1110011");
        compMap.put("D+1",      "0011111");
        compMap.put("A+1",      "0110111");
        compMap.put("M+1",      "1110111");
        compMap.put("D-1",      "0001110");
        compMap.put("A-1",      "0110010");
        compMap.put("M-1",      "1110010");
        compMap.put("D+A",      "0000010");
        compMap.put("D+M",      "1000010");
        compMap.put("D-A",      "0010011");
        compMap.put("D-M",      "1010011");
        compMap.put("A-D",      "0000111");
        compMap.put("M-D",      "1000111");
        compMap.put("D&A",      "0000000");
        compMap.put("D&M",      "1000000");
        compMap.put("D|A",      "0010101");
        compMap.put("D|M",      "1010101");
    }

    public static String codeJump(String jmp) {
        if (jmp != null) {
            switch (jmp) {
                case "JGT":
                    return "001";
                case "JEQ":
                    return "010";
                case "JGE":
                    return "011";
                case "JLT":
                    return "100";
                case "JNE":
                    return "101";
                case "JLE":
                    return "110";
                case "JMP":
                    return "111";
            }
        }
        return "000";
    }

}
