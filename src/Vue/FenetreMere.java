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
 * La classe qui va créée l'interface graphique de l'application, à partir de différents composants graphiques.
 * @see Vue.ConsoleAndMemory
 * @see Vue.Memory
 * @author Kosin
 */

public class FenetreMere extends JFrame implements ActionListener{

	
	/**
	 * JPanel où sont contenu tous les autres élèments graphiques.
	 */
	JPanel contentPane ;
	
	
	/**
	 * JPanel qui contient la zone de texte pour le code C, ainsi que la console et la table récapitulant les variables.
	 */
	JPanel panel ;

	/**
	 * JPanel qui contient tous les différents boutons afin d'intéragir avec l'application.
	 */
	JPanel ExecutePane ;
	
	/**
	 * JPanel qui contient tous les boutons qui permettent de lancer la lecture du code ou de la réinitialiser.
	 */
	JPanel ExePane;
	
	/**
	 * DefaulHighlighterPainter qui définit la couleur du surlignement lors de la lecture "Pas à pas".
	 */
	DefaultHighlightPainter couleurHighlight = new DefaultHighlighter.DefaultHighlightPainter(Color.red);
	
	/**
	 * JTextPane qui contient le texte en C à traiter.
	 */
	JTextPane InterfaceC ;
	
	/**
	 * JScrollPane qui contient InterfaceC.
	 */
	JScrollPane InterfacePane; 
	
	/**f
	 * ConsoleAndMemory qui gère graphiquement la console et la mémoire.
	 */
	static ConsoleAndMemory ConsoleMemoire ;
	
	/**
	 * JLabel et JTextField qui gère l'entre de donner par l'utilisateur (suite à un scanf par exemple).
	 */
	JLabel InputLT;
	JLabel InputL ;
	JTextField InputT = new JTextField(10) ;
	
	/**
	 * JButton qui sert à effacer le contenu de InterfaceC.
	 */
	JButton Erase ;
	/**
	 * JButton qui sert à lancer la lecture de la ligne suivante du code C (Mode Pas à pas).
	 */
	JButton Execute ;
	/**
	 * JButton qui sert à lancer la lecture de la ligne précédente du code C.
	 */
	JButton BackExecute ;
	/**
	 * JButton qui sert à lancer la lecture de tout le code C.
	 */
	JButton AllExecute ;
	/**
	 * JButton qui sert à réinitialiser la lecture du mode Pas à pas.
	 */
	JButton Reset ;
	/**
	 * JButton qui sert à valider la valeur entrée par l'utilisateur (lors d'un scanf par exemple).
	 */
	JButton InputValider ;
	
	/**
	 * String qui indice la ligne de la table.
	 */
	static String Indice = "0" ;
	/**
	 * int qui correspont à l'index du caractère qui vient d'être lu (fin d'une ligne).
	 */
	int indiceLecture = 0 ;
	/**
	 * int qui correspond au nombre de ligne présent dans le code.
	 */
	int ligneActive = 0 ;
	/**
	 *int qui enregistre l'index afin de pourvoir le restaurer ulterieurement dans la méthode listeRepere().
	 */
	int indiceLecturePrec = 0 ;
	/**
	 * int qui correspond au début de la ligne actuellement lue.
	 */
	int iteration = 0 ;
	
	/**
	 * ArrayList qui contient l'index de tous les débuts de ligne du code saisi par l'utilisateur.
	 */
	ArrayList<Integer> refDebutLigne = new ArrayList<Integer>() ;
	
	/**
	 * String qui recoit les valeurs entrées par l'utilisateur
	 */
	String EntreeScanf ;
	
	static Code codeObjet ;
	
	Object valeurRetour ;
	
	HashMap<String, Variable> valeurLecture ;
	
	
	/**
	 * Valeurs de tests
	 */
	/*static String [] VALEURS_TEST = {nextIndice(),"a","int","15","1564"} ;
	static String [] VALEURS_TEST_DEUX = {nextIndice(),"b","string","abc"} ;*/
	
	static String[] PHRASE = {"\"Tout le malheur des hommes vient d'un seule chose, qui est de ne pas savoir demeurer au repos dans une chambre.\"",
			"\"Tout ce qui ne me tues pas, me rends plus fort.\"",
			"\"L'amour a ses raisons que la raison ignore.\""};
	
	static String ERREUR = "\"Je pense donc je suis.\"" ;
	
	String testInterfaceC = "int a = 15 ;\nint b = 10 ;\nint c = 154 ;\n" ;
	
	
	/**
	 * Constructeur de la classe FenetreMere qui crée l'interface graphique de l'application, à partir de différents composants graphiques.
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
		 BackExecute = new JButton("Lire ligne précédente") ;
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
		InterfaceC.setText(testInterfaceC);
		
		
		
		
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
	 * Méthode qui renvoie l'id de chaque variable (valeur incrémentielle par ordre de création) dans la table.
	 * @return Indice String le numéro de l'id.
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
	 * Méthode qui ajoute une ligne à la table.
	 * @param valeur String[] liste des valeurs à ajouter dans la ligne.
	 */
	private static void ajoutTable(String[] valeur) {
		
		ConsoleMemoire.getMemory().addRow(valeur) ;
	}

	/**
	 * Méthode qui fait une lecture complète du code, afin de reperer chauque début de ligne et l'enregistre dans le champs refDebutLigne.
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
	 * Méthode qui envoie une ligne du code C entré par l'utilisateur.
	 * @throws BadLocationException si iteration superieur à indiceLecture+1.
	 */
	public String getLigne() throws BadLocationException {
		String text = "" ;
		String ligne = "";
		if(InterfaceC.getText() != "" && InterfaceC.getText().length() > 0 ) {
			text = InterfaceC.getText() ;
			text += ';' ;
			
			
			InterfaceC.getHighlighter().removeAllHighlights();
		while(text.charAt(indiceLecture) != ';') {
			
			char a = text.charAt(indiceLecture);
			if((int)a != 10) {
			ligne += String.valueOf(a) ;
			}
			indiceLecture ++ ;
		}
		ligne += String.valueOf(text.charAt(indiceLecture)) ;
		indiceLecture ++ ;
		InterfaceC.getHighlighter().addHighlight(iteration, indiceLecture, couleurHighlight) ;

		iteration = indiceLecture+1 ;
		}
		return ligne ;
	}
		/**
		 * Méthode qui lit la ligne et ajout les variables en mémoire
		 * @param ligne
		 */
		public void lectureLigne(String ligne) {
		valeurRetour = codeObjet.execLigne(ligne) ;
		if( valeurRetour instanceof String && valeurRetour != Parser.Parser.FIN_EXEC) {
			indiceLecture = 0 ;
			ligneActive =  0;
			iteration = 0 ;
			codeObjet.reset();
			affichePrintf((String)valeurRetour , 1) ;
			ConsoleMemoire.getMemory().setRowCount(0) ;
			InterfaceC.getHighlighter().removeAllHighlights();
			InterfaceC.setEditable(true);
			BackExecute.setVisible(false);
		}
		else if(valeurRetour instanceof String && valeurRetour == Parser.Parser.FIN_EXEC) {
			affichePrintf((String)valeurRetour , 0) ;
			Execute.setVisible(false);
		}
		else {
			valeurLecture = (HashMap<String, Variable>)valeurRetour ;
			String[] valeur = new String[4] ;
			ConsoleMemoire.getMemory().setRowCount(0) ;
			Indice = "0";
			for(String i : valeurLecture.keySet()) {
			valeur[0] = nextIndice() ;
			valeur[1] = i ;
			valeur[2] = valeurLecture.get(i).getType() ;
			valeur[3] = String.valueOf(valeurLecture.get(i).getValeurAffichage()) ;
			ajoutTable(valeur) ;
			}
		}
		if(ligneActive == 0) {
			BackExecute.setVisible(false) ;
		}
		}	

	
	/**
	 * Méthode qui renvoie le nombre de ligne totale du code entré.
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
	 * Méthode qui affiche un texte dans la console (ConsoleMemoire)
	 * @param message String qui correspond au message à écrire
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
	
	public String getScanF() {
		return InputT.getText() ;
	}

	/**
	 * Méthode qui écoute et attend l'action d'un bouton.
	 * @param event ActionEvent qui correspond au signal envoyé par les boutons.
	 */
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stubm

	  if(event.getActionCommand() == "reset"){
			//ConsoleMemoire.Clear(ConsoleMemoire.getConsole());
			indiceLecture = 0 ;
			ligneActive =  0;
			iteration = 0 ;
			Indice = "0" ;
			codeObjet.reset();
			ConsoleMemoire.getMemory().setRowCount(0) ;
			InterfaceC.getHighlighter().removeAllHighlights();
			InterfaceC.setEditable(true);
			Execute.setVisible(true);
			BackExecute.setVisible(false);
			
		}
	  else if(event.getActionCommand() == "allExec") {
			for(int k = 0; k < getNbLigne()+1-ligneActive ; k++ )
				try {			
					if(refDebutLigne.isEmpty()) {
					ListeRepere();
				}
					ligneActive ++ ;
					lectureLigne(getLigne());
					
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			InterfaceC.getHighlighter().removeAllHighlights();
			BackExecute.setVisible(true);
			Execute.setVisible(false) ;
			
		}
		else if(event.getActionCommand() == "backLigne") {
			if(ligneActive > 0) {
				try {
				ligneActive -= 1 ;
				if(ligneActive == 0) {
					indiceLecture = 0 ;
				}
				else {
				indiceLecture = refDebutLigne.get(ligneActive-1) ;
				}
				iteration = indiceLecture ;
				codeObjet.backLine();
				InterfaceC.getHighlighter().removeAllHighlights();
				if(ligneActive != 0) {
				getLigne();
				}
				else {
					codeObjet.reset();
					ConsoleMemoire.getMemory().setRowCount(1) ;
					InterfaceC.getHighlighter().removeAllHighlights();
					InterfaceC.setEditable(true);
					BackExecute.setVisible(false);
				}
				ConsoleMemoire.getMemory().setRowCount(ConsoleMemoire.getMemory().getRowCount()-1);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		/*	else {
				ConsoleMemoire.getMemory().setRowCount(ConsoleMemoire.getMemory().getRowCount()-1);
				InterfaceC.getHighlighter().removeAllHighlights();
				ligneActive -= 1 ;
				codeObjet.backLine();
				iteration = indiceLecture ;
				BackExecute.setVisible(false);
			}*/

			Execute.setVisible(true);
		}
		else if(event.getActionCommand() == "ligne") {
			ligneActive ++ ;
			if(refDebutLigne.isEmpty()) {
			ListeRepere();
			}
			InterfaceC.setEditable(false);
			if(ligneActive == 0) {
				BackExecute.setVisible(false);
			}
			else {
				BackExecute.setVisible(true);
			}
			try {

				lectureLigne(getLigne());
				
				
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	 * Méthode principale qui execute et crée une fenetre.
	 * @param args String[] qui contient le nom de la fenetre.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		codeObjet = new Code() ;
		FenetreMere fenetre = new FenetreMere("Interpréteur C") ;
		fenetre.setResizable(false);

		

	}
	
	
 private void debug(Object debug) {
	 System.out.println(debug);
 }
	
	
	
	
	
}