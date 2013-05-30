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

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.Logger;

/**
 * GUI related utility methods.
 */

public final class GuiUtils {

	private static final String OPAQUE_WINDOWS_NOT_SUPPORTED = "opaque windows not supported: ";

	private static final String SHAPED_WINDOWS_NOT_SUPPORTED = "shaped windows not supported: ";

	private static final String ESCAPE2 = "ESCAPE";

	/** The border color. */
	private static Color borderColor = Color.BLACK;

	/** The set window shape method. */
	private static Method setWindowShapeMethod;

	/** The set window opacity method. */
	private static Method setWindowOpacityMethod;

	/** The set window opaque method. */
	private static Method setWindowOpaqueMethod;

	static {
		try {
			Class<?> awtUtilities = Class.forName("com.sun.awt.AWTUtilities");
			setWindowShapeMethod = awtUtilities.getDeclaredMethod(
					"setWindowShape", Window.class, Shape.class);
			setWindowOpacityMethod = awtUtilities.getDeclaredMethod(
					"setWindowOpacity", Window.class, float.class);
			setWindowOpaqueMethod = awtUtilities.getDeclaredMethod(
					"setWindowOpaque", Window.class, boolean.class);
		} catch (ClassNotFoundException e) {
			Logger.info("class com.sun.awt.AWTUtilities not found");
		} catch (SecurityException e) {
			Logger.error(e);
		} catch (NoSuchMethodException e) {
			Logger.info("method in class com.sun.awt.AWTUtilities not found");
		}
	}

	private GuiUtils() {
	}

	/**
	 * Adds the close action with escape key.
	 * 
	 * @param window
	 *            the window
	 * @param rootPane
	 *            the root pane
	 * 
	 */
	public static void addCloseActionWithEscapeKey(final Window window,
			final JRootPane rootPane) {
		// Handle escape key to close the window

		KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction() {
			private static final long serialVersionUID = 0L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				window.setVisible(false);
			}
		};
		rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape,
				ESCAPE2);
		rootPane.getActionMap().put(ESCAPE2, escapeAction);
	}

	/**
	 * Adds the dispose action with escape key.
	 * 
	 * @param window
	 *            the window
	 * @param rootPane
	 *            the root pane
	 */
	public static void addDisposeActionWithEscapeKey(final Window window,
			final JRootPane rootPane) {
		// Handle escape key to close the window

		KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action disposeAction = new AbstractAction() {
			private static final long serialVersionUID = 0L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				window.dispose();
			}
		};
		rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape,
				ESCAPE2);
		rootPane.getActionMap().put(ESCAPE2, disposeAction);
	}

	/**
	 * Adds icons to a window
	 * 
	 * @param w
	 */
	public static void addAppIcons(final Window w) {
		w.setIconImages(Arrays.asList(Images.getImage(Images.APP_LOGO_16)
				.getImage(), Images.getImage(Images.APP_LOGO_32).getImage(),
				Images.getImage(Images.APP_LOGO_90).getImage()));
	}

	/**
	 * Collapses all nodes in a tree.
	 * 
	 * @param tree
	 *            the tree
	 */
	public static void collapseTree(final JTree tree) {
		for (int i = tree.getRowCount() - 1; i > 0; i--) {
			tree.collapseRow(i);
		}
		tree.setSelectionRow(0);
	}

	/**
	 * Expands all nodes in a tree.
	 * 
	 * @param tree
	 *            A tree
	 */
	public static void expandTree(final JTree tree) {
		for (int i = 1; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
		tree.setSelectionRow(0);
	}

	/**
	 * Returns background color for panels, as set by Look And Feel.
	 * 
	 * @return the background color
	 */
	public static Color getBackgroundColor() {
		return (Color) UIManager.get("Panel.background");
	}

	/**
	 * Returns foreground color for labels, as set by Look And Feel
	 * 
	 * @return the forefround color
	 */
	public static Color getForegroundColor() {
		return (Color) UIManager.get("Label.foreground");
	}

	/**
	 * Returns border color for panels, based on background color.
	 * 
	 * @return the border color
	 */
	public static Color getBorderColor() {
		return borderColor;
	}

	/**
	 * Sets the border color.
	 * 
	 * @param borderColor
	 *            the borderColor to set
	 */
	public static void setBorderColor(final Color borderColor) {
		GuiUtils.borderColor = borderColor;
	}

	/**
	 * Sets the window shape if possible.
	 * 
	 * @param window
	 *            A mindow
	 * @param mask
	 *            A mask
	 */
	public static void setWindowShape(final Window window, final Shape mask) {
		if (setWindowShapeMethod != null) {
			try {
				setWindowShapeMethod.invoke(null, window, mask);
				// any exception will disable call to method
			} catch (SecurityException e) {
				Logger.info(SHAPED_WINDOWS_NOT_SUPPORTED, e.getMessage());
				setWindowShapeMethod = null;
			} catch (IllegalArgumentException e) {
				Logger.info(SHAPED_WINDOWS_NOT_SUPPORTED, e.getMessage());
				setWindowShapeMethod = null;
			} catch (IllegalAccessException e) {
				Logger.info(SHAPED_WINDOWS_NOT_SUPPORTED, e.getMessage());
				setWindowShapeMethod = null;
			} catch (InvocationTargetException e) {
				Logger.info(SHAPED_WINDOWS_NOT_SUPPORTED, e.getMessage());
				setWindowShapeMethod = null;
			} catch (UnsupportedOperationException e) {
				Logger.info(SHAPED_WINDOWS_NOT_SUPPORTED, e.getMessage());
				setWindowShapeMethod = null;
			}
		}
	}

	/**
	 * Sets the window opacity if possible.
	 * 
	 * @param window
	 *            A window
	 * @param opacity
	 *            Opacity from 0 to 1
	 */

	public static void setWindowOpacity(final Window window, final float opacity) {
		if (setWindowOpacityMethod != null) {
			try {
				setWindowOpacityMethod.invoke(null, window, opacity);
				// any exception will disable call to method
			} catch (SecurityException e) {
				Logger.info(OPAQUE_WINDOWS_NOT_SUPPORTED, e.getMessage());
				setWindowOpacityMethod = null;
			} catch (IllegalArgumentException e) {
				Logger.info(OPAQUE_WINDOWS_NOT_SUPPORTED, e.getMessage());
				setWindowOpacityMethod = null;
			} catch (IllegalAccessException e) {
				Logger.info(OPAQUE_WINDOWS_NOT_SUPPORTED, e.getMessage());
				setWindowOpacityMethod = null;
			} catch (InvocationTargetException e) {
				Logger.info(OPAQUE_WINDOWS_NOT_SUPPORTED, e.getMessage());
				setWindowOpacityMethod = null;
			} catch (UnsupportedOperationException e) {
				Logger.info(OPAQUE_WINDOWS_NOT_SUPPORTED, e.getMessage());
				setWindowOpacityMethod = null;
			}
		}
	}

	/**
	 * Sets the window opaque if possible.
	 * 
	 * @param window
	 *            A window
	 * @param opaque
	 *            If the window should be opaque
	 */
	public static void setWindowOpaque(final Window window, final boolean opaque) {
		if (setWindowOpaqueMethod != null) {
			try {
				setWindowOpaqueMethod.invoke(null, window, opaque);
			} catch (SecurityException e) {
				Logger.error(e);
			} catch (IllegalArgumentException e) {
				Logger.info("opaque windows not supported");
			} catch (IllegalAccessException e) {
				Logger.error(e);
			} catch (InvocationTargetException e) {
				Logger.info("opaque windows not supported");
				// In some systems where window opacity is not supported
				// This method launches InvocationTargetException continuosly
				// So the first time exception is thrown, we disable
				// call to setWindowOpaqueMethod
				setWindowOpaqueMethod = null;
			} catch (UnsupportedOperationException e) {
				Logger.error(e);
			}
		}
	}

	/**
	 * Returns number of screens in current machine
	 * 
	 * @return
	 */
	public static int getNumberOfScreenDevices() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getScreenDevices().length;
	}

	/**
	 * Returns the screen where given point is or the default screen if it
	 * doesn't fit in any screen
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static GraphicsDevice getGraphicsDeviceForLocation(final int x,
			final int y) {
		GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		for (int i = 0; i < getNumberOfScreenDevices(); i++) {
			GraphicsDevice graphicsDevice = localGraphicsEnvironment
					.getScreenDevices()[i];
			if (graphicsDevice.getDefaultConfiguration().getBounds()
					.contains(x, y)) {
				return graphicsDevice;
			}
		}
		return localGraphicsEnvironment.getDefaultScreenDevice();
	}

	/**
	 * Convenience method to getScreenDeviceForLocation(int x, int y)
	 * 
	 * @param p
	 * @return
	 */
	public static GraphicsDevice getGraphicsDeviceForLocation(final Point p) {
		return getGraphicsDeviceForLocation(p.x, p.y);
	}

	/**
	 * Returns true if mouse event is from primary mouse button (left-click or
	 * not Ctrl-click in Mac)
	 * 
	 * @param e
	 * @return
	 */
	public static boolean isPrimaryMouseButton(final MouseEvent e) {
		return SwingUtilities.isLeftMouseButton(e) && !e.isControlDown();
	}

	/**
	 * Returns true if mouse event is from secondary mouse button (right-click
	 * or Ctrl-click in Mac)
	 * 
	 * @param osManager
	 * @param e
	 * @return
	 */
	public static boolean isSecondaryMouseButton(final IOSManager osManager,
			final MouseEvent e) {
		if (!osManager.isMacOsX()) {
			return SwingUtilities.isRightMouseButton(e);
		} else {
			// When Cmd key is pressed, left and right buttons seems pressed
			// In this case return false
			if (e.isMetaDown() && SwingUtilities.isRightMouseButton(e)
					&& SwingUtilities.isLeftMouseButton(e)) {
				return false;
			}
			return SwingUtilities.isRightMouseButton(e)
					|| SwingUtilities.isLeftMouseButton(e) && e.isControlDown();
		}
	}

	/**
	 * @param osManager
	 * @return mask to use Ctrl or Command keys, given the current operating
	 *         system
	 */
	public static int getCtrlOrMetaActionEventMask(final IOSManager osManager) {
		if (!osManager.isMacOsX()) {
			return InputEvent.CTRL_MASK;
		} else {
			return InputEvent.META_MASK;
		}
	}

	/**
	 * Executes a code in Event Dispatch Thread
	 * 
	 * @param runnable
	 */
	public static void callInEventDispatchThread(final Runnable runnable) {
		if (!EventQueue.isDispatchThread()) {
			SwingUtilities.invokeLater(runnable);
		} else {
			runnable.run();
		}
	}

	/**
	 * Executes a code in Event Dispatch Thread after all pending events
	 * 
	 * @param runnable
	 */
	public static void callInEventDispatchThreadLater(final Runnable runnable) {
		SwingUtilities.invokeLater(runnable);
	}

	/**
	 * Executes a code in Event Dispatch Thread, waiting for finalization
	 * 
	 * @param runnable
	 */
	public static void callInEventDispatchThreadAndWait(final Runnable runnable) {
		if (!EventQueue.isDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(runnable);
			} catch (InterruptedException e) {
				Logger.error(e);
			} catch (InvocationTargetException e) {
				Logger.error(e);
			}
		} else {
			runnable.run();
		}
	}
}
