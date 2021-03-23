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
					return "La variable "+ligne.get(1).getNom()+" a d�j� �t� d�clar�e pr�c�demment";
				variables.get(ligne.get(1).getNom()).setType((Type)ligne.get(0));//assigne le type
				ligne.remove(0);
				if (ligne.size()==1){//si d�claration sans assignation de valeur
					ligne.remove(0);
					return variables;
				}
			}
			else return "Variable attentue apr�s une d�claration de type";
		}
		else if (ligne.get(0) instanceof Variable) {
			if (!existe(ligne.get(0)))
				return "Token "+ligne.get(0).getNom()+" non initialis� pr�c�demment";
		}
		else if (ligne.get(0) instanceof OpeUnaire && ligne.size()==2 && ligne.get(1) instanceof Variable){//ex : "++a;"
			String courant = ligne.get(1).getNom();
			if (variables.get(courant).getValeur()==null)
				return "Attention ! Ne jamais incr�menter une variable non instanc�e";
			variables.get(courant).setValeur(((int)variables.get(courant).getValeur())+1);
			return variables;
		}
		else return "D�but d'expression interdite";
		String courant = ligne.get(0).getNom();
		ligne.remove(0);
		
		if (ligne.get(0) instanceof OpeUnaire && 1==ligne.size()){//ex : "a++;"
			if (variables.get(courant).getValeur()==null)
				return "Attention ! Ne jamais incr�menter une variable non instanc�e";
			variables.get(courant).setValeur(((int)variables.get(courant).getValeur())+1);
			return variables;
		}
		else if (!(ligne.get(0) instanceof Egal))//g�rer autres types d'assignation de valeur
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
		if (ligne.get(0) instanceof Constante)
			modifVariable(courant, ((Constante)ligne.get(0)).getValeur());//assignation
		else
			modifVariable(courant, getVariablesValeur(ligne.get(0).getNom()));//assignation
		for (int j=0; j<calculSuffixe.size();j++ ) {
			String nomamodifier = calculSuffixe.get(j);
			variables.get(nomamodifier).setValeur(variables.get(nomamodifier).getValeur());
			variables.get(nomamodifier).setValeur(((int)variables.get(nomamodifier).getValeur())+1);//incr�mentation dans la m�moire
		}
		ligne.remove(0);//suppresion de la constante finale
		return variables;
	}
	
	private Object execFonctions(int debut, int fin) {//de nombreuses erreurs � g�rer
		int i=debut;
		while (i<fin) {
			System.out.println("i : "+i+" fin : "+fin+" ligne : "+ligne);
			if (ligne.get(i) instanceof TokenFonction) {
				String nomFonction = ligne.get(i).getNom();ligne.remove(i); fin-=1;
				if (i<fin && ligne.get(i).getNom().equals("(")) {
					ligne.remove(i); fin-=1;
					ArrayList <Object> parametres = new ArrayList <Object>();
					while (i<fin && !ligne.get(i).getNom().equals(")")) {
						if (ligne.get(i) instanceof Variable) {
							parametres.add(getVariablesValeur(ligne.get(i).getNom()));
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
							//System.out.println(ligne+"   i : "+i+" fin : "+fin);
						}
						else if (ligne.get(i) instanceof TokenFonction) {//gestion des fonctions imbriqu�es
							int gauche=0, droite=0, indice=i+1;
							do {
								if (ligne.get(indice).getNom().equals("("))
									gauche++;
								else if (ligne.get(indice).getNom().equals(")"))
									droite++;
								indice++;
							}while (droite<gauche && indice<ligne.size());
							Object error = execFonctions(i,indice);
							fin-=indice-1;//-1 parce que d�calage d'un au-dessus, c'est le nombre de tokens que j'ai supprim�, permet � la fonction appelante de ne pas subir le d�calage( index out of range si fin sup�rieur � ligne.size() )
							parametres.add(((Constante)ligne.get(i)).getValeur());//erreur : ajout sans v�rification (,), rajouter conditions
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
						else return standardErrorMessage("param�tre", ligne.get(i).getNom());
						
					}
					Object resultat = Fonctions.execFonction(nomFonction, parametres);
					if (resultat instanceof String)
						return (String)resultat;
					ligne.set(i, new Constante(resultat));
				}
				else if (!(i<fin))//programme termin�
					return "Erreur 133";
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
	
	private String errorCalculUnaire() {//si tentative d'incr�menter constantes
		int i=0;
		while (i<ligne.size()) {
			if (!(ligne.get(i++) instanceof Variable)) {
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
		return "";
	}
	
	private void calculUnairePrefixe(){
		int i=0;
		while (i<ligne.size()) {
			if (ligne.get(i++) instanceof OpeUnaire) {
				if (i<ligne.size() && ligne.get(i) instanceof Variable) {					
					variables.get(ligne.get(i).getNom()).setValeur(((Variable)ligne.get(i)).getValeur());//incr�mentation dans la m�moire
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
		System.out.println(ligne);
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
		return "Parenth�se ouverte non ferm�e";
	}

	private boolean existe(Token token) {//si la variable (avec son type) n'existe pas dans la m�moire, false, sinon true
		return variables.containsKey(token.getNom());
	}

	private boolean declareVariable(Variable token) {//ajoute dans la m�moire la variable. Si pas d'erreur, return "", si la variable existe d�j�, erreur
		if (variables.containsKey(token.getNom()))
			return false;
		variables.put(token.getNom(), token);
		return true;
	}
	
	private void modifVariable(String nomToken, Object nvValeur) {//modifie la variable dans la m�moire la variable. Si pas d'erreur, return "", si la variable n'existe pas, si est d'un autre type, erreur
		if (!(nvValeur instanceof Integer))
			System.out.println("La valeur � mettre en m�moire n'est pas un Integer");
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
		return "Erreur, token '"+attendu+"' attendu, mais "+trouve+" trouv�";
	}
	
}