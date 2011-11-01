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

package net.sourceforge.atunes.gui.views.dialogs.editPreferences;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
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

import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.CustomTextField;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.ClearCachesAction;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILyricsEngineInfo;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

public final class ContextPanel extends AbstractPreferencesPanel {

    private static class OpenLastFmMouseAdapter extends MouseAdapter {
    	private IDesktop desktop;
    	
    	public OpenLastFmMouseAdapter(IDesktop desktop) {
    		this.desktop = desktop;
		}
    	
        @Override
        public void mousePressed(MouseEvent e) {
        	desktop.openURL("http://www.last.fm");
        }
    }

    private class LyricsEnginesTableModel implements TableModel {

        /** The lyrics engines info. */
        private List<ILyricsEngineInfo> lyricsEnginesInfo;

        /** The listeners. */
        private List<TableModelListener> listeners = new ArrayList<TableModelListener>();

        /**
         * Instantiates a new lyrics engines info model.
         */
        LyricsEnginesTableModel() {
            // Nothing to do
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            listeners.add(l);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? Boolean.class : String.class;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int column) {
            return "";
        }

        @Override
        public int getRowCount() {
            if (this.lyricsEnginesInfo != null) {
                return this.lyricsEnginesInfo.size();
            }
            return 0;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return lyricsEnginesInfo.get(rowIndex).isEnabled();
            }
            return lyricsEnginesInfo.get(rowIndex).getName();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }

        public void moveDown(int columnPos) {

            Collections.swap(lyricsEnginesInfo, columnPos, columnPos + 1);

            TableModelEvent event;
            event = new TableModelEvent(this, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);
            for (int i = 0; i < listeners.size(); i++) {
                listeners.get(i).tableChanged(event);
            }
            enginesTable.getColumnModel().getColumn(0).setMaxWidth(20);
            enginesTable.getSelectionModel().setSelectionInterval(columnPos + 1, columnPos + 1);

        }

        public void moveUp(int columnPos) {

            Collections.swap(lyricsEnginesInfo, columnPos, columnPos - 1);

            TableModelEvent event;
            event = new TableModelEvent(this, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);
            for (int i = 0; i < listeners.size(); i++) {
                listeners.get(i).tableChanged(event);
            }
            enginesTable.getColumnModel().getColumn(0).setMaxWidth(20);
            enginesTable.getSelectionModel().setSelectionInterval(columnPos - 1, columnPos - 1);
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
            listeners.remove(l);
        }

        public void setLyricsEnginesInfo(List<ILyricsEngineInfo> lyricsEnginesInfo) {
            this.lyricsEnginesInfo = new ArrayList<ILyricsEngineInfo>(lyricsEnginesInfo);
        }

        public List<ILyricsEngineInfo> getLyricsEnginesInfo() {
            return lyricsEnginesInfo;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                lyricsEnginesInfo.get(rowIndex).setEnabled((Boolean) aValue);
            }
        }

    }

    private static final long serialVersionUID = -9216216930198145476L;

    /** The activate context information. */
    private JCheckBox activateContext;

    /** The save pictures. */
    private JCheckBox savePictures;

    /**
     * Checkbox to let user select if want to hide albums of "Various Artists"
     */
    private JCheckBox hideVariousArtistsAlbums;

    /**
     * Checkbox to select minimum song number for each album
     */
    private JCheckBox minimumSongNumberPerAlbum;

    /**
     * Text field to set minimum song number for each album
     */
    private JTextField minimumSongNumber;

    /**
     * Checkbox to select if want to show albums in a grid
     */
    private JCheckBox showAlbumsInGrid;

    /** The info label. */
    private JLabel info;

    /** The lyrics engines table. */
    private JTable enginesTable;

    /**
     * Instantiates a new context panel.
     * @param lookAndFeel
     * @param desktop
     */
    public ContextPanel(ILookAndFeel lookAndFeel, IDesktop desktop) {
        super(I18nUtils.getString("CONTEXT_INFORMATION"));
        activateContext = new JCheckBox(I18nUtils.getString("ACTIVATE_CONTEXT_INFORMATION"));
        savePictures = new JCheckBox(I18nUtils.getString("SAVE_PICTURES_TO_AUDIO_FOLDERS"));
        hideVariousArtistsAlbums = new JCheckBox(I18nUtils.getString("HIDE_VARIOUS_ARTISTS_ALBUMS"));
        minimumSongNumberPerAlbum = new JCheckBox(I18nUtils.getString("MINIMUM_SONG_NUMBER_PER_ALBUM"));
        minimumSongNumber = new CustomTextField(4);
        minimumSongNumber.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar()) || minimumSongNumber.getText().length() > 2) {
                    e.consume();
                }
            }
        });
        minimumSongNumberPerAlbum.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                minimumSongNumber.setEnabled(minimumSongNumberPerAlbum.isSelected());
            }
        });
        Box minimumSongNumberBox = Box.createHorizontalBox();
        minimumSongNumberBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        minimumSongNumberBox.add(minimumSongNumberPerAlbum);
        minimumSongNumberBox.add(Box.createHorizontalStrut(6));
        minimumSongNumberBox.add(minimumSongNumber);
        minimumSongNumberBox.add(Box.createHorizontalGlue());
        minimumSongNumber.setMinimumSize(new Dimension(50, 20));
        showAlbumsInGrid = new JCheckBox(I18nUtils.getString("SHOW_ALBUMS_IN_GRID"));

        activateContext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                savePictures.setEnabled(activateContext.isSelected());
                hideVariousArtistsAlbums.setEnabled(activateContext.isSelected());
            }
        });
        JButton clearCache = new JButton(Actions.getAction(ClearCachesAction.class));
        info = new JLabel(Images.getImage(Images.POWERED_BY_LAST_FM), GuiUtils.getComponentOrientationAsSwingConstant());
        info.addMouseListener(new OpenLastFmMouseAdapter(desktop));
        JLabel enginesTableLabel = new JLabel(I18nUtils.getString("LYRICS_ENGINES_SELECTION"));
        final LyricsEnginesTableModel model = new LyricsEnginesTableModel();
        enginesTable = lookAndFeel.getTable();
        enginesTable.setModel(model);
        enginesTable.setTableHeader(null);
        enginesTable.getColumnModel().getColumn(0).setMaxWidth(20);
        enginesTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
        enginesTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        enginesTable.setDefaultRenderer(String.class, lookAndFeel.getTableCellRenderer(
                GuiUtils.getComponentOrientationTableCellRendererCode(lookAndFeel)));
        JButton upButton = new JButton(I18nUtils.getString("MOVE_UP"));
        upButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = enginesTable.getSelectedRow();
                if (selectedRow > 0) {
                    model.moveUp(selectedRow);
                }
            }
        });
        JButton downButton = new JButton(I18nUtils.getString("MOVE_DOWN"));
        downButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = enginesTable.getSelectedRow();
                if (selectedRow < enginesTable.getModel().getRowCount() - 1) {
                    model.moveDown(selectedRow);
                }
            }
        });
        JScrollPane enginesScrollPane = lookAndFeel.getTableScrollPane(enginesTable);
        enginesScrollPane.setMinimumSize(new Dimension(200, 100));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        add(activateContext, c);
        c.gridx = 0;
        c.gridy = 1;
        add(savePictures, c);
        c.gridy = 2;
        add(hideVariousArtistsAlbums, c);
        c.gridy = 3;
        // c.fill = GridBagConstraints.HORIZONTAL;
        add(minimumSongNumberBox, c);
        c.gridy = 4;
        add(showAlbumsInGrid, c);
        c.fill = GridBagConstraints.NONE;
        c.gridy = 5;
        c.insets = new Insets(10, 0, 10, 0);
        add(clearCache, c);
        c.gridy = 6;
        c.insets = new Insets(0, 0, 5, 0);
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        add(enginesTableLabel, c);
        c.gridy = 7;
        c.insets = new Insets(0, 10, 0, 0);
        add(enginesScrollPane, c);
        c.gridy = 8;
        c.gridheight = 1;
        c.insets = new Insets(0, 0, 0, 0);
        JPanel p = new JPanel(new FlowLayout());
        p.add(upButton);
        p.add(downButton);
        add(p, c);
        c.gridy = 9;
        c.insets = new Insets(20, 0, 0, 0);
        c.weighty = 1;
        add(info, c);
    }

    @Override
    public boolean applyPreferences(IState state) {
        state.setUseContext(activateContext.isSelected());
        state.setSaveContextPicture(savePictures.isSelected());
        state.setHideVariousArtistsAlbums(hideVariousArtistsAlbums.isSelected());
        state.setMinimumSongNumberPerAlbum(minimumSongNumberPerAlbum.isSelected() ? Integer.parseInt(minimumSongNumber.getText()) : 0);
        state.setLyricsEnginesInfo(((LyricsEnginesTableModel) enginesTable.getModel()).getLyricsEnginesInfo());
        boolean showAlbumsInGridPreviousValue = state.isShowContextAlbumsInGrid();
        state.setShowContextAlbumsInGrid(showAlbumsInGrid.isSelected());
        return showAlbumsInGridPreviousValue != showAlbumsInGrid.isSelected();
    }

    /**
     * Sets the activate context information.
     * 
     * @param activate
     *            the new activate context information
     */
    private void setActivateContext(boolean activate) {
        activateContext.setSelected(activate);
        savePictures.setEnabled(activate);
        hideVariousArtistsAlbums.setEnabled(activate);
    }

    /**
     * Sets the save pictures.
     * 
     * @param save
     *            the new save pictures
     */
    private void setSavePictures(boolean save) {
        savePictures.setSelected(save);
    }

    /**
     * Sets the hide various artists albums checkbox value
     * 
     * @param hide
     */
    private void setHideVariousArtistsAlbums(boolean hide) {
        hideVariousArtistsAlbums.setSelected(hide);
    }

    private void setMinimumSongNumberPerAlbum(int number) {
        minimumSongNumberPerAlbum.setSelected(number != 0);
        minimumSongNumber.setEnabled(number != 0);
        minimumSongNumber.setText(Integer.toString(number));
    }

    /**
     * Sets the lyrics engines info
     * 
     * @param list
     *            list with lyrics engines info
     */
    private void setLyricsEnginesInfo(List<ILyricsEngineInfo> list) {
        List<ILyricsEngineInfo> copy = new ArrayList<ILyricsEngineInfo>();
        for (ILyricsEngineInfo lyricsEngineInfo : list) {
            copy.add(lyricsEngineInfo.copy());
        }
        ((LyricsEnginesTableModel) enginesTable.getModel()).setLyricsEnginesInfo(copy);
    }

    /**
     * Sets the show albums in grid check box
     * 
     * @param show
     */
    private void setShowAlbumsInGrid(boolean show) {
        showAlbumsInGrid.setSelected(show);
    }

    @Override
    public void updatePanel(IState state) {
        setActivateContext(state.isUseContext());
        setSavePictures(state.isSaveContextPicture());
        setHideVariousArtistsAlbums(state.isHideVariousArtistsAlbums());
        setMinimumSongNumberPerAlbum(state.getMinimumSongNumberPerAlbum());
        setLyricsEnginesInfo(state.getLyricsEnginesInfo());
        setShowAlbumsInGrid(state.isShowContextAlbumsInGrid());
    }

    @Override
    public void resetImmediateChanges(IState state) {
        // Do nothing
    }

    @Override
    public void validatePanel() throws PreferencesValidationException {
    }

    @Override
    public void dialogVisibilityChanged(boolean visible) {
        // Do nothing
    }
}
