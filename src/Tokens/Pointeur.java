package Tokens;

public class Pointeur extends Variable{
	
	private Variable destination;
	
	public Pointeur(String nvNom, String nvType){
		super(nvNom,null);
		destination=null;
		type=nvType;
		
	}
	public Pointeur(String nvNom, Variable nvDest, String nvType){
		super(nvNom, null);
		destination=nvDest;
		type=nvType;
	}
	
	public void setDestination(Variable nvDestination) {
		destination=nvDestination;
	}
	
	public Variable getDestination() {
		return this.destination;
	}
	
	public String toString() {
		if (destination!=null)
			return "p -> "+destination.toString();
		return "p -> ";
	}
}
