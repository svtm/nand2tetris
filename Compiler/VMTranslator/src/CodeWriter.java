import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

/**
 * Created by enielsen on 03.02.17.
 */
public class CodeWriter {

    private static final String TEMP_BASE = "5";

    private String fileName;
    private PrintWriter writer;
    private int nextLabel = 0;

    public CodeWriter(String programName) throws IOException {
        writer = new PrintWriter(programName+".asm");
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        printComment("************ " + fileName.toUpperCase() + " ************");
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
                    writePush("ARG", index);
                } else if (segment.equals("local")) {
                    writePush("LCL", index);
                } else if (segment.equals("this")) {
                    writePush("THIS", index);
                } else if (segment.equals("that")) {
                    writePush("THAT", index);
                } else if (segment.equals("temp")) {
                    pushTempOrStatic(TEMP_BASE, index);
                } else if (segment.equals("static")) {
                    pushTempOrStatic(fileName + "." + index, index);
                } else if (segment.equals("pointer")) {
                    String seg = (index == 0) ? "THIS" : "THAT";
                    pushPointer(seg);
                }
                incSP();
                break;
            case VMParser.C_POP:
                decSP();

                String reg = "";
                if (segment.equals("constant")) {
                    // this shouldn't happen?
                    throw new IllegalArgumentException("POP CONSTANT??");
                } else if (segment.equals("local")) {
                    reg = "LCL";
                } else if (segment.equals("argument")) {
                    reg = "ARG";
                } else if (segment.equals("this")) {
                    reg = "THIS";
                } else if (segment.equals("that")) {
                    reg  = "THAT";
                } else if (segment.equals("temp")) {
                    popTempOrStatic(TEMP_BASE, index);
                    return;
                } else if (segment.equals("static")) {
                    popTempOrStatic(fileName + "." + index, index);
                    return;
                } else if (segment.equals("pointer")) {
                    String seg = (index == 0) ? "THIS" : "THAT";
                    popPointer(seg);
                    return;
                }
                writeCmds("@"+index, "D=A", "@"+reg, "D=M+D", "@R13", "M=D");
                setAtoSP();
                writeCmds("D=M", "@13", "A=M", "M=D");
                break;
        }
    }

    private void pushTempOrStatic(String base, int offset) {
        writeCmds("@" + offset, "D=A", "@"+base, "A=A+D", "D=M");
        setAtoSP();
        writeCmds("M=D");
    }

    private void popTempOrStatic(String base, int offset) {
        writeCmds("@" + offset, "D=A", "@"+base, "D=A+D", "@R13", "M=D");
        setAtoSP();
        writeCmds("D=M", "@13", "A=M", "M=D");
    }

    private void pushPointer(String seg) {
        writeCmds("@"+seg, "D=M");
        setAtoSP();
        writeCmds("M=D");
    }

    private void popPointer(String seg) {
        setAtoSP();
        writeCmds("@"+seg, "M=D");
    }
    public void printComment(String comment) {
        writer.println("// " + comment);
    }

    private void writePush(String segment, int index) {
        writeCmds("@" + index, "D=A", "@" + segment, "A=M+D", "D=M");
        setAtoSP();
        writeCmds("M=D");
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

}
