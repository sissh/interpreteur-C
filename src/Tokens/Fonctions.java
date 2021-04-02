package Tokens;

import java.util.ArrayList;

import com.sun.net.httpserver.Filter.Chain;

import Vue.FenetreMere;

public class Fonctions {

//Cette méthode reçoit le nom de la fonction demandées et ses arguments: elle vérifie la validité des arguments et exécute la fonction demandée	
	public static Object execFonction(String nomFonction, ArrayList<Object> argsArrayList) { 
		Object [] arguments = argsArrayList.toArray();
		
		switch(nomFonction) {			//Switch qui filtre les erreurs d'arguments et exécute les fonctions demandées: renvoi un msg d'erreur si arg invalide
			case "pow":
				if (arguments.length < 2) {	//Pas assez d'arguments
					return messageErreur(3);
				}
				
				if (arguments.length > 2) {							//Trop d'arguments
					return messageErreur(4);
				}
				
				if (!(arguments[0] instanceof Integer) && !(arguments[1] instanceof Integer)) {		//Aucun argument valide
					return messageErreur(5);
				}
				
				else if (arguments[0] instanceof Integer && !(arguments[1] instanceof Integer) ) {		//Premier argument valide, second non
					return messageErreur(1);
				}
				
				else if (!(arguments[0]instanceof Integer) && arguments[1] instanceof Integer) {		//Second argument valide, premier non
					return messageErreur(2);
				}
				
				else {
					return pow(arguments);			//Exécution de pow
				}
				
			case "printf":
				return print(arguments);
				
			default: return messageErreur(42);			//Fonction non reconnue : ne devrait pas apparaître car non appelé par le parser
		}

	}

//Cette méthode privée répertorie les différents message d'erreur selon leur code d'erreur, pour la lisibilité.
	private static String messageErreur(int codeErreur) {
		switch(codeErreur) {
			case 0: return "Aucun problï¿½me rencontrï¿½.";
			case 1: return "Erreur au second argument : type Int attendu.";
			case 2: return "Erreur au premier argument : type Int attendu.";
			case 3: return "Erreur : pas assez d'arguments.";
			case 4: return "Erreur : trop d'arguments.";
			case 5: return "Erreur : Aucun argument de type Int.";
			
			
			
			case 42: return "Fonction non reconnue.";
			default: return "Ce message n'est pas censï¿½ apparaï¿½tre.";
		}
	}

//Fonction power du C 	
	public static int pow(Object [] arguments) {	
		int nb = Integer.parseInt(arguments[0].toString());				//Récupération des type Object du parser et conversion en Int
		int puiss = Integer.parseInt(arguments[1].toString());
		int resultat = 1;
		for (int i = 0; i < puiss; i++) {
			resultat = resultat * nb;				//Calcul de puissance
		}		
		return resultat;		
	}
	
	
//Fonction printf du C	
	public static int print(Object [] arguments) {			
		String phrase = arguments[0].toString();								//La fonction travail avec des String, pour la méthode subString()
		String erreur1 = "Erreur : trop d'appel d'arguments.";								//Gestion d'erreur à l'interieur de print :
		String erreur2 = "Erreur : trop d'arguments en paramètre.";									//Impossible pour execFonction de savoir s'il y a trop ou pas assez de %
		int argCourant = 1;
		int i = 0;
		int nbArgs = arguments.length - 1;										//Compte le nb d'arguments
		int nbTrigger = nbArgs;														//Compte le nb de % détectés par la fonction: on part du principe qu'il y en aura autant que d'args
		

		while (i < phrase.length()-1 && nbArgs > 0) {			//Parcours de la phrase: taille variable car elle est changée quand on trouve des %; pas de parcours si pas d'arguments.
			
			if (phrase.charAt(i) == '%') {		//Détection d'un %
				
				if (i < phrase.length() - 1 && testVariable(phrase.charAt(i+1), arguments[argCourant])) {		//Vérifie si le charactère qui suit % fait parti de ceux appelant un argument
					if (nbTrigger > 0) {
						phrase = phrase.substring(0, i) + arguments[argCourant].toString() + phrase.substring(i+2);	//Découpe du String :
						argCourant += 1;									//On coupe jusqu'à avant le %, on concatène l'argument puis le reste de la phrase
					}
					nbTrigger--;	//Passe à la variable suivante; ajoute 1 au décompte de % trouvé. 			
				}
				
			}
			
			i++;
		}
		
		if (nbTrigger == 0) {							//Si il y a bien autant d'appel d'arguments que d'arguments, on renvoie le resultat
			FenetreMere.affichePrintf(phrase, 0);
		}
		
		else if (nbTrigger < 0) {							//S'il y a plus d'appel d'arguments que d'arguments, on renvoi une erreur
			FenetreMere.affichePrintf(erreur1, 1);
		}
		
		else {													//S'il y a moins d'appel d'arguments que d'arguments, on renvoi une erreur
			FenetreMere.affichePrintf(erreur2, 1);
		}
		
		return phrase.length();
		
	}

//Méthode privée utilisée par print permettant de savoir si le caractère suivant % est bien un des % appelant et vérifiant le type de variable correspondant
	private static boolean testVariable (char ch1, Object arg) { 
			switch (ch1) {
				case 'd', 'c' :													//On vérifie si au caractère détecté, on a bien un type d'argument correspondant.
					if (arg instanceof Integer) {return true;}
					else; return false;				
				case 's':
					if (arg instanceof String) {return true;}
					else; return false;
				case 'f':
					if (arg instanceof Float || arg instanceof Double) {return true;}
					else; return false;

				default:return false;
			}
	}
	

}
