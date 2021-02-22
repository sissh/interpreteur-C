package Tokens;

public class Variable extends Token{
	private Token type;
	private Object valeur;
	
	public Variable(String nvNom,Object nvValeur) {
		super(nvNom);
		this.valeur=nvValeur;
	}
	
	private Token getType() {
		return this.type;
	}
	
	private Object getValeur() {
		return this.valeur;
	}
	
	private void setType(Token nvType) {
		this.type=nvType;
	}

}
