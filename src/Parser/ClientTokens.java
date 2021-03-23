package Parser;

public class ClientTokens {

	public static void main(String []args) {
		
		String a = "int a=1; a=pow pow(2,pow(a,a)),a);";//attention, variable non acceptée, pas de vérification de type
											//5.5 variable, changer en constante
		Code code = new Code(a);
		code.makeTokens();
		SousFonction fonction = new SousFonction(code.getTokens());
		System.out.println(fonction.execLigneSuivante()+"\n");
		System.out.println(fonction.execLigneSuivante()+"\n");
		//System.out.println(fonction.execLigneSuivante()+"\n");
	}
}