package Parser;

import java.util.ArrayList;

import com.sun.net.httpserver.Filter.Chain;

import Vue.FenetreMere;

/** 
 * La classe qui r�pertorie toutes les fonctions basiques du C
 * @author Sissani **/

public class Fonctions {

/**Cette m�thode re�oit le nom de la fonction demand�es et ses arguments: elle v�rifie la validit� des arguments et ex�cute la fonction demand�e**/	
	public static Object execFonction(String nomFonction, ArrayList<Object> argsArrayList) { 
		Object [] arguments = argsArrayList.toArray();
		
		switch(nomFonction) {			/**Switch qui filtre les erreurs d'arguments et ex�cute les fonctions demand�es: renvoi un msg d'erreur si arg invalide**/
			case "pow":
				if (arguments.length < 2) {	/**Pas assez d'arguments**/
					return messageErreur(3);
				}
				
				if (arguments.length > 2) {							/**Trop d'arguments**/
					return messageErreur(4);
				}
				
				if (!(arguments[0] instanceof Integer) && !(arguments[1] instanceof Integer)) {		/**Aucun argument valide**/
					return messageErreur(5);
				}
				
				else if (arguments[0] instanceof Integer && !(arguments[1] instanceof Integer) ) {		/**Premier argument valide, second non**/
					return messageErreur(1);
				}
				
				else if (!(arguments[0]instanceof Integer) && arguments[1] instanceof Integer) {		/**Second argument valide, premier non**/
					return messageErreur(2);
				}
				
				else {
					return pow(arguments);			/**Ex�cution de pow**/
				}
			
			case "sqrt":
				if (!(arguments[0] instanceof Integer)) {
					return messageErreur(5);
				}
				
				else {
					return sqrt(arguments);
				}
			
			case "exp":
				if (!(arguments[0] instanceof Integer)) {
					return messageErreur(5);
				}
				
				else {
					return exp(arguments);
				}
				
			case "printf":
				return print(arguments);
			
			case "scanf":
				if (arguments.length <= 1) {
					return messageErreur(6);
				}
				
			default: return messageErreur(42);			/**Fonction non reconnue : ne devrait pas appara�tre car non appel� par le parser**/
		}

	}

/**Cette m�thode priv�e r�pertorie les diff�rents message d'erreur selon leur code d'erreur, pour la lisibilit�.**/
	private static String messageErreur(int codeErreur) {
		switch(codeErreur) {
			case 0: return "Aucun probl�me rencontr�.";
			case 1: return "Erreur au second argument : type Int attendu.";
			case 2: return "Erreur au premier argument : type Int attendu.";
			case 3: return "Erreur : pas assez d'arguments en param�tre.";
			case 4: return "Erreur : trop d'arguments en param�tre.";
			case 5: return "Erreur : Aucun argument de type Int.";
			case 6: return "Erreur : Aucune addresse de variable en argument.";
			case 7: return "Erreur : L'appel d'argument ne correspond pas au type en param�tre.";
			
			
			case 42: return "Fonction non reconnue.";
			default: return "Ce message n'est pas cens� appara�tre.";
		}
	}

/**Fonction puissance du C **/	
	public static int pow(Object [] arguments) {	
		int nb = Integer.parseInt(arguments[0].toString());				/**R�cup�ration des type Object du parser et conversion en Int**/
		int puiss = Integer.parseInt(arguments[1].toString());
		int resultat = 1;
		for (int i = 0; i < puiss; i++) {
			resultat = resultat * nb;				/**Calcul de puissance**/
		}		
		return resultat;		
	}
	
/**Fonction racine carr� du C**/
	public static int sqrt(Object [] arguments) {
		double nb = Double.parseDouble(arguments[0].toString());
		double i;
		double resultat = nb / 2;
		do {
			i = resultat;
			resultat = (i + (nb/i))/2;
		} while ((i-resultat)!=0);
		int resultatInt = (int)resultat;
		return resultatInt;
	}
	
/**Fonction exponentielle du C**/
	public static int exp(Object [] arguments) {
		double resultat = Math.exp(Double.parseDouble(arguments[0].toString()));
		int resultatInt = (int)resultat;
		return resultatInt;
	}
	
	
/**Fonction printf du C	**/
	public static int print(Object [] arguments) {			
		String phrase = arguments[0].toString();								/**La fonction travail avec des String, pour la m�thode subString()**/
		int argCourant = 1;
		int i = 0;
		int nbArgs = arguments.length - 1;										/**Compte le nb d'arguments**/
		int nbTrigger = nbArgs;														/**Compte le nb de % d�tect�s par la fonction: on part du principe qu'il y en aura autant que d'args**/
		
		while (i < phrase.length()-1 && nbArgs > 0) {			/**Parcours de la phrase: taille variable car elle est chang�e quand on trouve des %; pas de parcours si pas d'arguments.**/
			
			if (phrase.charAt(i) == '%') {		/**D�tection d'un %**/
				
				if (i < phrase.length() - 1 && testVariable(phrase.charAt(i+1), arguments[argCourant])==0) {		/**V�rifie si le charact�re qui suit % fait parti de ceux appelant un argument**/
					if (nbTrigger > 0) {
						phrase = phrase.substring(0, i) + arguments[argCourant].toString() + phrase.substring(i+2);	/**D�coupe du String : **/
						argCourant += 1;									/** On coupe jusqu'� avant le %, on concat�ne l'argument puis le reste de la phrase**/
					}
					nbTrigger--;	/**Passe � la variable suivante; ajoute 1 au d�compte de % trouv�.**/ 		
					System.out.println("Nbtrigger:");
					System.out.println(nbTrigger);
				}
				
				else if (i < phrase.length() - 1 && testVariable(phrase.charAt(i+1), arguments[argCourant])==1) {
					FenetreMere.affichePrintf(messageErreur(7), 1);
					System.out.println("oh wesh");
				}
				
			}
			
			i++;
		}
		
		if (nbTrigger == 0) {							/**Si il y a bien autant d'appel d'arguments que d'arguments, on renvoie le resultat**/
			FenetreMere.affichePrintf(phrase, 0);
			System.out.println("ok");
		}
		
		else if (nbTrigger < 0) {							/**S'il y a plus d'appel d'arguments que d'arguments, on renvoi une erreur**/
			FenetreMere.affichePrintf(messageErreur(3), 1);
			System.out.println("pipi caca");
		}
		
		else {													/**S'il y a moins d'appel d'arguments que d'arguments, on renvoi une erreur**/
			FenetreMere.affichePrintf(messageErreur(4), 1);
			System.out.println("zebi");
		}
		
		return phrase.length();
		
	}
	
	private static Object [] scanf(Object [] arguments) {
		String phrase = arguments[0].toString();
		Object [] resultat = null;
		
		
		
		
		
		return resultat;
	}


/**M�thode priv�e utilis�e par print permettant de savoir si le caract�re suivant % est bien un des % appelant et v�rifiant le type de variable correspondant**/
	private static int testVariable (char ch1, Object arg) { 
			switch (ch1) {
				case 'd':													/**On v�rifie si au caract�re d�tect�, on a bien un type d'argument correspondant.**/
					if (arg instanceof Integer) {return 0;}
					else; return 1;		
				case 'c':
					if (arg instanceof Integer) {return 0;}
					else; return 1;		
				case 's':
					if (arg instanceof String) {return 0;}
					else; return 1;
				case 'f':
					if (arg instanceof Float || arg instanceof Double) {return 0;}
					else; return 1;

				default:return 2;
			}
	}
	

}
