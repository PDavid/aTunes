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
package net.sourceforge.atunes.gui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;

import javax.swing.Timer;

import net.sourceforge.atunes.utils.GuiUtils;

/**
 * Helper class for window fading
 */
public class WindowFader {

    WeakReference<Window> window;
    Timer fadeInTimer;
    Timer fadeOutTimer;
    int currentOpacity;
    private int duration;

    public WindowFader(Window window, int duration) {
        this.window = new WeakReference<Window>(window);
        this.duration = duration;
    }

    /**
     * Starts the fade-in of the window
     */
    public void fadeIn() {
        stop();
        Window w = window.get();
        if (w != null) {
            GuiUtils.setWindowOpacity(w, currentOpacity / 100.0f);
            w.setVisible(true);
        }
        fadeInTimer = new Timer(duration, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                currentOpacity += 20;
                if (currentOpacity <= 100) {
                    Window w = window.get();
                    if (w != null) {
                        GuiUtils.setWindowOpacity(w, currentOpacity / 100.0f);
                        w.repaint();
                    }
                } else {
                    fadeInFinished();
                    currentOpacity = 100;
                    fadeInTimer.stop();
                }
            }

        });
        fadeInTimer.setRepeats(true);
        fadeInTimer.start();
    }

    /**
     * Starts the fade-out of the window
     */
    public void fadeOut() {
        stop();
        fadeOutTimer = new Timer(duration, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                currentOpacity -= 10;
                if (currentOpacity >= 0) {
                    Window w = window.get();
                    if (w != null) {
                        GuiUtils.setWindowOpacity(w, currentOpacity / 100.0f);
                        w.repaint();
                    }
                } else {
                    fadeOutFinished();
                    fadeOutTimer.stop();
                    currentOpacity = 0;
                }
            }

        });
        fadeOutTimer.setRepeats(true);
        fadeOutTimer.start();
    }

    protected void fadeOutFinished() {
        Window w = window.get();
        if (w != null) {
            w.dispose();
        }
    }

    protected void fadeInFinished() {
    }

    /**
     * Stops all fading effects
     */
    private void stop() {
        if (fadeInTimer != null) {
            fadeInTimer.stop();
            fadeInTimer = null;
        }
        if (fadeOutTimer != null) {
            fadeOutTimer.stop();
            fadeOutTimer = null;
        }
    }

    /**
     * Stops all fading effects and disposes window
     */
    public void clear() {
        stop();
        currentOpacity = 0;
        Window w = window.get();
        if (w != null) {
            GuiUtils.setWindowOpacity(w, 1);
            w.dispose();
        }

    }

}
