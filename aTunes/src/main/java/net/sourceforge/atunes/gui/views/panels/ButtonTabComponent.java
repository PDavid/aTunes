/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * Component to be used as as tabComponent; Contains a JLabel to show the text
 * and a JButton to close the tab it belongs to
 * 
 * http://weblogs.java.net/blog/alexfromsun/archive/2005/11/tabcomponents_i_1.
 * html
 */
public class ButtonTabComponent extends JPanel {

    /**
     * The Class TabButton.
     */
    private class TabButton extends JButton implements ActionListener {

        private static final long serialVersionUID = -2196619151804754334L;

        /**
         * Instantiates a new tab button.
         */
        public TabButton() {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText(LanguageTool.getString("CLOSE"));
            // Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            // Make it transparent
            setContentAreaFilled(false);
            // No need to be focusable
            setFocusable(false);
            setBorderPainted(false);
            setRolloverEnabled(true);
            // Close the proper tab by clicking the button
            addActionListener(this);
            //setIcon(ImageLoader.CLOSE_TAB);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
         * )
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            int i = pane.indexOfTabComponent(ButtonTabComponent.this);
            if (i != -1) {
                PlayListHandler.getInstance().removePlayList(i);
            }
        }

        // paint the cross
        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            // shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.drawImage(ImageLoader.CLOSE_TAB.getImage(), 0, 0, ImageLoader.CLOSE_TAB.getIconWidth(), ImageLoader.CLOSE_TAB.getIconHeight(), null);
        }

        // we don't want to update UI for this button
        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.JButton#updateUI()
         */
        @Override
        public void updateUI() {
            // Nothing to do
        }
    }

    private static final long serialVersionUID = 3945896434769821096L;

    /** The pane. */
    final JTabbedPane pane;

    /** The label. */
    private final JLabel label;

    /** The button. */
    private final JButton button = new TabButton();

    /**
     * Instantiates a new button tab component.
     * 
     * @param title
     *            the title
     * @param pane
     *            the pane
     */
    public ButtonTabComponent(String title, JTabbedPane pane) {
        // unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (pane == null) {
            throw new IllegalArgumentException("TabbedPane is null");
        }
        this.pane = pane;
        setOpaque(false);
        label = new JLabel(title);

        add(label);
        // add more space between the label and the button
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        add(button);
        // add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        GuiUtils.applyComponentOrientation(this);
    }

    /**
     * Gets the label.
     * 
     * @return the label
     */
    public JLabel getLabel() {
        return label;
    }
}
