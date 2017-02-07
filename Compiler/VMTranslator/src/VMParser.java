import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by enielsen on 01.02.17.
 */
public class VMParser extends Parser {

    public static final int C_ARITHMETIC    = 0;
    public static final int C_PUSH          = 1;
    public static final int C_POP           = 2;
    public static final int C_LABEL         = 3;
    public static final int C_GOTO          = 4;
    public static final int C_IF            = 5;
    public static final int C_FUNCTION      = 6;
    public static final int C_RETURN        = 7;
    public static final int C_CALL          = 8;

    private static Map<String, Integer> cmdMap;


    public VMParser(String file) throws IOException {
        super(file);
        if (cmdMap == null) {
            initCmdMap();
        }
    }

    private void initCmdMap() {
        cmdMap = new HashMap<>();
        cmdMap.put("add",       C_ARITHMETIC    );
        cmdMap.put("sub",       C_ARITHMETIC    );
        cmdMap.put("neg",       C_ARITHMETIC    );
        cmdMap.put("eq",        C_ARITHMETIC    );
        cmdMap.put("gt",        C_ARITHMETIC    );
        cmdMap.put("lt",        C_ARITHMETIC    );
        cmdMap.put("and",       C_ARITHMETIC    );
        cmdMap.put("or",        C_ARITHMETIC    );
        cmdMap.put("not",       C_ARITHMETIC    );
        cmdMap.put("push",      C_PUSH          );
        cmdMap.put("pop",       C_POP           );
        cmdMap.put("label",     C_LABEL         );
        cmdMap.put("goto",      C_GOTO          );
        cmdMap.put("if-goto",   C_IF            );
        cmdMap.put("function",  C_FUNCTION      );
        cmdMap.put("call",      C_CALL          );
        cmdMap.put("return",    C_RETURN        );
    }

    @Override
    public int getCommandType() {
        String cmd = currLine.split(" ")[0];
        currCmdType = cmdMap.get(cmd);
        return currCmdType;
    }

    public String getArg1() {
        String[] parts = currLine.split(" ");
        switch (currCmdType) {
            case C_ARITHMETIC:
                return parts[0];
            case C_RETURN:
                return null;
            default:
                return parts[1];
        }
    }

    public String getArg2() {
        String[] parts = currLine.split(" ");
        switch (currCmdType) {
            case C_PUSH:
            case C_POP:
            case C_FUNCTION:
            case C_CALL:
                return parts[2];
            default:
                return null;
        }
    }
}
