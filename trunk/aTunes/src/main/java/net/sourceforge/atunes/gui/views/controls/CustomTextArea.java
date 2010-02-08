package net.sourceforge.atunes.gui.views.controls;

import javax.swing.JTextArea;
import javax.swing.text.Document;

/**
 * Custom text area with edition popup
 * @author fleax
 *
 */
public class CustomTextArea extends JTextArea {

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
	public CustomTextArea() {
		super();
	}
	
	/**
	 * Constructor with document
	 * @param doc
	 */
	public CustomTextArea(Document doc) {
		super(doc);
	}
	
	/**
	 * Constructor with text
	 * @param text
	 */
	public CustomTextArea(String text) {
		super(text);
	}

	/**
	 * Constructor with rows and columns
	 * @param rows
	 * @param columns
	 */
	public CustomTextArea(int rows, int columns) {
		super(rows, columns);
	}
	
	/**
	 * Constructor with document, text, rows and columns
	 * @param doc
	 * @param text
	 * @param rows
	 * @param columns
	 */
	public CustomTextArea(Document doc, String text, int rows, int columns) {
		super(doc, text, rows, columns);
	}

}
