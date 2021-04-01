package Vue;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Classe qui détaille comment la table est crée et définit certaines méthodes.
 * @author kosin
 *
 */



public class Memory extends DefaultTableModel {
	
	
	String [] INTITULEES = {"ID","Nom", "Type","Valeur"} ; 
	int ligne = -1;
	/**
	 * Constructeur de la classe, qui crée un DefaultTableModel selon certain paramètre.
	 */
	public Memory() {
		
	this.setColumnIdentifiers(INTITULEES) ;
	this.setRowCount(0) ;
	this.setColumnCount(4) ;
	
	
	/**for (int i= 0 ;i < INTITULEES.length ; i++) 
		this.setValueAt(INTITULEES[i],ligne,i);*/
	//setValue(INTITULEES) ;
	
	}
	
	
	/**
	 * Méthode qui rempli la ligne d'indice "ligne".
	 * @param valeurs String[] qui contient les valeurs à rentrer dans la table.
	 */
	protected void setValue(String[] valeurs) {
		ligne += 1 ;
		for(int j = 0 ; j < valeurs.length; j++) {
			this.setValueAt(valeurs[j],ligne,j);
			this.isCellEditable(ligne, j) ;

		}
	}
	/**
	 * Méthode qui overwrite la méthode native de DefaultTableModel,
	 * afin de ne pas pouvoir éditer les cellules manuellement. 
	 * @param row int qui correspond à la ligne de la cellule.
	 * @param column int qui correspond à la colonne de la cellule.
	 */
	public boolean isCellEditable(int row, int column) {
		return false;
		
	}

}
