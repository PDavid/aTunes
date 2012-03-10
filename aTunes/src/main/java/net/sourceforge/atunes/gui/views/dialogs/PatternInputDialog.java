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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.CustomTextArea;
import net.sourceforge.atunes.kernel.modules.pattern.AbstractPattern;
import net.sourceforge.atunes.kernel.modules.pattern.Patterns;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IState;
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

		private OkActionListener(boolean massiveRecognition) {
			this.massiveRecognition = massiveRecognition;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		    result = (String) patternComboBox.getSelectedItem();

		    if (result != null && !result.trim().equals("")) {
		        // Upper case all patterns found in result
		        for (AbstractPattern pattern : Patterns.getPatterns()) {
		            result = result.replaceAll(pattern.getPattern().toLowerCase(), pattern.getPattern());
		        }

		        // If pattern was not already used add to list of previously used patterns
		        List<String> previousPatterns = null;
		        if (massiveRecognition) {
		            previousPatterns = state.getMassiveRecognitionPatterns();
		        } else {
		            previousPatterns = state.getRecognitionPatterns();
		        }

		        // Create previous list if necessary
		        if (previousPatterns == null) {
		            previousPatterns = new ArrayList<String>();
		            if (massiveRecognition) {
		                state.setMassiveRecognitionPatterns(previousPatterns);
		            } else {
		                state.setRecognitionPatterns(previousPatterns);
		            }
		        }

		        // Test
		        if (!previousPatterns.contains(result)) {
		            previousPatterns.add(result);
		        }
		    }
		    dispose();
		}
	}

	private static final class AvailablePatternsDefaultTableModel extends DefaultTableModel {
        /**
		 * 
		 */
        private static final long serialVersionUID = 7475413284696491261L;

        private Object[][] data;
        
        private AvailablePatternsDefaultTableModel(Object[][] data, Object[] columnNames) {
            super(data, columnNames);
            this.data = data;
        }

        @Override
        public boolean isCellEditable(int row, int column) {        	
            return false;
        }
        
        /**
         * Returns pattern at given row
         * @param row
         * @return pattern at given row
         */
        public String getPatternAtRow(int row) {
        	return (String) data[row][0];
        }
    }

    private static final class PatternPreviewDefaultTableModel extends DefaultTableModel {
        /**
		 * 
		 */
        private static final long serialVersionUID = 0L;

        private PatternPreviewDefaultTableModel(Object[][] data, Object[] columnNames) {
            super(data, columnNames);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    private static final class PatternPreviewDefaultTableColumnModel extends DefaultTableColumnModel {
        /**
		 * 
		 */
        private static final long serialVersionUID = -1915883409261076543L;

        @Override
        public void addColumn(TableColumn column) {
            super.addColumn(column);
            if (column.getHeaderValue().equals(I18nUtils.getString("NAME"))) {
                column.setPreferredWidth(100);
            } else {
                // Space removed from first column is given to second column
                column.setPreferredWidth(230);
            }
        }
    }

    private static final long serialVersionUID = -5789081662254435503L;

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

    private static final String[] PREVIEW_COLUMN_NAMES = new String[] { I18nUtils.getString("NAME"), I18nUtils.getString("VALUE") };

    /**
     * State of application
     */
    private IState state;
    
    /**
     * Instantiates a new pattern input dialog.
     * 
     * @param owner
     *            the owner
     * 
     * @param massiveRecognition
     *            <code>true</code> if the dialog will be used to enter a
     *            pattern for massive recognition or <code>false</code> for
     *            non-massive recognition (single file level)
     * @param state
     * @param lookAndFeel
     */
    public PatternInputDialog(Window owner, final boolean massiveRecognition, IState state, ILookAndFeel lookAndFeel) {
        super(owner, 550, 350, true, CloseAction.DISPOSE, lookAndFeel);
        this.state = state;
        setResizable(false);
        setIconImage(Images.getImage(Images.APP_LOGO_16).getImage());
        setTitle(I18nUtils.getString("PATTERN_INPUT"));

        // Label with instructions
        JTextArea textArea = new CustomTextArea(I18nUtils.getString("PATTERN_INPUT_INSTRUCTIONS"));
        textArea.setBorder(BorderFactory.createEmptyBorder());
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setOpaque(false);

        // Combo box used to enter pattern
        List<String> previousPatterns = null;
        if (massiveRecognition) {
            previousPatterns = state.getMassiveRecognitionPatterns();
        } else {
            previousPatterns = state.getRecognitionPatterns();
        }
        // Sort list
        if (previousPatterns != null) {
            Collections.sort(previousPatterns);
        }
        patternComboBox = new JComboBox(previousPatterns != null ? previousPatterns.toArray(new String[previousPatterns.size()]) : new String[0]);
        patternComboBox.setEditable(true);

        JPanel patternPreviewPanel = new JPanel(new BorderLayout());
        patternPreviewTable = lookAndFeel.getTable();
        JScrollPane patternPreviewTableScrollPane = lookAndFeel.getTableScrollPane(patternPreviewTable);
        patternPreviewPanel.add(patternPreviewTableScrollPane, BorderLayout.CENTER);
        patternPreviewPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), I18nUtils.getString("PREVIEW")));

        JPanel availablePatternsPanel = new JPanel(new BorderLayout());
        availablePatternsTable = lookAndFeel.getTable();
        availablePatternsTable.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		if (e.getClickCount() == 2) {
        			// Get pattern being clicked
        			int row = availablePatternsTable.rowAtPoint(e.getPoint());
        			// Access model to get pattern selected
        			String pattern = ((AvailablePatternsDefaultTableModel)availablePatternsTable.getModel()).getPatternAtRow(row);
        			// Add pattern to current one       
        			String newPattern = StringUtils.getString(patternComboBox.getEditor().getItem(), pattern);
        			patternComboBox.getEditor().setItem(newPattern);
        			patternComboBox.setSelectedItem(newPattern);
        			// Update pattern preview
        			previewPattern(massiveRecognition);
        		}
        	}
        });
        JScrollPane availablePatternsScrollPane = lookAndFeel.getTableScrollPane(availablePatternsTable);
        availablePatternsPanel.add(availablePatternsScrollPane, BorderLayout.CENTER);
        availablePatternsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), I18nUtils.getString("AVAILABLE_PATTERNS")));

        JButton okButton = new JButton(I18nUtils.getString("OK"));
        ActionListener okListener = new OkActionListener(massiveRecognition);
        okButton.addActionListener(okListener);
        JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result = null;
                dispose();
            }
        });

        patternComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previewPattern(massiveRecognition);
            }
        });

        patternComboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        previewPattern(massiveRecognition);
                    }
                });
            }
        });

        GridLayout gl = new GridLayout(1, 2, 30, 0);
        JPanel auxPanel = new JPanel(gl);
        auxPanel.add(okButton);
        auxPanel.add(cancelButton);

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
        panel.add(patternComboBox, c);
        c.gridx = 0;
        c.gridy = 2;
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
        c.gridy = 3;
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
    void previewPattern(boolean massiveRecognition) {
        Map<String, String> matches = Patterns.getPatternMatches(((JTextField) patternComboBox.getEditor().getEditorComponent()).getText(), previewString,
                massiveRecognition);

        String[][] data = new String[matches.size()][2];
        int i = 0;
        for (Entry<String, String> entry : matches.entrySet()) {
            data[i][0] = I18nUtils.getString(entry.getKey());
            data[i][1] = entry.getValue();
            i++;
        }

        ((DefaultTableModel) patternPreviewTable.getModel()).setDataVector(data, PREVIEW_COLUMN_NAMES);
    }

    /**
     * Gets the result.
     * 
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * Show this dialog
     * 
     * @param availablePatterns
     * @param previewString
     */
    public void show(List<AbstractPattern> availablePatterns, String previewString) {
        this.previewString = previewString;
        patternComboBox.setSelectedIndex(-1);

        String[][] patterns = new String[availablePatterns.size()][2];
        int i = 0;
        for (AbstractPattern p : availablePatterns) {
            patterns[i][0] = p.getPattern();
            patterns[i][1] = p.getDescription();
            i++;
        }
        // Disable autoresize, as we will control it
        patternPreviewTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        patternPreviewTable.setColumnModel(new PatternPreviewDefaultTableColumnModel());
        DefaultTableModel patternPreviewTableModel = new PatternPreviewDefaultTableModel(new String[0][2], PREVIEW_COLUMN_NAMES);
        patternPreviewTable.setModel(patternPreviewTableModel);

        DefaultTableModel availablePatternsTableModel = new AvailablePatternsDefaultTableModel(patterns, new String[] { I18nUtils.getString("PATTERN"),
                I18nUtils.getString("DESCRIPTION") });
        availablePatternsTable.setModel(availablePatternsTableModel);
        setVisible(true);
        patternComboBox.requestFocus();
    }

}
