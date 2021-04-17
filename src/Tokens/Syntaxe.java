package Tokens;

/**
 * Tout type d'�l�ments de syntaxe, virgule, parenth�se, point-virgule, etc
 * Aucune utilisation du type de cette classe (comportement tr�s vari� entre les diff�rents �l�ments), sert de cat�gorie par d�faut.
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
