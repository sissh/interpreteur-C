package Parser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import Tokens.*;


public class Parser implements ListeMots{

	private String chaine;
	private ArrayList<Token> setTokens;
	private HashMap<String, Token> variables;
	
	public Parser(String chaine, HashMap<String, Token> variables) {
		this.chaine = chaine;
		this.setTokens=new ArrayList<Token>();
		this.variables = variables;
	}
	
	public ArrayList<Token> getTokens() {
		return this.setTokens;
	}
	
	public void makeTokens() {
		String parse = "";
		for (int i=0; i < chaine.length() ; i++) {
			char chr = chaine.charAt(i);
			if (chr==' '){// si parse contient un objet, l'ajouter. Si c'est des espaces en trop, supprimer.
				if (parse.equals(" ") || parse.equals("")) {
					parse="";
					continue;
				}
				else {
					setTokens.add(differentiation(parse));
					parse="";
					continue;
				}
				
			}
			else if(isToken(chr)) {
				setTokens.add(differentiation(parse));
				setTokens.add(differentiation(chr));
				parse="";
				continue;
				}
			parse+=chr;
		}
		int i=0;
		while (i<setTokens.size()) {//Supprime des tokens vides en trop, précise les types pour faciliter l'algorithme
			if (setTokens.get(i).getNom().equals(""))
				setTokens.remove(i); ///// rajouter + et + juxtaposé, + =, etc
			else if (setTokens.get(i).getNom().equals("+")) {
				if (setTokens.get(i+2).getNom().equals("+")) {
					setTokens.remove(i+2);
					setTokens.set(i, new OpeUnaire("++"));
				}
				else i++;
					
			}
			else if (setTokens.get(i).getNom().equals("-")) {
				if (setTokens.get(i+2).getNom().equals("-")) {
					setTokens.remove(i+2);
					setTokens.set(i, new OpeUnaire("--"));
				}
				else i++;
					
			}
			else if (setTokens.get(i).getClass() == Variable.class) {// une fonction était de type variable, devient TokenFonction
				if (setTokens.get(i+1).getNom().equals("(")) {
					setTokens.set(i, new TokenFonction(setTokens.get(i).getNom()));
				}
				else i++;
				
			}
			else i++;
		}
	}//makeTokens
	
	public Object execution() {//retourne l'array de variables si ok
		int i=0;
		if (setTokens.get(i) instanceof Type) {
			i++;
			if (setTokens.get(i) instanceof Variable) {
				((Variable)setTokens.get(i)).setType((Type)setTokens.get(i-1));
				boolean ok = declareVariable((Variable)setTokens.get(i));
				if (!ok)
					return "La variable "+setTokens.get(i).getNom()+" a déjà été initialisée précédemment";
			}
		}
		else if (setTokens.get(i) instanceof Variable) {
			if (!existe(setTokens.get(i)))
				return "Token "+setTokens.get(i).getNom()+" non initialisé précédemment";
		}
		else return "Début d'expression interdite";
		
		int courant = i++;
		
		if (!(setTokens.get(i) instanceof Egal))
			return "Token "+setTokens.get(i).getNom()+" inattendu, le programme devait trouver un \"=\"";
		i++;
		
		Object resultat = compute(this.setTokens, i, true);
		if (resultat instanceof String)
			return resultat;
		modifVariable((Variable)setTokens.get(courant), resultat);//tester type de retour
		
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
		return calculLigne(tokens);
	}
		

	private Object calculLigne(ArrayList<Token> setTokens) {//création d'un arbre binaire de calcul
		int i=0;
		Object temp=0;// gestion du type plus tard
		// gestion du premier token de la ligne
		if (setTokens.get(i) instanceof Variable || setTokens.get(i) instanceof Constante) 
			temp=calculArithmetique(temp, new Operateur("+"), setTokens.get(i++));
		else if (setTokens.get(i).getNom().equals("(")){
			i++;
			temp=compute(setTokens, i, false);
		}
		else
			System.out.println("erreur, il faut interrompre");
		while (i<setTokens.size()) {
			if (setTokens.get(i) instanceof Operateur) {
				i++;
				if (setTokens.get(i) instanceof Variable || setTokens.get(i) instanceof Constante) {
					temp=calculArithmetique(temp, (Operateur)setTokens.get(i-1), setTokens.get(i));
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
	
	private int calculArithmetique(Object gauche, Operateur operateur, Token token) {
		int droite = 0;//gestion types
		if (token instanceof Variable)
			droite= (int)((Variable) token).getValeur();
		
		else if (token instanceof Constante)
			droite= (int)((Constante) token).getValeur();
		
		if (operateur.getNom().equals("+"))
			return (int)gauche+droite;
		if (operateur.getNom().equals("-"))
			return (int)gauche-droite;
		if (operateur.getNom().equals("*"))
			return (int)gauche*droite;
		return (int)gauche/droite;//gestion erreurs
	}

	@Override
	public boolean isToken(char token) {
		
		if (isOperateur(token))
			return true;
		for (int i=0; i< SYNTAXE.length ; i++) {
			if (token == SYNTAXE[i]) {
				return true;
			}
		}
		for (int i=0; i< COMPARATEUR.length ; i++) {
			if (token == COMPARATEUR[i]) {
				return true;
			}
		}
		if (isEgal(token))
			return true;
		return false;
	}//isToken

	@Override
	public boolean isToken(String token) {
		return isType(token);
	}
	
	public static boolean isInt(String str) {
		 
        if (str == null || str.length() == 0) {
            return false;
        }
 
        try {
            Integer.parseInt(str);
            return true;
 
        } catch (NumberFormatException e) {
            return false;
        }
 
    }

	@Override
	public boolean isType(String token) {
		for (int i=0; i< TYPES.length ; i++) {
			if (token.equals(TYPES[i])) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isOperateur(char token) {
		for (int i=0; i< OPERATEURS.length ; i++) {
			if (token == OPERATEURS[i]) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isOpeUnaire(String token) {
		for (int i=0; i< OPE_UNAIRE.length ; i++) {
			if (token == OPE_UNAIRE[i]) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isSyntaxe(char token) {
		for (int i=0; i< SYNTAXE.length ; i++) {
			if (token == SYNTAXE[i]) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isComparateur(char token) {
		for (int i=0; i< COMPARATEUR.length ; i++) {
			if (token == COMPARATEUR[i]) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isEgal(char token) {
		if (token == EGAL)
			return true;
		return false;
	}
	
	@Override
	public Token createToken(String nom) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Token createToken(char nom) {
		if (isOperateur(nom))
			return new Operateur(String.valueOf(nom));
		else if (isComparateur(nom))
			return new Comparateur(String.valueOf(nom));
		else if (isSyntaxe(nom))
			return new Syntaxe(String.valueOf(nom));
		else if (isEgal(nom))
			return new Egal(String.valueOf(nom));
		else
			return null;
	}

	@Override
	public Constante createToken(Object valeur) {
		return new Constante(valeur);
		
	}

	@Override
	public Variable createToken(String nom, Object valeur) {
		return new Variable(nom,valeur);
		
	}

	@Override
	public Token differentiation(String nom) {
		if (isInt(nom))
			return createToken(Integer.parseInt(nom));
		else if (isType(nom))
			return new Type(nom);
		else return createToken(nom,null);//variable sans valeur
	}

	@Override
	public Token differentiation(char nom) {
		return createToken(nom);
			}
}