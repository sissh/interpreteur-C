package Tokens;

/**
 * Classe repr�sentant un pointeur.
 * @author Alexis Fondet
 *
 */
//Pour plus d'informations, ou des conseils sur la continuation du projet : alexisfondet@gmail.com
public class Pointeur extends Variable{
	
	/**
	 * La variable sur laquelle pointe le pointeur.
	 */
	private Variable destination;
	
	/**
	 * Le constructeur de la classe.
	 * @param nvNom Le nom � attribuer au pointeur.
	 * @param nvType Le type de pointeur. Doit suivre ce format : [type]* ; exemple : char*.
	 */
	public Pointeur(String nvNom, String nvType){
		super(nvNom,null);
		destination=null;
		type=nvType;
		
	}
	
	/**
	 * Assigne au pointeur une variable sur laquelle pointer.
	 * @param nvDestination La variable sur laquelle pointer.
	 */
	public void setDestination(Variable nvDestination) {
		destination=nvDestination;
	}
	
	/**
	 * Pour r�cup�rer la variable sur laquelle pointe le pointeur.
	 * @return La variable sur laquelle pointe le pointeur.
	 */
	public Variable getDestination() {
		return this.destination;
	}
	
	/**
	 * Fonction seevant � l'affichage pour l'interface.
	 * Sert au polymorphisme.
	 * @see Tokens.Variable
	 * @return Le texte � afficher.
	 */
	public String getValeurAffichage() {
		return this.toString();
	}
	
	/**
	 * Repr�sentation du pointeur et de l'objet sur lequel il pointe.
	 */
	public String toString() {
		if (destination!=null)
			return "p -> "+destination.getNom();
		return "p -> ";
	}
}
