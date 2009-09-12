/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

import net.sourceforge.atunes.utils.DesktopUtils;

/**
 * The Class UrlTextArea.
 */
public class UrlTextArea extends JTextArea {

    private static final long serialVersionUID = -8368596300673361747L;

    /** The url. */
    String url;

    /**
     * Instantiates a new url text area.
     */
    public UrlTextArea() {
        super();
        setListenersAndVisual();
    }

    /**
     * Instantiates a new url text area.
     * 
     * @param text
     *            the text
     */
    public UrlTextArea(String text) {
        super(text);
        setListenersAndVisual();
    }

    /**
     * Sets the listeners and visual.
     */
    private void setListenersAndVisual() {
        setBorder(BorderFactory.createEmptyBorder());
        setEditable(false);
        setOpaque(false);
        setLineWrap(true);
        setWrapStyleWord(true);
        // setForeground(Color.BLUE);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DesktopUtils.openURL(url);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // setForeground(Color.GREEN.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // setForeground(Color.BLUE);
            }
        });
    }

    /**
     * Sets the url.
     * 
     * @param url
     *            the new url
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
