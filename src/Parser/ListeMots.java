package Parser;
import Tokens.Constante;
import Tokens.Token;
import Tokens.Variable;

/**
 * Interface pratique si l'on veut modifier ce que doit contenir un Token.
 * Les méthodes ne sont utiles que pour la Classe {@link Parser.Code Code} il serait d'ailleurs plus propre de les supprimer de l'interface (fonctions publiques qui ne devraient pas l'être)
 * @author alexi
 *
 */
public interface ListeMots {

	final String[] TYPES = {"char", "int", "void"};
	final char[] OPERATEURS_1 = {'/','*', '%'};
	final char[] OPERATEURS_2 = {'+','-'};
	final String[] OPE_UNAIRE= {"++","--"};
	final char[] SYNTAXE = {'(',')','{','}','[',']',';',','};
	final char[] COMPARATEUR = {'!','<','>'};
	final char EGAL = '=';
	final String CONSTANTE = "#CONST#";
	
	public boolean isToken(String token);
	public boolean isToken(char token);
	
	public boolean isType(String token);
	public boolean isOperateur_1(char token);
	public boolean isOperateur_2(char token);
	public boolean isOpeUnaire(String token);
	public boolean isSyntaxe(char token);
	public boolean isComparateur(char token);
	public boolean isEgal(char token);
	
	public Token createToken(String nom);
	public Token createToken(char nom);
	public Constante createToken(Object valeur);
	public Variable createToken(String nom, Object valeur);
	public Token differentiation(String nom);
	public Token differentiation(char nom);
}
