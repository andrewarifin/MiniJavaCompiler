
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import syntaxtree.AllocationExpression;
import syntaxtree.AndExpression;
import syntaxtree.ArrayAllocationExpression;
import syntaxtree.ArrayAssignmentStatement;
import syntaxtree.ArrayLength;
import syntaxtree.ArrayLookup;
import syntaxtree.ArrayType;
import syntaxtree.AssignmentStatement;
import syntaxtree.Block;
import syntaxtree.BooleanType;
import syntaxtree.BracketExpression;
import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.CompareExpression;
import syntaxtree.Expression;
import syntaxtree.ExpressionList;
import syntaxtree.ExpressionRest;
import syntaxtree.FalseLiteral;
import syntaxtree.FormalParameter;
import syntaxtree.FormalParameterList;
import syntaxtree.FormalParameterRest;
import syntaxtree.Goal;
import syntaxtree.Identifier;
import syntaxtree.IfStatement;
import syntaxtree.IntegerLiteral;
import syntaxtree.IntegerType;
import syntaxtree.MainClass;
import syntaxtree.MessageSend;
import syntaxtree.MethodDeclaration;
import syntaxtree.MinusExpression;
import syntaxtree.Node;
import syntaxtree.NodeChoice;
import syntaxtree.NodeList;
import syntaxtree.NodeListOptional;
import syntaxtree.NodeOptional;
import syntaxtree.NodeSequence;
import syntaxtree.NodeToken;
import syntaxtree.NotExpression;
import syntaxtree.PlusExpression;
import syntaxtree.PrimaryExpression;
import syntaxtree.PrintStatement;
import syntaxtree.Statement;
import syntaxtree.ThisExpression;
import syntaxtree.TimesExpression;
import syntaxtree.TrueLiteral;
import syntaxtree.Type;
import syntaxtree.TypeDeclaration;
import syntaxtree.VarDeclaration;
import syntaxtree.WhileStatement;
import visitor.GJDepthFirst;

public class MyDepthFirst<R,A> extends GJDepthFirst<R,A> {
	   //
	   // Auto class visitors--probably don't need to be overridden.
	   //
	   public R visit(NodeList n, A argu) {
	      R _ret=null;
	      int _count=0;
	      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
	         e.nextElement().accept(this,argu);
	         _count++;
	      }
	      return _ret;
	   }

	   public R visit(NodeListOptional n, A argu) {
	      if ( n.present() ) {
	         R _ret=null;
	         int _count=0;
	         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
	            e.nextElement().accept(this,argu);
	            _count++;
	         }
	         return _ret;
	      }
	      else
	         return null;
	   }

	   public R visit(NodeOptional n, A argu) {
	      if ( n.present() )
	         return n.node.accept(this,argu);
	      else
	         return null;
	   }

	   public R visit(NodeSequence n, A argu) {
	      R _ret=null;
	      int _count=0;
	      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
	         e.nextElement().accept(this,argu);
	         _count++;
	      }
	      return _ret;
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
	      
	      // Initial both orderTable and symbolTable
	      // Type Check Rule #21
	      Vector<Identifier> v = new Vector<Identifier>();
	      v.add(n.f0.f1);
	      for (int i = 0; i < n.f1.size(); i ++) {
	    	  TypeDeclaration typeNode = (TypeDeclaration)(n.f1.elementAt(i));
	    	  if (typeNode.f0.which == 0) {
	    		  ClassDeclaration classNode = (ClassDeclaration)(typeNode.f0.choice);
	    		  v.add(classNode.f1);
	    		  //System.out.println("Puting "+classNode.f1.getName()+" into symTable");
	    		  symTable.put(helper.getName(classNode.f1), classNode);
	    	  } else if (typeNode.f0.which == 1) {
	    		  ClassExtendsDeclaration classExNode = 
	    				  (ClassExtendsDeclaration)(typeNode.f0.choice);
	    		  v.add(classExNode.f1);
	    		  //System.out.println("Puting "+classExNode.f1.getName()+" into symTable Ex");
	    		  symTable.put(helper.getName(classExNode.f1), classExNode);
	    		  orderTable.put(helper.getName(classExNode.f1), helper.getName(classExNode.f3));
	    	  }
	      }
	      if (!helper.distinct(v)) {
	    	  helper.printError(21);
	      }
	      /*
	      Vector<Identifier> vp = new Vector<Identifier>();
	      for (int i = 0; i < n.f1.size(); i ++) {
	    	  TypeDeclaration typeNode = (TypeDeclaration)(n.f1.elementAt(i));
	    	  if (typeNode.f0.which == 1) {
	    		  ClassExtendsDeclaration classExNode = 
	    				  (ClassExtendsDeclaration)(typeNode.f0.choice);
	    		  vp.add(helper.linkset(classExNode).elementAt(0));
	    		  vp.add(helper.linkset(classExNode).elementAt(1));
	    		  
	    	  }
	      } */
	      if (!helper.acyclic(orderTable)) {
	    	  helper.printError(21);
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
	      
	      // Initialize curEnv
	      curEnv = n.f1;
	      // Type Check Rule #22
		  Vector<Identifier> v = new Vector<Identifier>();
		  HashMap<String, Type> env = new HashMap<String, Type>();
		  for (int i = 0; i < n.f14.size(); i ++) {
			  VarDeclaration varNode = (VarDeclaration)(n.f14.elementAt(i));
			  v.add(varNode.f1);
			  env.put(helper.getName(varNode.f1), varNode.f0);
		  }
		  if (!helper.distinct(v)) {
			  helper.printError(22);
		  }

		  n.f14.accept(this, (A)env);
		  n.f15.accept(this, (A)env);
		  
		  // Update curEnv
		  curEnv = null;
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
	      
	      // Initialize curEnv
	      curEnv = n.f1;
	      
	      // Type Check Rule #23
	   	  Vector<Identifier> v = new Vector<Identifier>();
	      Vector<Identifier> vm = new Vector<Identifier>();
	      for (int i = 0; i < n.f3.size(); i ++) {
	    	  VarDeclaration varNode = (VarDeclaration)(n.f3.elementAt(i));
	    	  v.add(varNode.f1);
	      }
	      for (int i = 0; i < n.f4.size(); i ++) {
	    	  MethodDeclaration medNode = (MethodDeclaration)(n.f4.elementAt(i));
	    	  vm.add(medNode.f2);
	      }
	      
	      if (!helper.distinct(v) || !helper.distinct(vm)) {
	    	  helper.printError(23);
	      }
	      
	      n.f3.accept(this, argu);
	      //System.out.println("ClassDeclaration: n.fields: "+n.fields().size());
	      n.f4.accept(this, (A)helper.fields(n));
	      
	      // Update curEnv
	      curEnv = null;
	      
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
	      
	      //System.out.println("ClassExtendsDeclaration: id: "+n.f1.getName());
	      // Initialize curEnv
	      curEnv = n.f1;
	      
	      // Type Check Rule #24
	      Vector<Identifier> v = new Vector<Identifier>();
	      Vector<Identifier> vm = new Vector<Identifier>();
	      for (int i = 0; i < n.f5.size(); i ++) {
	    	  VarDeclaration varNode = (VarDeclaration)(n.f5.elementAt(i));
	    	  v.add(varNode.f1);
	      }
	      for (int i = 0; i < n.f6.size(); i ++) {
	    	  MethodDeclaration medNode = (MethodDeclaration)(n.f6.elementAt(i));
	    	  vm.add(medNode.f2);
	    	  if (!helper.noOverloading(n.f1, n.f3, medNode, symTable, orderTable)) {
	    		  helper.printError(24);
	    	  }
	      }
	      
	      if (!helper.distinct(v) || !helper.distinct(vm)) {
	    	  helper.printError(24);
	      }

	      n.f5.accept(this, argu);
	      //System.out.println("ClassExtendsDeclaration: n.fields: "+n.fields(symTable).size());
	      n.f6.accept(this, (A)helper.fieldsEx(n, symTable, orderTable));
	      
	      // Update curEnv
	      curEnv = null;
	      
	      return _ret;
	   }

	   /**
	    * f0 -> Type()
	    * f1 -> Identifier()
	    * f2 -> ";"
	    */
	   public R visit(VarDeclaration n, A argu) {
	      R _ret=null;
	      
	      // We cannot use unknown type
	      if (n.f0.f0.which == 3) {
	          Identifier id = (Identifier)(n.f0.f0.choice);
	          if (!symTable.containsKey(helper.getName(id))) {
	              helper.printError(-1);
	          }
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
		  R _ret = null;
		  
		  // Type Check Rule #25
	      HashMap<String, Type> env = null;
	      
	      HashMap<String, Type> classField = (HashMap<String, Type>)argu;
	      HashMap<String, Type> localField = new HashMap<String, Type>();
	      Vector<Identifier> v = new Vector<Identifier>();
	      if (n.f4.node != null) {
	    	  FormalParameterList fplNode = (FormalParameterList)(n.f4.node);
	    	  FormalParameter fpNode = fplNode.f0;
	    	  v.add(fpNode.f1);
	    	  localField.put(helper.getName(fpNode.f1), fpNode.f0);
	    	  for (int i = 0; i < fplNode.f1.size(); i ++) {
	    		  FormalParameterRest fprNode = (FormalParameterRest)(fplNode.f1.elementAt(i));
	    		  v.add(fprNode.f1.f1);
	    		  localField.put(helper.getName(fprNode.f1.f1), fprNode.f1.f0);
	    	  }
	      }
	      for (int i = 0; i < n.f7.size(); i ++) {
	    	  VarDeclaration vNode = (VarDeclaration)(n.f7.elementAt(i));
	    	  localField.put(helper.getName(vNode.f1), vNode.f0);
	      }
	      //System.out.println("The size of classField & localField is "+classField.size()+" "+localField.size());
	      env = helper.fieldOperation(classField, localField);
	      if (!helper.distinct(v)) helper.printError(25);

	      n.f7.accept(this, (A)env);
	      //System.out.println("The size of env is "+env.size());
	      n.f8.accept(this, (A)env);
	      Type retNode = (Type)(n.f10.accept(this, (A)env));
	      if (n.f1.f0.which != retNode.f0.which) helper.printError(25);

	      return _ret;
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
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> ","
	    * f1 -> FormalParameter()
	    */
	   public R visit(FormalParameterRest n, A argu) {
	      R _ret=null;
	      n.f0.accept(this, argu);
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
	      n.f0.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> "int"
	    * f1 -> "["
	    * f2 -> "]"
	    */
	   public R visit(ArrayType n, A argu) {
	      R _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      n.f2.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> "boolean"
	    */
	   public R visit(BooleanType n, A argu) {
	      R _ret=null;
	      n.f0.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> "int"
	    */
	   public R visit(IntegerType n, A argu) {
	      R _ret=null;
	      n.f0.accept(this, argu);
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
	      R _ret=null;
	      n.f0.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> "{"
	    * f1 -> ( Statement() )*
	    * f2 -> "}"
	    */
	   public R visit(Block n, A argu) {
	      R _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      n.f2.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> Identifier()
	    * f1 -> "="
	    * f2 -> Expression()
	    * f3 -> ";"
	    */
	   public R visit(AssignmentStatement n, A argu) {
	      R _ret=null;
	      
	      // Type Check Rule #27
	      HashMap<String, Type> env = (HashMap<String, Type>)(argu);
	      Type t1 = env.get(helper.getName(n.f0));
	      Type t2 = (Type)(n.f2.accept(this, (A)env));
	      //System.out.println("AssignmentStatement: id: "+helper.getName(n.f0));
	      //System.out.println("AssignmentStatement: t1: "+helper.getName((Identifier)(t1.f0.choice)));
	      //System.out.println("AssignmentStatement: t2: "+helper.getName((Identifier)(t2.f0.choice)));
	      if (!helper.isOrder(t2, t1, orderTable)) helper.printError(27);
	      
	      return _ret;
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
	      R _ret=null;
	      
	      // Type Check Rule #28
	      HashMap<String, Type> env = (HashMap<String, Type>)(argu);
	      Type t1 = env.get(helper.getName(n.f0));
	      if (t1.f0.which != 0) helper.printError(28);
	      Type ret1 = (Type)(n.f2.accept(this, (A)env));
	      Type ret2 = (Type)(n.f5.accept(this, (A)env));
	      if (ret1.f0.which != 2 || ret2.f0.which != 2) helper.printError(28);
	      
	      return _ret;
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
	      R _ret=null;
	      
	      // Type Check Rule #29
	      Type ret = (Type)(n.f2.accept(this, argu));
	      if (ret.f0.which != 1) helper.printError(29);
	      n.f4.accept(this, argu);
	      n.f6.accept(this, argu);
	      
	      return _ret;
	   }

	   /**
	    * f0 -> "while"
	    * f1 -> "("
	    * f2 -> Expression()
	    * f3 -> ")"
	    * f4 -> Statement()
	    */
	   public R visit(WhileStatement n, A argu) {
	      R _ret=null;
	      
	      // Type Check Rule #30
	      Type ret = (Type)(n.f2.accept(this, argu));
	      if (ret.f0.which != 1) helper.printError(30);
	      n.f4.accept(this, argu);
	      
	      return _ret;
	   }

	   /**
	    * f0 -> "System.out.println"
	    * f1 -> "("
	    * f2 -> Expression()
	    * f3 -> ")"
	    * f4 -> ";"
	    */
	   public R visit(PrintStatement n, A argu) {
	      R _ret=null;

	      // Type Check Rule #31
	      Type ret = (Type)n.f2.accept(this, argu);
	      if (ret.f0.which != 2) helper.printError(31);

	      return _ret;
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
	      Type ret = (Type)n.f0.accept(this, argu);
//	    System.out.println("Expression: "+n.f0.which);
	      return (R)ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "&&"
	    * f2 -> PrimaryExpression()
	    */
	   public R visit(AndExpression n, A argu) {
		  NodeChoice cNode = new NodeChoice(null, 1);
		  Type ret = new Type(cNode);
		  
		  // Type Check Rule #32
	      Type ret1 = (Type)(n.f0.accept(this, argu));
	      Type ret2 = (Type)(n.f2.accept(this, argu));
	      if (ret1.f0.which != 1 || ret2.f0.which != 1) helper.printError(32);
	      
	      return (R)ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "<"
	    * f2 -> PrimaryExpression()
	    */
	   public R visit(CompareExpression n, A argu) { 
		   NodeChoice cNode = new NodeChoice(null, 1);
		   Type ret = new Type(cNode);
			  
		   // Type Check Rule #33
		   Type ret1 = (Type)(n.f0.accept(this, argu));
		   Type ret2 = (Type)(n.f2.accept(this, argu));
		   if (ret1.f0.which != 2 || ret2.f0.which != 2) helper.printError(33);
		      
		   return (R)ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "+"
	    * f2 -> PrimaryExpression()
	    */
	   public R visit(PlusExpression n, A argu) {
		   NodeChoice cNode = new NodeChoice(null, 2);
		   Type ret = new Type(cNode);
			  
		   // Type Check Rule #34
		   Type ret1 = (Type)(n.f0.accept(this, argu));
		   Type ret2 = (Type)(n.f2.accept(this, argu));
		   if (ret1.f0.which != 2 || ret2.f0.which != 2) helper.printError(34);
		      
		   return (R)ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "-"
	    * f2 -> PrimaryExpression()
	    */
	   public R visit(MinusExpression n, A argu) {
		   NodeChoice cNode = new NodeChoice(null, 2);
		   Type ret = new Type(cNode);
			  
		   // Type Check Rule #35
		   Type ret1 = (Type)(n.f0.accept(this, argu));
		   Type ret2 = (Type)(n.f2.accept(this, argu));
		   if (ret1.f0.which != 2 || ret2.f0.which != 2) helper.printError(35);
		      
		   return (R)ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "*"
	    * f2 -> PrimaryExpression()
	    */
	   public R visit(TimesExpression n, A argu) {
		   NodeChoice cNode = new NodeChoice(null, 2);
		   Type ret = new Type(cNode);
			  
		   // Type Check Rule #36
		   Type ret1 = (Type)(n.f0.accept(this, argu));
		   Type ret2 = (Type)(n.f2.accept(this, argu));
		   if (ret1.f0.which != 2 || ret2.f0.which != 2) helper.printError(36);
		      
		   return (R)ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "["
	    * f2 -> PrimaryExpression()
	    * f3 -> "]"
	    */
	   public R visit(ArrayLookup n, A argu) {
		   NodeChoice cNode = new NodeChoice(null, 2);
		   Type ret = new Type(cNode);
			  
		   // Type Check Rule #37
		   Type ret1 = (Type)(n.f0.accept(this, argu));
		   Type ret2 = (Type)(n.f2.accept(this, argu));
		   if (ret1.f0.which != 0 || ret2.f0.which != 2) helper.printError(37);
		      
		   return (R)ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "."
	    * f2 -> "length"
	    */
	   public R visit(ArrayLength n, A argu) {
		   NodeChoice cNode = new NodeChoice(null, 2);
		   Type ret = new Type(cNode);
			  
		   // Type Check Rule #38
		   Type ret1 = (Type)(n.f0.accept(this, argu));
		   if (ret1.f0.which != 0) helper.printError(38);
		   
		   return (R)ret;
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
			  
		   // Type Check Rule #39
		   Type retp = (Type)(n.f0.accept(this, argu));
		   Vector<Type> medType = null;
		   if (retp.f0.which != 3) helper.printError(39);
		   Identifier retpId = (Identifier)(retp.f0.choice);
		   if (orderTable.containsKey(helper.getName(retpId))) {
			   ClassExtendsDeclaration classExNode = (ClassExtendsDeclaration)(symTable.get(helper.getName(retpId)));
			   medType = helper.methodtypeEx(classExNode, n.f2, symTable, orderTable);
		   } else {
			   ClassDeclaration classNode = (ClassDeclaration)(symTable.get(helper.getName(retpId)));
			   medType = helper.methodtype(classNode, n.f2);
		   }
		   // This is needed because a there may be not such a method in class
		   if (medType == null) helper.printError(39);
		   if (n.f4.node != null) {
			   int count = 0;
			   ExpressionList elNode = (ExpressionList)n.f4.node;
			   Expression eNode = elNode.f0;
			   Type e1Ret = (Type)eNode.accept(this, argu);
			   if (!helper.isOrder(e1Ret, medType.elementAt(0), orderTable)) helper.printError(39);
			   count ++;
			   for (int i = 0; i < elNode.f1.size(); i ++) {
				   if (count == medType.size()-1) helper.printError(39);
				   ExpressionRest eiNode = (ExpressionRest)elNode.f1.elementAt(i);
				   Type eiRet = (Type)eiNode.accept(this, argu);
				   if (!helper.isOrder(eiRet, medType.elementAt(i+1), orderTable)) helper.printError(39);
				   count ++;
			   }
			   if (count != medType.size()-1) helper.printError(39);
		   }
//		   System.out.println("MesseagSend: id: "+n.f2.getName());
//		   System.out.println("MesseagSend: medType: "+medType.size());
//		   for (Type it:medType) {
//			   System.out.println(it.f0.which);
//		   }
		   return (R)(medType.lastElement());
	   }

	   /**
	    * f0 -> Expression()
	    * f1 -> ( ExpressionRest() )*
	    */
	   public R visit(ExpressionList n, A argu) {
	      R _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> ","
	    * f1 -> Expression()
	    */
	   public R visit(ExpressionRest n, A argu) {
	      Type ret = (Type)(n.f1.accept(this, argu));
	      return (R)ret;
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
	      Type ret = (Type)n.f0.accept(this, argu);
	      return (R)ret;
	   }

	   /**
	    * f0 -> <INTEGER_LITERAL>
	    */
	   public R visit(IntegerLiteral n, A argu) {
		   // Type Check Rule #40
		   NodeChoice cNode = new NodeChoice(null, 2);
		   Type ret = new Type(cNode);
		   return (R)ret;
	   }

	   /**
	    * f0 -> "true"
	    */
	   public R visit(TrueLiteral n, A argu) {
		   // Type Check Rule #41
		   NodeChoice cNode = new NodeChoice(null, 1);
		   Type ret = new Type(cNode);
		   return (R)ret;
	   }

	   /**
	    * f0 -> "false"
	    */
	   public R visit(FalseLiteral n, A argu) {
		   // Type Check Rule #42
		   NodeChoice cNode = new NodeChoice(null, 1);
		   Type ret = new Type(cNode);
		   return (R)ret;
	   }

	   /**
	    * f0 -> <IDENTIFIER>
	    */
	   public R visit(Identifier n, A argu) {
		   // System.out.println("The id I check is "+n.getName());
		   // Type Check Rule #43
		   HashMap<String, Type> env = (HashMap<String, Type>)(argu);
		   if (!env.containsKey(helper.getName(n))) helper.printError(43);
		   Type type = env.get(helper.getName(n));
		   return (R)type;
	   }

	   /**
	    * f0 -> "this"
	    */
	   public R visit(ThisExpression n, A argu) {
		  NodeChoice cNode = new NodeChoice(curEnv, 3);
	      Type ret = new Type(cNode);
	      return (R)ret;
	   }

	   /**
	    * f0 -> "new"
	    * f1 -> "int"
	    * f2 -> "["
	    * f3 -> Expression()
	    * f4 -> "]"
	    */
	   public R visit(ArrayAllocationExpression n, A argu) {
		   NodeChoice cNode = new NodeChoice(null, 0);
		   Type ret = new Type(cNode);
			  
		   // Type Check Rule #45
		   Type ret1 = (Type)(n.f3.accept(this, argu));
		   if (ret1.f0.which != 2) helper.printError(45);
		   return (R)ret;
	   }

	   /**
	    * f0 -> "new"
	    * f1 -> Identifier()
	    * f2 -> "("
	    * f3 -> ")"
	    */
	   public R visit(AllocationExpression n, A argu) {
		   // Type Check Rule #46
		   //System.out.println("The id I check is "+n.f1.getName());
		   if (!symTable.containsKey(helper.getName(n.f1))) helper.printError(46);

		   Identifier id = new Identifier(new NodeToken(helper.getName(n.f1)));
		   NodeChoice cNode = new NodeChoice(id, 3);
		   Type ret = new Type(cNode);
		   return (R)ret;
	   }

	   /**
	    * f0 -> "!"
	    * f1 -> Expression()
	    */
	   public R visit(NotExpression n, A argu) {
		   NodeChoice cNode = new NodeChoice(null, 1);
		   Type ret = new Type(cNode);
			  
		   // Type Check Rule #47
		   Type ret1 = (Type)(n.f1.accept(this, argu));
		   if (ret1.f0.which != 1) helper.printError(47);
		   return (R)ret;
	   }

	   /**
	    * f0 -> "("
	    * f1 -> Expression()
	    * f2 -> ")"
	    */
	   public R visit(BracketExpression n, A argu) {
		  // Type Check Rule #48
	      Type ret = (Type)(n.f1.accept(this, argu));
	      return (R)ret;
	   }

	   HelperFunction helper = new HelperFunction();
	   HashMap<String, Node> symTable = new HashMap<String, Node>();
	   HashMap<String, String> orderTable = new HashMap<String, String>();
	   Identifier curEnv = null;
	   
}
