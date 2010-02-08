package net.sourceforge.atunes.gui.views.controls;

import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 * Custom text field with edition popup
 * @author fleax
 *
 */
public class CustomTextField extends JTextField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3642106434825237789L;

	/**
	 * Menu associated
	 */
	private EditionPopUpMenu menu = new EditionPopUpMenu(this);
		
	/**
	 * Default constructor
	 */
	public CustomTextField() {
		super();
	}
	
	/**
	 * Constructor with number of columns
	 * @param columns
	 */
	public CustomTextField(int columns) {
		super(columns);
	}
	
	/**
	 * Constructor with text
	 * @param text
	 */
	public CustomTextField(String text) {
		super(text);
	}

	/**
	 * Constructor with text and columns
	 * @param text
	 * @param columns
	 */
	public CustomTextField(String text, int columns) {
		super(text, columns);
	}
	
	/**
	 * Constructor with document, text and columns
	 * @param doc
	 * @param text
	 * @param columns
	 */
	public CustomTextField(Document doc, String text, int columns) {
		super(doc, text, columns);
	}

}
