/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomModalDialog;
import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.gui.views.controls.CustomTextField;
import net.sourceforge.atunes.kernel.controllers.ripcd.RipCdDialogController;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.model.CDInfo;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The dialog for ripping cds
 */
public final class RipCdDialog extends AbstractCustomModalDialog {

    /**
     * The model for cd info
     */
    private static class CdInfoTableModel extends AbstractTableModel {

        private static final long serialVersionUID = -7577681531593039707L;

        private transient CDInfo cdInfo;
        private List<String> trackNames = new ArrayList<String>();
        private List<String> artistNames = new ArrayList<String>();
        private List<String> composerNames = new ArrayList<String>();
        private List<Boolean> tracksSelected;

        /**
         * Instantiates a new cd info table model.
         */
        public CdInfoTableModel() {
            // Nothing to do
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Boolean.class : String.class;
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public String getColumnName(int column) {
            if (column == 0) {
                return "";
            } else if (column == 1) {
                return I18nUtils.getString("TITLE");
            } else if (column == 2) {
                return I18nUtils.getString("ARTIST");
            } else if (column == 3) {
                return I18nUtils.getString("COMPOSER");
            } else {
                return "";
            }
        }

        @Override
        public int getRowCount() {
            return cdInfo != null ? cdInfo.getTracks() : 0;
        }

        /**
         * Gets the tracks selected.
         * 
         * @return the tracks selected
         */
        public List<Boolean> getTracksSelected() {
            return tracksSelected;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return tracksSelected.get(rowIndex);
            } else if (columnIndex == 1) {
                if (rowIndex > trackNames.size() - 1) {
                    trackNames.add(rowIndex, StringUtils.getString(I18nUtils.getString("TRACK"), " ", (rowIndex + 1)));
                    return StringUtils.getString(I18nUtils.getString("TRACK"), " ", (rowIndex + 1));
                }
                if (rowIndex < trackNames.size()) {
                    return trackNames.get(rowIndex);
                }

                trackNames.add(rowIndex, StringUtils.getString(I18nUtils.getString("TRACK"), " ", (rowIndex + 1)));
                return StringUtils.getString(I18nUtils.getString("TRACK"), " ", (rowIndex + 1));

            } else if (columnIndex == 2) {
                if (rowIndex > artistNames.size() - 1) {
                    // TODO if cdda2wav is modified for detecting song artist modify here
                    if (cdInfo.getArtist() != null) {
                        return cdInfo.getArtist();
                    }
                    return Artist.getUnknownArtist();
                }
                return artistNames.get(rowIndex);
            } else if (columnIndex == 4) {
                return cdInfo.getDurations().get(rowIndex);
            } else {
                if (rowIndex > composerNames.size() - 1) {
                    composerNames.add(rowIndex, "");
                    return "";
                }
                return composerNames.get(rowIndex);

            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex != 4;
        }

        /**
         * Sets the artist names.
         * 
         * @param artistNames
         *            the new artist names
         */
        public void setArtistNames(List<String> artistNames) {
            this.artistNames = artistNames;
        }

        /**
         * Sets the cd info.
         * 
         * @param cdInfo
         *            the new cd info
         */
        public void setCdInfo(CDInfo cdInfo) {
            if (cdInfo != null) {
                this.cdInfo = cdInfo;
                if (tracksSelected == null) {
                    tracksSelected = new ArrayList<Boolean>();
                }
                tracksSelected.clear();
                for (int i = 0; i < cdInfo.getTracks(); i++) {
                    tracksSelected.add(true);
                }
            }
        }

        /**
         * Sets the composer names.
         * 
         * @param composerNames
         *            the new composer names
         */
        public void setComposerNames(List<String> composerNames) {
            this.composerNames = composerNames;
        }

        /**
         * Sets the track names.
         * 
         * @param trackNames
         *            the new track names
         */
        public void setTrackNames(List<String> trackNames) {
            this.trackNames = trackNames;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                tracksSelected.remove(rowIndex);
                tracksSelected.add(rowIndex, (Boolean) aValue);
            } else if (columnIndex == 1) {
                trackNames.set(rowIndex, (String) aValue);
            } else if (columnIndex == 2) {
                artistNames.set(rowIndex, (String) aValue);
            } else if (columnIndex == 3) {
                composerNames.set(rowIndex, (String) aValue);
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    private static final long serialVersionUID = 1987727841297807350L;

    private JTable table;
    private JTextField artistTextField;
    private JTextField albumTextField;
    private JTextField yearTextField;
    private JComboBox genreComboBox;
    private JButton titlesButton;
    private JComboBox format;
    private JComboBox quality;
    private JComboBox filePattern;
    private JTextField folderName;
    private JButton folderSelectionButton;
    private JCheckBox useCdErrorCorrection;
    private JButton ok;
    private JButton cancel;
    private CdInfoTableModel tableModel;

    /**
     * Instantiates a new rip cd dialog.
     * 
     * @param owner
     *            the owner
     */
    public RipCdDialog(JFrame owner) {
        super(owner, 750, 540, true);
        setTitle(I18nUtils.getString("RIP_CD"));
        setContent(getContent());
        GuiUtils.applyComponentOrientation(this);
        enableCloseActionWithEscapeKey();
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        RipCdDialog dialog = new RipCdDialog(null);
        dialog.setVisible(true);
    }

    /**
     * Gets the album text field.
     * 
     * @return the album text field
     */
    public JTextField getAlbumTextField() {
        return albumTextField;
    }

    /**
     * Gets the amazon button.
     * 
     * @return the amazon button
     */
    public JButton getTitlesButton() {
        return titlesButton;
    }

    /**
     * Gets the artist names.
     * 
     * @return the artist names
     */
    public List<String> getArtistNames() {
        return tableModel.artistNames;
    }

    /**
     * Gets the artist text field.
     * 
     * @return the artist text field
     */
    public JTextField getArtistTextField() {
        return artistTextField;
    }

    /**
     * Gets the cancel.
     * 
     * @return the cancel
     */
    public JButton getCancel() {
        return cancel;
    }

    /**
     * Gets the composer names.
     * 
     * @return the composer names
     */
    public List<String> getComposerNames() {
        return tableModel.composerNames;
    }

    /**
     * Defines the content of the dialog box.
     * 
     * @return the content
     */
    private JPanel getContent() {
        JPanel panel = new JPanel(new GridBagLayout());

        tableModel = new CdInfoTableModel();
        table = new JTable(tableModel);
        table.setShowGrid(false);
        table.getColumnModel().getColumn(0).setMaxWidth(20);
        table.getColumnModel().getColumn(4).setMaxWidth(50);
        JCheckBox checkBox = new JCheckBox();
        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(checkBox));
        JTextField textfield1 = new CustomTextField();
        JTextField textfield2 = new CustomTextField();
        JTextField textfield3 = new CustomTextField();
        textfield1.setBorder(BorderFactory.createEmptyBorder());
        textfield2.setBorder(BorderFactory.createEmptyBorder());
        textfield3.setBorder(BorderFactory.createEmptyBorder());
        GuiUtils.applyComponentOrientation(textfield1);
        GuiUtils.applyComponentOrientation(textfield2);
        GuiUtils.applyComponentOrientation(textfield3);

        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(textfield1));
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(textfield2));
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(textfield3));

        table.setDefaultRenderer(String.class, LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(
                GuiUtils.getComponentOrientationTableCellRendererCode()));

        JScrollPane scrollPane = new JScrollPane(table);
        JLabel artistLabel = new JLabel(I18nUtils.getString("ALBUM_ARTIST"));
        artistTextField = new CustomTextField();
        JLabel albumLabel = new JLabel(I18nUtils.getString("ALBUM"));
        albumTextField = new CustomTextField();
        JLabel yearLabel = new JLabel();
        yearLabel.setText(I18nUtils.getString("YEAR"));
        yearTextField = new CustomTextField();
        JLabel genreLabel = new JLabel(I18nUtils.getString("GENRE"));

        genreComboBox = new JComboBox();
        genreComboBox.setEditable(true);
        titlesButton = new CustomButton(null, I18nUtils.getString("GET_TITLES"));
        JLabel formatLabel = new JLabel(I18nUtils.getString("ENCODE_TO"));

        format = new JComboBox();
        JLabel qualityLabel = new JLabel(I18nUtils.getString("QUALITY"));

        quality = new JComboBox(new String[] {});
        quality.setMinimumSize(new Dimension(150, 20));
        JLabel filePatternLabel = new JLabel(I18nUtils.getString("FILEPATTERN"));

        filePattern = new JComboBox(RipCdDialogController.FILENAMEPATTERN);
        JLabel dir = new JLabel(I18nUtils.getString("FOLDER"));

        folderName = new CustomTextField();
        folderSelectionButton = new JButton(I18nUtils.getString("SELECT_FOLDER"));

        // Explain what the file name pattern means
        JLabel explainPatterns = new JLabel(StringUtils.getString("%A=", I18nUtils.getString("ARTIST"), "  -  %L=", I18nUtils.getString("ALBUM"), "  -  %N=", I18nUtils
                .getString("TRACK"), "  -  %T=", I18nUtils.getString("TITLE")));

        useCdErrorCorrection = new JCheckBox(I18nUtils.getString("USE_CD_ERROR_CORRECTION"));
        ok = new CustomButton(null, I18nUtils.getString("OK"));
        cancel = new CustomButton(null, I18nUtils.getString("CANCEL"));

        JPanel auxPanel = new JPanel();
        auxPanel.setOpaque(false);
        auxPanel.add(ok);
        auxPanel.add(cancel);

        // Here we define the cd ripper dialog display layout
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridwidth = 4;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(20, 20, 10, 20);
        panel.add(scrollPane, c);
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 20, 5, 20);
        c.anchor = GridBagConstraints.WEST;
        panel.add(artistLabel, c);
        c.gridx = 1;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(artistTextField, c);
        c.gridx = 0;
        c.gridwidth = 1;
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        panel.add(albumLabel, c);
        c.gridx = 1;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(albumTextField, c);
        c.gridx = 0;
        c.gridy = 3;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        panel.add(genreLabel, c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(genreComboBox, c);
        c.gridx = 2;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        panel.add(yearLabel, c);
        c.gridx = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(yearTextField, c);
        c.gridx = 1;
        c.gridwidth = 3;
        c.gridy = 4;
        panel.add(titlesButton, c);
        c.gridx = 0;
        c.gridy = 5;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        panel.add(formatLabel, c);
        c.gridx = 1;
        c.weightx = 0.3;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(format, c);
        c.gridx = 2;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(qualityLabel, c);
        c.gridx = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(quality, c);
        c.gridx = 1;
        c.gridy = 6;
        c.gridwidth = 3;
        panel.add(useCdErrorCorrection, c);
        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        panel.add(dir, c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 3;
        panel.add(folderName, c);
        c.gridx = 0;
        c.gridy = 8;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        panel.add(filePatternLabel, c);
        c.gridx = 1;
        c.weightx = 0.3;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(filePattern, c);
        c.gridx = 2;
        c.gridwidth = 2;
        panel.add(folderSelectionButton, c);
        c.gridx = 1;
        c.gridy = 9;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = 3;
        panel.add(explainPatterns, c);
        c.gridx = 0;
        c.gridy = 10;
        c.gridwidth = 8;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(auxPanel, c);

        return panel;
    }

    /**
     * Returns the filename pattern selected in the CD ripper dialog.
     * 
     * @return Filename pattern
     */
    public String getFileNamePattern() {
        return (String) filePattern.getSelectedItem();
    }

    /**
     * Gets the file pattern.
     * 
     * @return the file pattern
     */
    public JComboBox getFilePattern() {
        return filePattern;
    }

    /**
     * Gets the folder name.
     * 
     * @return the folder name
     */
    public JTextField getFolderName() {
        return folderName;
    }

    /**
     * Gets the folder selection button.
     * 
     * @return the folder selection button
     */
    public JButton getFolderSelectionButton() {
        return folderSelectionButton;
    }

    /**
     * Gets the format.
     * 
     * @return the format
     */
    public JComboBox getFormat() {
        return format;
    }

    /**
     * Gets the genre combo box.
     * 
     * @return the genre combo box
     */
    public JComboBox getGenreComboBox() {
        return genreComboBox;
    }

    /**
     * Gets the ok.
     * 
     * @return the ok
     */
    public JButton getOk() {
        return ok;
    }

    /**
     * Gets the quality.
     * 
     * @return the quality
     */
    public String getQuality() {
        return (String) quality.getSelectedItem();
    }

    /**
     * Gets the quality combo box.
     * 
     * @return the quality combo box
     */
    public JComboBox getQualityComboBox() {
        return quality;
    }

    /**
     * Gets the track names.
     * 
     * @return the track names
     */
    public List<String> getTrackNames() {
        return tableModel.trackNames;
    }

    /**
     * Gets the tracks selected.
     * 
     * @return the tracks selected
     */

    public List<Integer> getTracksSelected() {
        List<Boolean> tracks = tableModel.getTracksSelected();
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i)) {
                result.add(i + 1);
            }
        }
        return result;
    }

    /**
     * Gets the CD error correction checkbox
     * 
     * @return The cd error correction checkbox
     */
    public JCheckBox getUseCdErrorCorrection() {
        return useCdErrorCorrection;
    }

    /**
     * Gets the year text field.
     * 
     * @return the year text field
     */
    public JTextField getYearTextField() {
        return yearTextField;
    }

    /**
     * Sets the table data.
     * 
     * @param cdInfo
     *            the new table data
     */
    public void setTableData(CDInfo cdInfo) {
        tableModel.setCdInfo(cdInfo);
        tableModel.fireTableDataChanged();
    }

    /**
     * Update artist names.
     * 
     * @param names
     *            the names
     */
    public void updateArtistNames(CDInfo cdInfo) {
        // Fill names of artists
        // Each track has an artist which can be "" if it's the same artist of all CD tracks
        List<String> names = new ArrayList<String>();
        for (String artist : cdInfo.getArtists()) {
            if (!artist.trim().equals("")) {
                names.add(artist);
            } else {
                // TODO if cdda2wav is modified for detecting song artist modify here
                if (cdInfo.getArtist() != null && !cdInfo.getArtist().trim().equals("")) {
                    names.add(cdInfo.getArtist());
                } else {
                    names.add(Artist.getUnknownArtist());
                }
            }
        }

        tableModel.setArtistNames(names);
        tableModel.fireTableDataChanged();
    }

    /**
     * Update composer names.
     * 
     * @param names
     *            the names
     */
    public void updateComposerNames(List<String> names) {
        tableModel.setComposerNames(names);
        tableModel.fireTableDataChanged();
    }

    /**
     * Update track names.
     * 
     * @param names
     *            the names
     */
    public void updateTrackNames(List<String> names) {
        tableModel.setTrackNames(names);
        tableModel.fireTableDataChanged();
    }

}
