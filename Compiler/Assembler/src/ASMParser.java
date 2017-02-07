import java.io.BufferedReader;
import java.io.FileReader;
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

    /*private BufferedReader reader;
    private String file;
    private String currLine;
    private int currCmdType;

    public Parser(String file) throws IOException {
        this.file = file;
        reader = new BufferedReader(new FileReader(file));
    }

    public void reset() {
        try {
            reader.close();
            reader = new BufferedReader(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasMoreCommands() {
        try {
            reader.mark(2);
            int i = reader.read();
            reader.reset();
            return i > 0;
        } catch (IOException e) {
            System.out.println("EXCEPTION hasMoreComma");
            e.printStackTrace();
            return false;
        }
    }

    public void advance() {
        try {
            String nextLine = "";
            do {
                nextLine = reader.readLine().split("//")[0].trim();
            } while (nextLine.equals(""));
            currLine = nextLine;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

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

    public void printCurrent() {
        System.out.println(currLine);
    }
}
