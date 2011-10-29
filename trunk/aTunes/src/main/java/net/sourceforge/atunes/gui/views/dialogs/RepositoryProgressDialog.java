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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.kernel.actions.RepositoryLoadCancelAction;
import net.sourceforge.atunes.kernel.actions.RepositoryLoadInBackgroundAction;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IRepositoryProgressDialog;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The repository progress dialog.
 * 
 * @author fleax
 */
public final class RepositoryProgressDialog extends AbstractCustomDialog implements IRepositoryProgressDialog {

    private static class GlassPaneMouseListener extends MouseAdapter {}

    private static final long serialVersionUID = -3071934230042256578L;

	private static final ImageIcon LOGO = Images.getImage(Images.APP_LOGO_150);
	private static final int LOGO_SIZE = 150;

    private JLabel pictureLabel;
    private JLabel taskLabel;
    private JLabel progressLabel;
    private JLabel separatorLabel;
	private JLabel totalFilesLabel;
    private JProgressBar progressBar;
    private JLabel folderLabel;
    private JLabel remainingTimeLabel;
    private JButton cancelButton;
    private JButton backgroundButton;
    private transient MouseListener listener = new GlassPaneMouseListener();

    /**
     * Instantiates a new repository progress dialog.
     * 
     * @param frame
     * @param lookAndFeelManager
     */
    public RepositoryProgressDialog(IFrame frame, ILookAndFeelManager lookAndFeelManager) {
        super(frame, 500, 250, false, CloseAction.NOTHING, lookAndFeelManager.getCurrentLookAndFeel());
        add(getContent());
        backgroundButton.setVisible(false);
        cancelButton.setVisible(false);
        setResizable(false);
    }

    /**
     * Activate glass pane.
     */
    private void activateGlassPane() {
        ((JFrame) getParent()).getGlassPane().setVisible(true);
        ((JFrame) getParent()).getGlassPane().addMouseListener(listener);
    }

    /**
     * Deactivate glass pane.
     */
    private void deactivateGlassPane() {
        ((JFrame) getParent()).getGlassPane().removeMouseListener(listener);
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    private JPanel getContent() {
    	JPanel container = new JPanel(new BorderLayout());

    	JPanel panel = new JPanel(new GridBagLayout());
        pictureLabel = new JLabel(LOGO);
        pictureLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
        taskLabel = new JLabel(StringUtils.getString(I18nUtils.getString("LOADING"), "..."));
        progressLabel = new JLabel();
        separatorLabel = new JLabel(" / ");
        separatorLabel.setVisible(false);
        totalFilesLabel = new JLabel();
        progressBar = new JProgressBar();
        progressBar.setBorder(BorderFactory.createEmptyBorder());
        folderLabel = new JLabel(" ");
        remainingTimeLabel = new JLabel(" ");
        backgroundButton = new JButton(Context.getBean(RepositoryLoadInBackgroundAction.class));
        cancelButton = new JButton(Context.getBean(RepositoryLoadCancelAction.class));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(backgroundButton);
        buttonsPanel.add(cancelButton);

        container.add(pictureLabel, BorderLayout.WEST);
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 20, 0, 20);
        c.anchor = GridBagConstraints.WEST;
        panel.add(taskLabel, c);
        c.gridx = 1;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(5, 0, 0, 3);
        c.anchor = GridBagConstraints.EAST;
        panel.add(progressLabel, c);
        c.gridx = 2;
        c.insets = new Insets(5, 0, 0, 0);
        panel.add(separatorLabel, c);
        c.gridx = 3;
        c.insets = new Insets(5, 0, 0, 20);
        panel.add(totalFilesLabel, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 20, 5, 20);
        c.anchor = GridBagConstraints.CENTER;
        panel.add(progressBar, c);
        c.gridy = 2;
        c.insets = new Insets(0, 20, 0, 20);
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.WEST;
        panel.add(folderLabel, c);
        c.gridy = 3;
        c.insets = new Insets(0, 20, 10, 20);
        panel.add(remainingTimeLabel, c);
        c.gridy = 4;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(buttonsPanel, c);
        
        container.add(panel, BorderLayout.CENTER);
        
        return container;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRepositoryProgressDialog#setCurrentTask(java.lang.String)
	 */
    @Override
	public void setCurrentTask(String task) {
    	taskLabel.setText(task);
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRepositoryProgressDialog#setCurrentFolder(java.lang.String)
	 */
    @Override
	public void setCurrentFolder(String folder) {
    	folderLabel.setText(folder);
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRepositoryProgressDialog#setProgressBarIndeterminate(boolean)
	 */
    @Override
	public void setProgressBarIndeterminate(boolean indeterminate) {
    	progressBar.setIndeterminate(indeterminate);
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRepositoryProgressDialog#setProgressBarValue(int)
	 */
    @Override
	public void setProgressBarValue(int value) {
    	progressBar.setValue(value);
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRepositoryProgressDialog#setTotalFiles(int)
	 */
    @Override
	public void setTotalFiles(int max) {
    	progressBar.setMaximum(max);
    	totalFilesLabel.setText(Integer.toString(max));
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRepositoryProgressDialog#setProgressText(java.lang.String)
	 */
    @Override
	public void setProgressText(String text) {
    	progressLabel.setText(text);
    	separatorLabel.setVisible(text != null && !text.equals(""));
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRepositoryProgressDialog#setRemainingTime(java.lang.String)
	 */
    @Override
	public void setRemainingTime(String text) {
    	remainingTimeLabel.setText(text);
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRepositoryProgressDialog#setButtonsEnabled(boolean)
	 */
    @Override
	public void setButtonsEnabled(boolean enabled) {
        cancelButton.setEnabled(enabled);
        backgroundButton.setEnabled(enabled);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRepositoryProgressDialog#setButtonsVisible(boolean)
	 */
    @Override
	public void setButtonsVisible(boolean visible) {
        cancelButton.setVisible(visible);
        backgroundButton.setVisible(visible);
    }

    @Override
    public void setVisible(final boolean b) {
        setLocationRelativeTo(getParent());
        super.setVisible(b);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRepositoryProgressDialog#showProgressDialog()
	 */
    @Override
	public void showProgressDialog() {
        setTitle(I18nUtils.getString("PLEASE_WAIT"));
        setVisible(true);
        activateGlassPane();
        setButtonsVisible(true);
        setButtonsEnabled(true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRepositoryProgressDialog#hideProgressDialog()
	 */
    @Override
	public void hideProgressDialog() {
        setVisible(false);
        deactivateGlassPane();
        dispose();
        //setButtonsVisible(false);
        //setButtonsEnabled(true);
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRepositoryProgressDialog#setImage(java.awt.Image)
	 */
    @Override
	public void setImage(Image image) {
    	pictureLabel.setIcon(image != null ? ImageUtils.scaleImageBicubic(image, LOGO_SIZE, LOGO_SIZE) : LOGO);
    }
    
}
