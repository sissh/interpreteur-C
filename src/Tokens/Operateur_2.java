package Tokens;

/**
 * Operateurs : +, -, s'ex�cute tardivement dans les calculs.
 * Utilis� {@link Parser.Parser#calculLigne2(int, int) ici}.
 * @see Operateur_1 Parser.Parser calculLigne2
 * @author alexi
 *
 */
public class Operateur_2 extends Token{
	/**
	 * Constructeur de la classe.
	 * @param nvNom Le nom du Token.
	 */
	public Operateur_2(String nvNom){
		super(nvNom);
	}

}
