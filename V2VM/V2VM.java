import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.IOException;
import java.util.HashMap;

import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.*;
import cs132.vapor.ast.VBuiltIn.Op;
import cs132.vapor.ast.VInstr;

public class V2VM {
	static Helper h = new Helper();
	
	public static void main(String[] args) throws IOException{
		InputStream in = System.in;
		PrintStream err = System.out;
		Op[] ops = {
			    Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS,
			    Op.PrintIntS, Op.HeapAllocZ, Op.Error,
			  };
		boolean allowLocals = true;
		String[] registers = null;
		boolean allowStack = false;
		VaporProgram program;
		try {
			program = VaporParser.run(new InputStreamReader(in), 1, 1,
		                              java.util.Arrays.asList(ops),
		                              allowLocals, registers, allowStack);
			//System.out.println("Vapor Parse Successfully");
			
			int max_out = 0;
			// print virtual table
			for (VDataSegment vd : program.dataSegments) {
				h.print("const "+vd.ident, false);
				for (VOperand.Static vos : vd.values) {
					VLabelRef vlr = (VLabelRef) vos;
					h.print(":"+vlr.ident, true);
				}
			}
			for (VFunction vf : program.functions) {
				max_out = Math.max(max_out, vf.params.length);
			}
			h.print("", false);
			
			for (VFunction vf : program.functions) {
				int label_index = 0;
				int instr_index = 0;
				int in1, out, local;
				in1 = vf.params.length;
				out = max_out;
				local = vf.vars.length;
				h.print("func "+vf.ident+" [in "+in1+", out "+out+", local "+local+"]", false);
				for (int i = 0; i < in1; i ++) {
					h.print("$s0 = in["+i+"]", true);
					h.print("local["+i+"] = $s0", true);
				}
				for (VInstr vi : vf.body) {
					while (vf.labels != null && label_index < vf.labels.length && 
							instr_index == vf.labels[label_index].instrIndex) {
						h.print(vf.labels[label_index].ident+":", false);
						label_index++;
					}
					vi.accept(new MyVisitor<RuntimeException>());
					instr_index++;
				}
				h.print("", false);
			}
		}
		catch (ProblemException ex) {
			err.println(ex.getMessage());
		}
	}
}
