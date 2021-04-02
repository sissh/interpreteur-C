package Parser;
import Tokens.Constante;
import Tokens.Token;
import Tokens.Variable;

/**
 * Interface pratique si l'on veut modifier ce que doit contenir un Token.
 * Les m�thodes ne sont utiles que pour la Classe {@link Parser.Code Code} il serait d'ailleurs plus propre de les supprimer de l'interface (fonctions publiques qui ne devraient pas l'�tre)
 * @author alexi
 *
 */
public interface ListeMots {

	final String[] TYPES = {"char", "int", "long", "float", "double", "void"};
	final char[] OPERATEURS_1 = {'/','*', '%'};
	final char[] OPERATEURS_2 = {'+','-'};
	final String[] OPE_UNAIRE= {"++","--"};
	final char[] SYNTAXE = {'(',')','{','}','[',']',';',',','"','\''};
	final char[] COMPARATEUR = {'!','<','>'};
	final char EGAL = '=';
	final String CONSTANTE = "#CONST#";
}
