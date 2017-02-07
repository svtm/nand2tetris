import java.util.HashMap;
import java.util.Map;

/**
 * Created by enielsen on 02.02.17.
 */
public class SymbolTable {
    private Map<String, Integer> table;
    private int nextRAM;


    public SymbolTable() {
        table = new HashMap<>();
        table.put("SP",     0);
        table.put("LCL",    1);
        table.put("ARG",    2);
        table.put("THIS",   3);
        table.put("THAT",   4);
        table.put("SCREEN", 16384);
        table.put("KBD",    24576);

        for (int i = 0; i <= 15; i++) {
            table.put("R"+i, i);
        }
        nextRAM = 16;
    }

    public void addEntry(String symbol, int address) {
        table.put(symbol, address);
    }

    public void addEntry(String symbol) {
        table.put(symbol, nextRAM++);
    }

    public boolean contains(String symbol) {
        return table.containsKey(symbol);
    }

    public String getAddress(String symbol) {
        if (contains(symbol)) {
            return Util.zeroPad(Integer.toBinaryString(table.get(symbol)), Util.ADDRESS_LEN);
        }
        return "0111111111111111";
    }

    @Override
    public String toString() {
        return table.toString();
    }
}
