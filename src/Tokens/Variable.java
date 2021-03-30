package Tokens;

/**
 * Ce Token est une variable dans le code. Exemple : dans "int a=5;", a est une constante
 * @author alexi
 *
 */
public class Variable extends Token{
	/**
	 * La variable a un type
	 */
	private Token type;
	/**
	 * La variable a une valeur, de type int, char, int[],...
	 */
	private Object valeur;
	
	/**
	 * 
	 * @param nvNom
	 * @param nvValeur Valeur à attribuer à la variable. Toujours fournis comme null : permet de distinguer les constructeurs
	 * @see Parser.ListeMots
	 * @see Parser.Code.differentiation
	 * @see Parser.Code.makeTokens En fin de fonction, conversion en TokenFonction possible
	 */
	public Variable(String nvNom,Object nvValeur) {//
		super(nvNom);
		this.valeur=nvValeur;
	}
	
	/**
	 * 
	 * @return Le type de la variable
	 */
	public Token getType() {
		return this.type;
	}
	
	/**
	 * 
	 * @return La valeur de la variable
	 */
	public Object getValeur() {
		return this.valeur;
	}
	
	/**
	 * 
	 * @param nvValeur Nouvelle valeur de la variable, 
	 */
	public void setValeur(Object nvValeur) {
		this.valeur = nvValeur;
	}
	
	/**
	 * 
	 * @param nvType Nouveau type de la variable, à n'utiliser qu'une fois, lors de la première utilisation de la variable
	 * @see Parser.Parser.execution Au début
	 */
	public void setType(Type nvType) {
		this.type=nvType;
	}
	
	public String toString() {
		return super.toString()+ " type : "+this.getType()+" valeur : "+this.getValeur();
	}

}
