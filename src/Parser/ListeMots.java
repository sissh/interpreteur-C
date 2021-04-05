package Parser;

/**
 * Interface pratique si l'on veut modifier ce que doit contenir un Token.
 * Les méthodes ne sont utiles que pour la Classe {@link Parser.Code Code} il serait d'ailleurs plus propre de les supprimer de l'interface (fonctions publiques qui ne devraient pas l'être)
 * @author alexi
 *
 */
public interface ListeMots {

	final char[] COMPARATEUR = {'!','<','>'};
	final String CONSTANTE = "#CONST#";
	final char EGAL = '=';
	final char[] OPERATEURS_1 = {'/','*', '%'};
	final char[] OPERATEURS_2 = {'+','-'};
	final String[] OPE_UNAIRE= {"++","--"};
	final char[] SYNTAXE = {'(',')','{','}','[',']',';',',','"','&'};
	final String[] TYPES = {"char", "int", "long", "float", "double", "void"};
	
	
}
