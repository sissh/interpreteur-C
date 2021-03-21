package Parser;

import java.util.ArrayList;
import java.util.HashMap;

import Tokens.*;

public class Fonction {
	
	Parser parser;
	HashMap<String, Variable> variablesLocales;
	private ArrayList<Token> arrayListTokens;
	private ArrayList<Token> ligne;
	int indice;
	
	public Fonction (ArrayList<Token> nvListeTokens) {
		arrayListTokens = nvListeTokens;
		ligne = new ArrayList<Token>();
		variablesLocales = new HashMap<String, Variable>();
		parser = new Parser(variablesLocales);
		indice = 0;
	}
	
	@SuppressWarnings("unchecked")
	public void execLigneSuivante(){
		while (!arrayListTokens.get(indice).getNom().equals(";") && indice < arrayListTokens.size())//gerer erreur
			ligne.add(arrayListTokens.get(indice++));
		Object resultat = parser.execution(ligne);
		if (resultat instanceof HashMap<?, ?>)
			variablesLocales = (HashMap<String, Variable>)resultat;
		else System.out.println(resultat);
	}

}
