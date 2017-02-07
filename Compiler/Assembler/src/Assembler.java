import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by enielsen on 01.02.17.
 */
public abstract class Assembler {

    public static void main(String[] args) {
        List<String> assembledFiles = new ArrayList<>();

        ASMParser parser;
        SymbolTable symbolTable;
        int ROMaddress;
        for (String arg : args) {
            try {
                parser = new ASMParser(arg);
                String outFile = arg.split(".asm")[0] + ".hack";

                // First pass
                ROMaddress = 0;
                symbolTable = new SymbolTable();
                while (parser.hasMoreCommands()) {
                    parser.advance();

                    if (parser.getCommandType() == ASMParser.L_COMMAND) {
                        symbolTable.addEntry(parser.getSymbol(), ROMaddress);
                    } else {
                        ROMaddress++;
                    }
                }
                parser.reset();


                // Second pass
                PrintWriter writer = new PrintWriter(outFile);
                while (parser.hasMoreCommands()) {
                    parser.advance();

                    switch (parser.getCommandType()) {
                        case ASMParser.A_COMMAND:
                            String symbol = parser.getSymbol();
                            String toWrite;
                            try {
                                int val = Integer.parseInt(symbol);
                                toWrite = Util.zeroPad(Integer.toBinaryString(val), Util.ADDRESS_LEN);
                            } catch (NumberFormatException e) {
                                if (!symbolTable.contains(symbol)) {
                                    symbolTable.addEntry(symbol);
                                }
                                toWrite = symbolTable.getAddress(symbol);
                            }
                            writer.println(toWrite);
                            break;
                        case ASMParser.C_COMMAND:
                            writer.println("111" + Coder.codeComp(parser.getComp()) + Coder.codeDest(parser.getDest()) + Coder.codeJump(parser.getJump()));
                            break;
                        case ASMParser.L_COMMAND:
                            // Do nothing
                            break;
                    }
                }
                writer.close();
                assembledFiles.add(outFile);

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(arg + " is an invalid file.");
            }
        }

        for (String assembled : assembledFiles) {
            System.out.print(assembled + " ");
        }
    }

}
