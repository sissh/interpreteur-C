package Tokens;

/**
 * Classe abstraite, posant les bases de la structure d'un Token.
 * Les Tokens de toutes sortes héritent de cette classe.
 * Les Tokens particuliers n'ont parfois rien de plus que cette classe, mais cela permet de les identifier.
 * Les seuls Tokens qui méritaient une classe à part sont : TokenFonction, Variable, Constante.
 * Les autres sont génériques, on peut utiliser à la place les méthodes : {@link Parser.Code#isEgal(char) isEgal} {@link Parser.Code#isOperateur_1(char) isOperateur_1}, etc
 * @author alexi
 *
 */
public abstract class Token {
	/**
	 * nom du Token, souvent identique à sa chaîne de caractère dans le code
	 */
	private String nom;
	
	/**
	 * Constructeur de la classe abstraite
	 * @param nvNom Nom que portera le Token, important pour connaître le fonctionnement à appliquer dans la classe Parser
	 */
	public Token(String nvNom) {
		this.nom=nvNom;
	}
	
	/**
	 * 
	 * @return Le nom du Token
	 */
	public String getNom() {
		return this.nom;
	}
	
	/**
	 * Affichage du Token, utile pour la maintenance (personnalisation en fonction des types de Tokens)
	 */
	public String toString() {
		return this.nom;
	}

}
