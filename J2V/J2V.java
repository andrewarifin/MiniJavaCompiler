import syntaxtree.Node;


public class J2V {
	public static void main(String [] args) {
	      try {
	         Node root = new MiniJavaParser(System.in).Goal();
	         root.accept(new MyVisitor<Integer,Integer>(), null);
	      }
	      catch (ParseException e) {
	         System.out.println(e.toString());
	      }
	   }
}