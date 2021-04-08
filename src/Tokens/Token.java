package Tokens;

/**
 * Classe abstraite, posant les bases de la structure d'un Token.
 * Les Tokens de toutes sortes h�ritent de cette classe.
 * Les Tokens particuliers n'ont parfois rien de plus que cette classe, mais cela permet de les identifier.
 * Les seuls Tokens qui m�ritaient une classe � part sont : TokenFonction, Variable, Constante.
 * Les autres sont g�n�riques, on peut utiliser � la place les m�thodes : {@link Parser.Code#isEgal(char) isEgal} {@link Parser.Code#isOperateur_1(char) isOperateur_1}, etc.
 * @author alexi
 *
 */
public abstract class Token {
	/**
	 * nom du Token, souvent identique � sa cha�ne de caract�re dans le code.
	 */
	private String nom;
	
	/**
	 * Constructeur de la classe abstraite.
	 * @param nvNom Nom que portera le Token, important pour conna�tre le fonctionnement � appliquer dans la classe Parser.
	 */
	public Token(String nvNom) {
		this.nom=nvNom;
	}
	
	/**
	 * 
	 * @return Le nom du Token.
	 */
	public String getNom() {
		return this.nom;
	}
	
	/**
	 * Affichage du Token, utile pour la maintenance (personnalisation en fonction des types de Tokens).
	 */
	public String toString() {
		return this.nom;
	}

}
