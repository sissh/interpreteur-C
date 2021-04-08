package Tokens;

/**
 * Les opérateurs : ++ et --.
 * @see Parser.Parser calculUnaireSuffixe
 * @author alexi
 *
 */
public class OpeUnaire extends Token{
	/**
	 * Constructeur de la classe.
	 * @param nvNom Le nom du Token.
	 */
	public OpeUnaire(String nvNom) {
		super(nvNom);
	}

}
