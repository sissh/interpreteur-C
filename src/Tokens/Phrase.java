package Tokens;

/**
 * Token Venant remplacer une chaine de caractères dans une ligne de code.
 * A remplacer plus tard toutefois par une classe générique pour tout type de tableaux.
 * @author alexi
 *
 */
public class Phrase extends Token{

	/**
	 * Le constructeur de la classe.
	 * @param phrase Le texte à enregistrer dans le token.
	 */
	public Phrase(String phrase) {
		super(phrase.subSequence(1, phrase.length()-1).toString());//on retire les guillemets de droite et de gauche.
	}

}
