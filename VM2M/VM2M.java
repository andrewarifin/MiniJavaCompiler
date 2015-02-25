import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.IOException;
import java.util.ArrayList;

import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.*;
import cs132.vapor.ast.VBuiltIn.Op;

public class VM2M {
	static Helper h = new Helper();
	
	public static void main(String[] args) throws IOException{
		InputStream in = System.in;
		PrintStream err = System.out;
		Op[] ops = {
			    Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS,
			    Op.PrintIntS, Op.HeapAllocZ, Op.Error,
			  };
		boolean allowLocals = false;
		String[] registers = {
			"v0", "v1",
		    "a0", "a1", "a2", "a3",
		    "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7",
		    "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
		    "t8",
		};
		boolean allowStack = true;
		VaporProgram program;
		try {
			program = VaporParser.run(new InputStreamReader(in), 1, 1,
		                              java.util.Arrays.asList(ops),
		                              allowLocals, registers, allowStack);
			//System.out.println("Vapor Parse Successfully");
			
			// print virtual table
			h.print(".data\n", false);
			for (VDataSegment vd : program.dataSegments) {
				h.print(vd.ident+":", false);
				for (VOperand.Static vos : vd.values) {
					VLabelRef vlr = (VLabelRef) vos;
					h.print(vlr.ident, true);
				}
			}
			
			h.print("", false);
			h.print(".text\n", false);
			h.print("jal Main", true);
			h.print("li $v0 10", true);
			h.print("syscall\n", true);
			
			for (VFunction vf : program.functions) {
				int label_index = 0;
				int instr_index = 0;
				ArrayList<Integer> stack_num = new ArrayList<Integer>();
				stack_num.add(vf.stack.out);
				stack_num.add(vf.stack.local);
				h.print(vf.ident+":", false);
				h.printHead(vf.stack.out+vf.stack.local);
				for (VInstr vi : vf.body) {
					while (vf.labels != null && label_index < vf.labels.length && 
							instr_index == vf.labels[label_index].instrIndex) {
						h.print(vf.labels[label_index].ident+":", false);
						label_index++;
					}
					vi.accept(stack_num, new MyVisitor<ArrayList<Integer>, RuntimeException>());
					instr_index++;
				}
				h.print("", false);
			}
			h.printHelper();
		}
		catch (ProblemException ex) {
			err.println(ex.getMessage());
		}
	}
}
