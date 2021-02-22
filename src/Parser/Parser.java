package Parser;
import java.util.ArrayList;

import Tokens.*;


public class Parser implements ListeMots{

	private String chaine;
	private ArrayList<Token> setTokens;
	
	public Parser(String chaine) {
		this.chaine = chaine;
		setTokens=new ArrayList<Token>();
	}
	
	public ArrayList<Token> getTokens() {
		return this.setTokens;
	}
	
	public void makeTokens() {
		String parse = "";
		for (int i=0; i < this.chaine.length() ; i++) {
			char chr = this.chaine.charAt(i);
			if (chr==' '){// si parse contient un objet, l'ajouter. Si c'est des espaces en trop, supprimer.
				if (parse.equals(" ") || parse.equals("")) {
					parse="";
					continue;
				}
				else {
					this.setTokens.add(differentiation(parse));
					parse="";
					continue;
				}
				
			}
			else if(isToken(chr)) {
				this.setTokens.add(differentiation(parse));
				this.setTokens.add(differentiation(chr));
				parse="";
				continue;
				}
			parse+=chr;
		}
		for (int i=0; i<this.setTokens.size(); i++) {//Supprime des tokens vide en trop
			if (this.setTokens.get(i).getNom().equals(""))
				this.setTokens.remove(i); ///// rajouter + et + juxtaposé, + =, etc
		}
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
	}

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
		else return createToken(nom,null);
	}

	@Override
	public Token differentiation(char nom) {
		return createToken(nom);
			}


}