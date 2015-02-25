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


public class MyVisitor<E extends Throwable> extends VInstr.Visitor<E>{

	Helper h = new Helper();
	@Override
	public void visit(VAssign arg0) throws E {
		VVarRef.Local vvrl = (VVarRef.Local) arg0.dest;
		if (arg0.source instanceof VLabelRef) {
			h.print("local["+vvrl.index+"] = "+arg0.source.toString(), true);
		}
		else if (arg0.source instanceof VLitInt) {
			h.print("local["+vvrl.index+"] = "+arg0.source.toString(), true);
		}
		else if (arg0.source instanceof VVarRef.Local) {
			VVarRef.Local vvrl1 = (VVarRef.Local) arg0.source;
			h.print("$s0 = local["+vvrl1.index+"]", true);
			h.print("local["+vvrl.index+"] = $s0", true);
		}
	}

	@Override
	public void visit(VCall arg0) throws E {
		int i = 0;
		VVarRef.Local vvrl = (VVarRef.Local) arg0.dest;
		
		for (VOperand vo : arg0.args) {
			if (vo instanceof VLabelRef) {
				h.print("out["+i+"] = "+vo.toString(), true);
			}
			else if (vo instanceof VLitInt) {
				h.print("out["+i+"] = "+vo.toString(), true);
			}
			else if (vo instanceof VVarRef.Local) {
				VVarRef.Local vvrl1 = (VVarRef.Local) vo;
				h.print("$s0 = local["+vvrl1.index+"]", true);
				h.print("out["+i+"] = $s0", true);
			}
			i++;
		}
		if (arg0.addr instanceof VAddr.Label) {
			h.print("call "+arg0.addr.toString(), true);
		}
		else if (arg0.addr instanceof VAddr.Var) {
			VAddr.Var vav = (VAddr.Var) arg0.addr;
			VVarRef.Local vvrl1 = (VVarRef.Local) vav.var;
			h.print("$s0 = local["+vvrl1.index+"]", true);
			h.print("call $s0", true);
		}
		h.print("local["+vvrl.index+"] = $v0", true);
	}

	@Override
	public void visit(VBuiltIn arg0) throws E {
		if (arg0.op == VBuiltIn.Op.Error) {
			h.print("Error("+arg0.args[0].toString()+")", true);
		}
		else if (arg0.op == VBuiltIn.Op.HeapAllocZ) {
			VVarRef.Local vvrl = (VVarRef.Local) arg0.dest;
			if (arg0.args[0] instanceof VLabelRef) {
				h.print("$v0 = "+arg0.args[0].toString(), true);
				h.print("$s0 = HeapAllocZ($v0)", true);
				h.print("local["+vvrl.index+"] = $s0", true);
			}
			else if (arg0.args[0] instanceof VLitInt) {
				h.print("$s0 = HeapAllocZ("+arg0.args[0].toString()+")", true);
				h.print("local["+vvrl.index+"] = $s0", true);
			}
			else if (arg0.args[0] instanceof VVarRef.Local) {
				VVarRef.Local vvr = (VVarRef.Local) arg0.args[0];
				h.print("$v0 = local["+vvr.index+"]", true);
				h.print("$s0 = HeapAllocZ($v0)", true);
				h.print("local["+vvrl.index+"] = $s0", true);
			}
		}
		else if (arg0.op == VBuiltIn.Op.PrintIntS) {
			if (arg0.args[0] instanceof VLabelRef) {
				h.print("$v0 = "+arg0.args[0].toString(), true);
				h.print("PrintIntS($v0)", true);
			}
			else if (arg0.args[0] instanceof VLitInt) {
				h.print("PrintIntS("+arg0.args[0].toString()+")", true);
			}
			else if (arg0.args[0] instanceof VVarRef.Local) {
				VVarRef.Local vvr = (VVarRef.Local) arg0.args[0];
				h.print("$v0 = local["+vvr.index+"]", true);
				h.print("PrintIntS($v0)", true);
			}
		}
		else {
			VVarRef.Local vvrl = (VVarRef.Local) arg0.dest;
			if (arg0.args[0] instanceof VLabelRef) {
				h.print("$v0 = "+arg0.args[0].toString(), true);
			}
			else if (arg0.args[0] instanceof VLitInt) {
				h.print("$v0 = "+arg0.args[0].toString(), true);
			}
			else if (arg0.args[0] instanceof VVarRef.Local) {
				VVarRef.Local vvr = (VVarRef.Local) arg0.args[0];
				h.print("$v0 = local["+vvr.index+"]", true);
			}
			if (arg0.args[1] instanceof VLabelRef) {
				h.print("$v1 = "+arg0.args[1].toString(), true);
			}
			else if (arg0.args[1] instanceof VLitInt) {
				h.print("$v1 = "+arg0.args[1].toString(), true);
			}
			else if (arg0.args[1] instanceof VVarRef.Local) {
				VVarRef.Local vvr = (VVarRef.Local) arg0.args[1];
				h.print("$v1 = local["+vvr.index+"]", true);
			}
			if (arg0.op == VBuiltIn.Op.Add) h.print("$s0 = Add($v0 $v1)", true);
			else if (arg0.op == VBuiltIn.Op.Sub) h.print("$s0 = Sub($v0 $v1)", true);
			else if (arg0.op == VBuiltIn.Op.MulS) h.print("$s0 = MulS($v0 $v1)", true);
			else if (arg0.op == VBuiltIn.Op.Eq) h.print("$s0 = Eq($v0 $v1)", true);
			else if (arg0.op == VBuiltIn.Op.Lt) h.print("$s0 = Lt($v0 $v1)", true);
			else if (arg0.op == VBuiltIn.Op.LtS) h.print("$s0 = LtS($v0 $v1)", true);
			h.print("local["+vvrl.index+"] = $s0", true);
		}
	}

	@Override
	public void visit(VMemWrite arg0) throws E {
		VMemRef.Global vmrg = (VMemRef.Global) arg0.dest;
		if (vmrg.base instanceof VAddr.Label) {
			if (arg0.source instanceof VLabelRef) {
				h.print("["+vmrg.base.toString()+"+"+vmrg.byteOffset+"] = "+arg0.source.toString(), true);
			}
			else if (arg0.source instanceof VLitInt) {
				h.print("["+vmrg.base.toString()+"+"+vmrg.byteOffset+"] = "+arg0.source.toString(), true);
			}
			else if (arg0.source instanceof VVarRef.Local) {
				VVarRef.Local vvr = (VVarRef.Local) arg0.source;
				h.print("$s0 = local["+vvr.index+"]", true);
				h.print("["+vmrg.base.toString()+"+"+vmrg.byteOffset+"] = $s0", true);
			}
		}
		else if (vmrg.base instanceof VAddr.Var) {
			VAddr.Var vav = (VAddr.Var) vmrg.base;
			VVarRef.Local vvrl = (VVarRef.Local) vav.var;
			if (arg0.source instanceof VLabelRef) {
				h.print("$s0 = local["+vvrl.index+"]", true);
				h.print("[$s0+"+vmrg.byteOffset+"] = "+arg0.source.toString(), true);
			}
			else if (arg0.source instanceof VLitInt) {
				h.print("$s0 = local["+vvrl.index+"]", true);
				h.print("[$s0+"+vmrg.byteOffset+"] = "+arg0.source.toString(), true);
			}
			else if (arg0.source instanceof VVarRef.Local) {
				VVarRef.Local vvr = (VVarRef.Local) arg0.source;
				h.print("$s0 = local["+vvrl.index+"]", true);
				h.print("$s1 = local["+vvr.index+"]", true);
				h.print("[$s0+"+vmrg.byteOffset+"] = $s1", true);
			}
		}
		
	}

	@Override
	public void visit(VMemRead arg0) throws E {
		VMemRef.Global vmrg = (VMemRef.Global) arg0.source;
		if (vmrg.base instanceof VAddr.Label) {
			VVarRef.Local vvrl = (VVarRef.Local) arg0.dest;
			h.print("$s0 = ["+vmrg.base.toString()+"+"+vmrg.byteOffset+"]", true);
			h.print("local["+vvrl.index+"] = $s0", true);
		}
		else if (vmrg.base instanceof VAddr.Var) {
			VAddr.Var vav = (VAddr.Var) vmrg.base;
			VVarRef.Local vvrl = (VVarRef.Local) arg0.dest;
			VVarRef.Local vvrl1 = (VVarRef.Local) vav.var;
			h.print("$s0 = local["+vvrl1.index+"]", true);
			h.print("$s1 = [$s0+"+vmrg.byteOffset+"]", true);
			h.print("local["+vvrl.index+"] = $s1", true);
		}
		
	}

	@Override
	public void visit(VBranch arg0) throws E {
		if (arg0.value instanceof VLabelRef) {
			h.print("$s0 = "+arg0.value.toString(), true);
			if (arg0.positive) {
				h.print("if $s0 goto "+arg0.target.toString(), true);
			}
			else h.print("if0 $s0 goto "+arg0.target.toString(), true);
		}
		else if (arg0.value instanceof VLitInt) {
			h.print("$s0 = "+arg0.value.toString(), true);
			if (arg0.positive) {
				h.print("if $s0 goto "+arg0.target.toString(), true);
			}
			else h.print("if0 $s0 goto "+arg0.target.toString(), true);
		}
		else if (arg0.value instanceof VVarRef.Local) {
			VVarRef.Local vvrl = (VVarRef.Local) arg0.value;
			h.print("$s0 = local["+vvrl.index+"]", true);
			if (arg0.positive) {
				h.print("if $s0 goto "+arg0.target.toString(), true);
			}
			else h.print("if0 $s0 goto "+arg0.target.toString(), true);
		}
	}

	@Override
	public void visit(VGoto arg0) throws E {
		if (arg0.target instanceof VAddr.Label) {
			h.print("goto "+arg0.target.toString(), true);
		}
		else if (arg0.target instanceof VAddr.Var) {
			VAddr.Var vav = (VAddr.Var) arg0.target;
			VVarRef.Local vvrl = (VVarRef.Local) vav.var;
			h.print("$s0 = local["+vvrl.index+"]", true);
			h.print("goto $s0", true);
		}
		
	}

	@Override
	public void visit(VReturn arg0) throws E {
		if (arg0.value == null) {
			// Do nothing
		}
		else if (arg0.value instanceof VLabelRef) {
			h.print("$v0 = "+arg0.value.toString(), true);
		}
		else if (arg0.value instanceof VLitInt) {
			h.print("$v0 = "+arg0.value.toString(), true);
		}
		else if (arg0.value instanceof VVarRef.Local) {
			VVarRef.Local vrl = (VVarRef.Local) arg0.value;
			h.print("$v0 = local["+vrl.index+"]", true);
		}
		h.print("ret", true);
	}

	
}
