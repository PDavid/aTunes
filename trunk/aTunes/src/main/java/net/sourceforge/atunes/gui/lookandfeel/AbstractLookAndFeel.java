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

package net.sourceforge.atunes.gui.lookandfeel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.util.Enumeration;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import net.sourceforge.atunes.gui.views.controls.CustomSplitPane;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IOSManager;

import org.commonjukebox.plugins.model.PluginApi;

@PluginApi
public abstract class AbstractLookAndFeel implements ILookAndFeel {

	/**
	 * Base font
	 */
	protected Font baseFont;
	
	protected IOSManager osManager;
	
    private static final class LookAndFeelListCellRenderer extends DefaultListCellRenderer {
        private final AbstractListCellRendererCode code;
        /**
		 * 
		 */
        private static final long serialVersionUID = 2572603555660744197L;

        private LookAndFeelListCellRenderer(AbstractListCellRendererCode code) {
            this.code = code;
        }

        @Override
        public JComponent getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        	JComponent c = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        	if (code != null) {
        		c = code.getComponent(c, list, value, index, isSelected, cellHasFocus);
        	}
            c.setOpaque(isSelected);
            return c;
        }
    }

    private static final class LookAndFeelTableHeaderCellRenderer extends DefaultTableCellRenderer {
        private final AbstractTableCellRendererCode code;
        /**
		 * 
		 */
        private static final long serialVersionUID = 1L;

        private LookAndFeelTableHeaderCellRenderer(AbstractTableCellRendererCode code) {
            this.code = code;
        }

        @Override
        public JComponent getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        	JComponent c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return code.getComponent(c, table, value, isSelected, hasFocus, row, column);
        }
    }

    private static final class LookAndFeelTableCellRenderer extends DefaultTableCellRenderer {
        private final AbstractTableCellRendererCode code;
        /**
		 * 
		 */
        private static final long serialVersionUID = 1L;

        private LookAndFeelTableCellRenderer(AbstractTableCellRendererCode code) {
            this.code = code;
        }

        @Override
        public JComponent getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        	JComponent c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        	if (code != null) {
        		c = code.getComponent(c, table, value, isSelected, hasFocus, row, column);
        	}
            c.setOpaque(isSelected);
            return c;
        }
    }

    private static final class LookAndFeelTreeCellRenderer extends DefaultTreeCellRenderer {
        private final AbstractTreeCellRendererCode code;
        /**
		 * 
		 */
        private static final long serialVersionUID = 5424315832943108932L;

        private LookAndFeelTreeCellRenderer(AbstractTreeCellRendererCode code) {
            this.code = code;
        }

        @Override
        public JComponent getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        	// Use custom JLabel component (if call super method, returned component can't be fully customized)
        	JComponent c = new JLabel(value.toString());
        	if (code != null) {
        		c = code.getComponent(c, tree, value,isSelected, expanded, leaf, row, hasFocus);
        	}
        	c.setOpaque(isSelected);
        	if (isSelected) {
        		c.setBackground(UIManager.getColor("Tree.selectionBackground"));
        		c.setForeground(UIManager.getColor("Tree.selectionForeground"));
        	}
            return c;
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getName()
	 */
    @Override
	public abstract String getName();

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getDescription()
	 */
    @Override
	public abstract String getDescription();

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#initializeLookAndFeel()
	 */
    @Override
	public abstract void initializeLookAndFeel();

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#setLookAndFeel(java.lang.String)
	 */
    @Override
	public abstract void setLookAndFeel(String skin);

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getDefaultSkin()
	 */
    @Override
	public abstract String getDefaultSkin();

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getSkins()
	 */
    @Override
	public abstract List<String> getSkins();

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#isDialogUndecorated()
	 */
    @Override
	public boolean isDialogUndecorated() {
        return false;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getTreeCellRenderer(net.sourceforge.atunes.gui.lookandfeel.AbstractTreeCellRendererCode)
	 */
    @Override
	public TreeCellRenderer getTreeCellRenderer(final AbstractTreeCellRendererCode code) {
        return new LookAndFeelTreeCellRenderer(code);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getTableCellRenderer(net.sourceforge.atunes.gui.lookandfeel.AbstractTableCellRendererCode)
	 */
    @Override
	public TableCellRenderer getTableCellRenderer(final AbstractTableCellRendererCode code) {
        return new LookAndFeelTableCellRenderer(code);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getTableHeaderCellRenderer(net.sourceforge.atunes.gui.lookandfeel.AbstractTableCellRendererCode)
	 */
    @Override
	public TableCellRenderer getTableHeaderCellRenderer(final AbstractTableCellRendererCode code) {
        return new LookAndFeelTableHeaderCellRenderer(code);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getListCellRenderer(net.sourceforge.atunes.gui.lookandfeel.AbstractListCellRendererCode)
	 */
    @Override
	public ListCellRenderer getListCellRenderer(final AbstractListCellRendererCode code) {
        return new LookAndFeelListCellRenderer(code);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#customComboBoxRenderersSupported()
	 */
    @Override
	public boolean customComboBoxRenderersSupported() {
    	return true;
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#putClientProperties(javax.swing.JComponent)
	 */
    @Override
	public void putClientProperties(JComponent c) {
        // No properties by default
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#isCustomPlayerControlsSupported()
	 */
    @Override
	public boolean isCustomPlayerControlsSupported() {
        return false;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getShadowBorder()
	 */
    @Override
	public Border getShadowBorder() {
        return null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#initializeFonts(java.awt.Font)
	 */
    @Override
	public void initializeFonts(Font baseFont) {
    	this.baseFont = baseFont;
    	setUIFont(new FontUIResource(baseFont));
    }

    /**
     * Changes all components' font to a given one
     * @param f
     */
    protected void setUIFont (FontUIResource f){
    	Enumeration<Object> keys = UIManager.getDefaults().keys();
    	while (keys.hasMoreElements()) {
    		Object key = keys.nextElement();
    		Object value = UIManager.get (key);
    		if (value instanceof FontUIResource) {
    			UIManager.put (key, f);
    		}
    	}
    }    
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#allowsSkins()
	 */
    @Override
	public boolean allowsSkins() {
    	return getSkins() != null && !getSkins().isEmpty();
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getPaintForSpecialControls()
	 */
	@Override
	public Paint getPaintForSpecialControls() {
		return UIManager.getColor("Label.foreground"); 
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getPaintForDisabledSpecialControls()
	 */
	@Override
	public Paint getPaintForDisabledSpecialControls() {
		return UIManager.getColor("Label.foreground"); 
	}	

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getPaintForColorMutableIcon(java.awt.Component, boolean)
	 */
	@Override
	public Paint getPaintForColorMutableIcon(Component c, boolean isSelected) {
		return isSelected ? UIManager.getColor("Tree.selectionForeground") : UIManager.getColor("Label.foreground");	
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getPopUpButtonSize()
	 */
	@Override
	public Dimension getPopUpButtonSize() {
		return new Dimension(20, 20);
	}

	
	/********************************************************* FONTS *******************************************************/
	
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getDefaultFont()
	 */
    @Override
	public Font getDefaultFont() {
        return this.baseFont;
    }
    
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#supportsCustomFontSettings()
	 */
	@Override
	public abstract boolean supportsCustomFontSettings();
	
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getAboutBigFont()
	 */
    @Override
	public final Font getAboutBigFont() {
        return getDefaultFont().deriveFont(getDefaultFont().getSize() + 8f);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getPlayListFont()
	 */
    @Override
	public Font getPlayListFont() {
        return getDefaultFont();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getPlayListSelectedItemFont()
	 */
    @Override
	public Font getPlayListSelectedItemFont() {
    	if (getPlayListFont() != null) {
    		return getPlayListFont().deriveFont(Font.BOLD);
    	}
    	return getDefaultFont();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getContextInformationBigFont()
	 */
    @Override
	public final Font getContextInformationBigFont() {
        return getDefaultFont().deriveFont(getDefaultFont().getSize() + 8f);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getPropertiesDialogBigFont()
	 */
    @Override
	public final Font getPropertiesDialogBigFont() {
        return getDefaultFont().deriveFont(getDefaultFont().getSize() + 4f);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getOsdLine1Font()
	 */
    @Override
	public final Font getOsdLine1Font() {
        return getDefaultFont().deriveFont(Font.BOLD, getDefaultFont().getSize() + 4f);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getOsdLine2Font()
	 */
    @Override
	public final Font getOsdLine2Font() {
        return getDefaultFont().deriveFont(getDefaultFont().getSize() + 2f);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getOsdLine3Font()
	 */
    @Override
	public final Font getOsdLine3Font() {
        return getOsdLine2Font();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getFullScreenLine1Font()
	 */
    @Override
	public final Font getFullScreenLine1Font() {
        return  getDefaultFont().deriveFont(getDefaultFont().getSize() + 25f);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getSplitPaneDividerSize()
	 */
    @Override
	public int getSplitPaneDividerSize() {
    	return new CustomSplitPane(JSplitPane.HORIZONTAL_SPLIT).getDividerSize();
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getTable()
	 */
    @Override
	public abstract JTable getTable();
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#decorateTable(javax.swing.JTable)
	 */
    @Override
	public abstract void decorateTable(JTable table);
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getTableScrollPane(javax.swing.JTable)
	 */
    @Override
	public abstract JScrollPane getTableScrollPane(JTable table);
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getTreeScrollPane(javax.swing.JTree)
	 */
    @Override
	public abstract JScrollPane getTreeScrollPane(JTree tree);

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getList()
	 */
    @Override
	public abstract JList getList();
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getListScrollPane(javax.swing.JList)
	 */
    @Override
	public abstract JScrollPane getListScrollPane(JList list);
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeel#getScrollPane(java.awt.Component)
	 */
    @Override
	public abstract JScrollPane getScrollPane(Component component);

	/**
	 * @param osManager the osManager to set
	 */
    @Override
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}

	@Override
	public void setBaseFont(Font f) {
		this.baseFont = f;
	}
}
