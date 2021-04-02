package Tokens;

import Parser.ListeMots;

/**
 * Ce Token est une contante dans le programme.
 * Exemple : dans "int a=5;", 5 est une constante
 * @author alexi
 *
 */
public class Constante extends Token{
	private Number valeur;
	private String type;
	
	/**
	 * Constructeur de la classe
	 * @param nvValeur La valeur de la constante (plus tart, gérer char et char[])
	 */
	public Constante(Number nvValeur) {
		/**
		 * nom de Token stéréotypé, car sans intérêt (inusité)
		 * Facile à repérer lors de la maintenance (affichage d'une ligne en cours d'exécution par exemple)
		 */
		super(ListeMots.CONSTANTE);
		this.valeur=nvValeur;
		this.type=nvValeur.getClass().toString();
	}
	
	/**
	 * Récupère la valeur de la variable
	 * @return La valeur de la variable, de type int, char, ...
	 */
	public Number getValeur() {
		return this.valeur;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String toString() {
		return super.toString()+ " valeur : "+this.getValeur();
	}

}
