package Parser;
import Tokens.Constante;
import Tokens.Token;
import Tokens.Variable;

public interface ListeMots {

	final String[] TYPES = {"char", "int", "void"};
	final char[] OPERATEURS = {'+','-','/','*'};
	final char[] SYNTAXE = {'(',')','{','}','[',']',';',','};
	final char[] COMPARATEUR = {'!','<','>'};
	final char EGAL = '=';
	final String CONSTANTE = "###CONSTANTE###";
	
	public boolean isToken(String token);
	public boolean isToken(char token);
	
	public boolean isType(String token);
	public boolean isOperateur(char token);
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
