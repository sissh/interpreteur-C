package Parser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import Tokens.*;

/**
 * Classe d'ex�cution du code
 * @author alexi
 *
 */
public class Parser{

	private HashMap<String, Variable> variables;
	private ArrayList<Token> ligne;
	public static String FIN_EXEC = "Fin d'ex�cution";

	public Parser() {
		variables = new HashMap<String, Variable>();
		ligne = new ArrayList<Token>();
	}
	
	public Object execution(ArrayList<Token> nvLigne, HashMap<String, Variable> nvVariables) {
		variables= nvVariables;
		ligne=nvLigne;
		if (ligne.size()==0)
			return FIN_EXEC;
		if (ligne.get(0) instanceof Type && ligne.size()>1) {
			if (ligne.get(1) instanceof Variable) {
				boolean ok = declareVariable((Variable)ligne.get(1));
				if (!ok)
					return "La variable "+ligne.get(1).getNom()+" a d�j� �t� d�clar�e pr�c�demment";
				variables.get(ligne.get(1).getNom()).setType(ligne.get(0).getNom());//assigne le type
				ligne.remove(0);
				if (ligne.size()==1){//si d�claration sans assignation de valeur
					ligne.remove(0);
					return variables;
				}
			}
			else if (ligne.get(1).getNom().equals("*") && (ligne.size()==3 || ligne.size()==5 || ligne.size()==6)) {
				if (ligne.get(2) instanceof Variable) {
					ligne.set(2, new Pointeur(ligne.get(2).getNom(), ligne.get(0).getNom()+"*"));
					boolean ok = declareVariable((Pointeur)ligne.get(2));
					if (!ok)
						return "La variable "+ligne.get(1).getNom()+" a d�j� �t� d�clar�e pr�c�demment";
					//variables.get(ligne.get(2).getNom()).setType(ligne.get(0).getNom()+"*");
					String courant = ligne.get(2).getNom();
					ligne.remove(0);ligne.remove(0);ligne.remove(0);
					if (ligne.size()==0)
						return variables;
					else if (ligne.get(0) instanceof Egal) {
						ligne.remove(0);
						if (ligne.get(0).getNom().equals("&") && ligne.size()==2) {
							ligne.remove(0);
							if (variables.get(ligne.get(0).getNom()) instanceof Variable) {
								if (!ajoutPointeur(courant,ligne.get(0).getNom()))
									return "Il faut utiliser un pointeur correspondant au type de la variable correspondante";
								ligne.remove(0);
							}
							else
								return "Tentative de placer un pointeur sur une variable non d�clar�e auparavant";
						}
						else if (variables.get(ligne.get(0).getNom()) instanceof Pointeur && ligne.size()==1) {
							if (!ajoutPointeur(courant, ((Pointeur)variables.get(ligne.get(0).getNom())).getDestination().getNom()))
								return "Il faut utiliser un pointeur correspondant au type de la variable correspondante";
							ligne.remove(0);
						}
						else return "Mauvaise d�claration de pointeur.";
						return variables;
					}
					else
						return standardErrorMessage("=", ligne.get(0).getNom());
				}
				else return standardErrorMessage("variable", ligne.get(1).getNom());
			}
			else return "Variable attendue apr�s une d�claration de type";
		}
		else if (ligne.get(0) instanceof Variable) {
			if (!existe(ligne.get(0)))
				return "Token "+ligne.get(0).getNom()+" non initialis� pr�c�demment";
		}
		else if (ligne.get(0) instanceof TokenFonction) {
			HashMap<String,OpeUnaire> calculSuffixe = calculUnaireSuffixe();
			Object erreur = initCalcul();
			if (erreur instanceof String)
				return erreur.toString();
			else if (ligne.size()!=1)
				return "Erreur lors de l'ex�cution";
			ligne.remove(0);
			calculSuffixe(calculSuffixe);
			return variables;
		}
		else if (ligne.get(0) instanceof OpeUnaire && ligne.size()==2 && ligne.get(1) instanceof Variable){//ex : "++a;", voir si possible de faire "++a=5;"
			String courant = ligne.get(1).getNom();
			if (variables.get(courant).getValeur()==null)
				return "Attention ! Il est vivement conseill� de ne pas incr�menter une variable non instanc�e auparavant "+ligne.get(1);
			incrementation(courant,(OpeUnaire)ligne.get(0));
			return variables;
		}
		else return "D�but d'expression interdite";
		String courant = ligne.get(0).getNom();
		ligne.remove(0);
		
		Token signe =null;//pour assignation sign�e
		if (ligne.get(0) instanceof OpeUnaire && 1==ligne.size()){//ex : "a++;"
			if (variables.get(courant).getValeur()==null)
				return "Attention ! Il est vivement conseill� de ne pas incr�menter une variable non instanc�e auparavant "+ligne.get(0);
			incrementation(courant,(OpeUnaire)ligne.get(0));
			return variables;
		}
		else if (ligne.get(0) instanceof Operateur_1 || ligne.get(0) instanceof Operateur_2) {
			if (ligne.size()!=1 && ligne.get(1) instanceof Egal)//assignation sign�e ex "a*=5;"
				signe = ligne.get(0);
			ligne.remove(0);
		}
		else if (!(ligne.get(0) instanceof Egal))//g�rer autres types d'assignation de valeur
			return standardErrorMessage("=", ligne.get(0).getNom());
		ligne.remove(0);

		HashMap<String,OpeUnaire> calculSuffixe = calculUnaireSuffixe();
		Object erreur = initCalcul();
		if (erreur instanceof String)
			return erreur.toString();
		if (ligne.size()!=1) {
			if (ligne.size()==2 && variables.get(courant) instanceof Pointeur && ligne.get(0).getNom().equals("&")) {//exemple : p=&a;, &a � la place de a / constante
				ligne.remove(0);
				if (variables.get(ligne.get(0).getNom()) instanceof Variable) {
					if (!ajoutPointeur(courant,ligne.get(0).getNom())) {
						return "Il faut utiliser un pointeur correspondant au type de la variable correspondante";}
					ligne.remove(0);
				}
				else
					return "Tentative de placer un pointeur sur une variable non d�clar�e auparavant";
			}
			else
				return "Erreur lors de l'ex�cution";
			return variables;
		}
		else if (variables.get(ligne.get(0).getNom()) instanceof Pointeur) {
			if (!ajoutPointeur(courant, ((Pointeur)variables.get(ligne.get(0).getNom())).getDestination().getNom()))
				return "Il faut utiliser un pointeur correspondant au type de la variable correspondante";
			ligne.remove(0);
			return variables;
		}
			
		if (signe!=null)
			ligne.set(0, calculArithmetique(variables.get(courant),signe, ligne.get(0)));
		modifVariable(courant, ligne.get(0));
		
		calculSuffixe(calculSuffixe);
		ligne.remove(0);//suppresion de la constante finale
		return variables;
	}
	
	private void calculSuffixe(HashMap<String,OpeUnaire> calculSuffixe) {
		Iterator<String> iterateur = calculSuffixe.keySet().iterator();
		String nomValeur;
		while (iterateur.hasNext()) {
			nomValeur=iterateur.next();
			incrementation(nomValeur, calculSuffixe.get(nomValeur));
		}
	}
	
	private Object execFonctions(int debut, int fin) {
		int i=debut;
		while (i<fin) {
			if (ligne.get(i) instanceof TokenFonction) {//faire calculLigne entre chaque virgule au lieu de gerer les cas particuliers
				String nomFonction = ligne.get(i).getNom();ligne.remove(i); fin-=1; //plus besoin donc de s'occuper des fonctions imbriquees
				if (i<fin && ligne.get(i).getNom().equals("(")) { //il faut gerer la valeur de fin cependant, qui va changer
					ligne.remove(i); fin-=1;
					ArrayList <Object> parametres = new ArrayList <Object>();
					int j=0;
					while (i+j<fin && !ligne.get(i+j).getNom().equals(")")) {//gestion des fonctions imbriqu�es : compter les parentheses
						int nbParentheses=0;
						while (i+j<fin && (nbParentheses>0 || (!ligne.get(i+j).getNom().equals(",") && !ligne.get(i+j).getNom().equals(")")))){//erreur potentielle si pow(,5) par ex
							if (ligne.get(i+j).getNom().equals("("))
								nbParentheses++;
							else if (ligne.get(i+j).getNom().equals(")"))
								nbParentheses--;
							j++;
						}
						if (i+j==fin)
							return standardErrorMessage("param�tre", "rien");
						else if (ligne.get(i+j).getNom().equals(",") || ligne.get(i+j).getNom().equals(")")) {//autres cas possibles ?
							Object nvFin = calculLigne(i,i+j-1);
							if (nvFin instanceof String)
								return nvFin.toString();
							else if ((int)nvFin!=i) {
								if(ligne.get(i).getNom().equals("&")) {
									ligne.remove(i);fin--;
									if (ligne.get(i) instanceof Variable) {
										parametres.add(variables.get(ligne.get(i).getNom()));
									}
									else
										return "Il faut placer une variable apr�s l'op�rateur '&', vous avez mis : "+ligne.get(i);
								}
								else if (ligne.get(i).getNom().equals("*")) {
									ligne.remove(i);fin--;
									if (variables.get(ligne.get(i).getNom()) instanceof Pointeur) {
										parametres.add(getTokenValeur(((Pointeur)variables.get(ligne.get(i).getNom())).getDestination()));
									}
									else
										return "Il faut placer un pointeur apr�s l'op�rateur '*', vous avez mis : '"+ligne.get(i).getNom()+"', qui est de type : "+ligne.get(i).getClass();
								}
								else
									return "Vous avez oubli� un param�tre dans la fonction "+nomFonction+", ou virgule en trop";
							}
							else if (ligne.get(i) instanceof Phrase) {
								parametres.add(ligne.get(i).getNom());
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
					if (i==fin && !ligne.get(i).getNom().equals(")"))
						return standardErrorMessage("param�tre' ou ')", "rien");
					Object resultat = Fonctions.execFonction(nomFonction, parametres);
					if (resultat instanceof String)
						return resultat.toString();
					ligne.set(i, new Constante((Number)resultat));// � la place du Token ')'
					
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
	
	private Object errorCalculUnaire() {//si tentative d'incr�menter constantes
		int i=0;
		while (i<ligne.size()) {
			if (!(ligne.get(i++) instanceof Variable)) {//double boucle inutile, on peut g�rer les deux en m�me temps
				if (i<ligne.size() && ligne.get(i) instanceof OpeUnaire) {
					return "Tentative d'incr�menter un Token qui n'est pas une variable";
				}
				else i++;
			}
		}
		i=0;
		while (i<ligne.size()) {
			if (ligne.get(i++) instanceof OpeUnaire) {
				if (i<ligne.size() && !(ligne.get(i) instanceof Variable)) {
					return "Tentative d'incr�menter un Token qui n'est pas une variable";
				}
				else i++;
			}
		}
		return null;
	}
	
	private Object verifNullVariables() {
		for (int i=0; i<ligne.size(); i++) {
			if (ligne.get(i).getNom().equals("\""))
			if (ligne.get(i) instanceof Variable && !estInstancie(ligne.get(i))) {
				if (!ligne.get(i-1).getNom().equals("%"))//ex : printf("%d",a);
					return "Attention, il est vivement d�conseill� d'utiliser une variable non instanci�e auparavant : "+ligne.get(i);
			}
		}
		return null;
	}
	
	private void incrementation(String nomVariable, OpeUnaire ope) {
		int plusmoins;
		if (ope.getNom().equals("++"))
			plusmoins=1; 
		else
			plusmoins=-1;
		variables.get(nomVariable).setValeur(variables.get(nomVariable).getValeur().doubleValue()+plusmoins);
	}
	
	private Object calculLigne1(int debut, int fin) {//calcul des multiplications, divisions, modulo
		int i;
		boolean continuer=true;
		while (debut<fin && continuer) {//erreurs potentielles si tokens en trop, � verifier
			i=debut;
			continuer = false;
			while (i<fin) {
				if (ligne.get(i) instanceof Variable || ligne.get(i) instanceof Constante) {
					i++;
					if (i<fin && ligne.get(i) instanceof Operateur_1) {
						continuer=true;//tant qu'il reste des tokens � ex�cuter, continuer
						if (i+1<ligne.size()) {
							if (ligne.get(i+1) instanceof Variable || ligne.get(i+1) instanceof Constante) {
								ligne.set(i-1, calculArithmetique(ligne.get(i-1),ligne.get(i),ligne.get(i+1)));
							ligne.remove(i);ligne.remove(i); i=debut;fin-=2;							
							}
							else return standardErrorMessage("variable ou constante", ligne.get(i+1).getNom());
						}
						else return "Erreur sur la ligne, probablement un Token mal plac� ou en trop";//ex : "int a= 1+2 * ;"
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
						continuer=true;//tant qu'il reste des tokens � ex�cuter, continuer
						if (i+1<ligne.size()) {
							if (ligne.get(i+1) instanceof Variable || ligne.get(i+1) instanceof Constante) {
							ligne.set(i-1, calculArithmetique(ligne.get(i-1),ligne.get(i),ligne.get(i+1)));
							ligne.remove(i);ligne.remove(i); i=debut;fin-=2;							
							}
							else return standardErrorMessage("variable ou constante", ligne.get(i+1).getNom());
						}
						else return "Erreur sur la ligne, probablement un Token mal plac� ou en trop";//ex : "int a= 1+2 * ;"
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
		Object erreur=errorCalculUnaire();
		if (erreur instanceof String)
			return erreur.toString();//
		
		calculUnairePrefixe();
		erreur = verifNullVariables(); //� corriger, car peut creer des erreurs si non utilise
		if (erreur instanceof String)
			return erreur.toString();
		int i =0;
		while (i<ligne.size()) {
			if (ligne.get(i).getNom().equals("(")) {
				if (i==0 || !(ligne.get(i-1) instanceof TokenFonction)) {
					ligne.remove(i);
					erreur = calculParentheses(i);
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
		return "Parenth�se ouverte non ferm�e";
	}

	private boolean existe(Token token) {//si la variable (avec son type) n'existe pas dans la m�moire, false, sinon true
		return variables.containsKey(token.getNom());
	}
	
	private boolean estInstancie(Token token) {
		if (existe(token)) {
			if (variables.get(token.getNom()).getValeur()==null && !(variables.get(token.getNom()) instanceof Pointeur && ((Pointeur)variables.get(token.getNom())).getDestination()!=null ))
				return false;
			return true;
		}
		return false;
	}

	private boolean declareVariable(Variable token) {//ajoute dans la m�moire la variable. Si pas d'erreur, return "", si la variable existe d�j�, erreur
		if (variables.containsKey(token.getNom()))
			return false;
		variables.put(token.getNom(), token);
		return true;
	}
	
	private void modifVariable(String nomToken, Token nvValeur) {//modifie la variable dans la m�moire. Si pas d'erreur, return "", si la variable n'existe pas, si est d'un autre type, erreur
		variables.get(nomToken).setValeur(getTokenValeur(nvValeur));
	}
	
	private Constante calculArithmetique(Token gauche, Token token, Token droite) {
		Number numGauche=getTokenValeur(gauche);
		Number numDroite=getTokenValeur(droite);
		Number resultat;
		if (token.getNom().equals("+")) {
			if (numGauche instanceof Double || numDroite instanceof Double)
				resultat = numGauche.doubleValue() + numDroite.doubleValue();
			else if (numGauche instanceof Float || numDroite instanceof Float)
				resultat = numGauche.floatValue() + numDroite.floatValue();
			else if (numGauche instanceof Long || numDroite instanceof Long)
				resultat = numGauche.longValue() + numDroite.longValue();
			else
				resultat = numGauche.intValue() + numDroite.intValue();
		}
		else if (token.getNom().equals("-")) {
			if (numGauche instanceof Double || numDroite instanceof Double)
				resultat = numGauche.doubleValue() - numDroite.doubleValue();
			else if (numGauche instanceof Float || numDroite instanceof Float)
				resultat = numGauche.floatValue() - numDroite.floatValue();
			else if (numGauche instanceof Long || numDroite instanceof Long)
				resultat = numGauche.longValue() - numDroite.longValue();
			else
				resultat = numGauche.intValue() - numDroite.intValue();
		}
		else if (token.getNom().equals("*")) {
			if (numGauche instanceof Double || numDroite instanceof Double)
				resultat = numGauche.doubleValue() * numDroite.doubleValue();
			else if (numGauche instanceof Float || numDroite instanceof Float)
				resultat = numGauche.floatValue() * numDroite.floatValue();
			else if (numGauche instanceof Long || numDroite instanceof Long)
				resultat = numGauche.longValue() * numDroite.longValue();
			else
				resultat = numGauche.intValue() * numDroite.intValue();
		}
		else if (token.getNom().equals("/")) {
			if (numGauche instanceof Double || numDroite instanceof Double)
				resultat = numGauche.doubleValue() / numDroite.doubleValue();
			else if (numGauche instanceof Float || numDroite instanceof Float)
				resultat = numGauche.floatValue() / numDroite.floatValue();
			else if (numGauche instanceof Long || numDroite instanceof Long)
				resultat = numGauche.longValue() / numDroite.longValue();
			else
				resultat = numGauche.intValue() / numDroite.intValue();
		}
		else{//%
			if (numGauche instanceof Long || numDroite instanceof Long)
				resultat = numGauche.longValue() % numDroite.longValue();
			else if (numGauche instanceof Integer || numDroite instanceof Integer)
				resultat = numGauche.intValue() % numDroite.intValue();
			else
				resultat=null;//il n'est pas possible d'utiliser % avec float ou double, � faire : gestion d'erreur
		}
		
		return new Constante(resultat);
	}
	
	private boolean ajoutPointeur(String pointeur, String destination) {
		String typePointeur = variables.get(pointeur).getType().substring(0, variables.get(pointeur).getType().length()-1);
		if (!typePointeur.equals(variables.get(destination).getType()))
			return false;
		((Pointeur)variables.get(pointeur)).setDestination(variables.get(destination));
		return true;
	}
	
	private Number getTokenValeur(Token token) {
		if (token instanceof Variable)
			return variables.get(token.getNom()).getValeur();
		return ((Constante)token).getValeur();
	}

	private String standardErrorMessage(String attendu, String trouve) {
		return "Erreur, token '"+attendu+"' attendu, mais "+trouve+" trouv�";
	}
	
}