/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.utils.GuiUtils;

/**
 * A button with a popup menu.
 * 
 * @author fleax
 */
public final class PopUpButton extends JButton {

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

    /**
     * Instantiates a new pop up button.
     * 
     * @param action
     *            the action
     * @param location
     *            the location
     */
    public PopUpButton(Action action, int location) {
        super();
        setAction(action);
        setPreferredSize(new Dimension(20, 20));
        setButton(location);
    }

    /**
     * Instantiates a new pop up button.
     * 
     * @param text
     *            the text
     * @param location
     *            the location
     */
    public PopUpButton(String text, int location) {
        super(text, null);
        setButton(location);
        setIcon(location);
        GuiUtils.applyComponentOrientation(menu);
    }

    private void setIcon(int location) {
        if (location == TOP_LEFT || location == TOP_RIGHT) {
            setIcon(Images.getImage(Images.ARROW_UP));
        } else if (location == BOTTOM_LEFT || location == BOTTOM_RIGHT) {
            setIcon(Images.getImage(Images.ARROW_DOWN));
        }
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

    /**
     * Gets the location property.
     * 
     * @return the location property
     */
    int getLocationProperty() {
        return location;
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
     * 
     * @param location
     *            the new button
     */
    private void setButton(int location) {
        this.location = location;
        menu = new JPopupMenu();
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                setMenuLocation(getLocationProperty());
                menu.show(PopUpButton.this, xLocation, yLocation);
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

}
