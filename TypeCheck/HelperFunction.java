
import syntaxtree.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class HelperFunction {
	
	public String getName(Identifier n) {
		return n.f0.toString();
	}
	   
	// Helper function: Rule #10
	public Vector<Identifier> linkset(ClassExtendsDeclaration n) {
		Vector<Identifier> pair = new Vector<Identifier>();
		pair.add(n.f1);
		pair.add(n.f3);
		return pair;
	}
	   
	// Helper function: Rule #12
	public Boolean distinct(Vector<Identifier> v) {
//		System.out.println("checking distinct with size: "
//							+v.size());
		if (v.size() == 0  || v.size() == 1) return true;
		for (int i = 0; i < v.size(); i ++) {
//			System.out.println("checking distinct with string: "
//								+v.elementAt(i).getName());
			for (int j = 0; j < v.size(); j ++) {
				if (i != j && getName(v.elementAt(i)) 
								== getName(v.elementAt(j))) {
					return false;
				}
			}
		}
		return true;
	}
	
	// Helper function: Rule #13
	public Boolean acyclic(HashMap<String, String> orderTable) {

		Boolean ret = true;
		ArrayList<String> keyArray = new ArrayList<String>(orderTable.keySet());
		int n = orderTable.keySet().size();
		for ( int i = 0; i < n; i ++) {
			String start = keyArray.get(i);
			String end = orderTable.get(start);
			while (end != null && start != end) {
				end = orderTable.get(end);
			}
			if (end != null) return false;
	    }		
		return ret;
	}
	
	// Helper function: Rule #14
   public HashMap<String, Type> fields(ClassDeclaration n) {
	   HashMap<String, Type> ret = new HashMap<String, Type>();
	   //System.out.println("fields: size is "+f3.size());
	   for ( int i = 0; i < n.f3.size(); i ++) {
		   ret.put(getName(((VarDeclaration)n.f3.elementAt(i)).f1), 
				   ((VarDeclaration)n.f3.elementAt(i)).f0);
	   }
	   return ret;
   }
	   
// Helper function: Rule #15
   public HashMap<String, Type> fieldsEx(ClassExtendsDeclaration n,HashMap<String, Node> map, HashMap<String, String> orderTable) {
	   String supperClass = getName(n.f3);
	   HashMap<String, Type> sup = null;
	   if (!orderTable.containsKey(supperClass)) {
	   		sup = fields((ClassDeclaration)map.get(supperClass));
	   }
	   else {
	   		sup = fieldsEx((ClassExtendsDeclaration)map.get(supperClass), map, orderTable);
	   }
	   HashMap<String, Type> normal = new HashMap<String, Type>();
	   for ( int i = 0; i < n.f5.size(); i ++) {
		   normal.put(getName(((VarDeclaration)n.f5.elementAt(i)).f1), 
				   ((VarDeclaration)n.f5.elementAt(i)).f0);
	   }
	   HashMap<String, Type> ret = fieldOperation(sup, normal);
	   return ret;
   }
   
   // Helper function: Rule #16-17
   public Vector<Type> methodtype(ClassDeclaration n, Identifier id) {
	   Vector<Type> ret = null;
	   Boolean found = false;
	   int foundIndex = 0;
	   for (int i = 0; i < n.f4.size(); i ++) {
		   MethodDeclaration mdNode = (MethodDeclaration)(n.f4.elementAt(i));
		   if (getName(id) == getName(mdNode.f2)) {
			   found = true;
			   foundIndex = i;
		   }
	   }
	   if (!found) return ret;
	   MethodDeclaration mdNode = ((MethodDeclaration)n.f4.elementAt(foundIndex));
	   ret = new Vector<Type>();
	   if (mdNode.f4.node != null) {
		   FormalParameterList fplNode = (FormalParameterList)(mdNode.f4.node);
		   FormalParameter fpNode = fplNode.f0;
		   ret.add(fpNode.f0);
		   NodeListOptional nlNode = fplNode.f1;
		   for (int i = 0; i < nlNode.size(); i ++) {
			   FormalParameterRest fprNode = ((FormalParameterRest)(nlNode.elementAt(i)));
			   //System.out.println("methodtype: adding "+fprNode.f1.f1.getName());
			   ret.add(fprNode.f1.f0);
		   }
	   }
	   
	   //System.out.println("methodtype: adding "+mdTypes.f2.getName());
	   ret.add(mdNode.f1);
	   return ret;
   }
   
// Helper function: Rule #18-19
   public Vector<Type> methodtypeEx(ClassExtendsDeclaration n, Identifier id, HashMap<String, Node> map, HashMap<String, String> orderTable) {
	   Vector<Type> ret = null;
	   Boolean found = false;
	   int foundIndex = 0;
	   for (int i = 0; i < n.f6.size(); i ++) {
		   if (getName(id) == getName(((MethodDeclaration)n.f6.elementAt(i)).f2)) {
			   found = true;
			   foundIndex = i;
		   }
	   }
	   if (!found) {
	   		if (!orderTable.containsKey(getName(n.f3))) {
	   			ret = methodtype((ClassDeclaration)map.get(getName(n.f3)), id);
	   		}
		    else {
		    	ret = methodtypeEx((ClassExtendsDeclaration)map.get(getName(n.f3)), 
		    		id, map, orderTable);
		    } 
		   return ret;
	   }
	   MethodDeclaration mdNode = ((MethodDeclaration)n.f6.elementAt(foundIndex));
	   ret = new Vector<Type>();
	   if (mdNode.f4.node != null) {
		   FormalParameterList fplNode = (FormalParameterList)(mdNode.f4.node);
		   FormalParameter fpNode = fplNode.f0;
		   //System.out.println("methodtype: adding "+ fpNode.f1.getName());
		   ret.add(fpNode.f0);
		   NodeListOptional nlNode = fplNode.f1;
		   for (int i = 0; i < nlNode.size(); i ++) {
			   FormalParameterRest fprNode = ((FormalParameterRest)(nlNode.elementAt(i)));
			   //System.out.println("methodtype: adding "+fprNode.f1.f1.getName());
			   ret.add(fprNode.f1.f0);
		   }
	   }

	   ret.add(mdNode.f1);
	   return ret;
   }
   
	// Helper function: Rule #20
	public Boolean noOverloading(Identifier id, Identifier idp, MethodDeclaration idmNode,
			HashMap<String, Node> map, HashMap<String, String> orderTable) {
		//System.out.println("noOverloading: "+getName(id)+" "+getName(idp)+" "+getName(idmNode.f2));
		ClassDeclaration idNode = null;
		ClassExtendsDeclaration idExNode = null;
		Vector<Type> vd = null;
		if (!orderTable.containsKey(getName(idp))) {
			idNode = 
				(ClassDeclaration)(map.get(getName(idp)));
			vd = methodtype(idNode,idmNode.f2);
		}
		else {
			idExNode = 
				(ClassExtendsDeclaration)(map.get(getName(idp)));
			vd = methodtypeEx(idExNode,idmNode.f2, map, orderTable);
		}
		ClassExtendsDeclaration idpNode = 
				(ClassExtendsDeclaration)(map.get(getName(id)));
		Vector<Type> v = methodtypeEx(idpNode, idmNode.f2, map, orderTable);

		//System.out.println("noOverloading: size "+v.size()+" "+vd.size());
		if (v != null && v.size()==vd.size()) {
			Boolean same = true;
			for (int i = 0; i < v.size(); i ++) {
				//System.out.println("noOverloading: elementAt: "+v.elementAt(i).f0.which+" "+vd.elementAt(i).f0.which);
				if (v.elementAt(i).f0.which !=vd.elementAt(i).f0.which) {
					same = false;
					break;
				} else {
					if (v.elementAt(i).f0.which == 3 && vd.elementAt(i).f0.which == 3) {
						Identifier id1 = (Identifier)v.elementAt(i).f0.choice;
						Identifier id2 = (Identifier)vd.elementAt(i).f0.choice;
						if (getName(id1) != getName(id2)) {
							same = false;
							break;
						}
					}
					
				}
			}
			if (same) return true;
		}
		return false;
	}
	
	public HashMap<String, Type> fieldOperation(HashMap<String, Type> hm1, HashMap<String, Type>hm2) {
		HashMap<String, Type> ret = hm2;
		ArrayList<String> supCol = new ArrayList<String>(hm1.keySet());
		int n = hm1.keySet().size();
		for ( int i = 0; i < n; i ++) {
			if (!ret.containsKey(supCol.get(i))) {
			    ret.put(supCol.get(i), hm1.get(supCol.get(i)));
		    }
	    }
		return ret;
	}
	
	public Boolean isOrder(Type a, Type b, HashMap<String, String> orderTable) {
		if (a == null || b == null) return false;
		if (a.f0.which != 3 || b.f0.which != 3) {
			return a.f0.which == b.f0.which;
		} else {
			Identifier taNode = (Identifier)(a.f0.choice);
			Identifier tbNode = (Identifier)(b.f0.choice);
			//System.out.println("isOrder: typeA :"+getName(taNode)+" "+getName(tbNode));
			if (getName(taNode) == getName(tbNode)) return true;
			String tra = orderTable.get(getName(taNode));
			//if (!orderTable.containsKey(getName(tbNode))) printError(-1);
			while (tra != null && tra != getName(tbNode)) {
				tra = orderTable.get(tra);
			}
			if (tra == null) return false;
			else return true;
		}
	}
	public void printError(int r) {
		System.out.println("Type error");
		//System.out.println("Type error Rule #"+r);
  	  	System.exit(1);
	}
}

