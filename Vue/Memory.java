package Vue;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;





public class Memory extends DefaultTableModel {
	
	
	String [] INTITULEES = {"ID","Nom", "Type","Valeur"} ; 
	int ligne = 0;
	
	public Memory() {
		
	this.setColumnIdentifiers(INTITULEES) ;
	this.setRowCount (20) ;
	this.setColumnCount(4) ;
	
	
	for (int i= 0 ;i < INTITULEES.length ; i++) 
		this.setValueAt(INTITULEES[i],ligne,i);
	
	}
	
	
	
	protected void setValue(String[] valeurs) {
		ligne += 1 ;
		for(int j = 0 ; j < valeurs.length; j++) {
			this.setValueAt(valeurs[j],ligne,j);

		}
		
		
		
		
	}

}
