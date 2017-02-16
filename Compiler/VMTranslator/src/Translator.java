import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Created by enielsen on 07.02.17.
 */
public abstract class Translator {
    private static CodeWriter cw;
    private static VMParser parser;


    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Only one argument allowed");
            return;
        }

        File source = new File(args[0]);
        if (source.isDirectory()) {
            cw = new CodeWriter(source.toString());
            cw.writeInit();
            Stream<Path> paths = Files.walk(Paths.get(source.getCanonicalPath()));
            paths.forEach(filePath -> {
                if (filePath.toString().endsWith(".vm")) {
                    System.out.println(filePath.getFileName());
                    translateFile(filePath);
                }
            });
        } else if (source.getName().endsWith(".vm")){
            cw = new CodeWriter(source.toString().split(".vm")[0]);
            cw.writeInit();
            System.out.println(source.getName());
            translateFile(source.toPath());
        } else {
            System.out.println("Invalid source file, only directories or .vm-files allowed");
        }
        if (cw != null) cw.close();
    }

    public static void translateFile(Path path) {
        try {
            parser = new VMParser(path.toString());
            cw.setFileName(path.getFileName().toString().split(".vm")[0]);
            while (parser.hasMoreCommands()) {
                parser.advance();

                if (CodeWriter.DEBUG) cw.printComment(parser.getCurrent());

                int cmdType = parser.getCommandType();
                switch (cmdType) {
                    case VMParser.C_ARITHMETIC:
                        cw.writeArithmetic(parser.getArg1());
                        break;
                    case VMParser.C_PUSH:
                    case VMParser.C_POP:
                        cw.writePushPop(cmdType, parser.getArg1(), Integer.parseInt(parser.getArg2()));
                        break;
                    case VMParser.C_GOTO:
                        cw.writeGoto(parser.getArg1());
                        break;
                    case VMParser.C_IF:
                        cw.writeIf(parser.getArg1());
                        break;
                    case VMParser.C_LABEL:
                        cw.writeLabel(parser.getArg1());
                        break;
                    case VMParser.C_FUNCTION:
                        cw.writeFunction(parser.getArg1(), Integer.parseInt(parser.getArg2()));
                        break;
                    case VMParser.C_RETURN:
                        cw.writeReturn();
                        break;
                    case VMParser.C_CALL:
                        cw.writeCall(parser.getArg1(), Integer.parseInt(parser.getArg2()));
                        break;
                }
            }
            parser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}



