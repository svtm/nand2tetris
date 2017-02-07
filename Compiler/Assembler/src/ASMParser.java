import java.io.IOException;

/**
 * Created by enielsen on 01.02.17.
 */
public class ASMParser extends Parser {

    public static final int A_COMMAND = 0;
    public static final int C_COMMAND = 1;
    public static final int L_COMMAND = 2;

    public ASMParser(String file) throws IOException{
        super(file);
    }


    public String getSymbol() {
        switch (currCmdType) {
            case A_COMMAND:
                return currLine.substring(1);
            case L_COMMAND:
                return currLine.substring(1, currLine.length() - 1);
            default:
                return null;
        }
    }

    public String getDest() {
        if (currCmdType != C_COMMAND || !currLine.contains("=") ) {
            return null;
        }
        return currLine.split("=")[0];
    }

    public String getComp() {
        if (currCmdType != C_COMMAND) {
            return null;
        }
        String comp = currLine;
        if (comp.contains("=") ) {
            comp = comp.split("=")[1];
        }
        if (comp.contains(";") ) {
            comp = comp.split(";")[0];
        }
        return comp;
    }

    public String getJump() {
        if (currCmdType != C_COMMAND || !currLine.contains(";") ) {
            return null;
        }
        return currLine.split(";")[1];
    }


    @Override
    public int getCommandType() {
        if (currLine.charAt(0) == '@' ) {
            currCmdType = A_COMMAND;
            return A_COMMAND;
        }
        if (currLine.charAt(0) == '(' ) {
            currCmdType = L_COMMAND;
            return L_COMMAND;
        }
        currCmdType = C_COMMAND;
        return C_COMMAND;
    }

}
