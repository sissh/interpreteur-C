package Tokens;

public class Pointeur {
	
	Object destination;
	
	Pointeur(){
		destination=null;
	}
	Pointeur(Object nvDest){
		destination=nvDest;
	}
	
	public Object getDestination() {
		return this.destination;
	}
	
	public String toString() {
		return "p -> "+destination.toString();
	}
}
