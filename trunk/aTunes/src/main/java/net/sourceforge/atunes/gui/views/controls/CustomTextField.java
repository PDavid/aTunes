/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.gui.views.controls;

import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 * Custom text field with edition popup
 * 
 * @author fleax
 * 
 */
public class CustomTextField extends JTextField {

    private static final long serialVersionUID = 3642106434825237789L;

    {
        new EditionPopUpMenu(this);
    }

    /**
     * Default constructor
     */
    public CustomTextField() {
        super();
    }

    /**
     * Constructor with number of columns
     * 
     * @param columns
     */
    public CustomTextField(int columns) {
        super(columns);
    }

    /**
     * Constructor with text
     * 
     * @param text
     */
    public CustomTextField(String text) {
        super(text);
    }

    /**
     * Constructor with text and columns
     * 
     * @param text
     * @param columns
     */
    public CustomTextField(String text, int columns) {
        super(text, columns);
    }

    /**
     * Constructor with document, text and columns
     * 
     * @param doc
     * @param text
     * @param columns
     */
    public CustomTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
    }

}
