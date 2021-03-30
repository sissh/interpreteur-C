package Tokens;

/**
 * Un Token qui est une fonction dans le code. Exemple : dans "pow(a,2);", pow est un TokenFonction
 * @author alexi
 *
 */
public class TokenFonction extends Token{

	/**
	 * @see Tokens.Token
	 * @param nvNom
	 */
	public TokenFonction(String nvNom) {
		super(nvNom);
	}
}
