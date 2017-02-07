import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by enielsen on 03.02.17.
 */
public abstract class Parser {
    protected BufferedReader reader;
    protected String file;
    protected String currLine;
    protected int currCmdType;

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
            String nextLine;
            do {
                nextLine = reader.readLine().split("//")[0].trim();
            } while (nextLine.equals(""));
            currLine = nextLine;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract int getCommandType();

    public String getCurrent() {
        return currLine;
    }
}
