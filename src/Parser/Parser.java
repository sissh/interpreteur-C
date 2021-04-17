package Parser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import Tokens.*;

/**
 * Classe d'ex�cution du code
 * @author Alexis Fondet
 *
 */
//Pour plus d'informations, ou des conseils sur la continuation du projet : alexisfondet@gmail.com
public class Parser{

	/**
	 * Variables courantes.
	 */
	private HashMap<String, Variable> variables;
	/**
	 * Les tokens de la ligne � interpr�ter.
	 */
	private ArrayList<Token> ligne;
	/**
	 * Terme g�n�rique pour communiquer � l'interface que la ligne entr�e est vide, donc que l'ex�cution est termin�e. Attention ligne avec seulement ';', g�rer �a dans Code.
	 */
	public static String FIN_EXEC = "Fin d'ex�cution";

	/**
	 * Constructeur du Parser. Initialise les variables.
	 */
	public Parser() {
		variables = new HashMap<String, Variable>();
		ligne = new ArrayList<Token>();
	}
	
	/**
	 * Algorithme central de l'application. D�termine Les actions � faire � partir d'une ligne de code, et de l'�tat d'une m�moire donn�e. Contacter le concepteur (moi) pour plus de questions.
	 * @param nvLigne La ligne � interpr�ter.
	 * @param nvVariables Les variables � utiliser, puis modifier et renvoyer.
	 * @return Les variables re�ues, apr�s modification.
	 */
	public Object execution(ArrayList<Token> nvLigne, HashMap<String, Variable> nvVariables) {
		variables= nvVariables;//copie, car plusieurs m�thodes utilisent l'objet global "variables".
		ligne=nvLigne;//idem. ATTENTION. Notez que l'objet "Code" appelant voit aussi sa variable modifi�e, donc il faut toujours la vider compl�tement, sous peine de bugs. Ou alors la mettre � z�ro dans Code. Ou alors transmettre une copie. Aucune importance, c'est un choix de conception comme un autre.
		if (ligne.size()==0)//Si la ligne est vide d�s le d�but, programme termin�.
			return FIN_EXEC;
		if (ligne.get(0) instanceof Type && ligne.size()>1) {//D�claration de variable
			if (ligne.get(1) instanceof Variable) {
				boolean ok = declareVariable((Variable)ligne.get(1));
				if (!ok)
					return "La variable "+ligne.get(1).getNom()+" a d�j� �t� d�clar�e pr�c�demment";
				variables.get(ligne.get(1).getNom()).setType(ligne.get(0).getNom());//assigne le type
				ligne.remove(0);//suppression du token Type
				if (ligne.size()==1){//si d�claration sans assignation de valeur
					ligne.remove(0);//supprime le token Variable
					return variables;
				}
			}
			else if (ligne.get(1).getNom().equals("*") && (ligne.size()==3 || ligne.size()==5 || ligne.size()==6)) {//d�claration de pointeur
				if (ligne.get(2) instanceof Variable) {//un pointeur est identifi� comme une variable classique
					boolean ok = declareVariable(new Pointeur(ligne.get(2).getNom(), ligne.get(0).getNom()+"*"));
					if (!ok)
						return "La variable "+ligne.get(2).getNom()+" a d�j� �t� d�clar�e pr�c�demment";
					String courant = ligne.get(2).getNom();
					ligne.remove(0);ligne.remove(0);ligne.remove(0);//suppression de "[type] * [p]"
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
						else return "Mauvaise d�claration de pointeur";
						return variables;
					}
					else
						return standardErrorMessage("=", ligne.get(0).getNom());
				}
				else return standardErrorMessage("variable", ligne.get(1).getNom());
			}
			else return "Variable attendue apr�s une d�claration de type";
		}
		else if (ligne.get(0) instanceof Variable) {//assignation de valeur d'une variable d�j� d�clar�e
			if (!existe(ligne.get(0)))
				return "Variable "+ligne.get(0).getNom()+" non initialis�e pr�c�demment";
		}
		else if (ligne.get(0) instanceof TokenFonction) {//exemple : "printf("texte");"
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
				return "Attention ! Il est vivement conseill� de ne pas incr�menter une variable non instanci�e auparavant : "+ligne.get(1);
			incrementation(courant,(OpeUnaire)ligne.get(0));
			return variables;
		}
		else return "D�but d'expression interdite";
		String courant = ligne.get(0).getNom();//on r�cup�re le nom de la variable
		ligne.remove(0);
		
		Token signe =null;//pour assignation sign�e
		if (ligne.get(0) instanceof OpeUnaire && 1==ligne.size()){//ex : "a++;"
			if (variables.get(courant).getValeur()==null)
				return "Attention ! Il est vivement conseill� de ne pas incr�menter une variable non instanci�e auparavant : "+ligne.get(0);
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

		HashMap<String,OpeUnaire> calculSuffixe = calculUnaireSuffixe();//enregistre les variable � modifier post ex�cution.
		Object erreur = initCalcul();//lance les calculs sur toute la ligne.
		if (erreur instanceof String)
			return erreur.toString();
		if (ligne.size()!=1) {//� modifier, source de bugs futur (d�placer plus haut ?). Cas : "p = &a;" Vrai ? idem dans execFonction, et chaque fois qu'on aura un pointeur sur la ligne de code
			if (ligne.size()==2 && variables.get(courant) instanceof Pointeur && ligne.get(0).getNom().equals("&")) {//exemple : p=&a;, &a � la place de a / constante
				ligne.remove(0);
				if (variables.get(ligne.get(0).getNom()) instanceof Variable) {
					if (!ajoutPointeur(courant,ligne.get(0).getNom()))
						return "Il faut utiliser un pointeur correspondant au type de la variable correspondante";
					ligne.remove(0);
				}
				else
					return "Tentative de placer un pointeur sur une variable non d�clar�e auparavant";
			}
			else//il reste plus d'un token, mais il ne s'agit pas d'une assignation de la variable d'un pointeur, donc probl�me.
				return "Erreur lors de l'ex�cution";
			return variables;
		}
		else if (variables.get(ligne.get(0).getNom()) instanceof Pointeur) {//au lieu de "p=&a;", on a "p= pp;"
			if (!ajoutPointeur(courant, ((Pointeur)variables.get(ligne.get(0).getNom())).getDestination().getNom()))
				return "Il faut utiliser un pointeur correspondant au type de la variable correspondante";
			ligne.remove(0);
			return variables;
		}
			
		if (signe!=null)//assignation sign�e.
			ligne.set(0, calculArithmetique(variables.get(courant),signe, ligne.get(0)));
		modifVariable(courant, ligne.get(0));//on modifie dans la m�moire la variable � modifier, d'apr�s le r�sultat des calculs.
		//il n'est pas possible d'incr�menter via un pointeur pour le moment : "(*p)++"
		calculSuffixe(calculSuffixe);//on effectue les incr�mentations post ex�cution de la ligne de code
		ligne.remove(0);//suppresion de la constante finale
		return variables;
	}
	
	/**
	 * Permet de r�soudre les fonctions d'une section de code (toutes les fonctions d'une ligne si de 0 � ligne.size()).
	 * @param debut Indice de d�but des calculs sur la ligne.
	 * @param fin Indice de fin des calculs sur la ligne.
	 * @return Une String si erreur, la nouvelle fin sinon (= fin - le nombre de tokens retir�s de la ligne).
	 */
	private Object execFonctions(int debut, int fin) {
		int i=debut;
		while (i<fin) {//tant que toute la fonction n'a pas �t� r�solue
			if (ligne.get(i) instanceof TokenFonction) {
				String nomFonction = ligne.get(i).getNom();ligne.remove(i); fin-=1;
				if (i<fin && ligne.get(i).getNom().equals("(")) {//Le contraire ne peut pas arriver en th�orie, s�curit�
					ligne.remove(i); fin-=1;//�limination des tokens au fur et � mesure. Pour bien comprendre, faire des println(ligne)
					ArrayList <Object> parametres = new ArrayList <Object>();//param�tres � transmettre � la classe Fonctions
					int j=0;
					while (i+j<fin && !ligne.get(i+j).getNom().equals(")")) {//gestion des fonctions imbriqu�es : compter les parentheses
						int nbParentheses=0;//si d'autres fonctions et calculs de parenth�se imbriqu�s, pour ne pas se faire arr�ter par la mauvaise parenth�se fermante
						while (i+j<fin && i+j<ligne.size() && (nbParentheses>0 || (!ligne.get(i+j).getNom().equals(",") && !ligne.get(i+j).getNom().equals(")")))){//erreur si parenth�ses rajout�es au hasard
							if (ligne.get(i+j).getNom().equals("("))
								nbParentheses++;
							else if (ligne.get(i+j).getNom().equals(")"))
								nbParentheses--;
							j++;//j est utilis�, car i est le d�but de la nouvelle fonction, et j un offset, qui est plac� sur la fin de la fonction (parenth�se fermante)
						}
						if (i+j==fin)//La boucle s'est termin�e car on a atteint la fin, "pow(2, ;", ou autre
							return standardErrorMessage("param�tre", "rien");
						else if (ligne.get(i+j).getNom().equals(",") || ligne.get(i+j).getNom().equals(")")) {//autres cas possibles ?
							Object nvFin = calculLigne(i,i+j-1);//i+j pointe sur ',' ou ')', donc -1
							if (nvFin instanceof String)//si erreur
								return nvFin.toString();
							else if ((int)nvFin!=i) {//si le r�sultat des calculs n'est pas une constante
								if(ligne.get(i).getNom().equals("&")) {//envoie de l'adresse m�moire d'une variable (puis modification directe dans la classe Fonctions)
									ligne.remove(i);fin--;
									if (ligne.get(i) instanceof Variable) {
										parametres.add(variables.get(ligne.get(i).getNom()));//on transmet la variable comme param�tre
									}
									else
										return "Il faut placer une variable apr�s l'op�rateur '&', vous avez mis : "+ligne.get(i);
								}
								else if (ligne.get(i).getNom().equals("*")) {//on r�cup�re la variable associ�e � un pointeur
									ligne.remove(i);fin--;
									if (variables.get(ligne.get(i).getNom()) instanceof Pointeur) {
										parametres.add(getTokenValeur(((Pointeur)variables.get(ligne.get(i).getNom())).getDestination()));
									}
									else
										return "Il faut placer un pointeur apr�s l'op�rateur '*', vous avez mis : '"+ligne.get(i).getNom();
								}
								else
									return "Vous avez oubli� un param�tre dans la fonction "+nomFonction+", ou virgule en trop";
							}
							else if (ligne.get(i) instanceof Phrase) {//� modifier plus tard quand tableaux int�gr�s, et convertir pr�-processeur (makeTokens) en un objet contenant le tableau de caract�re, g�r� de mani�re g�n�rique avec les autres types. (je me suis fait comprendre ? Me contacter sinon, important)
								parametres.add(ligne.get(i).getNom());
							}
							else//si le r�sultat des calculs n'est rien de tout cela, c'est donc une constante. On peut rajouter une condition pour la s�curit�, mais ne pose normalement pas de probl�me.
								parametres.add(getTokenValeur(ligne.get(i)));
							ligne.remove(i);
							fin-=j;
							if (ligne.get(i).getNom().equals(","))
								ligne.remove(i);
							else
								fin--;
							j=0;
						}
					}//la recherche des param�tres est finie
					if (i==fin && !ligne.get(i).getNom().equals(")"))//le programme s'est fini sans trouver la parenth�se de fin de la fonction.
						return standardErrorMessage("param�tre' ou ')", "rien");
					Object resultat = Fonctions.execFonction(nomFonction, parametres);//envoi des param�tres � la classe Fonctions, et r�cup�ration du r�sultat
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
	
	/**
	 * Liste des variables � incr�menter.
	 * @return La liste des variables � incr�menter.
	 */
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
	
	/**
	 * Calcul incr�mental unaire des variables avant l'ex�cution de l'algorithme
	 */
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
	
	/**
	 * Utilis� avant le calcul unaire pour d�tecter les erreurs de l'utilisateur.
	 * @return Une String si erreur, null sinon.
	 */
	private Object errorCalculUnaire() {//si tentative d'incr�menter constantes. Pourrait �tre supprim� et int�gr� � calculUnairePrefixe(), utilis� qu'avec et une fois dans initCalcul()
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
	
	/**
	 * V�rifie si l'utilisateur tente de faire un calcul en utilisant des variables non instanci�es.
	 * @return Une String si erreur, null sinon.
	 */
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
	
	/**
	 * Effectue une it�ration d'incr�mentation/d�cr�mentation de variable.
	 * @param nomVariable Le nom de la variable � incr�menter en m�moire.
	 * @param ope Le signe de l'incr�mentation : ++ / --.
	 */
	private void incrementation(String nomVariable, OpeUnaire ope) {
		int plusmoins;
		if (ope.getNom().equals("++"))
			plusmoins=1; 
		else
			plusmoins=-1;
		variables.get(nomVariable).setValeur(variables.get(nomVariable).getValeur().doubleValue()+plusmoins);
	}
	
	/**
	 * Fonction utilis�e � la fin de l'ex�cution de l'algorithme, effectue l'incr�mentation des variables.
	 * @param calculSuffixe Variables � incr�menter.
	 */
	private void calculSuffixe(HashMap<String,OpeUnaire> calculSuffixe) {
		Iterator<String> iterateur = calculSuffixe.keySet().iterator();
		String nomValeur;
		while (iterateur.hasNext()) {
			nomValeur=iterateur.next();
			incrementation(nomValeur, calculSuffixe.get(nomValeur));
		}
	}
	
	/**
	 * Effectue les calculs de multiplication, division et modulo sur une section de la ligne.
	 * @param debut Indice de d�but des calculs sur la ligne.
	 * @param fin Indice de fin des calculs sur la ligne.
	 * @return Une String si erreur, la nouvelle fin sinon (= fin - le nombre de tokens retir�s de la ligne).
	 */
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
	
	/**
	 * Effectue les calculs d'addition et soustraction sur une section de la ligne.
	 * @param debut Indice de d�but des calculs sur la ligne.
	 * @param fin Indice de fin des calculs sur la ligne.
	 * @return Une String si erreur, la nouvelle fin sinon (= fin - le nombre de tokens retir�s de la ligne).
	 */
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
	
	/**
	 * Lance les diff�rentes fonctions de calcul sur une section de la ligne.
	 * @param debut Indice de d�but des calculs sur la ligne.
	 * @param fin Indice de fin des calculs sur la ligne.
	 * @return Une String si erreur, la nouvelle fin sinon (= fin - le nombre de tokens retir�s de la ligne).
	 */
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
	
	/**
	 * Lance tous les calculs effectu�s sur une ligne. Wrapper.
	 * @return Une String si erreur, la nouvelle fin sinon (= fin - le nombre de tokens retir�s de la ligne).
	 */
	private Object initCalcul() {
		Object erreur=errorCalculUnaire();
		if (erreur instanceof String)
			return erreur.toString();//
		
		calculUnairePrefixe();
		erreur = verifNullVariables();
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
	
	/**
	 * Effectue les calculs de parenth�se sur une section du code.
	 * @param debut Indice de d�but des calculs sur la ligne.
	 * @return Une String si erreur, la nouvelle fin sinon (= fin - le nombre de tokens retir�s de la ligne).
	 */
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

	/**
	 * Pour d�terminer si une variable est d�clar�e en m�moire.
	 * @param token Le token � tester, pris sur la ligne.
	 * @return true si oui, false sinon.
	 */
	private boolean existe(Token token) {//si la variable (avec son type) n'existe pas dans la m�moire, false, sinon true
		return variables.containsKey(token.getNom());
	}
	
	/**
	 * Pour d�terminer si une variable d�clar�e est instanci�e.
	 * @param token Le token � tester, pris sur la ligne.
	 * @return true si oui, false sinon.
	 */
	private boolean estInstancie(Token token) {
		if (existe(token)) {
			if (variables.get(token.getNom()).getValeur()==null && !(variables.get(token.getNom()) instanceof Pointeur && ((Pointeur)variables.get(token.getNom())).getDestination()!=null ))
				return false;
			return true;
		}
		return false;
	}

	/**
	 * Pour d�clarer une variable en m�moire.
	 * @param token Le token � d�clarer.
	 * @return true si l'op�ration s'est effectu�e, false sinon.
	 */
	private boolean declareVariable(Variable token) {//ajoute dans la m�moire la variable. Si pas d'erreur, return "", si la variable existe d�j�, erreur
		if (existe(token))
			return false;
		variables.put(token.getNom(), token);
		return true;
	}
	
	/**
	 * Modifie la valeur d'une variable en m�moire.
	 * @param nomToken Le nom du token � modifier.
	 * @param nvValeur La valeur � attribuer au Token.
	 */
	private void modifVariable(String nomToken, Token nvValeur) {//Dans l'�tat actuel, utilis� seulement une fois, de mani�re s�curis�e. Mais on peut renvoyer un bool�en si n�cessaire.
		variables.get(nomToken).setValeur(getTokenValeur(nvValeur));
	}
	
	/**
	 * Effectue le calcul entre deux tokens : addition, soustraction, multiplication, division, modulo.
	 * @param gauche Le token de gauche.
	 * @param operateur Le type d'op�ration � effectuer.
	 * @param droite Le token de droite.
	 * @return Un token de type {@link Tokens.Constante Constante}, correspondant au r�sultat de l'op�ration.
	 */
	private Constante calculArithmetique(Token gauche, Token operateur, Token droite) {
		Number numGauche=getTokenValeur(gauche);
		Number numDroite=getTokenValeur(droite);
		Number resultat;
		if (operateur.getNom().equals("+")) {
			if (numGauche instanceof Double || numDroite instanceof Double)
				resultat = numGauche.doubleValue() + numDroite.doubleValue();
			else if (numGauche instanceof Float || numDroite instanceof Float)
				resultat = numGauche.floatValue() + numDroite.floatValue();
			else if (numGauche instanceof Long || numDroite instanceof Long)
				resultat = numGauche.longValue() + numDroite.longValue();
			else
				resultat = numGauche.intValue() + numDroite.intValue();
		}
		else if (operateur.getNom().equals("-")) {
			if (numGauche instanceof Double || numDroite instanceof Double)
				resultat = numGauche.doubleValue() - numDroite.doubleValue();
			else if (numGauche instanceof Float || numDroite instanceof Float)
				resultat = numGauche.floatValue() - numDroite.floatValue();
			else if (numGauche instanceof Long || numDroite instanceof Long)
				resultat = numGauche.longValue() - numDroite.longValue();
			else
				resultat = numGauche.intValue() - numDroite.intValue();
		}
		else if (operateur.getNom().equals("*")) {
			if (numGauche instanceof Double || numDroite instanceof Double)
				resultat = numGauche.doubleValue() * numDroite.doubleValue();
			else if (numGauche instanceof Float || numDroite instanceof Float)
				resultat = numGauche.floatValue() * numDroite.floatValue();
			else if (numGauche instanceof Long || numDroite instanceof Long)
				resultat = numGauche.longValue() * numDroite.longValue();
			else
				resultat = numGauche.intValue() * numDroite.intValue();
		}
		else if (operateur.getNom().equals("/")) {
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
	
	/**
	 * Assigne � un pointeur une variable.
	 * @param pointeur Le nom du pointeur.
	 * @param destination Le nom de la variable.
	 * @return true si pas d'erreur, false sinon.
	 */
	private boolean ajoutPointeur(String pointeur, String destination) {
		String typePointeur = variables.get(pointeur).getType().substring(0, variables.get(pointeur).getType().length()-1);
		if (!typePointeur.equals(variables.get(destination).getType()))
			return false;
		((Pointeur)variables.get(pointeur)).setDestination(variables.get(destination));
		return true;
	}
	
	/**
	 * Permet d'obtenir la valeur en m�moire d'un token de la ligne de code (valeur sur la ligne : null), ou d'une constante, par polymorphisme.
	 * @param token Le token dont on veut la valeur.
	 * @return La valeur du token en question.
	 */
	private Number getTokenValeur(Token token) {
		if (token instanceof Variable)
			return variables.get(token.getNom()).getValeur();
		return ((Constante)token).getValeur();
	}

	/**
	 * Une erreur standardis�e, utilis�e un peu partout dans l'algorithme.
	 * @param attendu Ce que l'on s'attendait � trouver.
	 * @param trouve Ce qui a �t� obtenu � la place.
	 * @return
	 */
	private String standardErrorMessage(String attendu, String trouve) {
		return "Erreur, token '"+attendu+"' attendu, mais "+trouve+" trouv�";
	}
	
}