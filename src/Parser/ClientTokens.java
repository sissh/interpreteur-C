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
		test.add("int a=15/pow(2,pow(2,1));");
		test.add("int b=a%5;");
		test.add("int c=a+b;");
		for (int i=0;i<test.size();i++) {
			Object erreur =code.execLigne(test.get(i));
			System.out.println(erreur);
			if (erreur instanceof String) break;
		}
		/*System.out.println("record  :"+code.getRecord());
		code.backLine();
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