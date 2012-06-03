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
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.CloseAction;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IProgressDialog;
import net.sourceforge.atunes.utils.I18nUtils;

public class ProgressDialog extends AbstractCustomDialog implements IProgressDialog {

    private static final long serialVersionUID = 5792663290880992661L;

    /** The progress bar. */
    private JProgressBar progressBar;

    /** The image label. */
    private JLabel imagelabel;

    /** The info label. */
    private JLabel infoLabel;

    /** The received label. */
    private JLabel currentLabel;

    /** The total label. */
    private JLabel totalLabel;

    /** The cancel button. */
    private JButton cancelButton;

    /**
     * Instantiates a new transfer progress dialog.
     * 
     * @param frame
     */
    public ProgressDialog(IFrame frame) {
        super(frame, 450, 150, false, CloseAction.DISPOSE);
        add(getContent());
        setResizable(false);
    }

    /**
     * Gets the conentent.
     * 
     * @return the conentent
     */
    private JPanel getContent() {
        JPanel panel = new JPanel(new GridBagLayout());
        progressBar = new JProgressBar();
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        progressBar.setStringPainted(true);
        imagelabel = new JLabel(Images.getImage(Images.APP_LOGO_90));
        infoLabel = new JLabel();
        currentLabel = new JLabel();
        JLabel separatorLabel = new JLabel(" / ");
        totalLabel = new JLabel();
        cancelButton = new JButton(I18nUtils.getString("CANCEL"));

        arrangePanel(panel, separatorLabel);

        return panel;
    }

	/**
	 * @param panel
	 * @param separatorLabel
	 */
	private void arrangePanel(JPanel panel, JLabel separatorLabel) {
		GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 5;
        c.insets = new Insets(0, 20, 0, 0);
        panel.add(imagelabel, c);
        c.gridheight = 1;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 20, 0, 20);
        c.anchor = GridBagConstraints.WEST;
        panel.add(infoLabel, c);
        c.gridx = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(5, 0, 0, 3);
        c.anchor = GridBagConstraints.EAST;
        panel.add(currentLabel, c);
        c.gridx = 3;
        c.insets = new Insets(5, 0, 0, 0);
        panel.add(separatorLabel, c);
        c.gridx = 4;
        c.insets = new Insets(5, 0, 0, 20);
        panel.add(totalLabel, c);
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 1;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 20, 5, 20);
        c.anchor = GridBagConstraints.CENTER;
        panel.add(progressBar, c);
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(cancelButton, c);
	}

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IProgressDialog#setInfoText(java.lang.String)
	 */
    @Override
	public void setInfoText(String s) {
        infoLabel.setText(s);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IProgressDialog#setProgressBarValue(int)
	 */
    @Override
	public void setProgressBarValue(int value) {
        progressBar.setValue(value);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IProgressDialog#setCurrentProgress(long)
	 */
    @Override
	public void setCurrentProgress(long value) {
        currentLabel.setText(Long.toString(value));
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IProgressDialog#setTotalProgress(long)
	 */
    @Override
	public void setTotalProgress(long value) {
        totalLabel.setText(Long.toString(value));
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IProgressDialog#addCancelButtonActionListener(java.awt.event.ActionListener)
	 */
    @Override
	public void addCancelButtonActionListener(ActionListener a) {
        cancelButton.addActionListener(a);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IProgressDialog#disableCancelButton()
	 */
    @Override
	public void disableCancelButton() {
        cancelButton.setEnabled(false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IProgressDialog#setIcon(javax.swing.ImageIcon)
	 */
    @Override
	public void setIcon(ImageIcon icon) {
        imagelabel.setIcon(icon);
    }

    /**
     * @return the currentLabel
     */
    protected JLabel getCurrentLabel() {
        return currentLabel;
    }

    /**
     * @return the totalLabel
     */
    protected JLabel getTotalLabel() {
        return totalLabel;
    }
    
    @Override
    public void hideDialog() {
    	setVisible(false);
    }
    
    @Override
    public void showDialog() {
    	setVisible(true);
    }
}
