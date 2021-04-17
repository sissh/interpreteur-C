package Tokens;

/**
 * Les opérateurs : ++ et --.
 * @see Parser.Parser calculUnaireSuffixe
 * @author Alexis Fondet
 *
 */
//Pour plus d'informations, ou des conseils sur la continuation du projet : alexisfondet@gmail.com
public class OpeUnaire extends Token{
	/**
	 * Constructeur de la classe.
	 * @param nvNom Le nom du Token.
	 */
	public OpeUnaire(String nvNom) {
		super(nvNom);
	}

}
