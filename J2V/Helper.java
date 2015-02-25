import java.util.ArrayList;
import java.util.HashMap;

import syntaxtree.Identifier;


public class Helper {
	public void print(String s) {
		System.out.println(s);
	}
	
	public String getName(Identifier id) {
		return id.f0.toString();
	}
	
	public Integer getIndex(HashMap<String, ArrayList<String>> vtable, String env, String med) {
		ArrayList<String> stable = vtable.get(env);
		for (int i = 0; i < stable.size(); i ++) {
			String ele = stable.get(i);
			int index = ele.indexOf('.');
			ele = ele.substring(index+1);
			if (ele.equals(med)) return new Integer(i);
		}
		return null;
	}
	
	public Integer getParaSize(HashMap<String, ArrayList<String>> vartable, Identifier classId) {
		Integer result = new Integer(1);
		ArrayList<String> array = vartable.get(getName(classId));
		//System.out.println(array.size());
		result += array.size();
		return result;
	}
	
	public Integer getParaPos(HashMap<String, ArrayList<String>> vartable, String classString, Identifier vId) {
		ArrayList<String> array = vartable.get(classString);
		//System.out.println(classString+" "+array.toString());
		for (int i = 0; i < array.size(); i ++) {
			if (array.get(i).equals(getName(vId))) return i;
		}
		return null;
	}
	
	public Boolean findPara(HashMap<String, ArrayList<String>> medtable, Identifier classId, Identifier medId, Identifier vId) {
		String query = getName(classId)+"."+getName(medId);
		//System.out.println(query);
		ArrayList<String> array = medtable.get(query);
		//System.out.println("findPara"+array.toString());
		if (array.contains(getName(vId))) return true;
		return false;
	}
}
