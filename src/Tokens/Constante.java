package Tokens;

import Parser.ListeMots;

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
