package Parser;

import java.util.ArrayList;
import java.util.HashMap;
import Tokens.*;

public class Code implements ListeMots{
	
	private Parser parser;
	private ArrayList<Token> ligne;
	private ArrayList<Token> arrayListTokens;
	private ArrayList<HashMap<String, Variable>> arrayListRecord;
	public int indice=0;
	
	public Code() {
		arrayListTokens=new ArrayList<Token>();
		parser = new Parser();
		arrayListRecord = new ArrayList<HashMap<String, Variable>>();
		arrayListRecord.add(new HashMap<String, Variable>());
	}

	public ArrayList<HashMap<String, Variable>> getRecord() {
		return this.arrayListRecord;
	}
	
	private void makeTokens(String chaine) {
		String parse = "";
		for (int i=0; i < chaine.length() ; i++) {
			char chr = chaine.charAt(i);
			if (chr==' '){// si parse contient un objet, l'ajouter. Si c'est des espaces en trop, supprimer.
				if (parse.equals(" ") || parse.equals("")) {
					parse="";
					continue;
				}
				else {
					arrayListTokens.add(differentiation(parse));
					parse="";
					continue;
				}
				
			}
			else if(isToken(chr)) {
				arrayListTokens.add(differentiation(parse));
				arrayListTokens.add(differentiation(chr));
				parse="";
				continue;
				}
			parse+=chr;
		}
		int i=0;
		while (i<arrayListTokens.size()) {//Supprime des tokens vides en trop, pr�cise les types pour faciliter l'algorithme
			if (arrayListTokens.get(i).getNom().equals(""))
				arrayListTokens.remove(i); ///// rajouter + et + juxtapos�, + =, etc
			else if (arrayListTokens.get(i).getNom().equals("+")) {
				if (arrayListTokens.get(i-1).getNom().equals("+")) {
					arrayListTokens.remove(i);
					arrayListTokens.set(i-1, new OpeUnaire("++"));
				}
				else i++;
					
			}
			else if (arrayListTokens.get(i).getNom().equals("-")) {
				if (arrayListTokens.get(i+2).getNom().equals("-")) {
					arrayListTokens.remove(i+2);
					arrayListTokens.set(i, new OpeUnaire("--"));
				}
				else i++;
					
			}
			else if (arrayListTokens.get(i).getClass() == Variable.class) {// une fonction �tait de type variable, devient TokenFonction
				if (arrayListTokens.get(i+1).getNom().equals("(")) {
					arrayListTokens.set(i, new TokenFonction(arrayListTokens.get(i).getNom()));
				}
				else i++;
				
			}
			else i++;
		}
	}//makeTokens
	
	@SuppressWarnings("unchecked")
	public Object execLigne(String chaine){//solution potentielle : copier ce qui est � envoyer dans une variable, puis assigner dans tab
		indice++;
		arrayListRecord.add(arrayListRecord.get(indice-1));
		makeTokens(chaine);
		ligne=arrayListTokens;
		if (0 == arrayListTokens.size())
			return "Fin d'ex�cution";
		else {
			while (0 < arrayListTokens.size() && !arrayListTokens.get(0).getNom().equals(";")){
				ligne.add(arrayListTokens.get(0));
				arrayListTokens.remove(0);
			}
			if (0 == arrayListTokens.size() || !arrayListTokens.get(0).getNom().equals(";"))
				return "Ligne finie sans ';'";
			arrayListTokens.remove(0);//suppression du token ';'
			
			HashMap<String, Variable> temp = new HashMap<String, Variable>();
			temp.putAll(arrayListRecord.get(indice));
			
			Object resultat = parser.execution(ligne, temp);

			if (resultat instanceof HashMap<?, ?>) {
				arrayListRecord.set(indice,(HashMap<String, Variable>)resultat);
				return (HashMap<String, Variable>)resultat;//variables � afficher dans la m�moire de l'interface
			}
			else 
				return resultat.toString();// erreur � afficher dans la console de l'interface
		}
	}
	
	public HashMap<String, Variable> backLine() {
		arrayListRecord.remove(indice--);
		return arrayListRecord.get(indice);
	}
	
	@Override
	public boolean isToken(char token) {
		
		if (isOperateur_1(token))
			return true;
		if (isOperateur_2(token))
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
	
	public boolean isInt(String str) {
		 
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
	public boolean isOperateur_1(char token) {
		for (int i=0; i< OPERATEURS_1.length ; i++) {
			if (token == OPERATEURS_1[i]) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isOperateur_2(char token) {
		for (int i=0; i< OPERATEURS_2.length ; i++) {
			if (token == OPERATEURS_2[i]) {
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
		if (isOperateur_1(nom))
			return new Operateur_1(String.valueOf(nom));
		else if (isOperateur_2(nom))
			return new Operateur_2(String.valueOf(nom));
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
	
	public ArrayList<Token> getTokens(){
		return this.arrayListTokens;
	}
	
}
