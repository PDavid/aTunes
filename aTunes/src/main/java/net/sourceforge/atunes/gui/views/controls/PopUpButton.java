/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPopUpButton;

/**
 * A button with a popup menu.
 * 
 * @author fleax
 */
public class PopUpButton extends JButton implements IPopUpButton {

	private static final long serialVersionUID = 5193978267971626102L;

	/**
	 * Menu above button and adjusted to left
	 */
	public static final int TOP_LEFT = 0;

	/**
	 * Menu above button and adjusted to right
	 */
	public static final int TOP_RIGHT = 1;

	/**
	 * Menu below button and adjusted to left
	 */
	public static final int BOTTOM_LEFT = 3;

	/**
	 * Menu below button and adjusted to right
	 */
	public static final int BOTTOM_RIGHT = 4;

	private JPopupMenu menu;
	private final int location;
	private int xLocation;
	private int yLocation;
	private final List<Component> items = new ArrayList<Component>();

	private boolean menuShown = false;

	private Polygon topShape;

	private ILookAndFeel lookAndFeel;

	private final IControlsBuilder controlsBuilder;

	/**
	 * Instantiates a new pop up button with an arrow
	 * 
	 * @param location
	 * @param lookAndFeelManager
	 * @param controlsBuilder
	 */
	PopUpButton(final int location,
			final ILookAndFeelManager lookAndFeelManager,
			final IControlsBuilder controlsBuilder) {
		super();
		this.controlsBuilder = controlsBuilder;
		this.location = location;
		this.lookAndFeel = lookAndFeelManager.getCurrentLookAndFeel();
		setButton();
		controlsBuilder.applyComponentOrientation(this.menu);

		Dimension dimension = this.lookAndFeel.getPopUpButtonSize();
		if (dimension != null) {
			setMinimumSize(dimension);
			setPreferredSize(dimension);
			setMaximumSize(dimension);
		}

		this.topShape = new Polygon();
		this.topShape.addPoint(-4, 4);
		this.topShape.addPoint(4, 4);
		this.topShape.addPoint(0, -2);
	}

	/**
	 * Instantiates a new pop up button with a text
	 * 
	 * @param location
	 * @param text
	 * @param controlsBuilder
	 */
	PopUpButton(final int location, final String text,
			final IControlsBuilder controlsBuilder) {
		super(text);
		this.controlsBuilder = controlsBuilder;
		this.location = location;
		setButton();
		this.controlsBuilder.applyComponentOrientation(this.menu);
	}

	@Override
	public Component add(final Component item) {
		if (!(item instanceof JSeparator)) {
			this.items.add(item);
		}
		Component c = this.menu.add(item);
		this.controlsBuilder.applyComponentOrientation(this.menu);
		return c;
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);

		if (this.topShape != null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setPaint(isEnabled() ? this.lookAndFeel
					.getPaintForSpecialControls() : this.lookAndFeel
					.getPaintForDisabledSpecialControls());
			g2.translate(getWidth() / 2, getHeight() / 2);
			if (this.location == BOTTOM_LEFT || this.location == BOTTOM_RIGHT) {
				g2.rotate(Math.PI);
			}
			g2.fill(this.topShape);
			g2.dispose();
		}
	}

	/**
	 * Adds a new MenuItem with an action
	 * 
	 * @param action
	 * @return
	 */
	public Component add(final Action action) {
		JMenuItem item = this.menu.add(action);
		this.items.add(item);
		this.controlsBuilder.applyComponentOrientation(this.menu);
		return item;
	}

	/**
	 * Removes the all items.
	 */
	public void removeAllItems() {
		this.menu.removeAll();
		this.items.clear();
	}

	/**
	 * Sets the button.
	 */
	private void setButton() {
		this.menu = new JPopupMenu();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (isEnabled()) {
					if (!PopUpButton.this.menuShown) {
						setMenuLocation(PopUpButton.this.location);
						PopUpButton.this.menu.show(PopUpButton.this,
								PopUpButton.this.xLocation,
								PopUpButton.this.yLocation);
					} else {
						PopUpButton.this.menu.setVisible(false);
					}
				}
			}
		});
		this.menu.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
				PopUpButton.this.menuShown = true;
			}

			@Override
			public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
				PopUpButton.this.menuShown = false;
			}

			@Override
			public void popupMenuCanceled(final PopupMenuEvent e) {
				PopUpButton.this.menuShown = false;
			}
		});
	}

	/**
	 * Adds a new popup menu listener
	 * 
	 * @param listener
	 */
	public void addPopupMenuListener(final PopupMenuListener listener) {
		this.menu.addPopupMenuListener(listener);
	}

	/**
	 * Sets the menu location.
	 * 
	 * @param location
	 *            the new menu location
	 */
	final void setMenuLocation(final int location) {
		if (!this.items.isEmpty()) {
			if (location == TOP_LEFT || location == TOP_RIGHT) {
				this.yLocation = -(int) this.items.get(0).getPreferredSize()
						.getHeight()
						* this.items.size() - 5;
			} else {
				this.yLocation = 21;
			}
			if (location == TOP_LEFT || location == BOTTOM_LEFT) {
				int maxWidth = 0;
				for (int i = 0; i < this.items.size(); i++) {
					if ((int) this.items.get(i).getPreferredSize().getWidth() > maxWidth) {
						maxWidth = (int) this.items.get(i).getPreferredSize()
								.getWidth();
					}
				}
				this.xLocation = -maxWidth
						+ (int) getPreferredSize().getWidth();
			} else {
				this.xLocation = 0;
			}
		}
	}

	/**
	 * Add a separator
	 */
	public void addSeparator() {
		this.menu.addSeparator();
	}

	/**
	 * In certain situations we need to hide menu programmatically without
	 * clicking button again
	 */
	public void hideMenu() {
		this.menu.setVisible(false);
	}

	@Override
	public Component getSwingComponent() {
		return this;
	}
}
