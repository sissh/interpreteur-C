package Parser;

import java.util.ArrayList;

import Tokens.Token;

public class ClientTokens {

	public static void main(String []args) {
		
		Code code = new Code();
		ArrayList<String> test = new ArrayList<String>();
		test.add("int a=pow((2*2),2);");
		/*test.add("int b=a*5;");
		test.add("int c=pow(pow(1,1),b);");
		test.add("int d=c-5*a;");
		test.add("a++;");*/
		
		for (int i=0;i<test.size();i++) {
			System.out.println(code.execLigne(test.get(i)));
		}
		System.out.println(code.getRecord());
	}
		
}