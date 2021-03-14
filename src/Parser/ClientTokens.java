package Parser;

public class ClientTokens {

	public static void main(String []args) {
		//Parser tok = new Parser("int a=5;int b=3 ;int c=a + 3+b+ 4 ;") ;
		Parser tiktok = new Parser("int a = 5+ pow(2,3);");
		//tok.makeTokens();
		tiktok.makeTokens();
		Class a = null;
		for (int i=0; i<tiktok.getTokens().size();i++) {
			a = tiktok.getTokens().get(i).getClass();
			System.out.println(tiktok.getTokens().get(i).getNom()+" "+a);
		}
	}
}
