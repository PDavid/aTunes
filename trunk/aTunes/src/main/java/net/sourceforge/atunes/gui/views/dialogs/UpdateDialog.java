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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.CustomFrame;
import net.sourceforge.atunes.gui.views.controls.CustomTextArea;
import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.kernel.modules.updates.ApplicationVersion;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

public final class UpdateDialog extends CustomFrame {

    private static final long serialVersionUID = -778226654176158965L;

    public UpdateDialog(ApplicationVersion version, Component owner) {
        super(I18nUtils.getString("NEW_VERSION_AVAILABLE"), 400, 150, owner);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        String text = I18nUtils.getString("NEW_VERSION_AVAILABLE_TEXT");
        text = text.replace("(%VERSION%)", Constants.VERSION.toShortString());
        text = text.replace("(%NEW_VERSION%)", version.toShortString());

        JLabel img = new JLabel(Images.getImage(Images.APP_LOGO_90));

        JTextArea text1 = new CustomTextArea(text);
        text1.setBorder(BorderFactory.createEmptyBorder());
        text1.setEditable(false);
        text1.setOpaque(false);
        text1.setLineWrap(true);
        text1.setWrapStyleWord(true);

        UrlLabel url = new UrlLabel(I18nUtils.getString("GO_TO_DOWNLOAD_PAGE"), version.getDownloadURL());
        url.setHorizontalAlignment(SwingConstants.CENTER);

        JButton ok = new JButton(I18nUtils.getString("OK"));
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 3;
        c.insets = new Insets(10, 10, 10, 10);
        panel.add(img, c);
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(text1, c);
        c.gridy = 1;
        c.insets = new Insets(0, 0, 0, 0);
        c.fill = GridBagConstraints.BOTH;
        panel.add(url, c);
        c.gridy = 2;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(15, 0, 10, 0);
        panel.add(ok, c);

        add(panel);

        enableDisposeActionWithEscapeKey();
        GuiUtils.applyComponentOrientation(this);
    }

}
