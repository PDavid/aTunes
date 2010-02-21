/*
 * aTunes 2.0.0-SNAPSHOT
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

/*
 * This class is based on Java6Tray class from TVBrowser project
 * (http://sourceforge.net/projects/tvbrowser/)
 */
/**
 * Tray icon with a Swing popup menu.
 */
public final class JTrayIcon extends TrayIcon {

    /**
     * This special JPopupMenu prevents the user from removing the popup menu
     * listener.
     */
    public class JTrayIconPopupMenu extends JPopupMenu {

        private static final long serialVersionUID = -220434547680783992L;

        @Override
        public void removePopupMenuListener(PopupMenuListener l) {
            if (l == popupMenuListener) {
                return;
            }
            super.removePopupMenuListener(l);
        }
    }

    private JDialog trayParent;
    private JPopupMenu popupMenu;
    private MouseListener trayIconMouseListener;
    private PopupMenuListener popupMenuListener;
    private boolean isLinux;
    
    private Action action;

    /**
     * Instantiates a new j tray icon.
     * 
     * @param image
     *            the image
     */
    public JTrayIcon(Image image, boolean isLinux, Action action) {
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
    private Point computeDisplayPoint(int x, int y, Dimension dim) {
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
        trayParent = new JDialog();
        trayParent.setSize(0, 0);
        trayParent.setUndecorated(true);
        trayParent.setAlwaysOnTop(true);
        trayParent.setVisible(false);
        
        addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		if (e.getButton() == MouseEvent.BUTTON1) {
        			action.actionPerformed(null);
        		}
        	}
		});
    }

    @Override
    public synchronized void removeMouseListener(MouseListener listener) {
        if (listener == trayIconMouseListener) {
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
    public void setJTrayIconJPopupMenu(JTrayIconPopupMenu popup) {
        popupMenu = popup;
        popupMenuListener = new PopupMenuListener() {
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                // Nothing to do
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                trayParent.setVisible(false);
            }

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                // Nothing to do
            }
        };
        popupMenu.addPopupMenuListener(popupMenuListener);
        setTrayIconMouseListener();
    }

    @Override
    public void setPopupMenu(PopupMenu popup) {
        throw new UnsupportedOperationException("use setJTrayIconPopupMenu(JTrayIconPopupMenu popup) instead");
    }

    /**
     * Sets the tray icon mouse listener.
     */
    private void setTrayIconMouseListener() {
        if (trayIconMouseListener == null) {
            trayIconMouseListener = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        showPopup(e.getPoint());
                        if (!isLinux) {
                            trayParent.setVisible(false);
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        if (trayParent.isVisible()) {
                            trayParent.setVisible(false);
                        } else {
                            showPopup(e.getPoint());
                        }

                    }
                }
            };
            addMouseListener(trayIconMouseListener);
        }
    }

    /**
     * Show popup.
     * 
     * @param p
     *            the p
     */
    void showPopup(final Point p) {
        trayParent.setVisible(true);
        trayParent.toFront();
        Point p2 = computeDisplayPoint(p.x, p.y, popupMenu.getPreferredSize());
        popupMenu.show(trayParent, p2.x - trayParent.getLocation().x, p2.y - trayParent.getLocation().y);
    }

}
