/*
 * aTunes 2.0.0-SNAPSHOT
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

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.CustomWindow;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class SplashScreenDialog.
 */
public final class SplashScreenDialog extends CustomWindow {

    private static final long serialVersionUID = -7279259267018738903L;

    /**
     * Instantiates a new splash screen dialog.
     */
    public SplashScreenDialog() {
        super(null, 475, 200);
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
        new SplashScreenDialog().setVisible(true);
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
        image.setSize(new Dimension(475, 200));
        image.setLocation(0, 0);

        JLabel label = new JLabel(StringUtils.getString(Constants.APP_VERSION, "  ", Character.valueOf((char) 169), " ", Constants.APP_AUTHOR));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(Fonts.getAppVersionLittleFont());
        label.setForeground(ColorDefinitions.TITLE_DIALOG_FONT_COLOR);
        label.setSize(475, 20);
        label.setLocation(0, 170);

        panel.add(label);
        panel.add(image);

        return panel;
    }
}
