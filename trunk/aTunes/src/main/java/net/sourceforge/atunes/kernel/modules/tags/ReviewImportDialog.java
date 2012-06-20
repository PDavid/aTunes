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
import net.sourceforge.atunes.gui.views.controls.CustomTextArea;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IReviewImportDialog;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.model.ITagAttributesReviewed;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.jdesktop.swingx.JXTreeTable;

/**
 * Dialog to review and change tags before an import process
 * @author alex
 *
 */
public final class ReviewImportDialog extends AbstractCustomDialog implements IReviewImportDialog {

    private static final long serialVersionUID = 8523236886848649698L;

    /** The table. */
    private JXTreeTable treeTable;

    /** True when user pressed Cancel or closes the window */
    private boolean dialogCancelled = true;

    private IStateRepository stateRepository;
    
    private List<File> folders;
    
    private List<ILocalAudioObject> filesToLoad;
    
    private IDialogFactory dialogFactory;
    
    /**
     * Instantiates a new ReviewImportDialog
     * @param frame
     * @param stateRepository
     */
    public ReviewImportDialog(IFrame frame) {
        super(frame, 800, 600);
    }
    
    /**
     * @param dialogFactory
     */
    public void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

    /**
     * @param stateRepository
     */
    public void setStateRepository(IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}
    
    @Override
    public void initialize() {
        setTitle(I18nUtils.getString("REVIEW_TAGS"));
        setContent(getLookAndFeel());
    }
    
    /**
     * @return tree table
     */
    JXTreeTable getTreeTable() {
		return treeTable;
	}
    
    /**
     * @return stateRepository
     */
    IStateRepository getStateRepository() {
		return stateRepository;
	}

    /**
     * Sets the content.
     */
    private void setContent(final ILookAndFeel lookAndFeel) {
        JPanel panel = new JPanel(new GridBagLayout());
        treeTable = new JXTreeTable();
        treeTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        treeTable.setRootVisible(false);
        treeTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        treeTable.getTableHeader().setReorderingAllowed(false);
        treeTable.setSurrendersFocusOnKeystroke(true);
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        JTextArea reviewInstructions = new CustomTextArea(I18nUtils.getString("REVIEW_TAGS_INSTRUCTIONS"));
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
        fillTagsFromFolderName.addActionListener(new FillTagsFromFolderNameActionListener(this, dialogFactory));

        treeTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                fillTagsFromFolderName.setEnabled(treeTable.getSelectedRowCount() != 0);
            }
        });

        arrangePanel(lookAndFeel, panel, topPanel, okButton, cancelButton,
				fillTagsFromFolderName);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dialogCancelled = true;
            }
        });
    }

	/**
	 * @param lookAndFeel
	 * @param panel
	 * @param topPanel
	 * @param okButton
	 * @param cancelButton
	 * @param fillTagsFromFolderName
	 */
	private void arrangePanel(final ILookAndFeel lookAndFeel, JPanel panel,
			JPanel topPanel, JButton okButton, JButton cancelButton,
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
        panel.add(lookAndFeel.getScrollPane(treeTable), c);
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
        return dialogCancelled;
    }

    @Override
    public void setVisible(boolean b) {
        // as this dialog is modal we must initialize value of dialogCancelled before setting visibility to true
        if (b) {
            dialogCancelled = true;
        }
        super.setVisible(b);
        if (!b) {
        	dispose();
        }
    }
    
    @Override
    public void setFilesToLoad(List<ILocalAudioObject> filesToLoad) {
    	this.filesToLoad = filesToLoad;
    }
    
    @Override
    public void setFolders(List<File> folders) {
    	this.folders = folders;
    }
    
    @Override
	public void showDialog() {
        treeTable.setTreeTableModel(new ReviewImportTreeTableModel(folders, filesToLoad, treeTable));
        treeTable.getColumnExt(0).setPreferredWidth(300);
        ((ReviewImportTreeTableModel) treeTable.getTreeTableModel()).setCellEditors();
        treeTable.expandAll();
        setVisible(true);
    }

    @Override
	public ITagAttributesReviewed getResult() {
        return ((ReviewImportTreeTableModel) treeTable.getTreeTableModel()).getTagAttributesReviewed();
    }
    
    @Override
    public void hideDialog() {
    	setVisible(false);
    }
}
