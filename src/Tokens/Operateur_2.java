package Tokens;

/**
 * Operateurs : +, -, s'exécute tardivement dans les calculs.
 * Utilisé {@link Parser.Parser#calculLigne2(int, int) ici}.
 * @see Operateur_1 Parser.Parser calculLigne2
 * @author Alexis Fondet
 *
 */
//Pour plus d'informations, ou des conseils sur la continuation du projet : alexisfondet@gmail.com
public class Operateur_2 extends Token{
	/**
	 * Constructeur de la classe.
	 * @param nvNom Le nom du Token.
	 */
	public Operateur_2(String nvNom){
		super(nvNom);
	}

}
