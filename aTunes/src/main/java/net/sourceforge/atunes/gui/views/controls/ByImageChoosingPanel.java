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

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public final class ByImageChoosingPanel<T> extends ScrollableFlowPanel {

    public static class ImageEntry<U> {

        private U object;
        private ImageIcon image;

        public ImageEntry(U object, ImageIcon image) {
            super();
            this.object = object;
            this.image = image;
        }

        public U getObject() {
            return object;
        }

        public ImageIcon getImage() {
            return image;
        }

    }

    private class CustomJRadioButton extends JRadioButton {

        private static final long serialVersionUID = -6933585654529381134L;

        private T object;

        public CustomJRadioButton(T object) {
            this.object = object;
        }

        public T getObject() {
            return object;
        }
    }

    private static final long serialVersionUID = -3541046889511348904L;

    private Set<CustomJRadioButton> buttons = new HashSet<CustomJRadioButton>();
    private ButtonGroup buttonGroup = new ButtonGroup();

    public ByImageChoosingPanel(List<? extends ImageEntry<T>> entries) {
        setLayout(new FlowLayout(FlowLayout.LEADING, 20, 20));
        init(entries);
        setOpaque(false);
    }

    private void init(List<? extends ImageEntry<T>> entries) {
        for (ImageEntry<T> entry : entries) {
            JLabel imageLabel = new JLabel();
            final CustomJRadioButton button = new CustomJRadioButton(entry.getObject());
            button.setFocusPainted(false);
            imageLabel.setIcon(entry.getImage());
            buttons.add(button);
            buttonGroup.add(button);

            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    button.setSelected(!button.isSelected());
                }
            });

            JPanel panel = createPanel(imageLabel, button);
            add(panel);
        }
    }

    private JPanel createPanel(JLabel imageLabel, CustomJRadioButton button) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        p.add(imageLabel, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        p.add(button, c);
        return p;
    }

    public void setSelectedItem(T object) {
        for (CustomJRadioButton button : buttons) {
            if (button.getObject().equals(object)) {
                button.setSelected(true);
            }
        }
    }

    public T getSelectedItem() {
        for (CustomJRadioButton button : buttons) {
            if (button.isSelected()) {
                return button.getObject();
            }
        }
        return null;
    }

}
