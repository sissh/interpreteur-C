package Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import Tokens.Token;

public class Code {
	
	Parser parser;
	HashMap<String, Token> variables; //ajouter plus tard les variables globales
	
	public Code() {
		variables = new HashMap<String, Token>();
	}
	
	@SuppressWarnings("unchecked")
	public void execLigne(String ligne) {
		parser = new Parser(ligne, variables);
		parser.makeTokens();
		Object resultat = parser.execution();
		if (resultat instanceof String)
			System.out.println(resultat);
		else
			variables = (HashMap<String, Token>) resultat;
	}
	
	public String toString() {
		return variables.toString();
	}
	
}
