package Parser;
import java.util.ArrayList;
import java.util.HashMap;
import Tokens.*;


public class Parser{

	private HashMap<String, Variable> variables;
	private ArrayList<Token> ligne;
	
	public Parser(HashMap<String, Variable> variables) {
		this.variables = variables;
		this.ligne = new ArrayList<Token>();
	}
	
	public Object execution(ArrayList<Token> nvLigne) {//retourne l'array de variables si ok
		int i=0;
		ligne=nvLigne;
		if (nvLigne.get(i) instanceof Type) {
			if (ligne.get(i+1) instanceof Variable) {
				((Variable)(ligne.get(i+1))).setType((Type)ligne.get(i));
				boolean ok = declareVariable((Variable)ligne.get(i+1));
				if (!ok)
					return "La variable "+ligne.get(i+1).getNom()+" a déjà été initialisée précédemment";
			}
			ligne.remove(i);
		}
		else if (ligne.get(i) instanceof Variable) {
			if (!existe(ligne.get(i)))
				return "Token "+ligne.get(i).getNom()+" non initialisé précédemment";
		}
		else return "Début d'expression interdite";
		
		Variable courant = (Variable)ligne.get(i);
		ligne.remove(i);
		
		if (!(ligne.get(i) instanceof Egal))//gérer autres types d'assignation de valeur
			return "Token "+ligne.get(i).getNom()+" inattendu, le programme devait trouver un \"=\"";
		ligne.remove(i);

		ArrayList<String> calculSuffixe= calculUnaireSuffixe();
		calculUnairePrefixe();
		String erreur = execFonctions();
		if (!erreur.equals(""))
			return erreur;
		System.out.println("courant : "+courant+"\nligne : "+ligne+"\n------\n\n");
		calculLigne1();
		calculLigne2();
		System.out.println("courant : "+courant+"\nligne : "+ligne+"\n------\n\n");
		System.out.println(variables);
		modifVariable(courant, ligne.get(i));//assignation
		System.out.println(variables);
		for (int j=0; j<calculSuffixe.size();j++ ) {
			String nomamodifier = calculSuffixe.get(j);
			variables.get(calculSuffixe.get(j)).setValeur(((int)variables.get(nomamodifier).getValeur())+1);//incrémentation dans la mémoire
		}
		return variables;
	}

	private Object compute(ArrayList<Token> setTokens, int i, boolean start) {
		ArrayList<Token> tokens= new ArrayList<Token>();
		if (start) {
			while(!setTokens.get(i).getNom().equals(";"))
				tokens.add(setTokens.get(i++));
		}
		else {
			while(i<setTokens.size() || setTokens.get(i).getNom().equals(")"))
				tokens.add(setTokens.get(i++));
			if (!setTokens.get(i).getNom().equals(")"))
				return "Parenthèse ouverte non fermée";
		}
		//int indiceTokens = 0;
		//while () {
			
		//}
		return calculLigne(tokens);
	}
	
	public String execFonctions() {//de nombreuses erreurs à gérer
		int i=0;
		while (i<ligne.size()) {
			if (ligne.get(i) instanceof TokenFonction) {
				String nomFonction = ligne.get(i++).getNom();
				if (i<ligne.size() && ligne.get(i).getNom().equals("(")) {
					ligne.remove(i);
					ArrayList <Object> parametres = new ArrayList <Object>();
					while (i<ligne.size() && !ligne.get(i).getNom().equals(")")) {
						if (ligne.get(i) instanceof Variable) {
							parametres.add( ((Variable)ligne.get(i)).getValeur() );
							ligne.remove(i);
							if (ligne.get(i).getNom().equals(","))
								ligne.remove(i);
							else System.out.println("erreur 88");
						}
						else if (ligne.get(i) instanceof Constante) {
							parametres.add(((Constante)ligne.get(i)).getValeur());
							ligne.remove(i);
							if (ligne.get(i).getNom().equals(","))
								ligne.remove(i);
							else System.out.println("erreur 95");
						}
						else System.out.println("erreur 97");
						
					}
					if (!ligne.get(i).getNom().equals(")"))
						System.out.println("erreur 101");
					Object resultat = Fonctions.execFonction(nomFonction, parametres);
					if (resultat instanceof String)
						return (String)resultat;
					ligne.set(i, new Constante(resultat));
				}
				else System.out.println("erreur, parenthèse attendue après une fonction");//fonction sans parenthèses
			}
			i++;
		}
		return "";
	}
	
	public ArrayList<String> calculUnaireSuffixe(){
		ArrayList<String> variablesAIncrementer = new ArrayList<String>();
		int i=0;
		while (i<ligne.size()) {
			if (ligne.get(i++) instanceof Variable) {
				if (i<ligne.size() && ligne.get(i) instanceof OpeUnaire) {
					ligne.remove(i);
					variablesAIncrementer.add(ligne.get(i-1).getNom());
				}
				else i++;
			}
		}
		return variablesAIncrementer;
	}
	
	public void calculUnairePrefixe(){
		int i=0;
		while (i<ligne.size()) {
			if (ligne.get(i++) instanceof OpeUnaire) {
				if (i<ligne.size() && ligne.get(i) instanceof Variable) {
					((Variable)ligne.get(i)).setValeur((int)(((Variable)ligne.get(i)).getValeur())+1);//incrémentation locale, pour calculs de ligne					
					variables.get(ligne.get(i).getNom()).setValeur(((Variable)ligne.get(i)).getValeur());//incrémentation dans la mémoire
					ligne.remove(i-1);
				}
				else i++;
			}
		}
	}
	
	public void calculLigne1 () {//calcul des multiplications, divisions, modulo
		int i=0;
		boolean continuer=true;
		while (i<ligne.size() && continuer) {
			i=0;
			continuer = false;
			while (i<ligne.size()) {
				if (ligne.get(i) instanceof Variable) {
					i++;
					if (i<ligne.size() && ligne.get(i) instanceof Operateur_1) {
						continuer=true;
						if (i<ligne.size() && ligne.get(i+1) instanceof Variable) {
							ligne.set(i-1, calculArithmetique(((Variable)ligne.get(i-1)).getValeur(),ligne.get(i),((Variable)ligne.get(i+1)).getValeur()));
							ligne.remove(i);ligne.remove(i); i=0;
						}
						else if (i<ligne.size() && ligne.get(i+1) instanceof Constante) {
							ligne.set(i-1, calculArithmetique(((Variable)ligne.get(i-1)).getValeur(),ligne.get(i),((Constante)ligne.get(i+1)).getValeur()));
							ligne.remove(i);ligne.remove(i); i=0;
						}
						else System.out.println("erreur, ou parenthèse pas encore gérée");
					}
					else i++;
				}
				else if (ligne.get(i) instanceof Constante) {
					i++;
					if (i<ligne.size() && ligne.get(i) instanceof Operateur_1) {
						continuer=true;
						if (i<ligne.size() && ligne.get(i+1) instanceof Variable) {
							ligne.set(i-1, calculArithmetique(((Constante)ligne.get(i-1)).getValeur(),ligne.get(i),((Variable)ligne.get(i+1)).getValeur()));
							ligne.remove(i);ligne.remove(i); i=0;
						}
						else if (i<ligne.size() && ligne.get(i+1) instanceof Constante) {
							ligne.set(i-1, calculArithmetique(((Constante)ligne.get(i-1)).getValeur(),ligne.get(i),((Constante)ligne.get(i+1)).getValeur()));
							ligne.remove(i);ligne.remove(i); i=0;
						}
						else System.out.println("erreur, ou parenthèse pas encore gérée");
					}
					else i++;
				}
				else i++;
			}
		}
	}
	
	public void calculLigne2 () {//calcul des multiplications, divisions, modulo
		int i=0;
		boolean continuer=true;
		while (i<ligne.size() && continuer) {
			i=0;
			continuer = false;
			while (i<ligne.size()) {
				if (ligne.get(i) instanceof Variable) {
					i++;
					if (i<ligne.size() && ligne.get(i) instanceof Operateur_2) {
						continuer=true;
						if (i<ligne.size() && ligne.get(i+1) instanceof Variable) {
							ligne.set(i-1, calculArithmetique(((Variable)ligne.get(i-1)).getValeur(),ligne.get(i),((Variable)ligne.get(i+1)).getValeur()));
							ligne.remove(i);ligne.remove(i); i=0;
						}
						else if (i<ligne.size() && ligne.get(i+1) instanceof Constante) {
							ligne.set(i-1, calculArithmetique(((Variable)ligne.get(i-1)).getValeur(),ligne.get(i),((Constante)ligne.get(i+1)).getValeur()));
							ligne.remove(i);ligne.remove(i); i=0;
						}
						else System.out.println("erreur, ou parenthèse pas encore gérée");
					}
					else i++;
				}
				else if (ligne.get(i) instanceof Constante) {
					i++;
					if (i<ligne.size() && ligne.get(i) instanceof Operateur_2) {
						continuer=true;
						if (i<ligne.size() && ligne.get(i+1) instanceof Variable) {
							ligne.set(i-1, calculArithmetique(((Constante)ligne.get(i-1)).getValeur(),ligne.get(i),((Variable)ligne.get(i+1)).getValeur()));
							ligne.remove(i);ligne.remove(i); i=0;
						}
						else if (i<ligne.size() && ligne.get(i+1) instanceof Constante) {
							ligne.set(i-1, calculArithmetique(((Constante)ligne.get(i-1)).getValeur(),ligne.get(i),((Constante)ligne.get(i+1)).getValeur()));
							ligne.remove(i);ligne.remove(i); i=0;
						}
						else System.out.println("erreur, ou parenthèse pas encore gérée");
					}
					else i++;
				}
				else i++;
			}
		}
	}
		

	private Object calculLigne(ArrayList<Token> setTokens) {//création d'un arbre binaire de calcul
		int i=0;
		Object temp=0;// gestion du type plus tard
		// gestion du premier token de la ligne
		if (setTokens.get(i) instanceof Variable || setTokens.get(i) instanceof Constante) 
			temp=calculArithmetique(temp, new Operateur_1("+"), setTokens.get(i++));
		else if (setTokens.get(i).getNom().equals("(")){
			i++;
			temp=compute(setTokens, i, false);
		}
		else
			System.out.println("erreur, il faut interrompre");
		while (i<setTokens.size()) {
			if (setTokens.get(i) instanceof Operateur_1) {
				i++;
				if (setTokens.get(i) instanceof Variable || setTokens.get(i) instanceof Constante) {
					temp=calculArithmetique(temp, (Operateur_1)setTokens.get(i-1), setTokens.get(i));
				}
				else if (setTokens.get(i).getNom().equals("(")){
					i++;
					temp=compute(setTokens, i, false);
				}
				else
					System.out.println("erreur, il faut interrompre");
			}
		}
		return temp;
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
	
	private void modifVariable(Variable token, Object nvValeur) {//modifie la variable dans la mémoire la variable. Si pas d'erreur, return "", si la variable n'existe pas, si est d'un autre type, erreur
		token.setValeur(nvValeur);
		variables.put(token.getNom(),token);
	}
	
	private Constante calculArithmetique(Object gauche, Token token, Object droite) {
		//int droite = 0;//gestion types
		/*if (token instanceof Variable)
			droite= (int)((Variable) token).getValeur();
		
		else if (token instanceof Constante)
			droite= (int)((Constante) token).getValeur();*/
		int resultat = 0;
		if (token.getNom().equals("+"))
			resultat = (int)gauche+(int)droite;
		else if (token.getNom().equals("-"))
			resultat = (int)gauche-(int)droite;
		else if (token.getNom().equals("*"))
			resultat = (int)gauche*(int)droite;
		else if (token.getNom().equals("/"))
			resultat = (int)gauche/(int)droite;
		else if (token.getNom().equals("%"))
			resultat = (int)gauche % (int)droite;
		else System.out.println("erreur");
		return new Constante(resultat);//gestion erreurs
	}

	
}