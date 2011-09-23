/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.lookandfeel.system;

import java.awt.Component;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.sourceforge.atunes.gui.lookandfeel.AbstractLookAndFeel;
import net.sourceforge.atunes.utils.Logger;

public class SystemLookAndFeel extends AbstractLookAndFeel {

    public static final String SYSTEM = "System";

	@Override
    public String getName() {
        return SYSTEM;
    }

    @Override
    public String getDescription() {
        return "System Look And Feel";
    }

    @Override
    public List<String> getSkins() {
        return null;
    }

    @Override
    public void initializeLookAndFeel() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        // There is a problem with GTKLookAndFeel which shows slider values so we disable it
        UIManager.put("Slider.paintValue", false);
    }

    @Override
    public String getDefaultSkin() {
        return null;
    }
    
    @Override
    public boolean customComboBoxRenderersSupported() {
    	return false;
    }

    @Override
    public void setLookAndFeel(String skin) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            Logger.error(e);
        } catch (InstantiationException e) {
            Logger.error(e);
        } catch (IllegalAccessException e) {
            Logger.error(e);
        } catch (UnsupportedLookAndFeelException e) {
            Logger.error(e);
        }
    }    
    
    @Override
    public boolean supportsCustomFontSettings() {
    	return false;
    }

    @Override
    public JScrollPane getScrollPane(Component component) {
    	return new JScrollPane(component);
    }
    
    @Override
    public JScrollPane getTableScrollPane(JTable table) {
    	return getScrollPane(table);
    }
    
    @Override
    public JTable getTable() {
    	JTable table = new JTable();
    	table.setShowGrid(false);
    	return table;
    }
    
    @Override
    public void decorateTable(JTable table) {
    	table.setShowGrid(false);
    }
    
    @Override
    public JScrollPane getTreeScrollPane(JTree tree) {
    	return getScrollPane(tree);
    }
    
    @Override
    public JScrollPane getListScrollPane(JList list) {
    	return getScrollPane(list);
    }
    
    @Override
    public JList getList() {
    	return new JList();
    }

}
