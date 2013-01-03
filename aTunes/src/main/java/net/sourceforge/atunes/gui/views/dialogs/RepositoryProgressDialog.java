/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.CloseAction;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IRepositoryProgressDialog;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The repository progress dialog.
 * 
 * @author fleax
 */
public final class RepositoryProgressDialog extends AbstractCustomDialog
		implements IRepositoryProgressDialog {

	private static class GlassPaneMouseListener extends MouseAdapter {
	}

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
	private AbstractAction repositoryLoadInBackgroundAction;
	private AbstractAction repositoryLoadCancelAction;

	/**
	 * Instantiates a new repository progress dialog.
	 * 
	 * @param frame
	 */
	/**
	 * @param frame
	 * @param controlsBuilder
	 */
	public RepositoryProgressDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 500, 250, false, CloseAction.NOTHING, controlsBuilder);
	}

	/**
	 * Initializes this dialog
	 */
	@Override
	public void initialize() {
		add(getContent());
		this.backgroundButton.setVisible(false);
		this.cancelButton.setVisible(false);
		setResizable(false);
	}

	/**
	 * @param repositoryLoadCancelAction
	 */
	public void setRepositoryLoadCancelAction(
			final AbstractAction repositoryLoadCancelAction) {
		this.repositoryLoadCancelAction = repositoryLoadCancelAction;
	}

	/**
	 * @param repositoryLoadInBackgroundAction
	 */
	public void setRepositoryLoadInBackgroundAction(
			final AbstractAction repositoryLoadInBackgroundAction) {
		this.repositoryLoadInBackgroundAction = repositoryLoadInBackgroundAction;
	}

	/**
	 * Activate glass pane.
	 */
	private void activateGlassPane() {
		((JFrame) getParent()).getGlassPane().setVisible(true);
		((JFrame) getParent()).getGlassPane().addMouseListener(this.listener);
	}

	/**
	 * Deactivate glass pane.
	 */
	private void deactivateGlassPane() {
		((JFrame) getParent()).getGlassPane()
				.removeMouseListener(this.listener);
	}

	/**
	 * Gets the content.
	 * 
	 * @return the content
	 */
	private JPanel getContent() {
		JPanel container = new JPanel(new BorderLayout());

		JPanel panel = new JPanel(new GridBagLayout());
		this.pictureLabel = new JLabel(LOGO);
		this.pictureLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0,
				10));
		this.taskLabel = new JLabel(StringUtils.getString(
				I18nUtils.getString("LOADING"), "..."));
		this.progressLabel = new JLabel();
		this.separatorLabel = new JLabel(" / ");
		this.separatorLabel.setVisible(false);
		this.totalFilesLabel = new JLabel();
		this.progressBar = new JProgressBar();
		this.progressBar.setBorder(BorderFactory.createEmptyBorder());
		this.folderLabel = new JLabel(" ");
		this.remainingTimeLabel = new JLabel(" ");
		this.backgroundButton = new JButton(
				this.repositoryLoadInBackgroundAction);
		this.cancelButton = new JButton(this.repositoryLoadCancelAction);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(this.backgroundButton);
		buttonsPanel.add(this.cancelButton);

		container.add(this.pictureLabel, BorderLayout.WEST);

		arrangePanel(panel, buttonsPanel);

		container.add(panel, BorderLayout.CENTER);

		return container;
	}

	/**
	 * @param panel
	 * @param buttonsPanel
	 */
	private void arrangePanel(final JPanel panel, final JPanel buttonsPanel) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 20, 0, 20);
		c.anchor = GridBagConstraints.WEST;
		panel.add(this.taskLabel, c);
		c.gridx = 1;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(5, 0, 0, 3);
		c.anchor = GridBagConstraints.EAST;
		panel.add(this.progressLabel, c);
		c.gridx = 2;
		c.insets = new Insets(5, 0, 0, 0);
		panel.add(this.separatorLabel, c);
		c.gridx = 3;
		c.insets = new Insets(5, 0, 0, 20);
		panel.add(this.totalFilesLabel, c);
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.gridwidth = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 20, 5, 20);
		c.anchor = GridBagConstraints.CENTER;
		panel.add(this.progressBar, c);
		c.gridy = 2;
		c.insets = new Insets(0, 20, 0, 20);
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.WEST;
		panel.add(this.folderLabel, c);
		c.gridy = 3;
		c.insets = new Insets(0, 20, 10, 20);
		panel.add(this.remainingTimeLabel, c);
		c.gridy = 4;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(buttonsPanel, c);
	}

	@Override
	public void setCurrentTask(final String task) {
		this.taskLabel.setText(task);
	}

	@Override
	public void setCurrentFolder(final String folder) {
		this.folderLabel.setText(folder);
	}

	@Override
	public void setProgressBarIndeterminate(final boolean indeterminate) {
		this.progressBar.setIndeterminate(indeterminate);
	}

	@Override
	public void setProgressBarValue(final int value) {
		this.progressBar.setValue(value);
	}

	@Override
	public void setTotalFiles(final int max) {
		this.progressBar.setMaximum(max);
		this.totalFilesLabel.setText(Integer.toString(max));
	}

	@Override
	public void setProgressText(final String text) {
		this.progressLabel.setText(text);
		this.separatorLabel.setVisible(text != null && !text.equals(""));
	}

	@Override
	public void setRemainingTime(final String text) {
		this.remainingTimeLabel.setText(text);
	}

	@Override
	public void setButtonsEnabled(final boolean enabled) {
		this.cancelButton.setEnabled(enabled);
		this.backgroundButton.setEnabled(enabled);
	}

	@Override
	public void setButtonsVisible(final boolean visible) {
		this.cancelButton.setVisible(visible);
		this.backgroundButton.setVisible(visible);
	}

	@Override
	public void setVisible(final boolean b) {
		setLocationRelativeTo(getParent());
		super.setVisible(b);
	}

	@Override
	public void showDialog() {
		setTitle(I18nUtils.getString("PLEASE_WAIT"));
		setVisible(true);
		activateGlassPane();
		setButtonsVisible(true);
		setButtonsEnabled(true);
	}

	@Override
	public void hideDialog() {
		setVisible(false);
		deactivateGlassPane();
		dispose();
	}

	@Override
	public void setImage(final Image image) {
		this.pictureLabel.setIcon(image != null ? ImageUtils.scaleImageBicubic(
				image, LOGO_SIZE, LOGO_SIZE) : LOGO);
	}
}
