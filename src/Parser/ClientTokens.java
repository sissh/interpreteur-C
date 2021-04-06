package Parser;

import java.util.ArrayList;

/**
 * Classe de tests de l'algorithme, pour pouvoir cibler les éléments à tester
 * @author alexi
 *
 */
public class ClientTokens {
	/**
	 * Main de tests
	 * @param args Arguments du main, non utilisé 
	 */
	public static void main(String []args) {
		Code code = new Code();
		ArrayList<String> test = new ArrayList<String>();
		test.add("int a =5;");
		test.add("int* b =&a;");
		test.add("pow((,2);");
		/*test.add("int * b=&a;");
		test.add("int * c=&a;");
		test.add("int *d=c;");
		test.add("int *e;");
		test.add("e=c;");
		test.add("int g=pow(2,2);");*/
		//test.add("int d=pow(a, * b);");
		
		//test.add("int*c=a;");
		for (int i=0;i<test.size();i++) {
			Object erreur =code.execLigne(test.get(i));
			System.out.println(erreur);
			if (erreur instanceof String) break;
		}
		//System.out.println("record  :"+code.getRecord().get(code.getRecord().size()-1).get(test));
		/*code.backLine();
		System.out.println("record  :"+code.getRecord());
		System.out.println(code.execLigne("a=2;"));*/
		/*System.out.println("record  :"+code.getRecord());
		code.reset();
		for (int i=0;i<test.size();i++) {
			System.out.println(code.execLigne(test.get(i)));
		}
		System.out.println("record  :"+code.getRecord());*/
	}
		
}