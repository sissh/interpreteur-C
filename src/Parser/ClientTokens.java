package Parser;

public class ClientTokens {

	public static void main(String []args) {
		
		String a = "int a=2; int b=3; a+=b;";//5.5 variable, changer en constante
		Code code = new Code(a);
		code.makeTokens();
		SousFonction fonction = new SousFonction(code.getTokens());
		int i=0;
		boolean cont=true;
		while (cont && i++!=3) {
			Object b =fonction.execLigneSuivante();
			System.out.println(b.toString()+"\n");
			if (b instanceof String) cont=false;
		}
	}
		
}