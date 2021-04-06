package Tokens;

public class Phrase extends Token{

	public Phrase(String phrase) {
		super(phrase.subSequence(1, phrase.length()-1).toString());
		
	}

}
