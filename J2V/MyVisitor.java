import syntaxtree.*;
import visitor.GJVisitor;

import java.util.*;

public class MyVisitor<R,A> implements GJVisitor<R,A>{
	
	Helper h = new Helper();
	HashMap<String, ArrayList<String>> vtable =  new HashMap<String, ArrayList<String>>();
	HashMap<String, ArrayList<String>> vartable = new HashMap<String, ArrayList<String>>();
	HashMap<String, ArrayList<String>> medtable = new HashMap<String, ArrayList<String>>();
	HashMap<String, String> typetable = null;
	HashMap<String, HashMap<String, String>> typetableEntry = new HashMap<String, HashMap<String, String>>();
	Identifier thisId = null;
	Identifier medId = null;
	ArrayList<Integer> paraTemp = null;
	int ifCounter = 0;
	int whileCounter = 0;
	int boundCounter = 0;
	int nullCounter = 0;
	int classCounter = 0;
	HashMap<String, Boolean> classReadyTable = new HashMap<String, Boolean>();
	HashMap<String, String> returnType = new HashMap<String, String>();
	ArrayList<String> initable = null;
	Boolean ifMain = false;
	//
	// Auto class visitors--probably don't need to be overridden.
	//
	public R visit(NodeList n, A argu) {
	   Integer start = (Integer)argu;
	   int _count=0;
	   for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
	      start = (Integer)e.nextElement().accept(this,(A)start);
	      _count++;
	   }
	   return (R)start;
	}

	public R visit(NodeListOptional n, A argu) {
	   if ( n.present() ) {
		   Integer start = (Integer)argu;
		   int _count=0;
		   for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
		      start = (Integer)e.nextElement().accept(this,(A)start);
		      _count++;
		   }
		   return (R)start;
	   }
	   else
	      return (R)argu;
	}

	public R visit(NodeOptional n, A argu) {
	   if ( n.present() ) {
		   Integer start = (Integer)argu;
		   start = (Integer)n.node.accept(this,(A)start);
		   return (R)start;
	   }
	      
	   else
	      return (R)argu;
	}

	public R visit(NodeSequence n, A argu) {
		Integer start = (Integer)argu;
		int _count=0;
		for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
		   start = (Integer)e.nextElement().accept(this,(A)start);
		   _count++;
		}
		return (R)start;
	}

	public R visit(NodeToken n, A argu) { return null; }

	//
	// User-generated visitor methods below
	//

	/**
	 * f0 -> MainClass()
	 * f1 -> ( TypeDeclaration() )*
	 * f2 -> <EOF>
	 */
	public R visit(Goal n, A argu) {
	   R _ret=null;
	   
	   for (int i = 0; i < n.f1.size(); i ++) {
		   TypeDeclaration tNode = (TypeDeclaration)n.f1.elementAt(i);
		   if (tNode.f0.which == 0) {
			   ClassDeclaration cNode = (ClassDeclaration)tNode.f0.choice;
			   String name = h.getName(cNode.f1);
			   h.print("const vmt_" + name);
			   ArrayList<String> insert = new ArrayList<String>();
			   ArrayList<String> var = new ArrayList<String>();
			   HashMap<String, String> ttable = new HashMap<String, String>();
			   for (int j = 0; j < cNode.f4.size(); j ++) {
				   MethodDeclaration mNode = (MethodDeclaration)cNode.f4.elementAt(j);
				   h.print("\t:"+name+"."+h.getName(mNode.f2));
				   insert.add(name+"."+h.getName(mNode.f2));
				   if (mNode.f1.f0.which == 3) {
					   Identifier iNode = (Identifier)mNode.f1.f0.choice;
					   returnType.put(h.getName(mNode.f2), h.getName(iNode));
				   }
			   }
			   for (int j = 0; j < cNode.f3.size(); j ++) {
				   VarDeclaration vNode = (VarDeclaration)cNode.f3.elementAt(j);
				   var.add(h.getName(vNode.f1));
				   if (vNode.f0.f0.which == 3) {
					   Identifier iNode = (Identifier)vNode.f0.f0.choice;
					   ttable.put(h.getName(vNode.f1), h.getName(iNode));
				   }
			   }
			   h.print("\n");
			   vtable.put(name, insert);
			   vartable.put(name, var);
			   classReadyTable.put(name, true);
			   typetableEntry.put(name, ttable);
		   } else {
			   ClassExtendsDeclaration ceNode = (ClassExtendsDeclaration)tNode.f0.choice;
			   String name = h.getName(ceNode.f1);
			   classReadyTable.put(name, false);
			   classCounter ++;
		   }
	   }
	   while (classCounter != 0) {
		   for (int i = 0; i < n.f1.size(); i ++) {
			   TypeDeclaration tNode = (TypeDeclaration)n.f1.elementAt(i);
			   if (tNode.f0.which == 1) {
				   ClassExtendsDeclaration ceNode = (ClassExtendsDeclaration)tNode.f0.choice;
				   String name = h.getName(ceNode.f1);
				   String eName = h.getName(ceNode.f3);
				   if (classReadyTable.get(name)) continue;
				   if (!classReadyTable.get(eName)) continue;
				   h.print("const vmt_" + name);
				   ArrayList<String> insert = new ArrayList<String>();
				   ArrayList<String> realInsert = new ArrayList<String>();
				   ArrayList<String> var = new ArrayList<String>();
				   HashMap<String, String> ttable = new HashMap<String, String>();
				   ArrayList<String> varray = vtable.get(eName);
				   ArrayList<String> vararray = vartable.get(eName);
				   HashMap<String, String> exttable = typetableEntry.get(eName);

				   for (int j = 0; j < ceNode.f6.size(); j ++) {
					   MethodDeclaration mNode = (MethodDeclaration)ceNode.f6.elementAt(j);
					   insert.add(h.getName(mNode.f2));
					   if (mNode.f1.f0.which == 3) {
						   Identifier iNode = (Identifier)mNode.f1.f0.choice;
						   returnType.put(h.getName(mNode.f2), h.getName(iNode));
					   }
				   }
				   for (int j = 0; j < ceNode.f5.size(); j ++) {
					   VarDeclaration vNode = (VarDeclaration)ceNode.f5.elementAt(j);
					   var.add(h.getName(vNode.f1));
					   if (vNode.f0.f0.which == 3) {
						   Identifier iNode = (Identifier)vNode.f0.f0.choice;
						   ttable.put(h.getName(vNode.f1), h.getName(iNode));
					   }
				   }
				   for (int j = 0; j < varray.size(); j ++) {
					   String ele = varray.get(j);
					   Integer index = ele.indexOf('.');
					   ele = ele.substring(index+1);
					   if (insert.contains(ele)) {
						   h.print("\t:"+name+"."+ele);
						   realInsert.add(name+"."+ele);
						   insert.remove(ele);
					   } else {
						   h.print("\t:"+varray.get(j));
						   realInsert.add(varray.get(j));
					   }
				   }
				   for (int j = 0; j < vararray.size(); j ++) {
					   if (!var.contains(vararray.get(j))) var.add(vararray.get(j));
				   }
				   for (int j = 0; j < insert.size(); j ++) {
					   h.print("\t:"+name+"."+insert.get(j));
					   realInsert.add(name+"."+insert.get(j));
				   }
				   for (String key : exttable.keySet()) {
					   if (!ttable.containsKey(key)) {
						   ttable.put(key, exttable.get(key));
					   }
				   }
				   h.print("\n");
				   vtable.put(h.getName(ceNode.f1), realInsert);
				   vartable.put(h.getName(ceNode.f1),var);
				   typetableEntry.put(name, ttable);
				   classReadyTable.put(name, true);
				   classCounter --;
			   }
		   }
	   }
		   
	   n.f0.accept(this, argu);
	   n.f1.accept(this, argu);
	   
	   return _ret;
	}

	/**
	 * f0 -> "class"
	 * f1 -> Identifier()
	 * f2 -> "{"
	 * f3 -> "public"
	 * f4 -> "static"
	 * f5 -> "void"
	 * f6 -> "main"
	 * f7 -> "("
	 * f8 -> "String"
	 * f9 -> "["
	 * f10 -> "]"
	 * f11 -> Identifier()
	 * f12 -> ")"
	 * f13 -> "{"
	 * f14 -> ( VarDeclaration() )*
	 * f15 -> ( Statement() )*
	 * f16 -> "}"
	 * f17 -> "}"
	 */
	public R visit(MainClass n, A argu) {
	   R _ret=null;
	   
	   ifMain = true;
	   thisId = n.f1;
	   typetable = new HashMap<String, String>();
	   initable = new ArrayList<String>();
	   
	   h.print("func Main()");
	   n.f14.accept(this, argu);
	   n.f15.accept(this, (A)new Integer(0));
	   h.print("\tret");
	   h.print("\n");
	   
	   thisId = null;
	   typetable = null;
	   ifMain = false;
	   initable = null;
	   
	   return _ret;
	}

	/**
	 * f0 -> ClassDeclaration()
	 *       | ClassExtendsDeclaration()
	 */
	public R visit(TypeDeclaration n, A argu) {
	   R _ret=null;
	   n.f0.accept(this, argu);
	   return _ret;
	}

	/**
	 * f0 -> "class"
	 * f1 -> Identifier()
	 * f2 -> "{"
	 * f3 -> ( VarDeclaration() )*
	 * f4 -> ( MethodDeclaration() )*
	 * f5 -> "}"
	 */
	public R visit(ClassDeclaration n, A argu) {
	   R _ret=null;
	 
	   thisId = n.f1;
	   typetable = typetableEntry.get(h.getName(n.f1));
	   
	   n.f3.accept(this, argu);
	   n.f4.accept(this, argu);
	   
	   thisId = null;
	   typetable = null;
	   
	   return _ret;
	}

	/**
	 * f0 -> "class"
	 * f1 -> Identifier()
	 * f2 -> "extends"
	 * f3 -> Identifier()
	 * f4 -> "{"
	 * f5 -> ( VarDeclaration() )*
	 * f6 -> ( MethodDeclaration() )*
	 * f7 -> "}"
	 */
	public R visit(ClassExtendsDeclaration n, A argu) {
		R _ret=null;
		
		thisId = n.f1;
		typetable = typetableEntry.get(h.getName(n.f1));
		
		n.f5.accept(this, argu);
		n.f6.accept(this, argu);
		
		thisId = null;
		typetable = null;
		
		return _ret;
	}

	/**
	 * f0 -> Type()
	 * f1 -> Identifier()
	 * f2 -> ";"
	 */
	public R visit(VarDeclaration n, A argu) {
	   R _ret=null;
	   if (n.f0.f0.which == 3) {
		   Identifier iNode = (Identifier)n.f0.f0.choice;
		   typetable.put(h.getName(n.f1), h.getName(iNode));
	   }
	   return _ret;
	}

	/**
	 * f0 -> "public"
	 * f1 -> Type()
	 * f2 -> Identifier()
	 * f3 -> "("
	 * f4 -> ( FormalParameterList() )?
	 * f5 -> ")"
	 * f6 -> "{"
	 * f7 -> ( VarDeclaration() )*
	 * f8 -> ( Statement() )*
	 * f9 -> "return"
	 * f10 -> Expression()
	 * f11 -> ";"
	 * f12 -> "}"
	 */
	public R visit(MethodDeclaration n, A argu) {
	   
	   medId = n.f2;
	   
	   ArrayList<String> para = new ArrayList<String>();
	   initable = new ArrayList<String>();
	   
	   String output = "func "+h.getName(thisId)+"."+h.getName(n.f2)+"(this"; 
	   if (n.f4.node != null) {
		   FormalParameterList fplNode = (FormalParameterList)n.f4.node;
		   FormalParameter fpNode = fplNode.f0;
		   output += " "+h.getName(fpNode.f1);
		   para.add(h.getName(fpNode.f1));
		   initable.add(h.getName(fpNode.f1));
		   for (int i = 0; i < fplNode.f1.size(); i ++) {
			   FormalParameterRest fprNode = (FormalParameterRest)fplNode.f1.elementAt(i);
			   output += " "+h.getName(fprNode.f1.f1);
			   para.add(h.getName(fprNode.f1.f1));
			   initable.add(h.getName(fprNode.f1.f1));
		   }
	   }
	   output += ")";
	   h.print(output);
	   for (int i = 0; i < n.f7.size(); i ++) {
		   VarDeclaration vNode = (VarDeclaration)n.f7.elementAt(i);
		   para.add(h.getName(vNode.f1));
	   }
	   medtable.put(h.getName(thisId)+"."+h.getName(n.f2), para);
	   n.f4.accept(this, argu);
	   n.f7.accept(this, argu);
	   Integer start = (Integer) n.f8.accept(this, (A)new Integer(0));
	   Integer exp = (Integer) n.f10.accept(this, (A)start);
	   
	   if (exp == start) h.print("\tret");
	   else h.print("\tret t."+(exp-1)+"\n");
	   
	   medId = null;
	   initable = null;
	   
	   return (R)exp;
	}

	/**
	 * f0 -> FormalParameter()
	 * f1 -> ( FormalParameterRest() )*
	 */
	public R visit(FormalParameterList n, A argu) {
	   R _ret=null;
	   n.f0.accept(this, argu);
	   n.f1.accept(this, argu);
	   return _ret;
	}

	/**
	 * f0 -> Type()
	 * f1 -> Identifier()
	 */
	public R visit(FormalParameter n, A argu) {
	   R _ret=null;
	   if (n.f0.f0.which == 3) {
		   Identifier iNode = (Identifier)n.f0.f0.choice;
		   typetable.put(h.getName(n.f1), h.getName(iNode));
	   }
	   return _ret;
	}

	/**
	 * f0 -> ","
	 * f1 -> FormalParameter()
	 */
	public R visit(FormalParameterRest n, A argu) {
	   R _ret=null;
	   n.f1.accept(this, argu);
	   return _ret;
	}

	/**
	 * f0 -> ArrayType()
	 *       | BooleanType()
	 *       | IntegerType()
	 *       | Identifier()
	 */
	public R visit(Type n, A argu) {
	   R _ret=null;
	   return _ret;
	}

	/**
	 * f0 -> "int"
	 * f1 -> "["
	 * f2 -> "]"
	 */
	public R visit(ArrayType n, A argu) {
	   R _ret=null;
	   return _ret;
	}

	/**
	 * f0 -> "boolean"
	 */
	public R visit(BooleanType n, A argu) {
	   R _ret=null;
	   return _ret;
	}

	/**
	 * f0 -> "int"
	 */
	public R visit(IntegerType n, A argu) {
	   R _ret=null;
	   return _ret;
	}

	/**
	 * f0 -> Block()
	 *       | AssignmentStatement()
	 *       | ArrayAssignmentStatement()
	 *       | IfStatement()
	 *       | WhileStatement()
	 *       | PrintStatement()
	 */
	public R visit(Statement n, A argu) {
	   Integer start = (Integer)argu;
	   start = (Integer)n.f0.accept(this, (A)start);
	   return (R)start;
	}

	/**
	 * f0 -> "{"
	 * f1 -> ( Statement() )*
	 * f2 -> "}"
	 */
	public R visit(Block n, A argu) {
	   Integer start = (Integer)argu;
	   start = (Integer) n.f1.accept(this, (A)start);
	   return (R)start;
	}

	/**
	 * f0 -> Identifier()
	 * f1 -> "="
	 * f2 -> Expression()
	 * f3 -> ";"
	 */
	public R visit(AssignmentStatement n, A argu) {
	   Integer start = (Integer)argu;
	   
	   start = (Integer) n.f2.accept(this, (A)start);
	   //System.out.print(h.findPara(medtable, thisId, medId, n.f0));
	   if (medId == null || h.findPara(medtable, thisId, medId, n.f0)) {
		   h.print("\t"+h.getName(n.f0)+" = t."+(start-1));
		   initable.add(h.getName(n.f0));
	   } else {
		   Integer index = h.getParaPos(vartable, h.getName(thisId), n.f0);
		   h.print("\t[this+"+4*(index+1)+"] = t."+(start-1));
		   start ++;
	   }
	   return (R)start;
	}

	/**
	 * f0 -> Identifier()
	 * f1 -> "["
	 * f2 -> Expression()
	 * f3 -> "]"
	 * f4 -> "="
	 * f5 -> Expression()
	 * f6 -> ";"
	 */
	public R visit(ArrayAssignmentStatement n, A argu) {
	   Integer start = (Integer)argu;
	   Integer mid1 = (Integer)n.f2.accept(this, (A)start);
	   Integer mid2 = (Integer)n.f5.accept(this, (A)mid1);
	   int count;
	   
	   if (h.findPara(medtable, thisId, medId, n.f0)) {
		   if (!initable.contains(h.getName(n.f0))) h.print("\t"+h.getName(n.f0)+" = 0");
		   count = nullCounter++;
		   h.print("\tt."+mid2+" = Eq("+h.getName(n.f0)+" 0)");
		   h.print("\tif0 t."+mid2+" goto :null"+count);
		   h.print("\tError(\"null pointer\")");
		   h.print("null"+count+":");
		   h.print("\tt."+mid2+" = ["+h.getName(n.f0)+"]");
		   h.print("\tt."+mid2+" = Lt(t."+(mid1-1)+" t."+mid2+")");
		   count = boundCounter++;
		   h.print("\tif t."+mid2+" goto :bounds"+count);
		   h.print("\tError(\"array index out of bounds\")");
		   h.print("bounds"+count+":");
		   h.print("\tt."+(mid1-1)+" = Add(t."+(mid1-1)+" 1)");
		   h.print("\tt."+(mid1-1)+" = MulS(t."+(mid1-1)+" 4)");
		   h.print("\tt."+(mid1-1)+" = Add(t."+(mid1-1)+" "+h.getName(n.f0)+")");
		   h.print("\t[t."+(mid1-1)+"] = t."+(mid2-1));
	   }
	   else {
		   Integer index = h.getParaPos(vartable, h.getName(thisId), n.f0);
		   h.print("\tt."+mid2+" = [this+"+(index+1)*4+"]");
		   count = nullCounter++;
		   h.print("\tt."+mid2+" = Eq(t."+mid2+" 0)");
		   h.print("\tif0 t."+mid2+" goto :null"+count);
		   h.print("\tError(\"null pointer\")");
		   h.print("null"+count+":");
		   
		   h.print("\tt."+mid2+" = [this+"+(index+1)*4+"]");
		   h.print("\tt."+mid2+" = [t."+mid2+"]");
		   h.print("\tt."+mid2+" = Lt(t."+(mid1-1)+" t."+mid2+")");
		   count = boundCounter++;
		   h.print("\tif t."+mid2+" goto :bounds"+count);
		   h.print("\tError(\"array index out of bounds\")");
		   h.print("bounds"+count+":");
		   h.print("\tt."+(mid1-1)+" = Add(t."+(mid1-1)+" 1)");
		   h.print("\tt."+(mid1-1)+" = MulS(t."+(mid1-1)+" 4)");
		   h.print("\tt."+mid2+" = [this+"+(index+1)*4+"]");
		   h.print("\tt."+(mid1-1)+" = Add(t."+(mid1-1)+" t."+mid2+")");
		   h.print("\t[t."+(mid1-1)+"] = t."+(mid2-1));
	   }
	   
	   return (R)mid2; 
	}

	/**
	 * f0 -> "if"
	 * f1 -> "("
	 * f2 -> Expression()
	 * f3 -> ")"
	 * f4 -> Statement()
	 * f5 -> "else"
	 * f6 -> Statement()
	 */
	public R visit(IfStatement n, A argu) {
	   Integer start = (Integer)argu;

	   Integer counter = ifCounter ++;
	   start = (Integer)n.f2.accept(this, (A)start);
	   h.print("\tif0 t."+(start-1)+" goto :if"+counter+"_else");
	   start = (Integer)n.f4.accept(this, (A)start);
	   h.print("\tgoto :if"+counter+"_end");
	   h.print("if"+counter+"_else:");
	   start = (Integer)n.f6.accept(this, (A)start);
	   h.print("if"+counter+"_end:");
	   return (R)start;
	}

	/**
	 * f0 -> "while"
	 * f1 -> "("
	 * f2 -> Expression()
	 * f3 -> ")"
	 * f4 -> Statement()
	 */
	public R visit(WhileStatement n, A argu) {
	   Integer start = (Integer)argu;
	   
	   Integer counter = whileCounter++;
	   h.print("while"+counter+"_start:");
	   start = (Integer)n.f2.accept(this, (A)start);
	   h.print("\tif0 t."+(start-1)+" goto :while"+counter+"_end");
	   start = (Integer)n.f4.accept(this, (A)start);
	   h.print("\tgoto :while"+counter+"_start");
	   h.print("while"+counter+"_end:");
	   return (R)start;
	}

	/**
	 * f0 -> "System.out.println"
	 * f1 -> "("
	 * f2 -> Expression()
	 * f3 -> ")"
	 * f4 -> ";"
	 */
	public R visit(PrintStatement n, A argu) {
	   Integer start = (Integer)argu;
	   start = (Integer) n.f2.accept(this, (A)start);
	   h.print("\tPrintIntS(t."+(start-1)+")");
	   return (R)start;
	}

	/**
	 * f0 -> AndExpression()
	 *       | CompareExpression()
	 *       | PlusExpression()
	 *       | MinusExpression()
	 *       | TimesExpression()
	 *       | ArrayLookup()
	 *       | ArrayLength()
	 *       | MessageSend()
	 *       | PrimaryExpression()
	 */
	public R visit(Expression n, A argu) {
		Integer start = (Integer)argu;
		start = (Integer)n.f0.accept(this, (A)start);
		return (R)start;
	}

	/**
	 * f0 -> PrimaryExpression()
	 * f1 -> "&&"
	 * f2 -> PrimaryExpression()
	 */
	public R visit(AndExpression n, A argu) {
	   Integer start = (Integer)argu;
	   Integer mid1 = (Integer) n.f0.accept(this, (A)start);
	   Integer mid2 = (Integer) n.f2.accept(this, (A)mid1);
	   h.print("\tt."+(mid1-1)+" = Eq(t."+(mid1-1)+" 0)");
	   h.print("\tt."+(mid2-1)+" = Eq(t."+(mid2-1)+" 0)");
	   h.print("\tt."+(mid1-1)+" = Eq(t."+(mid1-1)+" 0)");
	   h.print("\tt."+(mid2-1)+" = Eq(t."+(mid2-1)+" 0)");
	   h.print("\tt."+start+" = MulS(t."+(mid1-1)+" t."+(mid2-1)+")");
	   start ++;
	   return (R)start;
	}

	/**
	 * f0 -> PrimaryExpression()
	 * f1 -> "<"
	 * f2 -> PrimaryExpression()
	 */
	public R visit(CompareExpression n, A argu) {
	   Integer start = (Integer)argu;
	   Integer mid1 = (Integer)n.f0.accept(this, (A)start);
	   Integer mid2 = (Integer)n.f2.accept(this, (A)mid1);
	   h.print("\tt."+mid2+" = LtS(t."+(mid1-1)+" t."+(mid2-1)+")");
	   mid2 ++;
	   return (R)mid2;
	}

	/**
	 * f0 -> PrimaryExpression()
	 * f1 -> "+"
	 * f2 -> PrimaryExpression()
	 */
	public R visit(PlusExpression n, A argu) {
	   Integer start = (Integer)argu;
	   Integer mid1 = (Integer)n.f0.accept(this, (A)start);
	   Integer mid2 = (Integer)n.f2.accept(this, (A)mid1);
	   h.print("\tt."+mid2+" = Add(t."+(mid1-1)+" t."+(mid2-1)+")");
	   mid2++;
	   return (R)mid2;
	}

	/**
	 * f0 -> PrimaryExpression()
	 * f1 -> "-"
	 * f2 -> PrimaryExpression()
	 */
	public R visit(MinusExpression n, A argu) {
		Integer start = (Integer)argu;
		Integer mid1 = (Integer)n.f0.accept(this, (A)start);
		Integer mid2 = (Integer)n.f2.accept(this, (A)mid1);
		h.print("\tt." +mid2+" = Sub(t."+(mid1-1)+" t."+(mid2-1)+")");
		mid2 ++;
		return (R)mid2;
	}

	/**
	 * f0 -> PrimaryExpression()
	 * f1 -> "*"
	 * f2 -> PrimaryExpression()
	 */
	public R visit(TimesExpression n, A argu) {
		Integer start = (Integer)argu;
		Integer mid1 = (Integer)n.f0.accept(this, (A)start);
		Integer mid2 = (Integer)n.f2.accept(this, (A)mid1);
		h.print("\tt."+mid2+" = MulS(t."+(mid1-1)+" t."+(mid2-1)+")");
		mid2 ++;
		return (R)mid2;
	}

	/**
	 * f0 -> PrimaryExpression()
	 * f1 -> "["
	 * f2 -> PrimaryExpression()
	 * f3 -> "]"
	 */
	public R visit(ArrayLookup n, A argu) {
	   Integer start = (Integer)argu;
	   
	   Integer mid1 = (Integer)n.f0.accept(this, (A)start);
	   Integer mid2 = (Integer)n.f2.accept(this, (A)mid1);
	   
	   int count = nullCounter++;
	   h.print("\tt."+mid2+" = Eq(t."+(mid1-1)+" 0)");
	   h.print("\tif0 t."+mid2+" goto :null"+count);
	   h.print("\tError(\"null pointer\")");
	   h.print("null"+count+":");
	   
	   h.print("\tt."+mid2+" = Add(t."+(mid2-1)+" 1)");
	   h.print("\tt."+mid2+" = MulS(t."+mid2+" 4)");
	   h.print("\tt."+mid2+" = Add(t."+mid2+" t."+(mid1-1)+")");
	   mid2 ++;
	   h.print("\tt."+mid2+" = [t."+(mid1-1)+"]");
	   h.print("\tt."+mid2+" = Lt(t."+(mid2-2)+" t."+mid2+")");
	   int counter = boundCounter++;
	   h.print("\tif t."+mid2+" goto :bounds"+counter);
	   h.print("\tError(\"array index out of bounds\")");
	   h.print("bounds"+counter+":");
	   h.print("\tt."+mid2+" = [t."+(mid2-1)+"]");
	   mid2 ++;
	   return (R)mid2;
	}

	/**
	 * f0 -> PrimaryExpression()
	 * f1 -> "."
	 * f2 -> "length"
	 */
	public R visit(ArrayLength n, A argu) {
	   Integer start = (Integer)argu;
	   
	   start = (Integer)n.f0.accept(this, (A)start);
	   int count = nullCounter++;
	   h.print("\tt."+start+" = Eq(t."+(start-1)+" 0)");
	   h.print("\tif0 t."+start+" goto :null"+count);
	   h.print("\tError(\"null pointer\")");
	   h.print("null"+count+":");
	   h.print("\tt."+start+" = "+"[t."+(start-1)+"]");
	   start ++;
	   return (R)start;
	}

	/**
	 * f0 -> PrimaryExpression()
	 * f1 -> "."
	 * f2 -> Identifier()
	 * f3 -> "("
	 * f4 -> ( ExpressionList() )?
	 * f5 -> ")"
	 */
	public R visit(MessageSend n, A argu) {
		Integer start = (Integer)argu;
		String env = null;
		String med = h.getName(n.f2);
		start = (Integer)n.f0.accept(this, (A)start);
		paraTemp = new ArrayList<Integer>();
		Integer mid = (Integer)n.f4.accept(this, (A)start);
		//System.out.println(n.f0.f0.which);
		if (n.f0.f0.which == 3) {
			Identifier iNode = (Identifier)n.f0.f0.choice;
			//System.out.println(typetable.toString());
			env = typetable.get(h.getName(iNode));
		} else if (n.f0.f0.which == 4) {
			env = h.getName(thisId);
		} else if (n.f0.f0.which == 6) {
			AllocationExpression axNode = (AllocationExpression)n.f0.f0.choice;
			env = h.getName(axNode.f1);
		} else {
			env = typetable.get("t."+(start-1));
		}
		String output = null;
		Integer index = h.getIndex(vtable, env, med);
		//System.out.println(index);
		int count = nullCounter++;
		h.print("\tt."+mid+" = Eq(t."+(start-1)+" 0)");
		h.print("\tif0 t."+mid+" goto :null"+count);
		h.print("\tError(\"null pointer\")");
		h.print("null"+count+":");
		h.print("\tt."+mid+" = [t."+(start-1)+"]");
		h.print("\tt."+mid+" = [t."+mid+"+"+index*4+"]");
		mid ++;
		output = "\tt."+mid+" = call t."+(mid-1)+"(t."+(start-1);
		typetable.put("t."+mid, returnType.get(h.getName(n.f2)));
		
		for (int i = 0; i < paraTemp.size(); i ++) {
			output += " t."+paraTemp.get(i);
		}
		output += ")";
		h.print(output);
		mid ++;
	    return (R)mid;
	}

	/**
	 * f0 -> Expression()
	 * f1 -> ( ExpressionRest() )*
	 */
	public R visit(ExpressionList n, A argu) {
	   Integer start = (Integer)argu;
	   start = (Integer)n.f0.accept(this, (A)start);
	   paraTemp.add(start-1);
	   start = (Integer)n.f1.accept(this, (A)start);
	   return (R)start;
	}

	/**
	 * f0 -> ","
	 * f1 -> Expression()
	 */
	public R visit(ExpressionRest n, A argu) {
	   Integer start = (Integer)argu;
	   start = (Integer)n.f1.accept(this, (A)start);
	   paraTemp.add(start-1);
	   return (R)start;
	}

	/**
	 * f0 -> IntegerLiteral()
	 *       | TrueLiteral()
	 *       | FalseLiteral()
	 *       | Identifier()
	 *       | ThisExpression()
	 *       | ArrayAllocationExpression()
	 *       | AllocationExpression()
	 *       | NotExpression()
	 *       | BracketExpression()
	 */
	public R visit(PrimaryExpression n, A argu) {
	   Integer start = (Integer)argu;
	   start = (Integer)n.f0.accept(this, (A)start);
	   return (R)start;
	}

	/**
	 * f0 -> <INTEGER_LITERAL>
	 */
	public R visit(IntegerLiteral n, A argu) {
	   Integer start = (Integer)argu;
	   h.print("\tt."+start+" = "+n.f0.toString());
	   start ++;
	   return (R)start; 
	}

	/**
	 * f0 -> "true"
	 */
	public R visit(TrueLiteral n, A argu) {
		Integer start = (Integer)argu;
		h.print("\tt."+start+" = 1");
		start ++;
		return (R)start; 
	}

	/**
	 * f0 -> "false"
	 */
	public R visit(FalseLiteral n, A argu) {
		Integer start = (Integer)argu;
		h.print("\tt."+start+" = 0");
		start ++;
		return (R)start; 
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public R visit(Identifier n, A argu) {
		Integer start = (Integer)argu;
		if (medId == null || h.findPara(medtable, thisId, medId, n)) {
			if (!initable.contains(h.getName(n))) h.print("\t"+h.getName(n)+" = 0");
			h.print("\tt."+start+" = "+h.getName(n));
		}
		else {
			Integer index = h.getParaPos(vartable, h.getName(thisId), n);
			//System.out.print(h.getName(n));
			h.print("\tt."+start+" = [this+"+4*(index+1)+"]");
		}
		start ++;
		return (R)start; 
	}

	/**
	 * f0 -> "this"
	 */
	public R visit(ThisExpression n, A argu) {
		Integer start = (Integer)argu;
		h.print("\tt."+start+" = this");
		start ++;
		return (R)start;
	}

	/**
	 * f0 -> "new"
	 * f1 -> "int"
	 * f2 -> "["
	 * f3 -> Expression()
	 * f4 -> "]"
	 */
	public R visit(ArrayAllocationExpression n, A argu) {
	   Integer start = (Integer)argu;
	   start = (Integer)n.f3.accept(this, (A)start);
	   h.print("\tt."+start+" = Add(t."+(start-1)+" 1)");
	   h.print("\tt."+start+" = MulS(t."+start+" 4)");
	   start ++;
	   h.print("\tt."+start+" = HeapAllocZ(t."+(start-1)+")");
	   h.print("\t[t."+start+"] = t."+(start-2)); // length
	   start ++;
	   return (R)start;
	}

	/**
	 * f0 -> "new"
	 * f1 -> Identifier()
	 * f2 -> "("
	 * f3 -> ")"
	 */
	public R visit(AllocationExpression n, A argu) {
	   Integer start = (Integer)argu;
	   Integer classSize = h.getParaSize(vartable, n.f1);
	   h.print("\tt."+start+" = HeapAllocZ("+classSize*4+")");
	   h.print("\t[t."+start+"] = :vmt_"+h.getName(n.f1));
	   start ++;
	   return (R)(start);
	}

	/**
	 * f0 -> "!"
	 * f1 -> Expression()
	 */
	public R visit(NotExpression n, A argu) {
	   Integer start = (Integer)argu;
	   start = (Integer)n.f1.accept(this, (A)start);
	   h.print("\tt."+start+" = Eq(t."+(start-1)+" 0)");
	   start ++;
	   return (R)start;
	}

	/**
	 * f0 -> "("
	 * f1 -> Expression()
	 * f2 -> ")"
	 */
	public R visit(BracketExpression n, A argu) {
	   Integer start = (Integer)argu;
	   start = (Integer) n.f1.accept(this, (A)start);
	   return (R)start;
	}
}

