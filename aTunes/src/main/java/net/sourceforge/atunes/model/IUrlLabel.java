package net.sourceforge.atunes.model;

import java.awt.Font;

/**
 * Graphical component with text and url
 * 
 * @author alex
 * 
 */
public interface IUrlLabel {

	/**
	 * Sets the text and url
	 * 
	 * @param text
	 * @param url
	 */
	void setText(final String text, final String url);

	/**
	 * @param font
	 */
	void setFont(Font font);

	/**
	 * @param b
	 */
	void setFocusPainted(boolean b);

	/**
	 * @param alignment
	 */
	void setHorizontalAlignment(int alignment);
}
