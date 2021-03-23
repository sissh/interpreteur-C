package Parser;

public class ClientTokens {

	public static void main(String []args) {
		
		String a = "int a= pow(pow(2,2),pow(a,1));";//attention, variable non acceptée, pas de vérification de type
											//5.5 variable, changer en constante
		Code code = new Code(a);
		code.makeTokens();
		SousFonction fonction = new SousFonction(code.getTokens());
		System.out.println(fonction.execLigneSuivante()+"\n");
		//System.out.println(fonction.execLigneSuivante()+"\n");
		//System.out.println(fonction.execLigneSuivante()+"\n");
		//Tester chaque fonction de calcul
	}
}