package Vue;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Classe qui d�taille comment la table est cr��e et d�finit certaines m�thodes.
 * @author kosin
 *
 */



public class Memory extends DefaultTableModel {
	
	
	String [] INTITULEES = {"ID","Nom", "Type","Valeur"} ; 
	int ligne = -1;
	/**
	 * Constructeur de la classe, qui cr�e un DefaultTableModel selon certain param�tre.
	 */
	public Memory() {
		
	this.setColumnIdentifiers(INTITULEES) ;
	this.setRowCount (1) ;
	this.setColumnCount(4) ;
	
	
	/**for (int i= 0 ;i < INTITULEES.length ; i++) 
		this.setValueAt(INTITULEES[i],ligne,i);*/
	setValue(INTITULEES) ;
	
	}
	
	
	/**
	 * M�thode qui rempli la ligne d'indice "ligne".
	 * @param valeurs String[] qui contient les valeurs � rentrer dans la table.
	 */
	protected void setValue(String[] valeurs) {
		ligne += 1 ;
		for(int j = 0 ; j < valeurs.length; j++) {
			this.setValueAt(valeurs[j],ligne,j);
			this.isCellEditable(ligne, j) ;

		}
	}
	/**
	 * M�thode qui overwrite la m�thode native de DefaultTableModel,
	 * afin de ne pas pouvoir �diter les cellules manuellement. 
	 * @param row int qui correspond � la ligne de la cellule.
	 * @param column int qui correspond � la colonne de la cellule.
	 */
	public boolean isCellEditable(int row, int column) {
		return false;
		
	}

}
