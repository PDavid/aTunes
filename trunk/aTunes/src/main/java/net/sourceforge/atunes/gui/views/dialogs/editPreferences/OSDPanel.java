/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.gui.views.dialogs.editPreferences;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.utils.I18nUtils;

public final class OSDPanel extends AbstractPreferencesPanel {

    private static final long serialVersionUID = 4489293347321979288L;

    private JComboBox osdDuration;
    private JComboBox osdWidth;
    private JComboBox osdHorizontalAlignment;
    private JComboBox osdVerticalAlignment;
    private JCheckBox useLibnotify;

    private static final String LEFT = I18nUtils.getString("LEFT");
    private static final String CENTER = I18nUtils.getString("CENTER");
    private static final String RIGHT = I18nUtils.getString("RIGHT");
    private static final String TOP = I18nUtils.getString("TOP");
    private static final String BOTTOM = I18nUtils.getString("BOTTOM");

    /**
     * Instantiates a new oSD panel.
     */
    public OSDPanel() {
        super(I18nUtils.getString("OSD"));
        JLabel label = new JLabel(I18nUtils.getString("OSD_DURATION"));
        osdDuration = new JComboBox(new Integer[] { 2, 4, 6 });
        JLabel label2 = new JLabel(I18nUtils.getString("OSD_WIDTH"));
        osdWidth = new JComboBox(new Integer[] { 400, 500, 600 });
        JLabel label3 = new JLabel(I18nUtils.getString("HORIZONTAL_ALIGNMENT"));
        osdHorizontalAlignment = new JComboBox(new String[] { LEFT, CENTER, RIGHT });
        JLabel label4 = new JLabel(I18nUtils.getString("VERTICAL_ALIGNMENT"));
        osdVerticalAlignment = new JComboBox(new String[] { TOP, CENTER, BOTTOM });
        JLabel label5 = new JLabel(I18nUtils.getString("USE_LIBNOTIFY"));
        useLibnotify = new JCheckBox();
        useLibnotify.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                osdDuration.setEnabled(!useLibnotify.isSelected());
                osdWidth.setEnabled(!useLibnotify.isSelected());
                osdHorizontalAlignment.setEnabled(!useLibnotify.isSelected());
                osdVerticalAlignment.setEnabled(!useLibnotify.isSelected());
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(10, 10, 0, 0);
        add(label, c);
        c.gridx = 1;
        c.weightx = 1;
        c.insets = new Insets(5, 10, 0, 0);
        add(osdDuration, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.insets = new Insets(10, 10, 0, 0);
        add(label2, c);
        c.gridx = 1;
        c.weightx = 1;
        c.insets = new Insets(5, 10, 0, 0);
        add(osdWidth, c);
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        c.insets = new Insets(10, 10, 0, 0);
        add(label3, c);
        c.gridx = 1;
        c.weightx = 1;
        c.insets = new Insets(5, 10, 0, 0);
        add(osdHorizontalAlignment, c);
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 0;
        c.insets = new Insets(10, 10, 0, 0);
        add(label4, c);
        c.gridx = 1;
        c.weightx = 1;
        c.insets = new Insets(5, 10, 0, 0);
        add(osdVerticalAlignment, c);
        c.gridx = 0;
        c.gridy = 4;
        c.weighty = 1;
        c.weightx = 0;
        c.insets = new Insets(20, 10, 0, 0);
        add(label5, c);
        c.gridx = 1;
        c.weightx = 1;
        c.insets = new Insets(15, 10, 0, 0);
        add(useLibnotify, c);
    }

    @Override
    public boolean applyPreferences(ApplicationState state) {
        state.setOsdDuration((Integer) osdDuration.getSelectedItem());
        state.setOsdWidth((Integer) osdWidth.getSelectedItem());
        state.setOsdHorizontalAlignment(getAlignment((String) osdHorizontalAlignment.getSelectedItem()));
        state.setOsdVerticalAlignment(getAlignment((String) osdVerticalAlignment.getSelectedItem()));
        state.setUseLibnotify(useLibnotify.isSelected());
        return false;
    }

    /**
     * Sets the oSD duration.
     * 
     * @param time
     *            the new oSD duration
     */
    private void setOSDDuration(int time) {
        osdDuration.setSelectedItem(time);
    }

    /**
     * Sets the OSD width
     * 
     * @param width
     */
    private void setOSDWidth(int width) {
        osdWidth.setSelectedItem(width);
    }

    /**
     * Sets the OSD horizontal alignment
     * 
     * @param alignment
     */
    private void setOSDHorizontalAlignment(int alignment) {
        if (alignment == SwingConstants.LEFT) {
            osdHorizontalAlignment.setSelectedItem(LEFT);
        } else if (alignment == SwingConstants.CENTER) {
            osdHorizontalAlignment.setSelectedItem(CENTER);
        } else if (alignment == SwingConstants.RIGHT) {
            osdHorizontalAlignment.setSelectedItem(RIGHT);
        }
    }

    /**
     * Returns alignment constant given a localized string
     * 
     * @param alignment
     * @return
     */
    private int getAlignment(String alignment) {
        if (LEFT.equals(alignment)) {
            return SwingConstants.LEFT;
        } else if (RIGHT.equals(alignment)) {
            return SwingConstants.RIGHT;
        } else if (TOP.equals(alignment)) {
            return SwingConstants.TOP;
        } else if (BOTTOM.equals(alignment)) {
            return SwingConstants.BOTTOM;
        } else if (CENTER.equals(alignment)) {
            return SwingConstants.CENTER;
        }
        return 0;
    }

    /**
     * Sets the OSD vertical alignment
     * 
     * @param alignment
     */
    private void setOSDVerticalAlignment(int alignment) {
        if (alignment == SwingConstants.TOP) {
            osdVerticalAlignment.setSelectedItem(TOP);
        } else if (alignment == SwingConstants.CENTER) {
            osdVerticalAlignment.setSelectedItem(CENTER);
        } else if (alignment == SwingConstants.BOTTOM) {
            osdVerticalAlignment.setSelectedItem(BOTTOM);
        }
    }

    public void setUseLibnotify(boolean b) {
        useLibnotify.setSelected(b);
    }

    @Override
    public void updatePanel(ApplicationState state) {
        setOSDDuration(state.getOsdDuration());
        setOSDWidth(state.getOsdWidth());
        setOSDHorizontalAlignment(state.getOsdHorizontalAlignment());
        setOSDVerticalAlignment(state.getOsdVerticalAlignment());
        setUseLibnotify(state.isUseLibnotify());
    }

    @Override
    public void resetImmediateChanges(ApplicationState state) {
        // Do nothing
    }

    @Override
    public boolean validatePanel() {
        return true;
    }

    @Override
    public void dialogVisibilityChanged(boolean visible) {
        // Do nothing
    }

    @Override
    public ImageIcon getIcon() {
        return Images.getImage(Images.OSD);
    }

}
