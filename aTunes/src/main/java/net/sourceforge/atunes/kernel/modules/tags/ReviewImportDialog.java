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

package net.sourceforge.atunes.kernel.modules.tags;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.kernel.modules.pattern.PatternMatcher;
import net.sourceforge.atunes.kernel.modules.pattern.Patterns;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IReviewImportDialog;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.model.ITagAttributesReviewed;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.jdesktop.swingx.JXTreeTable;

/**
 * Dialog to review and change tags before an import process
 * 
 * @author alex
 * 
 */
public final class ReviewImportDialog extends AbstractCustomDialog implements
		IReviewImportDialog {

	private static final long serialVersionUID = 8523236886848649698L;

	/** The table. */
	private JXTreeTable treeTable;

	/** True when user pressed Cancel or closes the window */
	private boolean dialogCancelled = true;

	private IStateRepository stateRepository;

	private List<File> folders;

	private List<ILocalAudioObject> filesToLoad;

	private IDialogFactory dialogFactory;

	private Patterns patterns;

	private PatternMatcher patternMatcher;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param patternMatcher
	 */
	public void setPatternMatcher(final PatternMatcher patternMatcher) {
		this.patternMatcher = patternMatcher;
	}

	/**
	 * @param patterns
	 */
	public void setPatterns(final Patterns patterns) {
		this.patterns = patterns;
	}

	/**
	 * Instantiates a new ReviewImportDialog
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public ReviewImportDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 800, 600, controlsBuilder);
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param stateRepository
	 */
	public void setStateRepository(final IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}

	@Override
	public void initialize() {
		setTitle(I18nUtils.getString("REVIEW_TAGS"));
		setContent();
	}

	/**
	 * @return tree table
	 */
	JXTreeTable getTreeTable() {
		return this.treeTable;
	}

	/**
	 * @return stateRepository
	 */
	IStateRepository getStateRepository() {
		return this.stateRepository;
	}

	/**
	 * Sets the content.
	 */
	private void setContent() {
		JPanel panel = new JPanel(new GridBagLayout());
		this.treeTable = new JXTreeTable();
		this.treeTable
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.treeTable.setRootVisible(false);
		this.treeTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		this.treeTable.getTableHeader().setReorderingAllowed(false);
		this.treeTable.setSurrendersFocusOnKeystroke(true);
		JPanel topPanel = new JPanel(new BorderLayout(10, 0));
		JTextArea reviewInstructions = getControlsBuilder().createTextArea();
		reviewInstructions.setText(I18nUtils
				.getString("REVIEW_TAGS_INSTRUCTIONS"));
		reviewInstructions.setEditable(false);
		reviewInstructions.setLineWrap(true);
		reviewInstructions.setWrapStyleWord(true);
		reviewInstructions.setOpaque(false);
		reviewInstructions.setBorder(BorderFactory.createEmptyBorder());
		JButton okButton = new JButton(I18nUtils.getString("OK"));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				ReviewImportDialog.this.dialogCancelled = false;
				setVisible(false);
			}
		});
		JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				setVisible(false);
			}
		});
		topPanel.add(reviewInstructions, BorderLayout.CENTER);

		final JButton fillTagsFromFolderName = new JButton(
				StringUtils.getString(
						I18nUtils.getString("FILL_TAGS_FROM_FOLDER_NAME"),
						"..."));
		// Disabled as initially there is no row selected
		fillTagsFromFolderName.setEnabled(false);
		fillTagsFromFolderName
				.addActionListener(new FillTagsFromFolderNameActionListener(
						this, this.dialogFactory, this.patterns,
						this.patternMatcher));

		this.treeTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(final ListSelectionEvent e) {
						fillTagsFromFolderName
								.setEnabled(ReviewImportDialog.this.treeTable
										.getSelectedRowCount() != 0);
					}
				});

		arrangePanel(panel, topPanel, okButton, cancelButton,
				fillTagsFromFolderName);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				ReviewImportDialog.this.dialogCancelled = true;
			}
		});
	}

	/**
	 * @param panel
	 * @param topPanel
	 * @param okButton
	 * @param cancelButton
	 * @param fillTagsFromFolderName
	 */
	private void arrangePanel(final JPanel panel, final JPanel topPanel,
			final JButton okButton, final JButton cancelButton,
			final JButton fillTagsFromFolderName) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 20, 10, 20);
		panel.add(topPanel, c);
		c.gridy = 1;
		c.weighty = 1;
		panel.add(getControlsBuilder().createScrollPane(this.treeTable), c);
		c.gridy = 2;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		panel.add(fillTagsFromFolderName, c);
		c.gridy = 3;
		c.anchor = GridBagConstraints.CENTER;
		JPanel auxPanel = new JPanel();
		auxPanel.add(okButton);
		auxPanel.add(cancelButton);
		panel.add(auxPanel, c);
		add(panel);
	}

	@Override
	public boolean isDialogCancelled() {
		return this.dialogCancelled;
	}

	@Override
	public void setVisible(final boolean b) {
		// as this dialog is modal we must initialize value of dialogCancelled
		// before setting visibility to true
		if (b) {
			this.dialogCancelled = true;
		}
		super.setVisible(b);
		if (!b) {
			dispose();
		}
	}

	@Override
	public void setFilesToLoad(final List<ILocalAudioObject> filesToLoad) {
		this.filesToLoad = filesToLoad;
	}

	@Override
	public void setFolders(final List<File> folders) {
		this.folders = folders;
	}

	@Override
	public void showDialog() {
		this.treeTable.setTreeTableModel(new ReviewImportTreeTableModel(
				this.folders, this.filesToLoad, this.treeTable,
				this.beanFactory.getBean(TagAttributesReviewed.class),
				this.beanFactory.getBean(IFileManager.class)));
		this.treeTable.getColumnExt(0).setPreferredWidth(300);
		((ReviewImportTreeTableModel) this.treeTable.getTreeTableModel())
				.setCellEditors();
		this.treeTable.expandAll();
		setVisible(true);
	}

	@Override
	public ITagAttributesReviewed getResult() {
		return ((ReviewImportTreeTableModel) this.treeTable.getTreeTableModel())
				.getTagAttributesReviewed();
	}

	@Override
	public void hideDialog() {
		setVisible(false);
	}
}
