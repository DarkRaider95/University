import java.util.LinkedList;
import java.io.*;

public class CodeGenerator {

    LinkedList <Instruction> instructions = new LinkedList <Instruction>();

    int label=0;

	public void emit( OpCode opCode) {
        instructions.add( new Instruction(opCode));
	}

	public void emit( OpCode opCode , int operand ) {
        instructions.add( new Instruction( opCode, operand ));
	}

	public void emitLabel (int operand ) {
        emit( OpCode.label , operand );
	}

	public int newLabel () {
		return label++;

	}

	public void toJasmin (String f) throws IOException{
		PrintWriter out = new PrintWriter(new FileWriter(f));
        f = f.substring(0,f.indexOf('.'));
		String temp = ".class public "+ f +"\n";
        temp = temp + header;
        while(instructions.size() > 0){
            Instruction tmp = instructions.remove();
            temp = temp + tmp.toJasmin(f);
	    }
        temp = temp + " return\n"
        + ".end method\n"
        + "\n"
        + ".method public static main([Ljava/lang/String;)V\n"
        + " invokestatic "+ f
        +"/run()V\n"
        + " return\n"
        + ".end method\n";
        out.println(temp);
        out.flush();
        out.close();
	}

	private static final String header = ".super java/lang/Object\n"
        + "\n"
        + ".method public <init>()V\n"
        + " aload_0\n"
        + " invokenonvirtual java/lang/Object/<init>()V\n"
        + " return\n"
        + ".end method\n"
        + "\n"
        + ".method public static printBool(I)V\n"
        + " .limit stack 3\n"
        + " getstatic java/lang/System/out Ljava/io/PrintStream;\n"
        + " iload_0 \n"
        + " bipush 1\n"
        + " if_icmpeq Ltrue\n"
        + " ldc \"false\"\n"
        + " goto Lnext\n"
        + " Ltrue:\n"
        + " ldc \"true\"\n"
        + " Lnext:\n"
        + " invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n"
        + " return\n"
        + ".end method\n"
        + "\n"
        + ".method public static printInt(I)V\n"
        + " .limit stack 2\n"
        + " getstatic java/lang/System/out Ljava/io/PrintStream;\n"
        + " iload_0 \n"
        + " invokestatic java/lang/Integer/toString(I)Ljava/lang/String;\n"
        + " invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n"
        + " return\n"
        + ".end method\n"
        + "\n"
        + ".method public static run()V\n"
        + " .limit stack 1024\n"
        + " .limit locals 256\n";

	/*private static final String footer = " return\n"
        + ".end method\n"
        + "\n"
        + ".method public static main([Ljava/lang/String;)V\n"
        + " invokestatic Output/run()V\n"
        + " return\n"
        + ".end method\n";*/
}