package Tokens;

/**
 * Operateurs : *, /, %, priorité sur Operateur_2 pour les calculs.
 * Utilisé {@link Parser.Parser#calculLigne1(int, int) ici}.
 * @see Operateur_2 Parser.Parser calculLigne1
 * @author Alexis Fondet
 *
 */
//Pour plus d'informations, ou des conseils sur la continuation du projet : alexisfondet@gmail.com
public class Operateur_1 extends Token{
	/**
	 * Constructeur de la classe.
	 * @param nvNom Le nom du Token.
	 */
	public Operateur_1(String nvNom){
		super(nvNom);
	}

}
