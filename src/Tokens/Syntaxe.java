package Tokens;

/**
 * Tout type d'éléments de syntaxe, virgule, parenthèse, point-virgule, etc
 * Aucune utilisation du type de cette classe (comportement très varié entre les différents éléments), sert de catégorie par défaut.
 * @author Alexis Fondet
 *
 */
//Pour plus d'informations, ou des conseils sur la continuation du projet : alexisfondet@gmail.com
public class Syntaxe extends Token{
	/**
	 * Le constructeur de la classe.
	 * @param nvNom Le nom du Token.
	 */
	public Syntaxe(String nvNom) {
		super(nvNom);
	}

}
