package Vue;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel ;


public class ConsoleAndMemory extends JPanel {
	
	JTextArea console ;

	Memory memory ;
	JTable memoire ;
	static String PHRASE = "Tout le malheur des hommes vient d'un seule chose,"
			+ " qui est de ne pas savoir demeurer au repos dans une anticonstitutionnelement";
	public ConsoleAndMemory() {
		
		
		setLayout(new GridLayout(2,1));
		

		
		console = new JTextArea("[Console ]: ",20,20) ;
		console.setSize(55, 100);

		memory = new Memory();
		memoire = new JTable() ;
		
		memoire.setModel(memory);
		memoire.setOpaque(true);
		memoire.setFont(new Font ("Verdana",Font.ITALIC,16));
		
		console.setEditable(false);
		console.setOpaque(true);
		console.setForeground(Color.GREEN) ;
		console.setFont(new Font ("Times",Font.ITALIC,18));
		console.setBackground(new Color(10,10,10));
		
		afficheConsole(console, PHRASE) ;
		
		
		System.out.println(console.getSize()) ;
		
		
		add(memoire);
		
		add(console);
		
		
		
	}
	
	private String[] coupeString(String chaine) {
		int nbEspace = 0 ;
	
		for(int taille = 0 ; taille < chaine.length(); taille++){
			if(chaine.charAt(taille) == ' ') {
				nbEspace++ ;
			}
		}
		String[] chaineCouper = new String[nbEspace+1]  ;
		String boutChaine = "" ;
		int j = 0 ;
		for(int i = 0; i < chaine.length();i++) {
		if(chaine.charAt(i) != ' ') {
			boutChaine += chaine.charAt(i) ;
		}
		else {
			chaineCouper[j] = boutChaine ;
			j++ ;
		}
		}
		chaineCouper[j] = boutChaine ;
		return chaineCouper ;
	}
	
	
	
	protected void afficheConsole(JTextArea textArea, String valeur) {
		for(int i = 0 ; i < valeur.length(); i ++) {
			if(i%5005 == 0) {
				textArea.append("\n");
			}
			char val = valeur.charAt(i) ;
			textArea.append(String.valueOf(val));
		}
		textArea.append("\n[Console ]: ");
	}
	
	
	
 protected Memory getMemory() {
	 return memory ;
 }
	
	
}