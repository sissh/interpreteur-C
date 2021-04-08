package Tokens;

/**
 * Pour un rendu plus agréable dans {@linkplain Parser.Parser#execution(java.util.ArrayList, java.util.HashMap) le Parser}.
 * Utilisé uniquement parce que le développeur trouvait plus joli de mettre dans le code "if token instanceof Egal" plutôt que "if token.getNom().equals("=").
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
