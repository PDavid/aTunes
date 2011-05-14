/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

import javax.swing.JTextArea;
import javax.swing.text.Document;

/**
 * Custom text area with edition popup
 * 
 * @author fleax
 */
public class CustomTextArea extends JTextArea {

    private static final long serialVersionUID = 3642106434825237789L;

    {
        new EditionPopUpMenu(this);
    }

    /**
     * Default constructor
     */
    public CustomTextArea() {
        super();
    }

    /**
     * Constructor with document
     * 
     * @param doc
     */
    public CustomTextArea(Document doc) {
        super(doc);
    }

    /**
     * Constructor with text
     * 
     * @param text
     */
    public CustomTextArea(String text) {
        super(text);
    }

    /**
     * Constructor with rows and columns
     * 
     * @param rows
     * @param columns
     */
    public CustomTextArea(int rows, int columns) {
        super(rows, columns);
    }

    /**
     * Constructor with document, text, rows and columns
     * 
     * @param doc
     * @param text
     * @param rows
     * @param columns
     */
    public CustomTextArea(Document doc, String text, int rows, int columns) {
        super(doc, text, rows, columns);
    }

}
