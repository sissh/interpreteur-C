package Vue;


import Parser.Code;
import Tokens.Variable;

import javax.swing.* ;

import javax.swing.text.* ;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

import java.awt.* ;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * La classe qui va cr��e l'interface graphique de l'application, � partir de diff�rents composants graphiques.
 * @see Vue.ConsoleAndMemory
 * @see Vue.Memory
 * @author Kosin
 */

public class FenetreMere extends JFrame implements ActionListener{

	
	/**
	 * JPanel o� sont contenu tous les autres �l�ments graphiques.
	 */
	JPanel contentPane ;
	
	
	/**
	 * JPanel qui contient la zone de texte pour le code C, ainsi que la console et la table r�capitulant les variables.
	 */
	JPanel panel ;

	/**
	 * JPanel qui contient tous les diff�rents boutons afin d'int�ragir avec l'application.
	 */
	JPanel ExecutePane ;
	
	/**
	 * JPanel qui contient tous les boutons qui permettent de lancer la lecture du code ou de la r�initialiser.
	 */
	JPanel ExePane;
	
	/**
	 * DefaulHighlighterPainter qui d�finit la couleur du surlignement lors de la lecture "Pas � pas".
	 */
	DefaultHighlightPainter couleurHighlight = new DefaultHighlighter.DefaultHighlightPainter(Color.red);
	
	/**
	 * JTextPane qui contient le texte en C � traiter.
	 */
	JTextPane InterfaceC ;
	
	/**
	 * JScrollPane qui contient InterfaceC.
	 */
	JScrollPane InterfacePane; 
	
	/**f
	 * ConsoleAndMemory qui g�re graphiquement la console et la m�moire.
	 */
	static ConsoleAndMemory ConsoleMemoire ;
	
	/**
	 * JLabel et JTextField qui g�re l'entre de donner par l'utilisateur (suite � un scanf par exemple).
	 */
	JLabel InputLT;
	JLabel InputL ;
	JTextField InputT = new JTextField(10) ;
	
	/**
	 * JButton qui sert � effacer le contenu de InterfaceC.
	 */
	JButton Erase ;
	/**
	 * JButton qui sert � lancer la lecture de la ligne suivante du code C (Mode Pas � pas).
	 */
	JButton Execute ;
	/**
	 * JButton qui sert � lancer la lecture de la ligne pr�c�dente du code C.
	 */
	JButton BackExecute ;
	/**
	 * JButton qui sert � lancer la lecture de tout le code C.
	 */
	JButton AllExecute ;
	/**
	 * JButton qui sert � r�initialiser la lecture du mode Pas � pas.
	 */
	JButton Reset ;
	/**
	 * JButton qui sert � valider la valeur entr�e par l'utilisateur (lors d'un scanf par exemple).
	 */
	JButton InputValider ;
	
	/**
	 * String qui indice la ligne de la table.
	 */
	static String Indice = "0" ;
	/**
	 * int qui correspont � l'index du caract�re qui vient d'�tre lu (fin d'une ligne).
	 */
	int indiceLecture = 0 ;
	/**
	 * int qui correspond au nombre de ligne pr�sent dans le code.
	 */
	int ligneActive = 0 ;
	/**
	 *int qui enregistre l'index afin de pourvoir le restaurer ulterieurement dans la m�thode listeRepere().
	 */
	int indiceLecturePrec = 0 ;
	/**
	 * int qui correspond au d�but de la ligne actuellement lue.
	 */
	int iteration = 0 ;
	
	/**
	 * ArrayList qui contient l'index de tous les d�buts de ligne du code saisi par l'utilisateur.
	 */
	ArrayList<Integer> refDebutLigne = new ArrayList<Integer>() ;
	
	/**
	 * String qui recoit les valeurs entr�es par l'utilisateur
	 */
	String EntreeScanf ;
	
	static Code codeObjet ;
	
	Object valeurRetour ;
	
	HashMap<String, Variable> valeurLecture ;
	
	
	/**
	 * Valeurs de tests
	 */
	static String [] VALEURS_TEST = {nextIndice(),"a","int","15","1564"} ;
	static String [] VALEURS_TEST_DEUX = {nextIndice(),"b","string","abc"} ;
	
	static String[] PHRASE = {"\"Tout le malheur des hommes vient d'un seule chose, qui est de ne pas savoir demeurer au repos dans une chambre.\"",
			"\"Tout ce qui ne me tues pas, me rends plus fort.\"",
			"\"L'amour a ses raisons que la raison ignore.\""};
	
	static String ERREUR = "\"Je pense donc je suis.\"" ;
	
	
	
	/**
	 * Constructeur de la classe FenetreMere qui cr�e l'interface graphique de l'application, � partir de diff�rents composants graphiques.
	 * @param parTitre String qui correspond au nom de la fenetre. 
	 */
	public FenetreMere(String parTitre){
		super(parTitre) ;
		

		
		contentPane = new JPanel();
		
		panel = new JPanel() ;
		

		
		 ExecutePane = new JPanel() ;
		 ExePane = new JPanel();
		
		
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
		 BackExecute = new JButton("Lire ligne pr�c�dente") ;
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

		 BackExecute.addActionListener(this);
		 BackExecute.setActionCommand("backLigne");
		 
		 InputValider.setActionCommand("enter") ;
		 InputValider.addActionListener(this) ;
		
		contentPane.setLayout(new BorderLayout()) ;
		
		panel.setLayout(new GridLayout(1,2)) ;
		ExecutePane.setLayout(new GridLayout(2,2)) ;
		ExePane.setLayout(new GridLayout(2,2));
		InputLT.setLayout(new BorderLayout()) ;
		setContentPane(contentPane);
		
		
		
		InterfaceC.setSize(500,500) ;
		InterfaceC.setText("int a = 15 ;\n"+
		"int b = 10 ;\n" +
		"int c = 154 ;\n");
		
		
		
		
		//InterfacePane.add(InterfaceC) ;
		ExePane.add(AllExecute);
		ExePane.add(Reset) ;		
		ExePane.add(BackExecute) ;
		BackExecute.setVisible(false);
		ExePane.add(Execute) ;

		
		InputLT.add(InputL,BorderLayout.WEST) ;
		InputLT.add(InputT,BorderLayout.CENTER);
		InputLT.add(InputValider, BorderLayout.EAST) ;

		ExecutePane.add(Erase) ;
		ExecutePane.add(ExePane) ;
		ExecutePane.add(InputLT) ;
		
		
		
		panel.add(InterfacePane) ;
		panel.add(ConsoleMemoire) ;
		
		
		contentPane.add(panel,BorderLayout.CENTER) ;
		
		contentPane.add(ExecutePane, BorderLayout.SOUTH) ;
		
		
		
		
		setDefaultCloseOperation(EXIT_ON_CLOSE) ;
		contentPane.setBackground(new Color(200,120,70));
		setSize(1000,600);
		setVisible(true);
		setLocation(400,300);
		
	}
	
	/**
	 * M�thode qui renvoie l'id de chaque variable (valeur incr�mentielle par ordre de cr�ation) dans la table.
	 * @return Indice String le num�ro de l'id.
	 */
	public static String nextIndice() {
		int indice = Integer.parseInt(Indice) ;
		indice += 1;
		Indice = String.valueOf(indice) ;
		return Indice ;
	}

	/**public Insets getInsets (){
		return new Insets (40,40,40,40);
	} CECI CREE UNE FENETRE EN ARRIRE PLAN */ 


	
	/**
	 * M�thode qui ajoute une ligne � la table.
	 * @param valeur String[] liste des valeurs � ajouter dans la ligne.
	 */
	private static void ajoutTable(String[] valeur) {
		
		ConsoleMemoire.getMemory().addRow(valeur) ;
	}

	/**
	 * M�thode qui fait une lecture compl�te du code, afin de reperer chauque d�but de ligne et l'enregistre dans le champs refDebutLigne.
	 */
	private void ListeRepere(){
		indiceLecture = 0 ;		
		for(int k = 0; k < getNbLigne() ; k++ ) {
		String text = "" ;
		refDebutLigne.add(indiceLecture) ;
		
		if(InterfaceC.getText() != "" && InterfaceC.getText().length() != 0 ) {
			text = InterfaceC.getText() ;	
			text += ';' ;

		while(text.charAt(indiceLecture) != ';') {
			indiceLecture ++ ;
		}
		
		indiceLecture ++ ;
		
		}
	}
		indiceLecture = 0 ;	
	}
	
	/**
	 * M�thode qui envoie une ligne du code C entr� par l'utilisateur.
	 * @throws BadLocationException si iteration superieur � indiceLecture+1.
	 */
	public void getLigne() throws BadLocationException {
		String text = "" ;
		String ligne = "";
		if(InterfaceC.getText() != "" && InterfaceC.getText().length() > 0 ) {
			text = InterfaceC.getText() ;
		
			
			text += ';' ;
			
			
			
			InterfaceC.getHighlighter().removeAllHighlights();
		while(text.charAt(indiceLecture) != ';') {
			ligne += String.valueOf(text.charAt(indiceLecture)) ;
			indiceLecture ++ ;
		}
		ligne += String.valueOf(text.charAt(indiceLecture)) ;
		indiceLecture ++ ;
		InterfaceC.getHighlighter().addHighlight(iteration, indiceLecture, couleurHighlight) ;
		iteration = indiceLecture+1 ;
		
		
		
		valeurRetour = codeObjet.execLigne(ligne) ;
		if( valeurRetour instanceof String) {
			indiceLecture = 0 ;
			ligneActive =  0;
			iteration = 0 ;
			codeObjet.reset();
			affichePrintf(/*codeObjet.toString()*/(String)valeurRetour , 1) ;
			ConsoleMemoire.getMemory().setRowCount(0) ;
			InterfaceC.getHighlighter().removeAllHighlights();
			InterfaceC.setEditable(true);
			BackExecute.setVisible(false);
		}
		else {
			valeurLecture = (HashMap<String, Variable>)valeurRetour ;
			String[] valeur = new String[4] ;
			for(String i : valeurLecture.keySet()) {
			valeur[0] = nextIndice() ;
			valeur[1] = i ;
			valeur[2] = valeurLecture.get(i).getType().getNom() ;
			valeur[3] = String.valueOf(valeurLecture.get(i).getValeur()) ;
			ajoutTable(valeur) ;
		}
		}
		System.out.println(ligne) ;
		if(ligneActive == 1) {
			BackExecute.setVisible(false) ;
		}
		}
		
	}
	
	
	
	
	
	/**
	 * M�thode qui renvoie le nombre de ligne totale du code entr�.
	 * @return int nbLigne qui est le nombre totale de ligne.
	 */
	private int getNbLigne() {
		String text = InterfaceC.getText();
		int nbLigne = 0 ;
			text += ';' ;
		for(int i = 0 ; i < text.length() ; i++) {
			if(text.charAt(i) == ';') {
				nbLigne++ ;
			}
		}
		return nbLigne ;
	}
	

	/**
	 * M�thode qui affiche un texte dans la console (ConsoleMemoire)
	 * @param message String qui correspond au message � �crire
	 * @param type int qui indique si c'est une erreur ou un message classique
	 */
	public static void affichePrintf(String message, int type){
		if(type == 0) {
			ConsoleMemoire.afficheMessage(ConsoleMemoire.getConsole(),message) ;
		}
		else if(type == 1) {
			ConsoleMemoire.afficheError(ConsoleMemoire.getConsole() , message);
		}
	}

	/**
	 * M�thode qui �coute et attend l'action d'un bouton.
	 * @param event ActionEvent qui correspond au signal envoy� par les boutons.
	 */
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stubm

	  if(event.getActionCommand() == "reset"){
			//ConsoleMemoire.Clear(ConsoleMemoire.getConsole());
			indiceLecture = 0 ;
			ligneActive =  0;
			iteration = 0 ;
			codeObjet.reset();
			ConsoleMemoire.getMemory().setRowCount(0) ;
			InterfaceC.getHighlighter().removeAllHighlights();
			InterfaceC.setEditable(true);
			BackExecute.setVisible(false);
			
		}else if(event.getActionCommand() == "allExec") {
			for(int k = 0; k < getNbLigne() ; k++ )
				try {
					getLigne();
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			InterfaceC.getHighlighter().removeAllHighlights();
			ConsoleMemoire.afficheMessage(ConsoleMemoire.getConsole(),"Lecture termin�e") ;
			indiceLecture = 0 ;
			ligneActive =  0;
			iteration = 0 ;
			
		}
		else if(event.getActionCommand() == "backLigne") {
			if(ligneActive >= 1) {
			ligneActive -= 1 ;
			indiceLecture = refDebutLigne.get(ligneActive-1) ;
			iteration = indiceLecture ;
			try {
				getLigne();
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
		else if(event.getActionCommand() == "ligne") {
			ligneActive ++ ;
			if(refDebutLigne.isEmpty()) {
			ListeRepere();
			}
			System.out.println(refDebutLigne) ;
			InterfaceC.setEditable(false);
			if(getNbLigne() >= ligneActive) {
				
				if(ligneActive >= 1) {
					BackExecute.setVisible(true);
				}
				else {
					BackExecute.setVisible(false);
				}
				try {
					getLigne();
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			else {
				indiceLecture = 0 ;
				ligneActive =  1;
				iteration = 0 ;
				try {
					getLigne();
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//ConsoleMemoire.afficheError(ConsoleMemoire.getConsole(),ERREUR) ;
		}
		else if(event.getActionCommand() == "erase") {
			indiceLecture = 0 ;
			ligneActive =  0;
			iteration = 0 ;
			InterfaceC.setText("");
		}
	  
		else if(event.getActionCommand() == "enter") {
			EntreeScanf = InputT.getText() ;
			affichePrintf(EntreeScanf, 0) ;
		
	}


	}
	/**
	 * M�thode principale qui execute et cr�e une fenetre.
	 * @param args String[] qui contient le nom de la fenetre.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		codeObjet = new Code() ;
		FenetreMere fenetre = new FenetreMere("Interpr�teur C") ;
		fenetre.setResizable(false);

		

	}
}