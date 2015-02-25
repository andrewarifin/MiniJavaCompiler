import java.util.ArrayList;

import cs132.vapor.ast.VAddr;
import cs132.vapor.ast.VAssign;
import cs132.vapor.ast.VBranch;
import cs132.vapor.ast.VBuiltIn;
import cs132.vapor.ast.VCall;
import cs132.vapor.ast.VGoto;
import cs132.vapor.ast.VInstr;
import cs132.vapor.ast.VLabelRef;
import cs132.vapor.ast.VLitInt;
import cs132.vapor.ast.VMemRead;
import cs132.vapor.ast.VMemRef;
import cs132.vapor.ast.VMemWrite;
import cs132.vapor.ast.VOperand;
import cs132.vapor.ast.VReturn;
import cs132.vapor.ast.VVarRef;


public class MyVisitor<P, E extends Throwable> extends VInstr.VisitorP<P,E>{

	Helper h = new Helper();

	@Override
	public void visit(P p, VAssign arg0) throws E {
		if (arg0.source instanceof VLabelRef) {
			VLabelRef<?> vlr = (VLabelRef<?>)arg0.source;
			h.print("la $t9 "+vlr.ident, true);
			h.print("move "+arg0.dest.toString()+" $t9", true);
		}
		else if (arg0.source instanceof VLitInt) {
			h.print("li "+arg0.dest.toString()+" "+arg0.source.toString(), true);
		}
		else if (arg0.source instanceof VVarRef.Register) {
			h.print("move "+arg0.dest.toString()+" "+arg0.source.toString(), true);
		}
	}

	@Override
	public void visit(P p, VCall arg0) throws E {
		if (arg0.addr instanceof VAddr.Label) {
			VAddr.Label<?> val = (VAddr.Label<?>)arg0.addr;
			h.print("la $t9 "+val.label.ident, true);
			h.print("jalr $t9", true);
		}
		else if (arg0.addr instanceof VAddr.Var) {
			h.print("jalr "+arg0.addr.toString(), true);
		}
		
	}

	@Override
	public void visit(P p, VBuiltIn arg0) throws E {
		if (arg0.op == VBuiltIn.Op.Error) {
			if (arg0.args[0].toString().equals("\"null pointer\"")) {
				h.print("la $a0 _str0", true);
			}
			else if (arg0.args[0].toString().equals("\"array index out of bound\"")) {
				h.print("la $a0 _str1", true);
			}
			h.print("j _error", true);
		} else if (arg0.op == VBuiltIn.Op.PrintIntS) {
			if (arg0.args[0] instanceof VVarRef) {
				h.print("move $a0 "+arg0.args[0].toString(), true);
			} else if (arg0.args[0] instanceof VLitInt) {
				h.print("li $a0 "+arg0.args[0].toString(), true);
			} else if (arg0.args[0] instanceof VLabelRef) {
				VLabelRef<?> vlr = (VLabelRef<?>)arg0.args[0];
				h.print("la $a0 "+vlr.ident, true);
			}
			h.print("jal _print", true);
		} else if (arg0.op == VBuiltIn.Op.HeapAllocZ) {
			if (arg0.args[0] instanceof VVarRef) {
				h.print("move $a0 "+arg0.args[0].toString(), true);
			} else if (arg0.args[0] instanceof VLitInt) {
				h.print("li $a0 "+arg0.args[0].toString(), true);
			}
			h.print("jal _heapAlloc", true);
			h.print("move "+arg0.dest.toString()+" $v0", true);
		} else {
			if (arg0.args[0] instanceof VVarRef) {
				h.print("move $a0 "+arg0.args[0].toString(), true);
			} else if (arg0.args[0] instanceof VLitInt) {
				h.print("li $a0 "+arg0.args[0].toString(), true);
			} else if (arg0.args[0] instanceof VLabelRef) {
				VLabelRef<?> vlr = (VLabelRef<?>)arg0.args[0];
				h.print("la $a0 "+vlr.ident, true);
			}
			if (arg0.args[1] instanceof VVarRef) {
				h.print("move $a1 "+arg0.args[1].toString(), true);
			} else if (arg0.args[1] instanceof VLitInt) {
				h.print("li $a1 "+arg0.args[1].toString(), true);
			} else if (arg0.args[1] instanceof VLabelRef) {
				VLabelRef<?> vlr = (VLabelRef<?>)arg0.args[1];
				h.print("la $a1 "+vlr.ident, true);
			}
			if (arg0.op == VBuiltIn.Op.Add) {
				h.print("addu "+arg0.dest.toString()+" $a0 $a1", true);
			} else if (arg0.op == VBuiltIn.Op.Sub) {
				h.print("subu "+arg0.dest.toString()+" $a0 $a1", true);
			} else if (arg0.op == VBuiltIn.Op.MulS) {
				h.print("mul "+arg0.dest.toString()+" $a0 $a1", true);
			} else if (arg0.op == VBuiltIn.Op.Eq) {
				h.print("subu $t9 $a0 $a1", true);
				h.print("sltiu "+arg0.dest.toString()+" $t9 1", true);
			} else if (arg0.op == VBuiltIn.Op.Lt) {
				h.print("sltu "+arg0.dest.toString()+" $a0 $a1", true);
			} else if (arg0.op == VBuiltIn.Op.LtS) {
				h.print("slt "+arg0.dest.toString()+" $a0 $a1", true);
			}
		}
		
	}

	@Override
	public void visit(P p, VMemWrite arg0) throws E {
		if (arg0.source instanceof VLabelRef) {
			VLabelRef<?> vlr = (VLabelRef<?>)arg0.source;
			h.print("la $t9 "+vlr.ident, true);
		}
		else if (arg0.source instanceof VLitInt) {
			h.print("li $t9 "+arg0.source.toString(), true);
		}
		else if (arg0.source instanceof VVarRef.Register) {
			h.print("move $t9 "+arg0.source.toString(), true);
		}
		
		if (arg0.dest instanceof VMemRef.Global) {
			VMemRef.Global vmrg = (VMemRef.Global)arg0.dest;
			if (vmrg.base instanceof VAddr.Label<?>) {
				VAddr.Label<?> val = (VAddr.Label<?>)vmrg.base;
				h.print("la $v1 "+val.label.ident, true);
				h.print("sw $t9 "+vmrg.byteOffset+"($v1)", true);
			}
			else if (vmrg.base instanceof VAddr.Var) {
				h.print("sw $t9 "+vmrg.byteOffset+"("+vmrg.base.toString()+")", true);
			}
		}
		else if (arg0.dest instanceof VMemRef.Stack) {
			ArrayList<Integer> stack_num = (ArrayList<Integer>)p;
			VMemRef.Stack vmrs = (VMemRef.Stack)arg0.dest;
			if (vmrs.region == VMemRef.Stack.Region.In) {
				h.print("sw $t9 "+vmrs.index*4+"($fp)", true);
			} else if (vmrs.region == VMemRef.Stack.Region.Out) {
				h.print("sw $t9 "+(vmrs.index)*4+"($sp)", true);
			} else if (vmrs.region == VMemRef.Stack.Region.Local) {
				h.print("sw $t9 "
						+(stack_num.get(0)+vmrs.index)*4+"($sp)", true);
			}
		}
		
	}

	@Override
	public void visit(P p, VMemRead arg0) throws E {
		if (arg0.source instanceof VMemRef.Global) {
			VMemRef.Global vmrg = (VMemRef.Global)arg0.source;
			if (vmrg.base instanceof VAddr.Label<?>) {
				VAddr.Label<?> val = (VAddr.Label<?>)vmrg.base;
				h.print("la $t9 "+val.label.ident, true);
				h.print("lw "+arg0.dest.toString()+" "+vmrg.byteOffset+"($t9)", true);
			}
			else if (vmrg.base instanceof VAddr.Var) {
				h.print("lw "+arg0.dest.toString()+" "+vmrg.byteOffset+"("+vmrg.base.toString()+")", true);
			}
		}
		else if (arg0.source instanceof VMemRef.Stack) {
			ArrayList<Integer> stack_num = (ArrayList<Integer>)p;
			VMemRef.Stack vmrs = (VMemRef.Stack)arg0.source;
			if (vmrs.region == VMemRef.Stack.Region.In) {
				h.print("lw "+arg0.dest.toString()+" "+vmrs.index*4+"($fp)", true);
			} else if (vmrs.region == VMemRef.Stack.Region.Out) {
				h.print("lw "+arg0.dest.toString()+" "+(vmrs.index)*4+"($sp)", true);
			} else if (vmrs.region == VMemRef.Stack.Region.Local) {
				h.print("lw "+arg0.dest.toString()+" "
						+(stack_num.get(0)+vmrs.index)*4+"($sp)", true);
			}
		}
		
	}

	@Override
	public void visit(P p, VBranch arg0) throws E {
		if (arg0.positive) {
			h.print("bnez "+arg0.value.toString()+" "+arg0.target.ident, true);
		}
		else {
			h.print("beqz "+arg0.value.toString()+" "+arg0.target.ident, true);
		}
	}

	@Override
	public void visit(P p, VGoto arg0) throws E {
		VAddr.Label<?> val = (VAddr.Label<?>)arg0.target;
		h.print("j "+val.label.ident, true);
	}

	@Override
	public void visit(P p, VReturn arg0) throws E {
		ArrayList<Integer> stack_num = (ArrayList<Integer>)p;
		h.printTail(stack_num.get(0)+stack_num.get(1));
		h.print("jr $ra", true);
	}
}
