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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.atunes.gui.views.controls.CustomTextField;
import net.sourceforge.atunes.gui.views.controls.LookAndFeelAwareButton;
import net.sourceforge.atunes.model.IFilterPanel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public class FilterPanel extends JPanel implements IFilterPanel {

    private static final long serialVersionUID = 1801321624657098000L;

    private JTextField filterTextField;
    private JButton clearButton;
    
    private boolean filterApplied;

    private ILookAndFeelManager lookAndFeelManager;
    
    /**
     * @param state
     * @param lookAndFeelManager
     */
    public FilterPanel() {
        super(new BorderLayout(3, 0));
    }
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

    /**
     * Initializes panel
     */
    public void initialize() {
        filterTextField = new CustomTextField(8);
        filterTextField.setText(StringUtils.getString(I18nUtils.getString("FILTER"), "..."));
        filterTextField.setToolTipText(I18nUtils.getString("FILTER_TEXTFIELD_TOOLTIP"));
        clearButton = new LookAndFeelAwareButton(lookAndFeelManager) {
        	/**
			 * 
			 */
			private static final long serialVersionUID = -8082456165867297443L;
			
			private final Dimension size = new Dimension(16, 16);
			
			public Dimension getPreferredSize() {
				return size;
			};
			
			public Dimension getMinimumSize() {
				return size;
			};
			
			public Dimension getMaximumSize() {
				return size;
			}
			
			protected void paintComponent(java.awt.Graphics g) {
				if (!filterApplied) {
					return;
				}
				Ellipse2D.Float e = new Ellipse2D.Float(-7, -7, 14, 14);
				Polygon p = new Polygon();
				p.addPoint(-4, -2);
				p.addPoint(-2, -4);
				p.addPoint(0, -2);
				p.addPoint(2, -4);
				p.addPoint(4, -2);
				p.addPoint(2, 0);
				p.addPoint(4, 2);
				p.addPoint(2, 4);
				p.addPoint(0, 2);
				p.addPoint(-2, 4);
				p.addPoint(-4, 2);
				p.addPoint(-2, 0);

		        Graphics2D g2 = (Graphics2D) g;
		        g2.translate(getBounds().width / 2, getBounds().height / 2);
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		        g2.setPaint(lookAndFeelManager.getCurrentLookAndFeel().getPaintForSpecialControls());
		        Area a = new Area(e);
		        a.subtract(new Area(p));
		        g2.fill(a);
		        g2.dispose();
        	};
        };
        add(filterTextField, BorderLayout.CENTER);
        add(clearButton, BorderLayout.EAST);
    }

    /**
     * @return the filterTextField
     */
    @Override
	public JTextField getFilterTextField() {
        return filterTextField;
    }

	/**
	 * @return the clearButton
	 */
	@Override
	public JButton getClearButton() {
		return clearButton;
	}

	/**
	 * Called to update filter panel
	 * @param filterApplied
	 */
	@Override
	public void setFilterApplied(boolean filterApplied) {
		this.filterApplied = filterApplied;
		this.clearButton.repaint();
	}
	
	@Override
	public JPanel getSwingComponent() {
		return this;
	}
}