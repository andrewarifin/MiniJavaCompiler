import java.util.HashMap;

import syntaxtree.*;
import visitor.*;

public class Typecheck {
   public static void main(String [] args) {
      try {
         Node root = new MiniJavaParser(System.in).Goal();
         root.accept(new MyDepthFirst<Type,HashMap<String, Type>>(), null);
         System.out.println("Program type checked successfully");
      }
      catch (ParseException e) {
         System.out.println(e.toString());
      }
   }
}