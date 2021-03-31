package Vue;


import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.*;
import javax.swing.text.*;

/**
 * Classe qui crée la console et la table et qui s'occupe de l'affichage sur la console.
 * @author kosin
 */
public class ConsoleAndMemory extends JPanel {
	
	JTextPane console ;
	JScrollPane consoleScrollable ;
	Memory memory ;
	JScrollPane memoireScrollable ;
	JTable memoire ;
	Color ERROR = Color.RED ;
	Color MSG = Color.GREEN ;
	
	
	/**
	 * Constructeur de la classe qui crée la console et la table "mémoire" et les fusionnent en une entité.
	 */
	public ConsoleAndMemory() {
		
		
		setLayout(new GridLayout(2,1));
		

		
		console = new JTextPane();
		appendToPane(console,"[Console ]:\n",MSG) ;
		console.setSize(55, 100);
		

		memory = new Memory();
		memoire = new JTable() ;
		
		memoire.setModel(memory);
		memoire.setOpaque(true);
		memoire.setDragEnabled(false);
		memoire.setFocusable(false);
		memoire.setCellSelectionEnabled(false);

		
		
		
		
		
		memoire.setFont(new Font ("Verdana",Font.ITALIC,16));
		memoireScrollable = new JScrollPane(memoire,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 
		
		console.setEditable(false);
		console.setOpaque(true);
		console.setForeground(Color.GREEN) ;
		console.setFont(new Font ("Times",Font.ITALIC,12));
		console.setBackground(new Color(10,10,10));
		consoleScrollable = new JScrollPane(console,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 
		
	/*	afficheConsole(console, PHRASE) ;
		String[] test = coupeString(PHRASE) ;
		for(int i = 0 ; i < test.length; i ++) {
			System.out.println(test[i]) ;
		}*/
		
		add(memoireScrollable);
		
		add(consoleScrollable);
		
		
		
	}
	

	
	
	/**
	 * Méthode qui affiche un message sur un textPane (la console).
	 * @param phrase String qui sera le texte affiché.
	 * @param textPane JTextPane qui sera le support du texte entré.
	 */
	public void afficheMessage(JTextPane textPane, String phrase) {
		textPane.setEditable(true);
		appendToPane(textPane,phrase,MSG) ;
		appendToPane(textPane,"\n[Console ]:\n",MSG);
		textPane.setEditable(false);
		}
	/**
	 * Méthode qui affiche une "erreur" sur un textPane (la console).
	 * @param error String qui sera le texte affiché.
	 * @param textPane JTextPane qui sera le support du texte entré.
	 */
	public void afficheError(JTextPane textPane, String error) {
		textPane.setEditable(true);
		appendToPane(textPane,error,ERROR) ;
		appendToPane(textPane,"\n[Console ]:\n",MSG);
		textPane.setEditable(false);
		}
	
	/**
	 * Méthode qui permet d'effacer la console
	 * @param textPane JTextPane qui est la console à effacer.
	 */
	protected void Clear(JTextPane textPane) {
		textPane.setText("[Console ]:\n");		
	}
	
	/**
	 * Méthode utilise par @see afficheMessage et @see afficheError pour afficher le texte sur la console.
	 * @param tp JTextPane qui sera le support du texte entré.
	 * @param msg String qui est le message à afficher.
	 * @param c Color quie est la couleur du message.
	 */
	private void appendToPane(JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Times");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }
	
	/**
	 * Méthode qui permet d'obtenir le champ memory.
	 * @return memory Le champ memory.
	 */
	public Memory getMemory() {
	 return memory ;
 }
	
	/**
	 * Méthode qui permet d'obtenir le champ console.
	 * @return console JTextPane qui est le champ console.
	 */
	public JTextPane getConsole() {
	 return console ;
 }



}