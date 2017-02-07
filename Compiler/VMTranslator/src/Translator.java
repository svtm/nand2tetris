import java.io.File;

/**
 * Created by enielsen on 07.02.17.
 */
public abstract class Translator {
    private static CodeWriter cw;
    private static VMParser parser;


    public static void main(String[] args) throws Exception {
        for (String arg : args) {
            String file = arg.split(".vm")[0];
            cw = new CodeWriter(file, file);
            parser = new VMParser(arg);
            while (parser.hasMoreCommands()) {
                parser.advance();
                int cmdType = parser.getCommandType();
                switch (cmdType) {
                    case VMParser.C_ARITHMETIC:
                        cw.writeArithmetic(parser.getArg1());
                        break;
                    case VMParser.C_PUSH:
                    case VMParser.C_POP:
                        cw.writePushPop(cmdType, parser.getArg1(), Integer.parseInt(parser.getArg2()));
                        break;
                }
            }
            parser.close();
            cw.close();
        }
    }

}



