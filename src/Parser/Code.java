package Parser;

import java.util.ArrayList;
import java.util.HashMap;
import Tokens.*;

/**
 * Classe préparant l'exécution du Parser, en créant les Tokens, et qui gère la sauvegarde de la mémoire.
 * @author Alexis Fondet
 *
 */
//Pour plus d'informations, ou des conseils sur la continuation du projet : alexisfondet@gmail.com
public class Code implements ListeMots{

	/**
	 * Objet Parser qui exécutera le code.
	 */
	private Parser parser;
	/**
	 * Liste de Tokens produite par {@link Parser.Code#makeTokens(String) makeTokens} (ligne de code), envoyée ensuite au Parser.
	 */
	private ArrayList<Token> arrayListTokens;
	/**
	 * Registre des différents états de la mémoire, pour permettre le retour en arrière.
	 */
	private ArrayList<HashMap<String, Variable>> arrayListRecord;
	/**
	 * indice correspondant à l'état de la mémoire en cours. Dispensable, car l'indice courant = arrayListRecord.size()-1 à tout moment.
	 */
	public int indice;
	
	public static String FIN_EXEC = "Fin d'exécution";
	
	/**
	 * Constructeur de la classe, initialise les variables.
	 */
	public Code() {
		/**
		 * contient les tokens à envoyer au Parser.
		 */
		arrayListTokens=new ArrayList<Token>();
		/**
		 * Parser pour l'exécution du code.
		 */
		parser = new Parser();
		/**
		 * Enregistre les différents états de la mémoire.
		 */
		arrayListRecord = new ArrayList<HashMap<String, Variable>>();
		/**
		 * initialise à zéro le prelier état de la mémoire.
		 */
		arrayListRecord.add(new HashMap<String, Variable>());
		indice=0;
	}
	
	/**
	 * Utilisé pour de la maintenance de code, mais peut avoir son utilité dans l'interface (pour accéder à des états antérieurs de la mémoire sans retour arrière).
	 * @return L'ensemble de la mémoire des variables, y compris les états antérieurs.
	 */
	public ArrayList<HashMap<String, Variable>> getRecord() {
		return this.arrayListRecord;
	}
	
	/**
	 * Remet à zéro l'objet Code, pour démarrer une nouvelle exécution.
	 * Utilisé lorsque l'utilisateur appuie sur le bouton {@linkplain Vue.FenetreMere#actionPerformed(java.awt.event.ActionEvent) RESET}.
	 */
	public void reset() {
		arrayListTokens=new ArrayList<Token>();
		parser = new Parser();
		arrayListRecord = new ArrayList<HashMap<String, Variable>>();
		arrayListRecord.add(new HashMap<String, Variable>());
		indice=0;
	}
	
	/**
	 * Conversion de la String envoyée par l'interface en une liste de Tokens (arrayListTokens).
	 * @param chaine Texte converti.
	 */
	private void makeTokens(String chaine) {
		String parse = "";//initialisation de cette variable, qui permet de distinguer les Tokens
		for (int i=0; i < chaine.length() ; i++) {
			char chr = chaine.charAt(i);//parcours de la chaine caractère par caractère
			if (chr==' '){// Les espaces délimitent les Tokens
				if (parse.equals("") || parse.equals("\n")) {//si parse est vide ou retour à la ligne inutile, il n'y a pas de Token à créer (par exemple, suite d'espaces dans une ligne de code)
					parse="";
					continue;
				}
				else {//sinon, alors le contenu de parse doit être un Token. Exemple : "int a = 5 ;", chaque token est séparé par un espace
					arrayListTokens.add(differentiation(parse));
					parse="";
					continue;
				}
				
			}
			else if(isToken(chr)) {//Si le caractère est un token stéréotypé à lui tout seul, il faut arrêter Exemple : ";", ",", "(", etc
				if (!parse.equals(""))
						arrayListTokens.add(differentiation(parse));//le token précédent est dont un Token aussi
				arrayListTokens.add(differentiation(chr));
				parse="";
				continue;
				}
			else if(chr=='\'') {
				String temp=""+chr;
				chr = chaine.charAt(++i);
				temp+=chr;
				chr=chaine.charAt(++i);
				temp+=chr;
				if (chr=='\'') {
					if (!parse.equals("") && !parse.equals(" ")) {
						arrayListTokens.add(differentiation(parse));
					}
					arrayListTokens.add(caractereToNum(temp.charAt(1)));
					parse="";
				}
				else
					parse+=temp;
				chr=' ';
			}
			else if (chr=='"') {
				String temp=""+chr;
				chr=' ';
				do {
					i++;
					chr=chaine.charAt(i);
					temp+=chr;
				}while(i < chaine.length() && chr!='"');
				arrayListTokens.add(differentiation(parse));
				arrayListTokens.add(new Phrase(temp));
				parse="";
				chr=' ';
			}
			parse+=chr;
		}
		int i=0;
		while (i<arrayListTokens.size()) {//Supprime des tokens vides en trop (ne doit plus arriver normalement, tester et supprimer si ok)
			if (arrayListTokens.get(i).getNom().equals("") || arrayListTokens.get(i).getNom().equals(" "))
				arrayListTokens.remove(i);
			else if (arrayListTokens.get(i).getNom().equals("+")) {// rajouter + et + juxtaposé, opérateur unaire
				if (arrayListTokens.get(i+1).getNom().equals("+")) {
					arrayListTokens.remove(i);
					arrayListTokens.set(i, new OpeUnaire("++"));
				}
				else i++;
					
			}
			else if (arrayListTokens.get(i).getNom().equals("-")) {//idem pour --
				if (arrayListTokens.get(i+1).getNom().equals("-")) {
					arrayListTokens.remove(i);
					arrayListTokens.set(i, new OpeUnaire("--"));
				}
				else i++;
					
			}
			else if (arrayListTokens.get(i) instanceof Variable) {// une fonction était de type variable (car ne se distingue pas sans vérification), devient TokenFonction
				if (arrayListTokens.get(i+1).getNom().equals("(")) {// int pow=5 est une variable, mais pow(2,2) est une fonction
					arrayListTokens.set(i, new TokenFonction(arrayListTokens.get(i).getNom()));
				}
				else i++;
				
			}
			else i++;
		}
	}//makeTokens
	
	/**
	 * Lance l'execution du {@linkplain Parser.Parser Parser}, gère la memoire, et traite les potentielles erreurs.
	 * @param chaine La String, traitee par {@link Parser.Code#makeTokens(String) makeTokens}.
	 * @return HashMap des Variables, ou erreur String.
	 */
	@SuppressWarnings("unchecked")
	public Object execLigne(String chaine){
		indice++;//sera l'indice de l'etat de la memoire après l'execution de la fonction
		arrayListRecord.add(arrayListRecord.get(indice-1));//copie de l'etat precedent de la memoire dans la nouvelle, avant modification
		makeTokens(chaine);
		if (0 == arrayListTokens.size() || !arrayListTokens.get(arrayListTokens.size()-1).getNom().equals(";"))//Ne doit pas arriver avec l'interface
			return "Ligne finie sans ';'";
		arrayListTokens.remove(arrayListTokens.size()-1);//suppression du token ';'
		
		Object resultat = parser.execution(arrayListTokens, arrayListRecord.get(indice));//on donne ici l'etat de la memoire à modifier
		if (resultat instanceof HashMap<?, ?>) {
			//arrayListRecord.set(indice,(HashMap<String, Variable>)resultat);
			return (HashMap<String, Variable>)resultat;//variables à afficher dans la mémoire de l'interface
		}
		else 
			return resultat.toString();// erreur à afficher dans la console de l'interface
	}
	
	/**
	 * Retrograde l'etat de la memoire.
	 * @return Etat precedent de la memoire.
	 */
	public HashMap<String, Variable> backLine() {
		arrayListRecord.remove(indice--);
		return arrayListRecord.get(indice);
	}
	
	/**
	 * Détermine si un caractère est un token stéréotypé.
	 * @param token caractère de la chaine passées à {@link Parser.Code#makeTokens(String) makeTokens}.
	 * @return true si oui, false sinon
	 */
	private boolean isToken(char token) {
		
		if (isOperateur_1(token))
			return true;
		if (isOperateur_2(token))
			return true;
		for (int i=0; i< SYNTAXE.length ; i++) {
			if (token == SYNTAXE[i]) {
				return true;
			}
		}
		if (isEgal(token))
			return true;
		return false;
	}//isToken
	
	/**
	 * Détermine si une chaine de caractères est identifiable à un nombre.
	 * @param str Chaine de caractères à tester.
	 * @return Le nombre en question, converti en le type correspondant.
	 */
	private Object isNumeric(String str) {//attention, la taille des variables n'est vraisemblablement pas la bonne. Cela se corrige dans la classe Tokens.Variable, suivre l'exemple de char
		try {
			return Integer.parseInt(str);
		}catch(Exception e) {};
		try {
			return Long.parseLong(str);
		}catch(Exception e) {};
		try {
			return Float.parseFloat(str);
		}catch(Exception e) {};
		try { 
			return Double.parseDouble(str);
		}catch(Exception e) {};
		return null;
	}

	/**
	 * Détermine si une chaine de caractères est identifiable à un type de C.
	 * @param token Chaine de caractère à tester.
	 * @return true si oui, false sinon.
	 */
	private boolean isType(String token) {
		for (int i=0; i< TYPES.length ; i++) {
			if (token.equals(TYPES[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Détermine si une chaine de caractères est identifiable à '*', '/', ou '%'.
	 * @param token Le caractère à tester.
	 * @return true si oui, false sinon.
	 */
	private boolean isOperateur_1(char token) {
		for (int i=0; i< OPERATEURS_1.length ; i++) {
			if (token == OPERATEURS_1[i]) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Détermine si une chaine de caractères est identifiable à '+', ou '-'.
	 * @param token Le caractère à tester.
	 * @return true si oui, false sinon.
	 */
	private boolean isOperateur_2(char token) {
		for (int i=0; i< OPERATEURS_2.length ; i++) {
			if (token == OPERATEURS_2[i]) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Détermine si une chaine de caractères est identifiable à un élément de syntaxe (quelconque, la classe {@link Tokens.Syntaxe Syntaxe} est un bouche trou) de C.
	 * @param token Chaine de caractère à tester.
	 * @return true si oui, false sinon.
	 */
	private boolean isSyntaxe(char token) {
		for (int i=0; i< SYNTAXE.length ; i++) {
			if (token == SYNTAXE[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Détermine si une chaine de caractères est identifiable à '='.
	 * @param token Caractère à tester.
	 * @return true si oui, false sinon.
	 */
	private boolean isEgal(char token) {
		return token == EGAL;
	}

	/**
	 * Crée un token, en fonction du caractère récupéré.
	 * @param nom Caractère à tester.
	 * @return Le token créé.
	 */
	private Token createToken(char nom) {
		if (isOperateur_1(nom))
			return new Operateur_1(String.valueOf(nom));
		else if (isOperateur_2(nom))
			return new Operateur_2(String.valueOf(nom));
		/*else if (isComparateur(nom))
			return new Comparateur(String.valueOf(nom));*/
		else if (isSyntaxe(nom))
			return new Syntaxe(String.valueOf(nom));
		else if (isEgal(nom))
			return new Egal(String.valueOf(nom));
		else
			return null;
	}

	/**
	 * Crée un token de type {@link Tokens.Constante Constante}.
	 * @param valeur La valeur à attribuer à la constante.
	 * @return Le token Constante nouvellement créé.
	 */
	private Constante createToken(Number valeur) {
		return new Constante(valeur);
		
	}

	/**
	 * Crée un token de type {@link Tokens.Variable Variable}.
	 * @param nom Le nom de la variable.
	 * @param valeur La valeur de la variable (toujours utilisé à null pour le moment, mais peut-être amené à être modifié suivant la direction que prend l'équipe de programmation.
	 * @return Le token Variable nouvellement créé.
	 */
	private Variable createToken(String nom, Number valeur) {
		return new Variable(nom,valeur);
		
	}

	/**
	 * Choisi le constructeur de token à utiliser.
	 * @param nom La chaine de caractère correspondant à la variable.
	 * @return Le token nouvellement créé.
	 */
	private Token differentiation(String nom) {
		Object result=isNumeric(nom);
		if (result!=null)
			return createToken((Number)result);
		else if (isType(nom))
			return new Type(nom);
		else return createToken(nom,null);//variable sans valeur
	}

	/**
	 * Choisi le constructeur de token à utiliser. Inutile dans l'état actuel, car il n'y a qu'un constructeur pour caractère.
	 * @param nom Le caractère correspondant à la variable.
	 * @return Le token nouvellement créé.
	 */
	private Token differentiation(char nom) {
		return createToken(nom);
	}
	
	/**
	 * Convertit un caractère entré par l'utilisateur ('A' par exemple) en son équivalent numérique.
	 * @param chr Le caractère à convertir.
	 * @return Le token de type {@link} Tokens.Constante Constante} associé.
	 */
	private Constante caractereToNum(char chr) {
		int a = chr;
		return new Constante(a);
	}
}
