package Tokens;

/**
 * Token Venant remplacer une chaine de caract�res dans une ligne de code.
 * A remplacer plus tard toutefois par une classe g�n�rique pour tout type de tableaux.
 * @author alexi
 *
 */
public class Phrase extends Token{

	/**
	 * Le constructeur de la classe.
	 * @param phrase Le texte � enregistrer dans le token.
	 */
	public Phrase(String phrase) {
		super(phrase.subSequence(1, phrase.length()-1).toString());//on retire les guillemets de droite et de gauche.
	}

}
