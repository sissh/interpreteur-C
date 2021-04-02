package Parser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import Tokens.*;

/**
 * Classe d'exécution du code
 * @author alexi
 *
 */
public class Parser{

	private HashMap<String, Variable> variables;
	private ArrayList<Token> ligne;
	public static String FIN_EXEC = "Fin d'exécution";

	public Parser() {
		variables = new HashMap<String, Variable>();
		ligne = new ArrayList<Token>();
	}
	
	public Object execution(ArrayList<Token> nvLigne, HashMap<String, Variable> nvVariables) {
		variables= nvVariables;
		ligne=nvLigne;
		if (ligne.size()==0)
			return FIN_EXEC;
		if (ligne.get(0) instanceof Type) {
			if (ligne.get(1) instanceof Variable) {
				boolean ok = declareVariable((Variable)ligne.get(1));
				if (!ok)
					return "La variable "+ligne.get(1).getNom()+" a déjà été déclarée précédemment";
				variables.get(ligne.get(1).getNom()).setType(ligne.get(0).getNom());//assigne le type
				ligne.remove(0);
				if (ligne.size()==1){//si déclaration sans assignation de valeur
					ligne.remove(0);
					return variables;
				}
			}
			else return "Variable attendue après une déclaration de type";
		}
		else if (ligne.get(0) instanceof Variable) {
			if (!existe(ligne.get(0)))
				return "Token "+ligne.get(0).getNom()+" non initialisé précédemment";
		}
		else if (ligne.get(0) instanceof OpeUnaire && ligne.size()==2 && ligne.get(1) instanceof Variable){//ex : "++a;", voir si possible de faire "++a=5;"
			String courant = ligne.get(1).getNom();
			if (variables.get(courant).getValeur()==null)
				return "Attention ! Il est vivement conseillé de ne pas incrémenter une variable non instancée";
			incrementation(courant,(OpeUnaire)ligne.get(0));
			return variables;
		}
		else return "Début d'expression interdite";
		String courant = ligne.get(0).getNom();
		ligne.remove(0);
		
		
		Token signe =null;//pour assignation signée
		if (ligne.get(0) instanceof OpeUnaire && 1==ligne.size()){//ex : "a++;"
			if (variables.get(courant).getValeur()==null)
				return "Attention ! Ne jamais incrémenter une variable non instancée";
			incrementation(courant,(OpeUnaire)ligne.get(0));
			return variables;
		}
		else if (ligne.get(0) instanceof Operateur_1 || ligne.get(0) instanceof Operateur_2) {
			if (ligne.size()!=1 && ligne.get(1) instanceof Egal)//assignation signée ex "a*=5;"
				signe = ligne.get(0);
			ligne.remove(0);
		}
		else if (!(ligne.get(0) instanceof Egal))//gérer autres types d'assignation de valeur
			return "Token "+ligne.get(0).getNom()+" inattendu, le programme devait trouver un \"=\"";
		ligne.remove(0);

		Object erreur=errorCalculUnaire();
		if (erreur instanceof String)
			return erreur.toString();
		HashMap<String,OpeUnaire> calculSuffixe= calculUnaireSuffixe();
		calculUnairePrefixe();
		erreur = initCalcul();
		if (erreur instanceof String)
			return erreur.toString();
		if (ligne.size()!=1)
			return "Erreur lors de l'exécution";
		
		if (signe!=null)
			ligne.set(0, calculArithmetique(variables.get(courant),signe, ligne.get(0)));
		modifVariable(courant, ligne.get(0));
		
		Iterator<String> iterateur = calculSuffixe.keySet().iterator();
		String nomValeur;
		while (iterateur.hasNext()) {
			nomValeur=iterateur.next();
			incrementation(nomValeur, calculSuffixe.get(nomValeur));
		}
		ligne.remove(0);//suppresion de la constante finale
		return variables;
	}
	
	private Object execFonctions(int debut, int fin) {
		int i=debut;
		//System.out.println(ligne+" debut :"+debut+" fin :"+fin+" i :"+i);
		while (i<fin) {
			if (ligne.get(i) instanceof TokenFonction) {//faire calculLigne entre chaque virgule au lieu de gerer les cas particuliers
				String nomFonction = ligne.get(i).getNom();ligne.remove(i); fin-=1; //plus besoin donc de s'occuper des fonctions imbriquees
				if (i<fin && ligne.get(i).getNom().equals("(")) { //il faut gerer la valeur de fin cependant, qui va changer
					ligne.remove(i); fin-=1;
					ArrayList <Object> parametres = new ArrayList <Object>();
					int j=0;
					while (i+j<fin && !ligne.get(i+j).getNom().equals(")")) {//gestion des fonctions imbriquées : compter les parentheses
						int nbParentheses=0;
						while (i+j<fin && (nbParentheses>0 || (!ligne.get(i+j).getNom().equals(",") && !ligne.get(i+j).getNom().equals(")")))){//erreur potentielle si pow(,5) par ex
							if (ligne.get(i+j).getNom().equals("("))
								nbParentheses++;
							else if (ligne.get(i+j).getNom().equals(")"))
								nbParentheses--;
							j++;
						}
						//System.out.println(ligne+" debut :"+debut+" fin :"+fin+" i :"+i+" j : "+j);
						if (i+j==fin)
							return standardErrorMessage("paramètre", "rien");
						else if (ligne.get(i+j).getNom().equals(",") || ligne.get(i+j).getNom().equals(")")) {//autres cas possibles ?
							Object nvFin = calculLigne(i,i+j-1);//peut-être inutile
							//System.out.println(ligne+" debut :"+debut+" fin :"+fin+" i :"+i+" j : "+j+" nvFin : "+nvFin);
							if (nvFin instanceof String)
								return nvFin.toString();
							else if ((int)nvFin!=i) {
								System.out.println("Vous avez oublié un paramètre dans la fonction "+nomFonction+", ou virgule en trop");
								//ici, envoi dans la fonction de la chaine de tokens
								if (ligne.get(i).getNom().equals("\"")) {
									ligne.remove(i);
									String phrase="";
									while (!ligne.get(i).getNom().equals("\"")) {
										phrase+=ligne.get(i).getNom();
										ligne.remove(i);
									}
									parametres.add(phrase);
								}
							}
							else
								parametres.add(getTokenValeur(ligne.get(i)));
							ligne.remove(i);
							fin-=j;
							if (ligne.get(i).getNom().equals(","))
								ligne.remove(i);
							else
								fin--;
							j=0;
						}
					}
					//System.out.println(ligne+" fin : "+fin+" i : "+i);
					if (i==fin && !ligne.get(i).getNom().equals(")"))
						return standardErrorMessage("paramètre' ou ')", "rien");
					Object resultat = Fonctions.execFonction(nomFonction, parametres);
					if (resultat instanceof String)
						return resultat.toString();
					ligne.set(i, new Constante((Number)resultat));// à la place du Token ')'
					
				}
				else return standardErrorMessage("(", ligne.get(i).getNom());
			}
			i++;
		}
		return fin;
	}
	
	private HashMap<String, OpeUnaire> calculUnaireSuffixe(){
		HashMap<String, OpeUnaire> variablesAIncrementer = new HashMap<String, OpeUnaire>();
		int i=0;
		while (i<ligne.size()) {
			if (ligne.get(i++) instanceof Variable) {
				if (i<ligne.size() && ligne.get(i) instanceof OpeUnaire) {
					variablesAIncrementer.put(ligne.get(i-1).getNom(), (OpeUnaire)ligne.get(i));
					ligne.remove(i);
				}
				else i++;
			}
		}
		return variablesAIncrementer;
	}
	
	private void calculUnairePrefixe(){
		int i=0;
		while (i<ligne.size()) {
			if (ligne.get(i++) instanceof OpeUnaire) {
				if (i<ligne.size() && ligne.get(i) instanceof Variable) {					
					incrementation(ligne.get(i).getNom(), (OpeUnaire)ligne.get(i-1));
					ligne.remove(i-1);
				}
				else i++;
			}
		}
	}
	
	private Object errorCalculUnaire() {//si tentative d'incrémenter constantes
		int i=0;
		while (i<ligne.size()) {
			if (!(ligne.get(i++) instanceof Variable)) {//double boucle inutile, on peut gérer les deux en même temps
				if (i<ligne.size() && ligne.get(i) instanceof OpeUnaire) {
					return "Tentative d'incrémenter un Token qui n'est pas une variable";
				}
				else i++;
			}
		}
		i=0;
		while (i<ligne.size()) {
			if (ligne.get(i++) instanceof OpeUnaire) {
				if (i<ligne.size() && !(ligne.get(i) instanceof Variable)) {
					return "Tentative d'incrémenter un Token qui n'est pas une variable";
				}
				else i++;
			}
		}
		return null;
	}
	
	private String verifNullVariables() {
		for (int i=0; i<ligne.size(); i++) {
			if (ligne.get(i) instanceof Variable && getTokenValeur(ligne.get(i))==null)
				return "Attention, il est vivement déconseillé d'utiliser une variable non instanciée auparavant.";
		}
		return null;
	}
	
	private void incrementation(String nomVariable, OpeUnaire ope) {
		String type=variables.get(nomVariable).getType();
		if (ope.getNom().equals("++")) {
			if (type.equals("int"))
				variables.get(nomVariable).setValeur((int)variables.get(nomVariable).getValeur()+1);
			else if (type.equals("long"))
				variables.get(nomVariable).setValeur((long)variables.get(nomVariable).getValeur()+1);
			else if (type.equals("float"))
				variables.get(nomVariable).setValeur((float)variables.get(nomVariable).getValeur()+1);
			else if (type.equals("double"))
				variables.get(nomVariable).setValeur((double)variables.get(nomVariable).getValeur()+1);
		}
		else {//return String erreur si aucun des deux ? est-ce que ça peut arriver ? à voir
			if (type.equals("int"))
				variables.get(nomVariable).setValeur((int)variables.get(nomVariable).getValeur()-1);
			else if (type.equals("long"))
				variables.get(nomVariable).setValeur((long)variables.get(nomVariable).getValeur()-1);
			else if (type.equals("float"))
				variables.get(nomVariable).setValeur((float)variables.get(nomVariable).getValeur()-1);
			else if (type.equals("double"))
				variables.get(nomVariable).setValeur((double)variables.get(nomVariable).getValeur()-1);
		}
	}
	
	private Object calculLigne1(int debut, int fin) {//calcul des multiplications, divisions, modulo
		int i;
		boolean continuer=true;
		while (debut<fin && continuer) {//erreurs potentielles si tokens en trop, à verifier
			i=debut;
			continuer = false;
			while (i<fin) {
				if (ligne.get(i) instanceof Variable || ligne.get(i) instanceof Constante) {
					i++;
					if (i<fin && ligne.get(i) instanceof Operateur_1) {
						continuer=true;//tant qu'il reste des tokens à exécuter, continuer
						if (i+1<ligne.size()) {
							if (ligne.get(i+1) instanceof Variable || ligne.get(i+1) instanceof Constante) {
								ligne.set(i-1, calculArithmetique(ligne.get(i-1),ligne.get(i),ligne.get(i+1)));
							ligne.remove(i);ligne.remove(i); i=debut;fin-=2;							
							}
							else return standardErrorMessage("variable ou constante", ligne.get(i+1).getNom());
						}
						else return "Erreur sur la ligne, probablement un Token mal placé ou en trop";//ex : "int a= 1+2 * ;"
					}
					else i++;
				}
				else i++;
			}
		}
		return fin;
	}
	
	private Object calculLigne2(int debut, int fin) {//calcul des additions, soustractions
		int i;
		boolean continuer=true;
		while (debut<fin && continuer) {
			i=debut;
			continuer = false;
			while (i<fin) {
				if (ligne.get(i) instanceof Variable || ligne.get(i) instanceof Constante) {
					i++;
					if (i<fin && ligne.get(i) instanceof Operateur_2) {
						continuer=true;//tant qu'il reste des tokens à exécuter, continuer
						if (i+1<ligne.size()) {
							if (ligne.get(i+1) instanceof Variable || ligne.get(i+1) instanceof Constante) {
							ligne.set(i-1, calculArithmetique(ligne.get(i-1),ligne.get(i),ligne.get(i+1)));
							ligne.remove(i);ligne.remove(i); i=debut;fin-=2;							
							}
							else return standardErrorMessage("variable ou constante", ligne.get(i+1).getNom());
						}
						else return "Erreur sur la ligne, probablement un Token mal placé ou en trop";//ex : "int a= 1+2 * ;"
					}
					else i++;
				}
				else i++;
			}
		}
		return fin;
	}
	
	private Object calculLigne(int debut, int fin) {
		Object erreur = execFonctions(debut, fin);
		if (erreur instanceof String)
			return String.valueOf(erreur);
		int nvFin = (int)erreur;
		erreur = calculLigne1(debut,nvFin);
		if (erreur instanceof String)
			return String.valueOf(erreur);
		nvFin = (int)erreur;
		return calculLigne2(debut,nvFin);
	}
	
	private Object initCalcul() {
		Object erreur = verifNullVariables(); //à corriger, car peut creer des erreurs si non utilise
		//if (erreur instanceof String)
			//return erreur.toString();
		int i =0;
		while (i<ligne.size()) {
			if (ligne.get(i).getNom().equals("(")) {
				if (i==0 || !(ligne.get(i-1) instanceof TokenFonction)) {
					ligne.remove(i);
					erreur = calculParentheses(i);
					//ici ajouter fonction (??)
					if (erreur instanceof String)
						return erreur.toString();
					
				}
				else i++;
			}
			else i++;
		}
		return calculLigne(0, ligne.size());
	}
	
	private Object calculParentheses(int debut) {
		int i=debut;
		while (i<ligne.size()) {
			if (ligne.get(i).getNom().equals("(")) {
				if (!(ligne.get(i-1) instanceof TokenFonction)) {
					ligne.remove(i);
					Object error = calculParentheses(i);
					if (error instanceof String)
						return error.toString();
				}
			}
			else if (ligne.get(i).getNom().equals(")")) {
				ligne.remove(i);
				return calculLigne(debut,i-1);
			}
			else i++;
		}
		return "Parenthèse ouverte non fermée";
	}

	private boolean existe(Token token) {//si la variable (avec son type) n'existe pas dans la mémoire, false, sinon true
		return variables.containsKey(token.getNom());
	}

	private boolean declareVariable(Variable token) {//ajoute dans la mémoire la variable. Si pas d'erreur, return "", si la variable existe déjà, erreur
		if (variables.containsKey(token.getNom()))
			return false;
		variables.put(token.getNom(), token);
		return true;
	}
	
	private void modifVariable(String nomToken, Token nvValeur) {//modifie la variable dans la mémoire. Si pas d'erreur, return "", si la variable n'existe pas, si est d'un autre type, erreur
		variables.get(nomToken).setValeur(getTokenValeur(nvValeur));
	}
	
	private Constante calculArithmetique(Token gauche, Token token, Token droite) {
		Number numGauche=getTokenValeur(gauche);
		Number numDroite=getTokenValeur(droite);
		Number resultat;
	    resultat = numGauche.doubleValue() + numDroite.doubleValue();//conversion ensuite en fonction du type à assigner
		return new Constante(resultat);

	}
	
	private Number getTokenValeur(Token token) {
		if (token instanceof Variable)
			return variables.get(token.getNom()).getValeur();
		return ((Constante)token).getValeur();
	}

	private String standardErrorMessage(String attendu, String trouve) {
		return "Erreur, token '"+attendu+"' attendu, mais "+trouve+" trouvé";
	}
	
}