/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.gui.views.controls;

import java.awt.FlowLayout;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JRadioButton;

public final class ByImageChoosingPanel<T> extends ScrollableFlowPanel {

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

    public ByImageChoosingPanel(Map<? extends T, ? extends ImageIcon> data) {
        setLayout(new FlowLayout(FlowLayout.LEADING, 20, 20));
        init(data);
    }

    private void init(Map<? extends T, ? extends ImageIcon> data) {
        for (Entry<? extends T, ? extends ImageIcon> entry : data.entrySet()) {
            CustomJRadioButton button = new CustomJRadioButton(entry.getKey());
            button.setIcon(entry.getValue());
            buttons.add(button);
            add(button);
            buttonGroup.add(button);
        }
    }

    public void setSelectedItem(T object) {
        for (CustomJRadioButton button : buttons) {
            if (button.getObject().equals(object)) {
                //buttonGroup.setSelected(button.getModel(), true);
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
