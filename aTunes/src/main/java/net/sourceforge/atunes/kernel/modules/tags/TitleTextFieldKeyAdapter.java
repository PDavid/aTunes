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

package net.sourceforge.atunes.kernel.modules.tags;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

final class TitleTextFieldKeyAdapter extends KeyAdapter {
    private final JTextField textField;
    private final String fileName;
    private int lenght = 0;

    TitleTextFieldKeyAdapter(JTextField textField, String fileName) {
        this.textField = textField;
        this.fileName = fileName;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String text = textField.getText();

                // User added a char
                if (text.length() > lenght && text.length() >= 3) {
                    int index = fileName.indexOf(text);
                    if (index != -1) {
                        textField.setText(fileName.substring(index));
                        textField.setSelectionStart(text.length());
                        textField.setSelectionEnd(textField.getText().length());
                    }
                }
                lenght = text.length();
            }
        });
    }
}