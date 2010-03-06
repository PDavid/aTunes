package net.sourceforge.atunes.gui.views.controls;

import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;

/**
 * Tray Icon that fires actions when left mouse button clicked
 * 
 * @author fleax
 * 
 */
public class ActionTrayIcon extends TrayIcon {

    /**
     * @param image
     * @param tooltip
     * @param popup
     */
    public ActionTrayIcon(Image image, String tooltip, PopupMenu popup, Action action) {
        super(image, tooltip, popup);
        addListener(action);
    }

    /**
     * @param image
     * @param tooltip
     */
    public ActionTrayIcon(Image image, String tooltip, Action action) {
        super(image, tooltip);
        addListener(action);
    }

    /**
     * @param image
     */
    public ActionTrayIcon(Image image, Action action) {
        super(image);
        addListener(action);
    }

    /**
     * Binds mouse listener to action
     * 
     * @param action
     */
    private void addListener(Action action) {
        addMouseListener(new ActionMouseListener(action));
    }

    /**
     * Mouse adapter to fire actions
     * 
     * @author fleax
     * 
     */
    private static class ActionMouseListener extends MouseAdapter {

        /**
         * Action to perform
         */
        private Action action;

        /**
         * Creates a new instance
         * 
         * @param action
         */
        public ActionMouseListener(Action action) {
            this.action = action;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                action.actionPerformed(null);
            }
        }
    }
}
