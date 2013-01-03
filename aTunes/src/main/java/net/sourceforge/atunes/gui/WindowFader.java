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

package net.sourceforge.atunes.gui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;

import javax.swing.Timer;

/**
 * Helper class for window fading
 */
public class WindowFader {

	private final WeakReference<Window> window;
	private Timer fadeInTimer;
	private Timer fadeOutTimer;
	private int currentOpacity;
	private final int duration;

	/**
	 * @param window
	 * @param duration
	 */
	public WindowFader(final Window window, final int duration) {
		this.window = new WeakReference<Window>(window);
		this.duration = duration;
	}

	/**
	 * Starts the fade-in of the window
	 */
	public void fadeIn() {
		stop();
		Window w = this.window.get();
		if (w != null) {
			GuiUtils.setWindowOpacity(w, this.currentOpacity / 100.0f);
			w.setVisible(true);
		}
		this.fadeInTimer = new Timer(this.duration, new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				WindowFader.this.currentOpacity += 20;
				if (WindowFader.this.currentOpacity <= 100) {
					Window w = WindowFader.this.window.get();
					if (w != null) {
						GuiUtils.setWindowOpacity(w,
								WindowFader.this.currentOpacity / 100.0f);
						w.repaint();
					}
				} else {
					fadeInFinished();
					WindowFader.this.currentOpacity = 100;
					WindowFader.this.fadeInTimer.stop();
				}
			}

		});
		this.fadeInTimer.setRepeats(true);
		this.fadeInTimer.start();
	}

	/**
	 * Starts the fade-out of the window
	 */
	public void fadeOut() {
		stop();
		this.fadeOutTimer = new Timer(this.duration, new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				WindowFader.this.currentOpacity -= 10;
				if (WindowFader.this.currentOpacity >= 0) {
					Window w = WindowFader.this.window.get();
					if (w != null) {
						GuiUtils.setWindowOpacity(w,
								WindowFader.this.currentOpacity / 100.0f);
						w.repaint();
					}
				} else {
					fadeOutFinished();
					WindowFader.this.fadeOutTimer.stop();
					WindowFader.this.currentOpacity = 0;
				}
			}

		});
		this.fadeOutTimer.setRepeats(true);
		this.fadeOutTimer.start();
	}

	protected void fadeOutFinished() {
		Window w = this.window.get();
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
		if (this.fadeInTimer != null) {
			this.fadeInTimer.stop();
			this.fadeInTimer = null;
		}
		if (this.fadeOutTimer != null) {
			this.fadeOutTimer.stop();
			this.fadeOutTimer = null;
		}
	}

	/**
	 * Stops all fading effects and disposes window
	 */
	public void clear() {
		stop();
		this.currentOpacity = 0;
		Window w = this.window.get();
		if (w != null) {
			GuiUtils.setWindowOpacity(w, 1);
			w.dispose();
		}

	}

}
