package Tokens;

/**
 * Ce Token est une variable dans le code. Exemple : dans "int a=5;", a est une constante.
 * @author Alexis Fondet
 *
 */
//Pour plus d'informations, ou des conseils sur la continuation du projet : alexisfondet@gmail.com
public class Variable extends Token{
	/**
	 * La variable a un type.
	 */
	protected String type;
	/**
	 * La variable a une valeur, de type int, char, int[],...
	 */
	private Number valeur;
	
	/**
	 * 
	 * @param nvNom Le nom du Token.
	 * @param nvValeur Valeur à attribuer à la variable. Toujours fournis comme null : permet de distinguer les constructeurs.
	 * @see Parser.ListeMots
	 * @see Parser.Code {@link Parser.Code#differentiation(String nom) differentiation}
	 * @see Parser.Code En fin de fonction {@link Parser.Code#makeTokens(String) makeTokens}, conversion en TokenFonction possible
	 */
	public Variable(String nvNom,Number nvValeur) {//
		super(nvNom);
		this.valeur=nvValeur;
		if (nvValeur==null)
				this.type=null;
		else
			this.type=nvValeur.getClass().toString();
	}
	
	/**
	 * 
	 * @return Le type de la variable.
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * 
	 * @return La valeur de la variable.
	 */
	public Number getValeur() {
		return this.valeur;
	}
	
	/**
	 * Fonction seevant à l'affichage pour l'interface.
	 * Sert au polymorphisme.
	 * @see Tokens.Pointeur
	 * @return La valeur à afficher.
	 */
	public Object getValeurAffichage() {
		if (type.equals("char"))
			return getValeur().toString()+" ("+(char)getValeur().intValue()+")";
		return this.getValeur();
	}
	
	/**
	 * 
	 * @param nvValeur Nouvelle valeur de la variable. Conversion automatique de la valeur Java à celle limitée par la taille des variables en C (pas terminé).
	 */
	public void setValeur(Number nvValeur) {
		if (type.equals("int"))
			valeur = nvValeur.intValue();
		else if (type.equals("long"))
			valeur = nvValeur.longValue();
		else if (type.equals("float"))
			valeur = nvValeur.floatValue();
		else if (type.equals("double"))
			valeur = nvValeur.doubleValue();
		else if (type.equals("char")) {
			valeur=nvValeur.intValue()%256;
			if (valeur.intValue()<-128)
				valeur=valeur.intValue()+256;
			else if (valeur.intValue()>127)
				valeur=valeur.intValue()-256;
		}
		else System.err.println("### Erreur non attendue lors de l'assignation de valeur, problème au niveau du Parser ###");
	}
	
	/**
	 * 
	 * @param nvType Nouveau type de la variable.
	 */
	public void setType(String nvType) {
		this.type=nvType;
	}
	
	/**
	 * Sert au déboguage, pour obtenir plus d'informations sur une ligne.
	 */
	public String toString() {
		return super.toString()+ " type : "+this.getType()+" valeur : "+this.getValeur();
	}

}
