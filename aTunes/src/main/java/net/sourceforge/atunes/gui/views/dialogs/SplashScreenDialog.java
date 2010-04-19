/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomWindow;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class SplashScreenDialog.
 */
public final class SplashScreenDialog extends AbstractCustomWindow {

    private static final long serialVersionUID = -7279259267018738903L;

    private Font versionAndCopyrightFont;
    
    private Color versionAndCopyrightColor;
    
    /**
     * Instantiates a new splash screen dialog.
     * @param versionAndCopyrightFont
     * @param versionAndCopyrightColor
     */
    public SplashScreenDialog(Font versionAndCopyrightFont, Color versionAndCopyrightColor) {
        super(null, 500, 200);
        this.versionAndCopyrightColor = versionAndCopyrightColor;
        this.versionAndCopyrightFont = versionAndCopyrightFont;
        add(getContent());
        GuiUtils.applyComponentOrientation(this);
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        new SplashScreenDialog(null, null).setVisible(true);
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    private JPanel getContent() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        JLabel image = new JLabel(Images.getImage(Images.APP_TITLE));
        image.setSize(new Dimension(500, 200));
        image.setLocation(0, 0);

        JLabel version = new JLabel(Constants.APP_VERSION);
        version.setHorizontalAlignment(SwingConstants.CENTER);
        if (versionAndCopyrightColor != null) {
        	version.setForeground(versionAndCopyrightColor);
        }
        version.setSize(265, 20);
        version.setLocation(200, 120);
        if (versionAndCopyrightFont != null) {
        	version.setFont(versionAndCopyrightFont);
        }
        
        JLabel copyright = new JLabel(StringUtils.getString(Character.valueOf((char) 169), " ", Constants.APP_AUTHOR));
        copyright.setHorizontalAlignment(SwingConstants.CENTER);
        if (versionAndCopyrightColor != null) {
        	copyright.setForeground(versionAndCopyrightColor);
        }
        copyright.setSize(265, 20);
        copyright.setLocation(200, 140);
        if (versionAndCopyrightFont != null) {
        	copyright.setFont(versionAndCopyrightFont);
        }
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setSize(180, 12);
        progressBar.setLocation(243, 170);
        progressBar.setBorder(null);

        panel.add(version);
        panel.add(copyright);
        panel.add(progressBar);
        panel.add(image);

        return panel;
    }
}
