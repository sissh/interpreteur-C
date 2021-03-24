package Parser;

import java.util.ArrayList;
import java.util.HashMap;

import Tokens.*;

public class SousFonction {
	
	Parser parser;
	HashMap<String, Variable> variablesLocales;
	private ArrayList<Token> arrayListTokens;
	private ArrayList<Token> ligne;
	
	public SousFonction (ArrayList<Token> nvListeTokens) {
		arrayListTokens = nvListeTokens;
		ligne = new ArrayList<Token>();
		variablesLocales = new HashMap<String, Variable>();
		parser = new Parser(variablesLocales);
	}
	
	@SuppressWarnings("unchecked")
	public Object execLigneSuivante(){
		if (0 == arrayListTokens.size())
			return "Fin d'exécution";
		else {
			while (0 < arrayListTokens.size() && !arrayListTokens.get(0).getNom().equals(";")){//gerer erreur
				ligne.add(arrayListTokens.get(0));
				arrayListTokens.remove(0);
			}
			if (0 == arrayListTokens.size() || !arrayListTokens.get(0).getNom().equals(";"))
				return "Ligne finie sans ';'";
			arrayListTokens.remove(0);//suppression du token ';'
			Object resultat = parser.execution(ligne);
			if (resultat instanceof HashMap<?, ?>)
				return (HashMap<String, Variable>)resultat;
			else 
				return resultat.toString();
		}
	}

}
