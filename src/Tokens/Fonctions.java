package Tokens;

import java.util.ArrayList;

public class Fonctions {
	private String fonctionNom;
	private Object fonctionArgs ;
	
	public Fonctions(String nom, Object [] arguments) {
		fonctionNom = nom;
		fonctionArgs = arguments;
	}

	
	public static String execFonction(String nomFonction, ArrayList<Object> argsArrayList) { 
		Object [] arguments = argsArrayList.toArray();
		
		switch(nomFonction) {
			case "pow":
				if (arguments[0] instanceof Integer && arguments[1] instanceof Integer) {
					pow(arguments);
					return messageErreur(0);
				}
				
				else if (arguments[0] instanceof Integer && !(arguments[1] instanceof Integer) ) {
					return messageErreur(1);
				}
				
				else if (!(arguments[0]instanceof Integer) && arguments[1] instanceof Integer) {
					return messageErreur(2);
				}
				
			default: return messageErreur(42);
		}

	}
	
	private static String messageErreur(int codeErreur) {
		switch(codeErreur) {
			case 0: return "Aucun probl�me rencontr�.";
			case 1: return "Erreur au second argument : type Int attendu.";
			case 2: return "Erreur au premier argument : type Int attendu.";
			
			
			
			case 42: return "Fonction non reconnue.";
			default: return "Ce message n'est pas cens� appara�tre.";
		}
	}
	
	public static int pow(Object [] arguments) {	
		int nb = Integer.parseInt(arguments[0].toString());
		int puiss = Integer.parseInt(arguments[1].toString());
		int resultat = 1;
		for (int i = 0; i < puiss; i++) {
			resultat = resultat * nb;
		}		
		return resultat;		
	}
	
	public static char[] print(Object [] arguments) throws Exception {			//TODO: trouver meilleur qu'une exception.
		String resultat = arguments[0].toString();
		int variable = 1;
		int i = 0;
		int nbArgs = arguments.length - 1;
		int nbTrigger = 0;

		while (i < resultat.length()-1) {
			
			if (nbTrigger > nbArgs) {
				throw new Exception ("Erreur: Appel d'argument non existant");
			}
			
			if (resultat.charAt(i) == '%') {
				
				if (i < resultat.length() - 2 && testVariablePrint(resultat.charAt(i+1), resultat.charAt(i+2)) ) {
					resultat = resultat.substring(0, i) + arguments[variable].toString() + resultat.substring(i+2);
					variable += 1;
					nbTrigger++;
					
				}
				
				else if (i <= resultat.length() - 1 && testVariablePrint(resultat.charAt(i+1), ' ')) {
					resultat = resultat.substring(0, i) + arguments[variable].toString();
				}
				
			}
			
			i++;
		}
		
		char [] resultatCharArray = new char[resultat.length()];
		
		for (int j = 0; j < resultat.length(); j++) {
			resultatCharArray[j] = resultat.charAt(j);
		}
		
		return resultatCharArray;
	}
	
	private static boolean testVariablePrint (char ch1, char ch2) { //TODO: FIX
		if (ch2 == ' ') {
			switch (ch1) {
				case 'd','s','c','f','n':return true;
				default:return false;
			}
		}
		return false;
	}
	

}
