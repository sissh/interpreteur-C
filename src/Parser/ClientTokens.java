package Parser;

public class ClientTokens {

	public static void main(String []args) {
		
		String a = "int a=pow(2,2)+pow(2,2);";//5.5 variable, changer en constante
		Code code = new Code(a);
		code.makeTokens();
		SousFonction fonction = new SousFonction(code.getTokens());
		System.out.println(fonction.execLigneSuivante()+"\n");
		System.out.println(fonction.execLigneSuivante()+"\n");
		System.out.println(fonction.execLigneSuivante()+"\n");
		//Tester chaque fonction de calcul
	}
}