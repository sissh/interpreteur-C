package Vue;

import javax.swing.* ;
import javax.swing.text.* ;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

import java.awt.* ;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FenetreMere extends JFrame implements ActionListener{

	JPanel contentPane ;
	JPanel Block  ;
	
	JPanel panel ;
	JPanel InputPanel ;

	
	JPanel ExecutePane ;
	JPanel ExePane;
	DefaultHighlightPainter couleurHighlight = new DefaultHighlighter.DefaultHighlightPainter(Color.red);
	
	JTextPane InterfaceC ;
	
	JScrollPane InterfacePane; 
	
	static ConsoleAndMemory ConsoleMemoire ;
	
	JLabel InputLT;
	JLabel InputL ;
	JTextField InputT = new JTextField(10) ;
	
	JButton Erase ;
	JButton Execute ;
	JButton AllExecute ;
	JButton Reset ;
	JButton InputValider ;
	
	static String Indice = "0" ;
	int indiceLecture = 0 ;
	int ligneActive = 0 ;
	int iteration = 0 ;
	
	static String [] VALEURS_TEST = {nextIndice(),"a","int","15"} ;
	static String [] VALEURS_TEST_DEUX = {nextIndice(),"b","string","abc"} ;
	
	static String[] PHRASE = {"\"Tout le malheur des hommes vient d'un seule chose, qui est de ne pas savoir demeurer au repos dans une chambre.\"",
			"\"Tout ce qui ne me tues pas, me rends plus fort.\"",
			"\"L'amour a ses raisons que la raison ignore.\""};
	
	static String ERREUR = "\"Je pense donc je suis.\"" ;
	
	
	
	
	public FenetreMere(String parTitre){
		super(parTitre) ;
		

		
		contentPane = new JPanel();
		Block = new JPanel() ;
		
		panel = new JPanel() ;
		

		
		 ExecutePane = new JPanel() ;
		 ExePane = new JPanel();
		 InputPanel = new JPanel() ;
		
		
		 InterfaceC = new JTextPane() ;
		
		 InterfacePane = new JScrollPane(InterfaceC,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
		
		 ConsoleMemoire = new ConsoleAndMemory() ;
		
		 InputLT = new JLabel() ;
		 InputL = new JLabel("Input :") ;
		 InputT = new JTextField(10) ;
		 InputValider = new JButton("Entrer") ;
		 
		 Erase = new JButton("Effacer") ;
		 Execute = new JButton("Lire prochaine ligne") ;
		 AllExecute = new JButton("Tout executer") ;
		 Reset = new JButton("RESET") ;
		
		 
		 Reset.setActionCommand("reset") ;
		 Reset.addActionListener(this) ;
		 
		 AllExecute.setActionCommand("allExec") ;
		 AllExecute.addActionListener(this) ;
		 
		 Execute.addActionListener(this);
		 Execute.setActionCommand("ligne") ;
		 
		 Erase.addActionListener(this);
		 Erase.setActionCommand("erase");


		
		
		contentPane.setLayout(new BorderLayout()) ;
		
		panel.setLayout(new GridLayout(1,2)) ;
		ExecutePane.setLayout(new GridLayout(2,2)) ;
		InputLT.setLayout(new BorderLayout()) ;
		setContentPane(contentPane);
		
		
		
		InterfaceC.setSize(500,500) ;
		InterfaceC.setText("#include <stdio.h>\n" + 
				"\n" + 
				"int main(void)\n" + 
				"{\n" + 
				"	char a[15] = \"Hello world\" ;\n" + 
				"	int chiffre = 18 ;\n" + 
				"	printf(\"%s\\n\", a) ;\n" + 
				"	printf(\"Welcome in the wonderful world of C%d programing\\n\", chiffre);\n" + 
				"	return 0;\n" + 
				"} \n" + 
				"");
		
		
		
		
		//InterfacePane.add(InterfaceC) ;
		
		ExePane.add(Execute) ;
		ExePane.add(AllExecute);
		ExePane.add(Reset) ;
		
		InputLT.add(InputL,BorderLayout.WEST) ;
		InputLT.add(InputT,BorderLayout.CENTER);
		InputLT.add(InputValider, BorderLayout.EAST) ;

		ExecutePane.add(Erase) ;
		ExecutePane.add(ExePane) ;
		ExecutePane.add(InputLT) ;
		
		
		
		panel.add(InterfacePane) ;
		panel.add(ConsoleMemoire) ;
		
		contentPane.add(Block, BorderLayout.WEST)  ;
		contentPane.add(panel,BorderLayout.CENTER) ;
		
		contentPane.add(ExecutePane, BorderLayout.SOUTH) ;
		
		
		
		
		setDefaultCloseOperation(EXIT_ON_CLOSE) ;
		contentPane.setBackground(new Color(200,120,70));
		setSize(1000,600);
		setVisible(true);
		setLocation(400,300);
		
	}
	
	public static String nextIndice() {
		int indice = Integer.parseInt(Indice) ;
		indice += 1;
		Indice = String.valueOf(indice) ;
		return Indice ;
	}

	/*public Insets getInsets (){
		return new Insets (40,40,40,40);
	} CECI CREE UNE FENETRE EN ARRIRE PLAN */ 

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FenetreMere fenetre = new FenetreMere("Interpréteur C") ;
		fenetre.setResizable(false);
		ConsoleMemoire.getMemory().setValue(VALEURS_TEST);

		ConsoleMemoire.getMemory().setValue(VALEURS_TEST_DEUX);

	}
	
	public void getLigne() throws BadLocationException {
		String text = "\n" ;
		String ligne = "";
		
		if(InterfaceC.getText() != "" && InterfaceC.getText().length() != 0 ) {
			text = InterfaceC.getText() ;
		
			if(text.charAt(text.length()-1) != '\n') {
				text += '\n' ;
			}
			
			
			InterfaceC.getHighlighter().removeAllHighlights();
		while(text.charAt(indiceLecture) != '\n') {
			ligne += String.valueOf(text.charAt(indiceLecture)) ;
			indiceLecture ++ ;
		}
		InterfaceC.getHighlighter().addHighlight(iteration, indiceLecture, couleurHighlight) ;
		
		indiceLecture ++ ;
		iteration = indiceLecture ;
		
		}
		
		System.out.println(ligne) ;
	}
	
	private int getNbLigne() {
		String text = InterfaceC.getText();
		int nbLigne = 0 ;
		if(text.length() > 0 && text.charAt(text.length()-1) != '\n') {
			text += '\n' ;
		}
		for(int i = 0 ; i < text.length() ; i++) {
			if(text.charAt(i) == '\n') {
				nbLigne++ ;
			}
		}
		return nbLigne ;
	}
	


	
	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stubm
		if(event.getActionCommand() == "reset"){
			//ConsoleMemoire.Clear(ConsoleMemoire.getConsole());
			indiceLecture = 0 ;
			ligneActive =  0;
			iteration = 0 ;
			InterfaceC.getHighlighter().removeAllHighlights();
			InterfaceC.setEditable(true);
			
		}else if(event.getActionCommand() == "allExec") {
			for(int k = 0; k < getNbLigne() ; k++ )
				try {
					getLigne();
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			InterfaceC.getHighlighter().removeAllHighlights();
			ConsoleMemoire.afficheMessage(ConsoleMemoire.getConsole(),"Lecture terminée") ;
			
		}
		else if(event.getActionCommand() == "ligne") {
			ligneActive ++ ;
			InterfaceC.setEditable(false);
			if(getNbLigne() >= ligneActive) {
				try {
					getLigne();
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				indiceLecture = 0 ;
				ligneActive =  0;
				iteration = 0 ;
			}
			//ConsoleMemoire.afficheError(ConsoleMemoire.getConsole(),ERREUR) ;
		}
		else if(event.getActionCommand() == "erase") {
			indiceLecture = 0 ;
			ligneActive =  0;
			iteration = 0 ;
			InterfaceC.setText("");
		}
	}
	

}