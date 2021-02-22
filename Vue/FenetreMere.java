package Vue;

import javax.swing.* ;

import java.awt.* ;

public class FenetreMere extends JFrame{

	JPanel contentPane ;
	JPanel Block  ;
	
	JPanel panel ;
	

	
	JPanel ExecutePane ;
	JPanel ExePane;
	
	
	JTextArea InterfaceC ;
	
	JScrollPane InterfacePane; 
	
	static ConsoleAndMemory ConsoleMemoire ;
	
	JLabel InputLT;
	JLabel InputL ;
	JTextField InputT = new JTextField(10) ;
	
	JButton Execute ;
	JButton AllExecute ;
	JButton Reset ;
	
	static String Indice = "0" ;
	
	static String [] VALEURS_TEST = {nextIndice(),"a","int","15"} ;
	static String [] VALEURS_TEST_DEUX = {nextIndice(),"b","string","abc"} ;
	
	public FenetreMere(String parTitre){
		super(parTitre) ;
		

		
		contentPane = new JPanel();
		Block = new JPanel() ;
		
		panel = new JPanel() ;
		

		
		 ExecutePane = new JPanel() ;
		 ExePane = new JPanel();
		
		
		 InterfaceC = new JTextArea(100,54) ;
		
		 InterfacePane = new JScrollPane(InterfaceC,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
		
		 ConsoleMemoire = new ConsoleAndMemory() ;
		
		 InputLT = new JLabel() ;
		 InputL = new JLabel("Input :") ;
		 InputT = new JTextField(10) ;
		
		 Execute = new JButton("Lire prochaine ligne") ;
		 AllExecute = new JButton("Tout executer") ;
		 Reset = new JButton("RESET") ;
		
		 
		 
		


		
		
		contentPane.setLayout(new BorderLayout()) ;
		
		panel.setLayout(new GridLayout(1,2)) ;
		ExecutePane.setLayout(new GridLayout(1,2)) ;
		InputLT.setLayout(new GridLayout(2,1)) ;
		
		setContentPane(contentPane);
		
		
		
		InterfaceC.setSize(500,500) ;
		
		
		
		
		
		//InterfacePane.add(InterfaceC) ;
		
		ExePane.add(Execute) ;
		ExePane.add(AllExecute);
		ExePane.add(Reset) ;
		
		InputLT.add(InputL) ;
		InputLT.add(InputT);
		
		ExecutePane.add(InputLT,BorderLayout.NORTH) ;
		ExecutePane.add(ExePane, BorderLayout.SOUTH) ;
		
		
		
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

}