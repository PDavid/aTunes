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

package net.sourceforge.atunes.kernel.modules.pattern;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class represents a dialog used to enter a pattern It contains a preview
 * table to see in real time how pattern will be applied
 * 
 * @author fleax
 */
public final class PatternInputDialog extends AbstractCustomDialog {

	private final class OkActionListener implements ActionListener {
		private final boolean massiveRecognition;

		private OkActionListener(final boolean massiveRecognition) {
			this.massiveRecognition = massiveRecognition;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			PatternInputDialog.this.result = (String) PatternInputDialog.this.patternComboBox
					.getSelectedItem();

			if (PatternInputDialog.this.result != null
					&& !PatternInputDialog.this.result.trim().equals("")) {
				// Upper case all patterns found in result
				for (AbstractPattern pattern : PatternInputDialog.this.patterns
						.getPatternsList()) {
					PatternInputDialog.this.result = PatternInputDialog.this.result
							.replace(pattern.getPattern().toLowerCase(),
									pattern.getPattern());
				}

				// If pattern was not already used add to list of previously
				// used patterns
				List<String> previousPatterns = null;
				if (this.massiveRecognition) {
					previousPatterns = PatternInputDialog.this.stateRepository
							.getMassiveRecognitionPatterns();
				} else {
					previousPatterns = PatternInputDialog.this.stateRepository
							.getRecognitionPatterns();
				}

				// Create previous list if necessary
				if (previousPatterns == null) {
					previousPatterns = new ArrayList<String>();
					if (this.massiveRecognition) {
						PatternInputDialog.this.stateRepository
								.setMassiveRecognitionPatterns(previousPatterns);
					} else {
						PatternInputDialog.this.stateRepository
								.setRecognitionPatterns(previousPatterns);
					}
				}

				// Test
				if (!previousPatterns.contains(PatternInputDialog.this.result)) {
					previousPatterns.add(PatternInputDialog.this.result);
				}
			}
			dispose();
		}
	}

	private static final long serialVersionUID = -5789081662254435503L;

	/** Shows string of file or folder to help user select pattern **/
	private JLabel firstElementLabel;

	/** The combo box used to enter or select pattern */
	private JComboBox patternComboBox;

	/** Table used to show pattern replace preview */
	private JTable patternPreviewTable;

	/** Table used to show available patterns */
	private JTable availablePatternsTable;

	/** String used to preview pattern entered */
	private String previewString = null;

	/** The pattern entered */
	private String result = null;

	private static final String[] PREVIEW_COLUMN_NAMES = new String[] {
			I18nUtils.getString("NAME"), I18nUtils.getString("VALUE") };

	private IStateRepository stateRepository;

	private final boolean massiveRecognition;

	private Patterns patterns;

	private PatternMatcher patternMatcher;

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
	 * @param stateRepository
	 */
	public void setStateRepository(final IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}

	/**
	 * Instantiates a new pattern input dialog.
	 * 
	 * @param frame
	 * @param massiveRecognition
	 *            <code>true</code> if the dialog will be used to enter a
	 *            pattern for massive recognition or <code>false</code> for
	 *            non-massive recognition (single file level)
	 * @param controlsBuilder
	 */
	public PatternInputDialog(final IFrame frame,
			final boolean massiveRecognition,
			final IControlsBuilder controlsBuilder) {
		super(frame, 700, 450, controlsBuilder);
		this.massiveRecognition = massiveRecognition;
	}

	@Override
	public void initialize() {
		setResizable(false);
		setIconImage(Images.getImage(Images.APP_LOGO_16).getImage());
		setTitle(I18nUtils.getString("PATTERN_INPUT"));

		// Label with instructions
		JTextArea textArea = getControlsBuilder().createTextArea();
		textArea.setText(I18nUtils.getString("PATTERN_INPUT_INSTRUCTIONS"));
		textArea.setBorder(BorderFactory.createEmptyBorder());
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setOpaque(false);

		this.firstElementLabel = new JLabel();

		// Combo box used to enter pattern
		List<String> previousPatterns = null;
		if (this.massiveRecognition) {
			previousPatterns = this.stateRepository
					.getMassiveRecognitionPatterns();
		} else {
			previousPatterns = this.stateRepository.getRecognitionPatterns();
		}
		// Sort list
		if (previousPatterns != null) {
			Collections.sort(previousPatterns);
		}
		this.patternComboBox = new JComboBox(
				previousPatterns != null ? previousPatterns
						.toArray(new String[previousPatterns.size()])
						: new String[0]);
		this.patternComboBox.setEditable(true);

		JPanel patternPreviewPanel = new JPanel(new BorderLayout());
		this.patternPreviewTable = getLookAndFeel().getTable();
		JScrollPane patternPreviewTableScrollPane = getControlsBuilder()
				.createScrollPane(this.patternPreviewTable);
		patternPreviewPanel.add(patternPreviewTableScrollPane,
				BorderLayout.CENTER);
		patternPreviewPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEmptyBorder(),
				I18nUtils.getString("PREVIEW")));

		JPanel availablePatternsPanel = new JPanel(new BorderLayout());
		this.availablePatternsTable = getLookAndFeel().getTable();
		this.availablePatternsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2) {
					// Get pattern being clicked
					int row = PatternInputDialog.this.availablePatternsTable
							.rowAtPoint(e.getPoint());
					// Access model to get pattern selected
					String pattern = ((AvailablePatternsDefaultTableModel) PatternInputDialog.this.availablePatternsTable
							.getModel()).getPatternAtRow(row);
					// Add pattern to current one
					String newPattern = StringUtils.getString(
							PatternInputDialog.this.patternComboBox.getEditor()
									.getItem(), pattern);
					PatternInputDialog.this.patternComboBox.getEditor()
							.setItem(newPattern);
					PatternInputDialog.this.patternComboBox
							.setSelectedItem(newPattern);
					// Update pattern preview
					previewPattern(PatternInputDialog.this.massiveRecognition);
				}
			}
		});
		JScrollPane availablePatternsScrollPane = getControlsBuilder()
				.createScrollPane(this.availablePatternsTable);
		availablePatternsPanel.add(availablePatternsScrollPane,
				BorderLayout.CENTER);
		availablePatternsPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEmptyBorder(),
				I18nUtils.getString("AVAILABLE_PATTERNS")));

		JButton okButton = new JButton(I18nUtils.getString("OK"));
		ActionListener okListener = new OkActionListener(
				this.massiveRecognition);
		okButton.addActionListener(okListener);
		JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				PatternInputDialog.this.result = null;
				dispose();
			}
		});

		this.patternComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				previewPattern(PatternInputDialog.this.massiveRecognition);
			}
		});

		this.patternComboBox.getEditor().getEditorComponent()
				.addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(final KeyEvent e) {
						super.keyTyped(e);

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								previewPattern(PatternInputDialog.this.massiveRecognition);
							}
						});
					}
				});

		GridLayout gl = new GridLayout(1, 2, 30, 0);
		JPanel auxPanel = new JPanel(gl);
		auxPanel.add(okButton);
		auxPanel.add(cancelButton);

		arrangeDialog(textArea, patternPreviewPanel, availablePatternsPanel,
				okButton, auxPanel);
	}

	private void arrangeDialog(final JTextArea textArea,
			final JPanel patternPreviewPanel,
			final JPanel availablePatternsPanel, final JButton okButton,
			final JPanel auxPanel) {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 10, 10, 10);
		panel.add(textArea, c);
		c.gridy = 1;
		c.insets = new Insets(5, 30, 5, 30);
		panel.add(this.firstElementLabel, c);
		c.gridy = 2;
		panel.add(this.patternComboBox, c);
		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 0.7;
		c.weighty = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 5, 5, 5);
		panel.add(patternPreviewPanel, c);
		c.gridx = 1;
		c.weightx = 0.3;
		panel.add(availablePatternsPanel, c);
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(10, 10, 10, 10);
		panel.add(auxPanel, c);
		add(panel);
		getRootPane().setDefaultButton(okButton);
	}

	/**
	 * Updated preview
	 * 
	 * @param massiveRecognition
	 */
	void previewPattern(final boolean massiveRecognition) {
		Map<String, String> matches = this.patternMatcher.getPatternMatches(
				((JTextField) this.patternComboBox.getEditor()
						.getEditorComponent()).getText(), this.previewString,
				massiveRecognition);

		String[][] data = new String[matches.size()][2];
		int i = 0;
		for (Entry<String, String> entry : matches.entrySet()) {
			data[i][0] = I18nUtils.getString(entry.getKey());
			data[i][1] = entry.getValue();
			i++;
		}

		((DefaultTableModel) this.patternPreviewTable.getModel())
				.setDataVector(data, PREVIEW_COLUMN_NAMES);
	}

	/**
	 * Gets the result.
	 * 
	 * @return the result
	 */
	public String getResult() {
		return this.result;
	}

	/**
	 * Show this dialog
	 * 
	 * @param availablePatterns
	 * @param previewString
	 */
	public void show(final List<AbstractPattern> availablePatterns,
			final String previewString) {
		this.previewString = previewString;
		this.patternComboBox.setSelectedIndex(-1);
		this.firstElementLabel.setText(previewString);

		String[][] patternsMatrix = new String[availablePatterns.size()][2];
		int i = 0;
		for (AbstractPattern p : availablePatterns) {
			patternsMatrix[i][0] = p.getPattern();
			patternsMatrix[i][1] = p.getDescription();
			i++;
		}
		// Disable autoresize, as we will control it
		this.patternPreviewTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.patternPreviewTable
				.setColumnModel(new PatternPreviewDefaultTableColumnModel());
		DefaultTableModel patternPreviewTableModel = new PatternPreviewDefaultTableModel(
				new String[0][2], PREVIEW_COLUMN_NAMES);
		this.patternPreviewTable.setModel(patternPreviewTableModel);

		DefaultTableModel availablePatternsTableModel = new AvailablePatternsDefaultTableModel(
				patternsMatrix, new String[] { I18nUtils.getString("PATTERN"),
						I18nUtils.getString("DESCRIPTION") });
		this.availablePatternsTable.setModel(availablePatternsTableModel);
		setVisible(true);
		this.patternComboBox.requestFocus();
	}

}
