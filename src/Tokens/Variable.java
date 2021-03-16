package Tokens;

public class Variable extends Token{
	private Token type;
	private Object valeur;
	
	public Variable(String nvNom,Object nvValeur) {//
		super(nvNom);
		this.valeur=nvValeur;
	}
	
	public Token getType() {
		return this.type;
	}
	
	public Object getValeur() {
		return this.valeur;
	}
	
	public void setType(Token nvType) {
		this.type=nvType;
	}

}
