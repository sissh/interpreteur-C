package Parser;

public class ClientTokens {

	public static void main(String []args) {
		Code code = new Code();
		String ligne = "int a=5+2;";//fonctionne pas
		code.execLigne(ligne);
		System.out.println(code.toString());
	}
}
