package Parser;

public class ClientTokens {

	public static void main(String []args) {
		
		String a = "int a = pow(2,2);";
		Code code = new Code(a);
		code.makeTokens();
		/*Class cla = null;
		for (int i=0; i<code.getTokens().size(); i++) {
			cla=code.getTokens().get(i).getClass();
			System.out.println(code.getTokens().get(i).toString()+" "+cla);
		}*/
		SousFonction fonction = new SousFonction(code.getTokens());
		fonction.execLigneSuivante();
		//Tester chaque fonction de calcul
	}
}