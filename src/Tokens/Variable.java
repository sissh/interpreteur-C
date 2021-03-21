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
	
	public void setValeur(Object nvValeur) {
		this.valeur = nvValeur;
	}
	
	public void setType(Type nvType) {
		this.type=nvType;
	}
	
	public String toString() {
		return super.toString()+ " type : "+this.getType()+" valeur : "+this.getValeur();
	}

}
