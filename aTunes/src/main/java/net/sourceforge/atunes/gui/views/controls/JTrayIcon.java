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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import net.sourceforge.atunes.gui.GuiUtils;

/*
 * This class is based on Java6Tray class from TVBrowser project
 * (http://sourceforge.net/projects/tvbrowser/)
 */
/**
 * Tray icon with a Swing popup menu.
 */
public final class JTrayIcon extends TrayIcon {

	private final class TrayIconMouseAdapter extends MouseAdapter {
		@Override
		public void mousePressed(final MouseEvent e) {
			if (e.isPopupTrigger()) {
				showPopup(e.getPoint());
				if (!JTrayIcon.this.isLinux) {
					JTrayIcon.this.trayParent.setVisible(false);
				}
			}
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			if (e.isPopupTrigger()) {
				if (JTrayIcon.this.trayParent.isVisible()) {
					JTrayIcon.this.trayParent.setVisible(false);
				} else {
					showPopup(e.getPoint());
				}

			}
		}
	}

	private JDialog trayParent;
	private JPopupMenu popupMenu;
	private MouseListener trayIconMouseListener;
	private PopupMenuListener popupMenuListener;
	private final boolean isLinux;

	private final Action action;

	/**
	 * Instantiates a new j tray icon.
	 * 
	 * @param image
	 * @param isLinux
	 * @param action
	 */
	public JTrayIcon(final Image image, final boolean isLinux,
			final Action action) {
		super(image);
		this.isLinux = isLinux;
		this.action = action;
		init();
	}

	/**
	 * Compute the proper position for a popup.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param dim
	 *            the dim
	 * 
	 * @return the point
	 */
	private Point computeDisplayPoint(final int x, final int y,
			final Dimension dim) {
		int computedX = x;
		int computedY = y;
		if (computedX - dim.width > 0) {
			computedX -= dim.width;
		}
		if (computedY - dim.height > 0) {
			computedY -= dim.height;
		}
		return new Point(computedX, computedY);
	}

	/**
	 * Inits the.
	 */
	private void init() {
		this.trayParent = new JDialog();
		this.trayParent.setSize(0, 0);
		this.trayParent.setUndecorated(true);
		this.trayParent.setAlwaysOnTop(true);
		this.trayParent.setVisible(false);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (GuiUtils.isPrimaryMouseButton(e)) {
					JTrayIcon.this.action.actionPerformed(null);
				}
			}
		});
	}

	@Override
	public synchronized void removeMouseListener(final MouseListener listener) {
		if (listener.equals(this.trayIconMouseListener)) {
			return;
		}
		super.removeMouseListener(listener);
	}

	/**
	 * Sets the j tray icon j popup menu.
	 * 
	 * @param popup
	 *            the new j tray icon j popup menu
	 */
	public void setJTrayIconJPopupMenu(final JPopupMenu popup) {
		this.popupMenu = popup;
		this.popupMenuListener = new PopupMenuListener() {
			@Override
			public void popupMenuCanceled(final PopupMenuEvent e) {
				// Nothing to do
			}

			@Override
			public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
				JTrayIcon.this.trayParent.setVisible(false);
			}

			@Override
			public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
				// Nothing to do
			}
		};
		this.popupMenu.addPopupMenuListener(this.popupMenuListener);
		setTrayIconMouseListener();
	}

	/**
	 * Returns popup menu
	 * 
	 * @return
	 */
	public JPopupMenu getJTrayIconPopup() {
		return this.popupMenu;
	}

	@Override
	public void setPopupMenu(final PopupMenu popup) {
		throw new UnsupportedOperationException(
				"use setJTrayIconPopupMenu(JTrayIconPopupMenu popup) instead");
	}

	/**
	 * Sets the tray icon mouse listener.
	 */
	private void setTrayIconMouseListener() {
		if (this.trayIconMouseListener == null) {
			this.trayIconMouseListener = new TrayIconMouseAdapter();
			addMouseListener(this.trayIconMouseListener);
		}
	}

	/**
	 * Show popup.
	 * 
	 * @param p
	 *            the p
	 */
	void showPopup(final Point p) {
		this.trayParent.setVisible(true);
		this.trayParent.toFront();
		Point p2 = computeDisplayPoint(p.x, p.y,
				this.popupMenu.getPreferredSize());
		this.popupMenu.show(this.trayParent,
				p2.x - this.trayParent.getLocation().x,
				p2.y - this.trayParent.getLocation().y);
	}

	/**
	 * @return the popupMenuListener
	 */
	protected PopupMenuListener getPopupMenuListener() {
		return this.popupMenuListener;
	}

}
