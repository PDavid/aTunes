package net.sourceforge.atunes.gui.views.controls;

import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * A CustomTextPane is a JTextPane using the fonts and colors configured for all application 
 * @author alex
 *
 */
public class CustomTextPane extends JTextPane {
	
	private static final long serialVersionUID = -3601855261867415475L;

	public CustomTextPane(int alignment) {
		super();		
 		MutableAttributeSet mainStyle = new SimpleAttributeSet();
		StyleConstants.setAlignment(mainStyle, alignment);
 		StyleConstants.setFontFamily(mainStyle, UIManager.getFont("Label.font").getFamily());
 		StyleConstants.setFontSize(mainStyle, UIManager.getFont("Label.font").getSize());
 		StyleConstants.setForeground(mainStyle, UIManager.getColor("Label.foreground"));
		getStyledDocument().setParagraphAttributes(0, 0, mainStyle, true);
	}	
} 
