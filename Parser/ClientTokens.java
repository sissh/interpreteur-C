package Parser;
import java.util.ArrayList;

import Tokens.Token;

public class ClientTokens {

	public static void main(String []args) {
		Parser tok = new Parser("int a=5;int b=3 ;int c=a + 3+b+ 4 ;") ;
		tok.makeTokens();
		ArrayList<Token> listeToken=tok.getTokens();
		Class a = null;
		for (int i=0; i<tok.getTokens().size();i++) {
			a = tok.getTokens().get(i).getClass();
			System.out.println(tok.getTokens().get(i).getNom()+" "+a);
		}
	}
}
