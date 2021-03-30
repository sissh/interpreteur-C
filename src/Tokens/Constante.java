package Tokens;

import Parser.ListeMots;

/**
 * Ce Token est une contante dans le code. Exemple : dans "int a=5;", 5 est une constante
 * @author alexi
 *
 */
public class Constante extends Token{
	private Object valeur;
	
	public Constante(Object nvValeur) {
		super(ListeMots.CONSTANTE);
		this.valeur=nvValeur;
	}
	
	public Object getValeur() {
		return this.valeur;
	}
	
	public String toString() {
		return super.toString()+ " valeur : "+this.getValeur();
	}

}
