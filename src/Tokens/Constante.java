package Tokens;

import Parser.ListeMots;

public class Constante extends Token{
	private Object valeur;
	
	public Constante(Object nvValeur) {
		super(ListeMots.CONSTANTE);
		this.valeur=nvValeur;
	}
	
	private Object getValeur() {
		return this.valeur;
	}

}
