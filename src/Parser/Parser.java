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
	
	public Object execution(ArrayList<Token> nvLigne) {
		ligne=nvLigne;
		if (nvLigne.get(0) instanceof Type) {
			if (ligne.get(1) instanceof Variable) {
				boolean ok = declareVariable((Variable)ligne.get(1));
				if (!ok)
					return "La variable "+ligne.get(1).getNom()+" a déjà été déclarée précédemment";
				variables.get(ligne.get(1).getNom()).setType((Type)ligne.get(0));//assigne le type
				ligne.remove(0);
				if (ligne.size()==1){//si déclaration sans assignation de valeur
					ligne.remove(0);
					return variables;
				}
			}
			else return "Variable attentue après une déclaration de type";
		}
		else if (ligne.get(0) instanceof Variable) {
			if (!existe(ligne.get(0)))
				return "Token "+ligne.get(0).getNom()+" non initialisé précédemment";
		}
		else if (ligne.get(0) instanceof OpeUnaire && ligne.size()==2 && ligne.get(1) instanceof Variable){//ex : "++a;"
			String courant = ligne.get(1).getNom();
			if (variables.get(courant).getValeur()==null)
				return "Attention ! Ne jamais incrémenter une variable non instancée";
			variables.get(courant).setValeur(((int)variables.get(courant).getValeur())+1);
			return variables;
		}
		else return "Début d'expression interdite";
		String courant = ligne.get(0).getNom();
		ligne.remove(0);
		
		if (ligne.get(0) instanceof OpeUnaire && 1==ligne.size()){//ex : "a++;"
			if (variables.get(courant).getValeur()==null)
				return "Attention ! Ne jamais incrémenter une variable non instancée";
			variables.get(courant).setValeur(((int)variables.get(courant).getValeur())+1);
			return variables;
		}
		else if (!(ligne.get(0) instanceof Egal))//gérer autres types d'assignation de valeur
			return "Token "+ligne.get(0).getNom()+" inattendu, le programme devait trouver un \"=\"";
		ligne.remove(0);

		String error=errorCalculUnaire();
		if (!error.equals(""))
			return error;
		ArrayList<String> calculSuffixe= calculUnaireSuffixe();
		calculUnairePrefixe();
		String erreur = initCalcul();
		if (!erreur.equals(""))
			return erreur;
		System.out.println(ligne);
		if (ligne.get(0) instanceof Constante)
			modifVariable(courant, ((Constante)ligne.get(0)).getValeur());//assignation
		else
			modifVariable(courant, getVariablesValeur(ligne.get(0).getNom()));//assignation
		for (int j=0; j<calculSuffixe.size();j++ ) {
			String nomamodifier = calculSuffixe.get(j);
			variables.get(nomamodifier).setValeur(variables.get(nomamodifier).getValeur());
			variables.get(nomamodifier).setValeur(((int)variables.get(nomamodifier).getValeur())+1);//incrémentation dans la mémoire
		}
		ligne.remove(0);//suppresion de la constante finale
		return variables;
	}
	
	private Object execFonctions(int debut, int fin) {//de nombreuses erreurs à gérer
		int i=debut;
		while (i<fin) {
			if (ligne.get(i) instanceof TokenFonction) {
				String nomFonction = ligne.get(i).getNom();ligne.remove(i); fin-=1;
				if (i<fin && ligne.get(i).getNom().equals("(")) {
					ligne.remove(i); fin-=1;
					ArrayList <Object> parametres = new ArrayList <Object>();
					while (i<fin && !ligne.get(i).getNom().equals(")")) {
						if (ligne.get(i) instanceof Variable) {
							if (!existe(ligne.get(i)))
								System.out.print("afafefefef");
							Object error=getVariablesValeur(ligne.get(i).getNom());
							if (error==null)
								return standardErrorMessage("variable", ligne.get(i).getNom());
							parametres.add(error);
							ligne.remove(i); fin-=1;
							if (i==ligne.size())
								return standardErrorMessage(")", "rien");
							else if (ligne.get(i).getNom().equals(",")) {
								ligne.remove(i); fin-=1;}
							else if(!ligne.get(i).getNom().equals(")"))
								return standardErrorMessage(")", ligne.get(i).getNom());
						}
						else if (ligne.get(i) instanceof Constante) {
							parametres.add(((Constante)ligne.get(i)).getValeur());
							ligne.remove(i); fin-=1;
							if (i==ligne.size())
								return standardErrorMessage(")", "rien");
							else if (ligne.get(i).getNom().equals(",")) {
								ligne.remove(i); fin-=1;}
							else if(!ligne.get(i).getNom().equals(")"))
								return standardErrorMessage(")", ligne.get(i).getNom());
						}
						else if (ligne.get(i) instanceof TokenFonction) {//gestion des fonctions imbriquées
							int gauche=0, droite=0, indice=i+1;
							do {
								if (ligne.get(indice).getNom().equals("("))
									gauche++;
								else if (ligne.get(indice).getNom().equals(")"))
									droite++;
								indice++;
							}while (droite<gauche && indice<ligne.size());
							Object error = execFonctions(i,indice);
							fin-=indice-1;//-1 parce que décalage d'un au-dessus, c'est le nombre de tokens que j'ai supprimé, permet à la fonction appelante de ne pas subir le décalage( index out of range si fin supérieur à ligne.size() )
							parametres.add(((Constante)ligne.get(i)).getValeur());//erreur : ajout sans vérification (,), rajouter conditions
							ligne.remove(i); fin-=1;
							if (error instanceof String)
								return error;
							else if (i==ligne.size())
								return standardErrorMessage(")", "rien");
							else if (ligne.get(i).getNom().equals(",")) {
								ligne.remove(i); fin-=1;}
							else if(!ligne.get(i).getNom().equals(")"))
								return standardErrorMessage(")", ligne.get(i).getNom());
						}
						else return standardErrorMessage("paramètre", ligne.get(i).getNom());
						
					}
					Object resultat = Fonctions.execFonction(nomFonction, parametres);
					if (resultat instanceof String)
						return (String)resultat;
					ligne.set(i, new Constante(resultat));
				}
				else if (!(i<fin))//programme terminé
					return standardErrorMessage("(", ligne.get(i).getNom());
				else return standardErrorMessage("(", ligne.get(i).getNom());
			}
			i++;
		}
		return fin;
	}
	
	private ArrayList<String> calculUnaireSuffixe(){
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
	
	private String errorCalculUnaire() {//si tentative d'incrémenter constantes
		int i=0;
		while (i<ligne.size()) {
			if (!(ligne.get(i++) instanceof Variable)) {
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
		return "";
	}
	
	private void calculUnairePrefixe(){
		int i=0;
		while (i<ligne.size()) {
			if (ligne.get(i++) instanceof OpeUnaire) {
				if (i<ligne.size() && ligne.get(i) instanceof Variable) {					
					variables.get(ligne.get(i).getNom()).setValeur(((Variable)ligne.get(i)).getValeur());//incrémentation dans la mémoire
					ligne.remove(i-1);
				}
				else i++;
			}
		}
	}
	
	private Object calculLigne1 (int debut, int fin) {//calcul des multiplications, divisions, modulo
		int i;
		boolean continuer=true;
		while (debut<fin && continuer) {
			i=debut;
			continuer = false;
			while (i<fin) {
				if (ligne.get(i) instanceof Variable) {
					i++;
					if (i<fin && ligne.get(i) instanceof Operateur_1) {
						continuer=true;
						if (i<fin && ligne.get(i+1) instanceof Variable) {
							ligne.set(i-1, calculArithmetique(getVariablesValeur(ligne.get(i-1).getNom()),ligne.get(i),getVariablesValeur(ligne.get(i+1).getNom())));
							ligne.remove(i);ligne.remove(i); i=debut;fin-=2;
						}
						else if (i<fin && ligne.get(i+1) instanceof Constante) {
							ligne.set(i-1, calculArithmetique(getVariablesValeur(ligne.get(i-1).getNom()),ligne.get(i),((Constante)ligne.get(i+1)).getValeur()));
							ligne.remove(i);ligne.remove(i); i=debut;fin-=2;
						}
						else return standardErrorMessage("variable ou constante", ligne.get(i+1).getNom());
					}
					else i++;
				}
				else if (ligne.get(i) instanceof Constante) {
					i++;
					if (i<fin && ligne.get(i) instanceof Operateur_1) {
						continuer=true;
						if (i<fin && ligne.get(i+1) instanceof Variable) {
							ligne.set(i-1, calculArithmetique(((Constante)ligne.get(i-1)).getValeur(),ligne.get(i),getVariablesValeur(ligne.get(i+1).getNom())));
							ligne.remove(i);ligne.remove(i); i=debut;fin-=2;
						}
						else if (i<fin && ligne.get(i+1) instanceof Constante) {
							ligne.set(i-1, calculArithmetique(((Constante)ligne.get(i-1)).getValeur(),ligne.get(i),((Constante)ligne.get(i+1)).getValeur()));
							ligne.remove(i);ligne.remove(i); i=debut;fin-=2;
						}
						else return standardErrorMessage("variable ou constante", ligne.get(i+1).getNom());
					}
					else i++;
				}
				else i++;
			}
		}
		return fin;
	}
	
	private String calculLigne2 (int debut, int fin) {//calcul des multiplications, divisions, modulo
		int i;
		boolean continuer=true;
		while (debut<fin && continuer) {
			i=debut;
			continuer = false;
			while (i<fin) {
				if (ligne.get(i) instanceof Variable) {
					i++;
					if (i<fin && ligne.get(i) instanceof Operateur_2) {
						continuer=true;
						if (i<fin && ligne.get(i+1) instanceof Variable) {
							ligne.set(i-1, calculArithmetique(getVariablesValeur(ligne.get(i-1).getNom()),ligne.get(i),getVariablesValeur(ligne.get(i+1).getNom())));
							ligne.remove(i);ligne.remove(i); i=debut;fin-=2;
						}
						else if (i<fin && ligne.get(i+1) instanceof Constante) {
							ligne.set(i-1, calculArithmetique(getVariablesValeur(ligne.get(i-1).getNom()),ligne.get(i),((Constante)ligne.get(i+1)).getValeur()));
							ligne.remove(i);ligne.remove(i); i=debut;fin-=2;
						}
						else return standardErrorMessage("variable ou constante", ligne.get(i+1).getNom());
					}
					else i++;
				}
				else if (ligne.get(i) instanceof Constante) {
					i++;
					if (i<fin && ligne.get(i) instanceof Operateur_2) {
						continuer=true;
						if (i<fin && ligne.get(i+1) instanceof Variable) {
							ligne.set(i-1, calculArithmetique(((Constante)ligne.get(i-1)).getValeur(),ligne.get(i),getVariablesValeur(ligne.get(i+1).getNom())));
							ligne.remove(i);ligne.remove(i); i=debut;fin-=2;
						}
						else if (i<fin && ligne.get(i+1) instanceof Constante) {
							ligne.set(i-1, calculArithmetique(((Constante)ligne.get(i-1)).getValeur(),ligne.get(i),((Constante)ligne.get(i+1)).getValeur()));
							ligne.remove(i);ligne.remove(i); i=debut;fin-=2;
						}
						else return standardErrorMessage("variable ou constante", ligne.get(i+1).getNom());
					}
					else i++;
				}
				else i++;
			}
		}
		return "";
	}
	
	private String calculLigne(int debut, int fin) {
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
	
	private String initCalcul() {
		int i =0;
		while (i<ligne.size()) {
			if (ligne.get(i).getNom().equals("(")) {
				if (!(ligne.get(i-1) instanceof TokenFonction)) {
					ligne.remove(i);
					String error = calculParentheses(i);
					if (!error.equals(""))
						return String.valueOf(error);
				}
				else i++;
			}
			else i++;
		}
		return calculLigne(0, ligne.size());
	}
	
	private String calculParentheses(int debut) {
		int i=debut;
		while (i<ligne.size()) {
			if (ligne.get(i).getNom().equals("(")) {
				if (!(ligne.get(i-1) instanceof TokenFonction))
					ligne.remove(i);
					calculParentheses(i);
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
	
	/*private boolean nonDeclare(Token token) {
		if existe(token && variables.get(token.getNom()))
	}*/

	private boolean declareVariable(Variable token) {//ajoute dans la mémoire la variable. Si pas d'erreur, return "", si la variable existe déjà, erreur
		if (variables.containsKey(token.getNom()))
			return false;
		variables.put(token.getNom(), token);
		return true;
	}
	
	private void modifVariable(String nomToken, Object nvValeur) {//modifie la variable dans la mémoire la variable. Si pas d'erreur, return "", si la variable n'existe pas, si est d'un autre type, erreur
		if (!(nvValeur instanceof Integer))
			System.out.println("La valeur à mettre en mémoire n'est pas un Integer");
		variables.get(nomToken).setValeur(nvValeur);
	}
	
	private Constante calculArithmetique(Object gauche, Token token, Object droite) {
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
	
	private Object getVariablesValeur(String nomVariable) {
		return variables.get(nomVariable).getValeur();
	}

	private String standardErrorMessage(String attendu, String trouve) {
		return "Erreur, token '"+attendu+"' attendu, mais "+trouve+" trouvé";
	}
	
}