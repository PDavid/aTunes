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
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.gui.model.ReviewImportTreeTableModel;
import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.gui.views.controls.CustomTextArea;
import net.sourceforge.atunes.kernel.modules.pattern.AbstractPattern;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.TagAttributesReviewed;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.jdesktop.swingx.JXTreeTable;

/**
 * The Class ReviewImportDialog.
 */
public final class ReviewImportDialog extends CustomModalDialog {

    private static final long serialVersionUID = 8523236886848649698L;

    /**
     * Review instructions
     */
    private JTextArea reviewInstructions;

    /** The table. */
    private JXTreeTable treeTable;

    /** True when user pressed Cancel or closes the window */
    private boolean dialogCancelled = true;

    /**
     * Instantiates a new ReviewImportDialog
     */
    public ReviewImportDialog(JFrame owner) {
        super(owner, GuiUtils.getComponentWidthForResolution(1280, 1200), GuiUtils.getComponentHeightForResolution(1024, 700), true);
        setTitle(I18nUtils.getString("REVIEW_TAGS"));
        setContent();
        GuiUtils.applyComponentOrientation(this);
        enableCloseActionWithEscapeKey();
    }

    /**
     * Sets the content.
     */
    private void setContent() {
        JPanel panel = new JPanel(new GridBagLayout());
        treeTable = new JXTreeTable();
        treeTable.setShowGrid(false);
        treeTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        treeTable.setRootVisible(false);
        treeTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        treeTable.getTableHeader().setReorderingAllowed(false);
        treeTable.setSurrendersFocusOnKeystroke(true);
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        reviewInstructions = new CustomTextArea(I18nUtils.getString("REVIEW_TAGS_INSTRUCTIONS"));
        reviewInstructions.setEditable(false);
        reviewInstructions.setLineWrap(true);
        reviewInstructions.setWrapStyleWord(true);
        reviewInstructions.setOpaque(false);
        reviewInstructions.setBorder(BorderFactory.createEmptyBorder());
        JButton okButton = new JButton(I18nUtils.getString("OK"));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialogCancelled = false;
                setVisible(false);
            }
        });
        JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        topPanel.add(reviewInstructions, BorderLayout.CENTER);

        final JButton fillTagsFromFolderName = new JButton(StringUtils.getString(I18nUtils.getString("FILL_TAGS_FROM_FOLDER_NAME"), "..."));
        // Disabled as initially there is no row selected
        fillTagsFromFolderName.setEnabled(false);
        fillTagsFromFolderName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = treeTable.getSelectedRows();
                if (selectedRows.length > 0) {
                    PatternInputDialog inputDialog = new PatternInputDialog(ReviewImportDialog.this, true);
                    inputDialog.show(AbstractPattern.getMassiveRecognitionPatterns(), (String) treeTable.getValueAt(selectedRows[0], 0));
                    String pattern = inputDialog.getResult();
                    for (int row : selectedRows) {
                        Map<String, String> matches = AbstractPattern.getPatternMatches(pattern, (String) treeTable.getValueAt(row, 0), true);
                        for (Entry<String, String> entry : matches.entrySet()) {
                            ((ReviewImportTreeTableModel) treeTable.getTreeTableModel()).setValueForColumn(row, entry.getKey(), entry.getValue());
                        }
                    }
                }
            }
        });

        treeTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                fillTagsFromFolderName.setEnabled(treeTable.getSelectedRowCount() != 0);
            }
        });

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
        panel.add(new JScrollPane(treeTable), c);
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

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dialogCancelled = true;
            }
        });
    }

    /**
     * @return the dialogCancelled
     */
    public boolean isDialogCancelled() {
        return dialogCancelled;
    }

    @Override
    public void setVisible(boolean b) {
        // as this dialog is modal we must initialize value of dialogCancelled before setting visibility to true
        if (b) {
            dialogCancelled = true;
        }
        super.setVisible(b);
    }

    /**
     * Shows dialog with given data
     * 
     * @param folders
     * @param files
     */
    public void show(List<File> folders, List<AudioFile> filesToLoad) {
        treeTable.setTreeTableModel(new ReviewImportTreeTableModel(folders, filesToLoad, treeTable));
        treeTable.getColumnExt(0).setPreferredWidth(300);
        ((ReviewImportTreeTableModel) treeTable.getTreeTableModel()).setCellEditors();
        treeTable.expandAll();
        setVisible(true);
    }

    /**
     * Returns result of reviewing tags
     * 
     * @return
     */
    public TagAttributesReviewed getResult() {
        return ((ReviewImportTreeTableModel) treeTable.getTreeTableModel()).getTagAttributesReviewed();
    }
}
