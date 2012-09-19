/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.CustomTextArea;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class MakeDonationDialog.
 */
public final class MakeDonationDialog extends AbstractCustomDialog {

    private static final long serialVersionUID = 4369595555397951445L;

    private IDesktop desktop;
    
    private String donationUrl;
    
    /**
     * Instantiates a new repository selection info dialog.
     * @param frame
     */
    public MakeDonationDialog(IFrame frame) {
        super(frame, 500, 300);
    }
    
    /**
     * @param donationUrl
     */
    public void setDonationUrl(String donationUrl) {
		this.donationUrl = donationUrl;
	}
    
    /**
     * @param desktop
     */
    public void setDesktop(IDesktop desktop) {
		this.desktop = desktop;
	}
    
    @Override 
    public void initialize() {
        setResizable(false);
        setTitle(I18nUtils.getString("MAKE_DONATION"));
        setContent();
    }

    /**
     * Sets the content.
     */
    private void setContent() {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel icon = new JLabel(Images.getImage(Images.APP_LOGO_90));
        JTextArea text = new CustomTextArea(I18nUtils.getString("MAKE_DONATION_INFO"));
        text.setOpaque(false);
        text.setEditable(false);
        text.setWrapStyleWord(true);
        text.setLineWrap(true);
        text.setBorder(BorderFactory.createEmptyBorder());
        JLabel donateButton = new JLabel(Images.getImage(Images.PROJECT_SUPPORT));
        donateButton.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		desktop.openURL(donationUrl);
        	}
		});
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(20, 20, 20, 20);
        panel.add(icon, c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(20, 0, 20, 20);
        panel.add(text, c);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.weighty = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 0, 0, 0);
        panel.add(donateButton, c);
        add(panel);
    }
}