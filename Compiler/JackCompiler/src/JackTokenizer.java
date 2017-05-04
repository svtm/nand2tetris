import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by enielsen on 17.02.17.
 */

// TODO: refactor Parser to be more general so this class can extend it
public class JackTokenizer {

    public enum TokenType {
        KEYWORD,
        SYMBOL,
        IDENTIFIER,
        INT_CONST,
        STRING_CONST
    }

    public enum KeyWord {
        CLASS,
        METHOD,
        FUNCTION,
        CONSTRUCTOR,
        INT,
        BOOLEAN,
        CHAR,
        VOID,
        VAR,
        STATIC,
        FIELD,
        LET,
        DO,
        IF,
        ELSE,
        WHILE,
        RETURN,
        TRUE,
        FALSE,
        NULL,
        THIS
    }

    public static final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
    public static final String DELIMS = "({|}|\\(|\\)|[|]|.|,|;|+|-|*|/|\\&|\\||<|>|=|~)";

    private BufferedReader reader;
    private String currLine;
    private Stack<String> token;

    public JackTokenizer(String file) throws IOException {
        reader = new BufferedReader(new FileReader(file));
    }

    public static void tokenize(String line) {
        List<String> tokens = new ArrayList<>();
        do {

        }
    }

    public static void main(String[] args) {
        String test = "public Parser(String file) throws IOException {";
        tokenize(test);
    }



    public void advance() {
        try {
            String nextLine;
            do {
                String[] cleaned = reader.readLine().trim().split("//");
                nextLine = (cleaned.length > 0) ? cleaned[0] : "";
            } while ( nextLine.equals(""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
