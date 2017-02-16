import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;

/**
 * Created by enielsen on 03.02.17.
 */
public class CodeWriter {

    private static final int TEMP_BASE = 5;
    public static final boolean DEBUG = true;

    private String fileName;
    private PrintWriter writer;
    private int nextLabel = 0;
    private Stack<String> funcNameStack;

    public CodeWriter(String programName) throws IOException {
        writer = new PrintWriter(programName+".asm");
        System.out.println(programName+".asm");
        funcNameStack = new Stack<>();
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

    private String getCurrFuncName() {
        return (funcNameStack.empty()) ? "" : (funcNameStack.peek() + "$");
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
            //if (DEBUG) System.out.println(cmd);
        }
    }

    private void setAtoSP() {
        writeCmds("@SP", "A=M");
    }

    public void writePushPop(int cmd, String segment, int index)  {
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
                    //pushTempOrStatic(Integer.toString(TEMP_BASE), index);
                    pushTempOrStatic("R5", index);
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
                   // popTempOrStatic(Integer.toString(TEMP_BASE), index);
                    popTempOrStatic("R5", index);
                    return;
                } else if (segment.equals("static")) {
                    popTempOrStatic(fileName + "." + index, index);
                    return;
                } else if (segment.equals("pointer")) {
                    String seg = (index == 0) ? "THIS" : "THAT";
                    popPointer(seg);
                    return;
                }
                writePop(reg, index);
                break;
        }
    }

    private void writePop(String segment, int index) {
        writeCmds("@"+index, "D=A", "@"+segment, "D=M+D", "@R13", "M=D");
        setAtoSP();
        writeCmds("D=M", "@R13", "A=M", "M=D");
    }

    private void pushTempOrStatic(String base, int offset) {
        writeCmds("@" + offset, "D=A", "@"+base, "A=A+D", "D=M");
        setAtoSP();
        writeCmds("M=D");
    }

    private void popTempOrStatic(String base, int offset) {
        writeCmds("@" + offset, "D=A", "@"+base, "D=A+D", "@R13", "M=D");
        setAtoSP();
        writeCmds("D=M", "@R13", "A=M", "M=D");
    }

    private void pushPointer(String seg) {
        writeCmds("@"+seg, "D=M");
        setAtoSP();
        writeCmds("M=D");
    }

    private void popPointer(String seg) {
        setAtoSP();
        writeCmds("D=M", "@"+seg, "M=D");
    }
    public void printComment(String comment) {
        writer.println("// " + comment);
    }

    private void writePush(String segment, int index) {
        writeCmds("@" + index, "D=A", "@" + segment, "A=M+D", "D=M");
        setAtoSP();
        writeCmds("M=D");
    }

    private void putVal(String reg, int val) {
        if (val < 0) {
            writeCmds("@0", "D=A", "@"+(-val), "D=D-A");
        } else {
            writeCmds("@"+val, "D=A");
        }
        writeCmds("@"+reg, "M=D");
    }

    public void writeInit() {
        writeCmds("@256", "D=A", "@SP", "M=D");
        putVal("LCL", -1);
        putVal("ARG", -2);
        putVal("THIS", -3);
        putVal("THAT", -4);
        writeCall("Sys.init", 0);
    }

    public void writeLabel(String label) {
        String func = getCurrFuncName();
        if (DEBUG) System.out.println("Printing label " + label + " from func " + func);
        writeCmds("(" + func + label + ")");
    }

    private void writeGlobalLabel(String label) {
        writeCmds("(" + label + ")");
    }

    private void writeGlobalGoto(String label) {
        writeCmds("@"+label, "0;JMP");
    }

    public void writeGoto(String label) {
        String func = getCurrFuncName();
        if (DEBUG) System.out.println("Printing goto " + label + " from func " + func);
        writeCmds("@"+func+label, "0;JMP");
    }


    public void writeIf(String label) {
        decSP();
        setAtoSP();
        writeCmds("D=M", "@" + label, "D;JNE");
    }

    public void writeCall(String functionName, int numArgs) {
        // push return-address to stack
        String func = getCurrFuncName();
        String retLabel = getLabel();
        writeCmds("@"+func+retLabel, "D=A");
        setAtoSP();
        writeCmds("M=D");
        incSP();

        // push registers
        pushRegister("LCL");
        pushRegister("ARG");
        pushRegister("THIS");
        pushRegister("THAT");

        // ARG = SP-n-5
        writeCmds("@SP", "D=M", "@"+numArgs, "D=D-A", "@5", "D=D-A", "@ARG", "M=D");
        // LCL = SP
        writeCmds("@SP", "D=M", "@LCL", "M=D");

        // goto f
        writeGlobalGoto(functionName);
        // (return-address)
        writeLabel(retLabel);
    }

    private void pushRegister(String register) {
        writeCmds("@" + register, "D=M");
        setAtoSP();
        writeCmds("M=D");
        incSP();
    }

    public void writeReturn() {
        // Temp var FRAME = LCL
        // (FRAME = R5);
        //writeCmds("@LCL", "D=M", "@"+TEMP_BASE, "M=D");
        writeCmds("@LCL", "D=M", "@R13", "M=D");
        // RET = *(FRAME-5)
        // (RET = R6);
        //writeCmds("@5", "D=A", "@"+TEMP_BASE, "A=M", "A=A-D", "D=M", "@"+(TEMP_BASE+1), "M=D");
        writeCmds("@5", "D=A", "@R13", "A=M", "A=A-D", "D=M", "@R14", "M=D");
        // *ARG = pop()
        decSP();
        setAtoSP();
        writeCmds("D=M", "@ARG", "A=M", "M=D");
        //writePop("ARG", 0);
        // SP = ARG+1
        writeCmds("@ARG", "D=M+1", "@SP", "M=D");
        // THAT = *(FRAME-1);
        resetCallVar("THAT", 1);
        // THIS = *(FRAME-2);
        resetCallVar("THIS", 2);
        // ARG = *(FRAME-3);
        resetCallVar("ARG", 3);
        // LCL = *(FRAME-4)
        resetCallVar("LCL", 4);
        // goto RET
     //   writeCmds("@"+(TEMP_BASE+1), "A=M", "0;JMP");
        writeCmds("@R14", "A=M", "0;JMP");
        funcNameStack.pop();
    }

    private void resetCallVar(String segment, int offset) {
        //writeCmds("@"+offset, "D=A", "@"+TEMP_BASE, "A=M", "A=A-D", "D=M", "@"+segment, "M=D");
        writeCmds("@"+offset, "D=A", "@R13", "A=M", "A=A-D", "D=M", "@"+segment, "M=D");
    }

    public void writeFunction(String functionName, int numLocals) {
        writeGlobalLabel(functionName);
        funcNameStack.push(functionName);
        for (int i = 0; i < numLocals; i++) {
            writePushPop(VMParser.C_PUSH, "constant", 0);
        }
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
