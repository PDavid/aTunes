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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.utils.GuiUtils;

/**
 * A button with a popup menu.
 * 
 * @author fleax
 */
public class PopUpButton extends JButton {

    private static final long serialVersionUID = 5193978267971626102L;

    public static final int TOP_LEFT = 0;
    public static final int TOP_RIGHT = 1;
    public static final int BOTTOM_LEFT = 3;
    public static final int BOTTOM_RIGHT = 4;

    private JPopupMenu menu;
    private int location;
    private int xLocation;
    private int yLocation;
    private List<Component> items = new ArrayList<Component>();
    
    private boolean menuShown = false;
    
    private Polygon topShape;
    
    /**
     * Instantiates a new pop up button with an arrow
     * 
     * @param location
     *            the location
     */
    public PopUpButton(int location) {
        super();
        this.location = location;
        setButton();
        GuiUtils.applyComponentOrientation(menu);
        
        Dimension dimension = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPopUpButtonSize();
        if (dimension != null) {
        	setMinimumSize(dimension);
        	setPreferredSize(dimension);
        	setMaximumSize(dimension);
        }
        
        topShape = new Polygon();
        topShape.addPoint(- 4, 4);
        topShape.addPoint(4, 4);
        topShape.addPoint(0, -2);        
    }
    
    /**
     * Instantiates a new pop up button with a text
     * @param location
     * @param text
     */
    public PopUpButton(int location, String text) {
        super(text);
        this.location = location;
        setButton();
        GuiUtils.applyComponentOrientation(menu);
    }

    @Override
    public Component add(Component item) {
        if (!(item instanceof JSeparator)) {
            items.add(item);
        }
        Component c = menu.add(item);
        GuiUtils.applyComponentOrientation(menu);
        return c;
    }

    @Override
    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);

    	if (topShape != null) {
    		Graphics2D g2 = (Graphics2D) g;
    		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);    	
    		g2.setPaint(isEnabled() ? LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPaintForSpecialControls() :
    			LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPaintForDisabledSpecialControls());
    		g2.translate(getWidth() / 2, getHeight() / 2);
    		if (this.location == BOTTOM_LEFT || this.location == BOTTOM_RIGHT) {
    			g2.rotate(Math.PI);
    		}
    		g2.fill(topShape);
    		g2.dispose();
    	}
    }

    /**
     * Adds a new MenuItem with an action
     * @param action
     * @return
     */
    public Component add(Action action) {
    	JMenuItem item = menu.add(action);
    	items.add(item);
    	GuiUtils.applyComponentOrientation(menu);
    	return item;    	
    }
    
    /**
     * Removes the all items.
     */
    public void removeAllItems() {
        menu.removeAll();
        items.clear();
    }

    /**
     * Sets the button.
     */
    private void setButton() {
        menu = new JPopupMenu();
        addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		if (isEnabled()) {
        			if (!menuShown) {
        				setMenuLocation(location);
        				menu.show(PopUpButton.this, xLocation, yLocation);
        			} else {
        				menu.setVisible(false);
        			}
        		}
        	}
		});
        menu.addPopupMenuListener(new PopupMenuListener() {
			
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				menuShown = true;
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				menuShown = false;
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				menuShown = false;
			}
		});
    }

    /**
     * Sets the menu location.
     * 
     * @param location
     *            the new menu location
     */
    void setMenuLocation(int location) {
        if (!items.isEmpty()) {
            if (location == TOP_LEFT || location == TOP_RIGHT) {
                yLocation = -(int) items.get(0).getPreferredSize().getHeight() * items.size() - 5;
            } else {
                yLocation = 21;
            }
            if (location == TOP_LEFT || location == BOTTOM_LEFT) {
                int maxWidth = 0;
                for (int i = 0; i < items.size(); i++) {
                    if ((int) items.get(i).getPreferredSize().getWidth() > maxWidth) {
                        maxWidth = (int) items.get(i).getPreferredSize().getWidth();
                    }
                }
                xLocation = -maxWidth + (int) getPreferredSize().getWidth();
            } else {
                xLocation = 0;
            }
        }
    }

	public void addSeparator() {
		menu.addSeparator();
	}	
	
	/**
	 * In certain situations we need to hide menu programatically without clicking button again
	 */
	public void hideMenu() {
		menu.setVisible(false);
	}
}
