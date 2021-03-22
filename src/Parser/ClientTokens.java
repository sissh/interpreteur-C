package Parser;

public class ClientTokens {

	public static void main(String []args) {
		
		String a = "int a; ++a;";//ne peut pas faire "a=a-2", pk a passe à null ?
		Code code = new Code(a);
		code.makeTokens();
		
		SousFonction fonction = new SousFonction(code.getTokens());
		System.out.println(fonction.execLigneSuivante()+"\n");
		System.out.println(fonction.execLigneSuivante()+"\n");
		System.out.println(fonction.execLigneSuivante()+"\n");
		//Tester chaque fonction de calcul
	}
}