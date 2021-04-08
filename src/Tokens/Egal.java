package Tokens;

/**
 * Pour un rendu plus agr�able dans {@linkplain Parser.Parser#execution(java.util.ArrayList, java.util.HashMap) le Parser}.
 * Utilis� uniquement parce que le d�veloppeur trouvait plus joli de mettre dans le code "if token instanceof Egal" plut�t que "if token.getNom().equals("=").
 * @author alexi
 *
 */
public class Egal extends Token{
	/**
	 * Constructeur de la classe.
	 * @param nom Le nom du Token.
	 */
	public Egal(String nom) {
		super(nom);
	}

}
