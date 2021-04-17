package Tokens;

/**
 * Ce Token est une fonction dans le code.
 * Exemple : dans "pow(a,2);", pow est un TokenFonction.
 * @author Alexis Fondet
 *
 */
//Pour plus d'informations, ou des conseils sur la continuation du projet : alexisfondet@gmail.com
public class TokenFonction extends Token{

	/**
	 * @see Tokens.Token
	 * @param nvNom Le nom du Token.
	 */
	public TokenFonction(String nvNom) {
		super(nvNom);
	}
}
