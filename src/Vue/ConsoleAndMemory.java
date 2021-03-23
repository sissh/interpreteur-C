package Vue;


import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.*;
import javax.swing.text.*;


public class ConsoleAndMemory extends JPanel {
	
	JTextPane console ;
	JScrollPane consoleScrollable ;
	Memory memory ;
	JScrollPane memoireScrollable ;
	JTable memoire ;
	Color ERROR = Color.RED ;
	Color MSG = Color.GREEN ;
	
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
	

	
	
	
	public void afficheMessage(JTextPane textPane, String phrase) {
		textPane.setEditable(true);
		appendToPane(textPane,phrase,MSG) ;
		appendToPane(textPane,"\n[Console ]:\n",MSG);
		textPane.setEditable(false);
		}
	
	public void afficheError(JTextPane textPane, String error) {
		textPane.setEditable(true);
		appendToPane(textPane,error,ERROR) ;
		appendToPane(textPane,"\n[Console ]:\n",MSG);
		textPane.setEditable(false);
		}
	
	
	protected void Clear(JTextPane textPane) {
		textPane.setText("[Console ]:\n");		
	}
	
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
	
	
	public Memory getMemory() {
	 return memory ;
 }
	

	public JTextPane getConsole() {
	 return console ;
 }



}