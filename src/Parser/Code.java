package Parser;

import java.util.ArrayList;
import java.util.HashMap;
import Tokens.*;

/**
 * Classe pr�parant l'ex�cution du Parser, en cr�ant les Tokens, et qui g�re la sauvegarde de la m�moire
 * @author alexi
 *
 */
public class Code implements ListeMots{

	/**
	 * Objet Parser qui ex�cutera le code.
	 */
	private Parser parser;
	/**
	 * Liste de Tokens produite par {@link Parser.Code#makeTokens(String) makeTokens} (ligne de code), envoy�e ensuite au Parser.
	 */
	private ArrayList<Token> arrayListTokens;
	/**
	 * Registre des diff�rents �tats de la m�moire, pour permettre le retour en arri�re.
	 */
	private ArrayList<HashMap<String, Variable>> arrayListRecord;
	/**
	 * indice correspondant � l'�tat de la m�moire en cours. Dispensable, car l'indice courant = arrayListRecord.size()-1 � tout moment.
	 */
	public int indice;
	
	public static String FIN_EXEC = "Fin d'ex�cution";
	
	/**
	 * Constructeur de la classe, initialise les variables.
	 */
	public Code() {
		
		arrayListTokens=new ArrayList<Token>();//contient les tokens � envoyer au Parser
		parser = new Parser();//Parser pour l'ex�cution du code
		arrayListRecord = new ArrayList<HashMap<String, Variable>>();//Enregistre les diff�rents �tats de la m�moire
		arrayListRecord.add(new HashMap<String, Variable>());
		indice=0;
	}
	
	/**
	 * Utilis� pour de la maintenance de code, mais peut avoir son utilit� dans l'interface (pour acc�der � des �tats ant�rieurs de la m�moire).
	 * @return
	 */
	public ArrayList<HashMap<String, Variable>> getRecord() {
		return this.arrayListRecord;
	}
	
	/**
	 * Remet � z�ro l'objet Code, pour d�marrer une nouvelle ex�cution.
	 * Utilis� lorsque l'utilisateur appuie sur le bouton {@linkplain Vue.FenetreMere#actionPerformed(java.awt.event.ActionEvent) RESET}.
	 */
	public void reset() {
		arrayListTokens=new ArrayList<Token>();
		parser = new Parser();
		arrayListRecord = new ArrayList<HashMap<String, Variable>>();
		arrayListRecord.add(new HashMap<String, Variable>());
		indice=0;
	}
	
	/**
	 * Conversion de la String envoy�e par l'interface en une liste de Tokens (arrayListTokens)
	 * @param chaine Texte converti
	 */
	private void makeTokens(String chaine) {
		String parse = "";//initialisation de cette variable, qui permet de distinguer les Tokens
		for (int i=0; i < chaine.length() ; i++) {
			char chr = chaine.charAt(i);//parcours de la chaine caract�re par caract�re
			if (chr==' '){// Les espaces d�limitent les Tokens
				if (parse.equals("") || parse.equals("\n")) {//si parse est vide ou retour � la ligne inutile, il n'y a pas de Token � cr�er (par exemple, suite d'espaces dans une ligne de code)
					parse="";
					continue;
				}
				else {//sinon, alors le contenu de parse doit �tre un Token. Exemple : "int a = 5 ;", chaque token est s�par� par un espace
					arrayListTokens.add(differentiation(parse));
					parse="";
					continue;
				}
				
			}
			else if(isToken(chr)) {//Si le caract�re est un token st�r�otyp� � lui tout seul, il faut arr�ter Exemple : ";", ",", "(", etc
				if (!parse.equals(""))
						arrayListTokens.add(differentiation(parse));//le token pr�c�dent est dont un Token aussi
				arrayListTokens.add(differentiation(chr));
				parse="";
				continue;
				}
			parse+=chr;
		}
		int i=0;
		while (i<arrayListTokens.size()) {//Supprime des tokens vides en trop (ne doit plus arriver normalement, tester et supprimer si ok)
			if (arrayListTokens.get(i).getNom().equals(""))
				arrayListTokens.remove(i);
			else if (arrayListTokens.get(i).getNom().equals("+")) {// rajouter + et + juxtapos�, op�rateur unaire
				if (arrayListTokens.get(i-1).getNom().equals("+")) {
					arrayListTokens.remove(i);
					arrayListTokens.set(i-1, new OpeUnaire("++"));
				}
				else i++;
					
			}
			else if (arrayListTokens.get(i).getNom().equals("-")) {//idem pour --
				if (arrayListTokens.get(i+2).getNom().equals("-")) {
					arrayListTokens.remove(i+2);
					arrayListTokens.set(i, new OpeUnaire("--"));
				}
				else i++;
					
			}
			else if (arrayListTokens.get(i) instanceof Variable) {// une fonction �tait de type variable (car ne se distingue pas sans v�rification), devient TokenFonction
				if (arrayListTokens.get(i+1).getNom().equals("(")) {// int pow=5 est une variable, mais pow(2,2) est une fonction
					arrayListTokens.set(i, new TokenFonction(arrayListTokens.get(i).getNom()));
				}
				else i++;
				
			}
			else i++;
		}
	}//makeTokens
	
	/**
	 * Lance l'execution du {@linkplain Parser.Parser Parser}, g�re la memoire, et traite les potentielles erreurs.
	 * @param chaine La String, traitee par {@link Parser.Code#makeTokens(String) makeTokens}
	 * @return HashMap des Variables, ou erreur String.
	 */
	@SuppressWarnings("unchecked")
	public Object execLigne(String chaine){
		indice++;//sera l'indice de l'etat de la memoire apr�s l'execution de la fonction
		arrayListRecord.add(arrayListRecord.get(indice-1));//copie de l'etat precedent de la memoire dans la nouvelle, avant modification
		makeTokens(chaine);
		if (0 == arrayListTokens.size() || !arrayListTokens.get(arrayListTokens.size()-1).getNom().equals(";"))//Ne doit pas arriver avec l'interface
			return "Ligne finie sans ';'";
		arrayListTokens.remove(arrayListTokens.size()-1);//suppression du token ';'

		HashMap<String, Variable> temp = new HashMap<String, Variable>();
		temp.putAll(arrayListRecord.get(indice));
			
		Object resultat = parser.execution(arrayListTokens, temp);

		if (resultat instanceof HashMap<?, ?>) {
			arrayListRecord.set(indice,(HashMap<String, Variable>)resultat);
			return (HashMap<String, Variable>)resultat;//variables � afficher dans la m�moire de l'interface
		}
		else 
			return resultat.toString();// erreur � afficher dans la console de l'interface
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
