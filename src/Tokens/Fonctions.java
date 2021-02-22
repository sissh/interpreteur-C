package Tokens;

public class Fonctions {
	private String fonctionNom;
	private Object fonctionArgs ;
	
	public Fonctions(String nom, Object [] arguments) {
		fonctionNom = nom;
		fonctionArgs = arguments;
	}

	
	public static String execFonction(String nomFonction, Object [] arguments) {	
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
			case 0: return "Aucun problème rencontré.";
			case 1: return "Erreur au second argument : type Int attendu.";
			case 2: return "Erreur au premier argument : type Int attendu.";
			
			
			
			case 42: return "Fonction non reconnue.";
			default: return "Ce message n'est pas censé apparaître.";
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
	
	public static String print(Object [] arguments) {
		return arguments[0].toString();
	}

}
