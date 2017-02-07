import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

/**
 * Created by enielsen on 03.02.17.
 */
public class CodeWriter {

    private static final String TRUE = Util.zeroPad(Integer.toBinaryString(-1), Util.ADDRESS_LEN);
    private static final String FALSE = "000000000000000";

    private String fileName;
    private PrintWriter writer;
    private int nextLabel = 0;

    public CodeWriter(String programName, String fileName) throws IOException {
        this.fileName = fileName;
        writer = new PrintWriter(programName+".asm");
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void writeArithmetic(String cmd) {
        if (cmd.equals("not")) {
            decSP();
            setAtoSP();
            writeCmds("M=!M");
        } else if (cmd.equals("neg")) {
            decSP();
            setAtoSP();
            writeCmds("M=-M");
        } else {
            popXandY();
            if (cmd.equals("add")) {
                writeCmds("M=M+D");
            } else if (cmd.equals("sub")) {
                writeCmds("M=M-D");
            } else if (cmd.equals("and")) {
                writeCmds("M=M&D");
            } else if (cmd.equals("or")) {
                writeCmds("M=M|D");
            } else {
                String jmp = "";
                if (cmd.equals("eq")) {
                    jmp = "JNE";
                } else if (cmd.equals("gt")) {
                    jmp = "JLE";
                } else if (cmd.equals("lt")) {
                    jmp = "JGE";
                }
                String label1 = getLabel();
                String label2 = getLabel();
                writeCmds("D=M-D");
                writeCmds("@" + label1, "D;"+jmp, "@1", "D=-A");
                setAtoSP();
                writeCmds("M=D", "@"+label2, "0;JMP", "("+label1+")", "@0", "D=A");
                setAtoSP();
                writeCmds("M=D", "("+label2+")");
            }
        }
        incSP();
    }

    private String getLabel() {
        return "SOME_LABEL." + (nextLabel++);
    }

    // After this, Y is in D, X is in M
    private void popXandY() {
        decSP();
        setAtoSP();
        writeCmds("D=M");
        decSP();
        setAtoSP();
    }

    private void writeCmds(String... cmds) {
        for (String cmd : cmds) {
            System.out.println(cmd);
            writer.println(cmd);
        }
    }

    private void setAtoSP() {
        writeCmds("@SP", "A=M");
    }

    public void writePushPop(int cmd, String segment, int index) throws IOException {
        switch (cmd) {
            case VMParser.C_PUSH:
                if (segment.equals("constant")) {
                    writeCmds("@" + index, "D=A", "@SP", "A=M", "M=D");
                } else if (segment.equals("argument")) {
                    writePush(pushLoadTemplate("ARG", index));
                } else if (segment.equals("local")) {
                    writePush(pushLoadTemplate("LCL", index));
                } else if (segment.equals("this")) {
                    writePush(pushLoadTemplate("THIS", index));
                } else if (segment.equals("that")) {
                    writePush(pushLoadTemplate("THAT", index));
                } else if (segment.equals("temp")) {
                    writePush(pushLoadTemplate("R5", index));
                } else if (segment.equals("static")) {
                    // Static direct addressing, use general locations starting at 16
                    writePush(pushLoadStaticTemplate(index));
                } else if (segment.equals("pointer") && index == 0) {
                    writePush(pushLoadTemplate("THIS", 0));
                } else if (segment.equals("pointer") && index == 1) {
                    writePush(pushLoadTemplate("THAT", 0));
                }
                incSP();
                break;
            case VMParser.C_POP:

                String reg = "";
                if (segment.equals("constant")) {
                    // TODO
                } else if (segment.equals("local")) {
                    reg = "LCL";
                } else if (segment.equals("argument")) {
                    reg = "ARG";
                } else if (segment.equals("this")) {
                    reg = "THIS";
                } else if (segment.equals("that")) {
                    reg  = "THAT";
                } else if (segment.equals("temp")) {
                    reg = "R5";
                } else if (segment.equals("static")) {
                    // Static direct addressing, use general locations starting at 16

                } else if (segment.equals("pointer") && index == 0) {

                } else if (segment.equals("pointer") && index == 1) {

                }
                writeCmds("@"+reg, "D=A", "@"+index, "A=A+D");
                decSP();
                setAtoSP();
                writeCmds("D=M");
                break;
        }
    }

    private String pushLoadTemplate(String segment, int index) {
        String offsetString = (index == 0) ? "" :  "@" + index + "\n" +  // put index in A
                                                    "A=D+A" + "\n" +      // calc absolute address
                                                    "D=M";          // put value in D
        return  "@" + segment + "\n" +      // load data from memory
                "D=M" + "\n" +              // store value in D
                offsetString;

    }

    private void writePush(String pushString) {
        writeCmds(pushString, "@SP", "A=M", "M=D");
    }

    private String pushLoadStaticTemplate(int index) {
        return  "@"+fileName+"."+index + "\n" +
                "D=M" + "\n";
    }

    public void close() {
        writer.close();
    }

    private void incSP() {
        writeCmds("@SP", "M=M+1");
    }

    private void decSP() {
        writeCmds("@SP", "M=M-1");
    }

    public static void main(String[] args) throws Exception {
 /*       CodeWriter cw = new CodeWriter("Testfile.asm");
        cw.writePushPop(VMParser.C_PUSH, "constant", 7);
        cw.writePushPop(VMParser.C_PUSH, "constant", 8);
        cw.writeArithmetic("lt");
        cw.writePushPop(VMParser.C_PUSH, "constant", 9);
        cw.writePushPop(VMParser.C_PUSH, "constant", 10);
        cw.writeArithmetic("gt");

        cw.close();*/
        CodeWriter cw = new CodeWriter("../projects/07/StackArithmetic/StackTest/", "StackTest");
        VMParser parser = new VMParser("../projects/07/StackArithmetic/StackTest/StackTest.vm");
        while (parser.hasMoreCommands()) {
            parser.advance();
            parser.printCurrent();
            int cmdType = parser.getCommandType();
            switch (cmdType) {
                case VMParser.C_ARITHMETIC:
                    cw.writeArithmetic(parser.getArg1());
                    break;
                case VMParser.C_PUSH:
                    cw.writePushPop(cmdType, parser.getArg1(), Integer.parseInt(parser.getArg2()));
                    break;
            }
        }
        cw.close();
    }
}
