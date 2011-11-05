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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.TransferHandler;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.kernel.actions.CollapseTreesAction;
import net.sourceforge.atunes.kernel.actions.ExpandTreesAction;
import net.sourceforge.atunes.kernel.actions.ShowAlbumsInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowArtistsInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowFoldersInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowGenresInNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowNavigationTableAction;
import net.sourceforge.atunes.kernel.actions.ShowYearsInNavigatorAction;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationTreePanel;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.utils.GuiUtils;

public final class NavigationTreePanel extends JPanel implements INavigationTreePanel {

    private static final long serialVersionUID = -2900418193013495812L;

    private PopUpButton options;
    
    private JComboBox treeComboBox;
    
    private JPanel treePanel;
    
    /**
     * Instantiates a new navigation panel.
     * @param lookAndFeelManager
     */
    public NavigationTreePanel(ILookAndFeelManager lookAndFeelManager)  {
        super(new GridBagLayout(), true);
        addContent(lookAndFeelManager);
    }

    /**
     * Adds the content.
     * @param lookAndFeelManager 
     */
    private void addContent(ILookAndFeelManager lookAndFeelManager) {
    	options = new PopUpButton(PopUpButton.BOTTOM_RIGHT, lookAndFeelManager);
        JRadioButtonMenuItem showArtist = new JRadioButtonMenuItem(Context.getBean(ShowArtistsInNavigatorAction.class));
        JRadioButtonMenuItem showAlbum = new JRadioButtonMenuItem(Context.getBean(ShowAlbumsInNavigatorAction.class));
        JRadioButtonMenuItem showGenre = new JRadioButtonMenuItem(Context.getBean(ShowGenresInNavigatorAction.class));
        JRadioButtonMenuItem showYear = new JRadioButtonMenuItem(Context.getBean(ShowYearsInNavigatorAction.class));
        JRadioButtonMenuItem showFolder = new JRadioButtonMenuItem(Context.getBean(ShowFoldersInNavigatorAction.class));
        ButtonGroup group = new ButtonGroup();
        group.add(showArtist);
        group.add(showAlbum);
        group.add(showGenre);
        group.add(showYear);
        group.add(showFolder);
        options.add(showArtist);
        options.add(showAlbum);
        options.add(showGenre);
        options.add(showYear);
        options.add(showFolder);
        options.add(new JSeparator());
        options.add(Context.getBean(ExpandTreesAction.class));
        options.add(Context.getBean(CollapseTreesAction.class));
        options.addSeparator();
        options.add(new JCheckBoxMenuItem(Context.getBean(ShowNavigationTableAction.class)));


    	treeComboBox = new JComboBox();
        treePanel = new JPanel(new CardLayout());
        addTrees();
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        add(options, c);
        c.gridx = 1;
        c.weightx = 1;
        add(treeComboBox, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weighty = 1;
        add(treePanel, c);

        // Apply component orientation to all popup menus
        for (INavigationView view : Context.getBean(INavigationHandler.class).getNavigationViews()) {
            GuiUtils.applyComponentOrientation(view.getTreePopupMenu());
        }
       
    }

    /**
     * Updates panel to show all trees
     */
    private void addTrees() {
    	treeComboBox.removeAllItems();
    	treePanel.removeAll();

        for (INavigationView view : Context.getBean(INavigationHandler.class).getNavigationViews()) {
        	treeComboBox.addItem(view);
        	treePanel.add(view.getClass().getName(), view.getTreeScrollPane());
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.panels.INavigationTreePanel#updateTrees()
	 */
    @Override
	public void updateTrees() {
        addTrees();
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.panels.INavigationTreePanel#showNavigationView(net.sourceforge.atunes.model.INavigationView)
	 */
	@Override
	public void showNavigationView(INavigationView view) {		
		((CardLayout)treePanel.getLayout()).show(treePanel, view.getClass().getName());
		treeComboBox.setSelectedItem(view);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.panels.INavigationTreePanel#getTreeComboBox()
	 */
	@Override
	public JComboBox getTreeComboBox() {
		return treeComboBox;
	}
	
	@Override
	public JPanel getSwingComponent() {
		return this;
	}
	
	@Override
	public void enableDragAndDrop(TransferHandler transferHandler) {
		setTransferHandler(transferHandler);
	}
}
