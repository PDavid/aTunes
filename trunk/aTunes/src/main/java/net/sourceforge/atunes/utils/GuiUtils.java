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
package net.sourceforge.atunes.utils;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.sourceforge.atunes.gui.LookAndFeelSelector;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

import org.commonjukebox.plugins.PluginApi;

/**
 * GUI related utility methods.
 */
@PluginApi
public final class GuiUtils {

    private static Logger logger = new Logger();

    /** The border color. */
    private static Color borderColor = Color.BLACK;

    /** The component orientation. */
    private static ComponentOrientation componentOrientation;

    /** The set window shape method. */
    private static Method setWindowShapeMethod;

    /** The set window opacity method. */
    private static Method setWindowOpacityMethod;

    /** The set window opaque method. */
    private static Method setWindowOpaqueMethod;

    /**
     * Bounds of the main screen device, used to calculate sizes
     */
    private static Rectangle mainDeviceBounds;

    public static final int MAX_COMPONENTS_WIDTH = 1280;

    static {
        try {
            Class<?> awtUtilities = Class.forName("com.sun.awt.AWTUtilities");
            setWindowShapeMethod = awtUtilities.getDeclaredMethod("setWindowShape", Window.class, Shape.class);
            setWindowOpacityMethod = awtUtilities.getDeclaredMethod("setWindowOpacity", Window.class, float.class);
            setWindowOpaqueMethod = awtUtilities.getDeclaredMethod("setWindowOpaque", Window.class, boolean.class);
        } catch (ClassNotFoundException e) {
            logger.info(LogCategories.DESKTOP, "class com.sun.awt.AWTUtilities not found");
        } catch (SecurityException e) {
            logger.error(LogCategories.DESKTOP, e);
        } catch (NoSuchMethodException e) {
            logger.info(LogCategories.DESKTOP, "method in class com.sun.awt.AWTUtilities not found");
        }

        mainDeviceBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds();
    }

    private GuiUtils() {
    }

    /**
     * Returns the width in pixels of the main device
     * 
     * @return
     */
    public static int getDeviceWidth() {
        return mainDeviceBounds.width;
    }

    /**
     * Returns the height in pixels of the main device
     * 
     * @return
     */
    public static int getDeviceHeight() {
        return mainDeviceBounds.height;
    }

    /**
     * Sets location of a window centered in given screen device. Window must
     * have size different of (0,0)
     * 
     * @param window
     */
    public static void setLocationInScreen(Window window, GraphicsDevice screen) {
        Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
        window.setLocation(screenBounds.width / 2 - window.getWidth() / 2 + screenBounds.x, screenBounds.height / 2 - window.getHeight() / 2 + screenBounds.y);
    }

    /**
     * Updates the user interface to use a new theme
     * 
     * @param selectedTheme
     *            The new theme
     */
    public static void applyTheme(String selectedTheme) {
        LookAndFeelSelector.setLookAndFeel(selectedTheme);
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
        }
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
    public static void addCloseActionWithEscapeKey(final Window window, JRootPane rootPane) {
        // Handle escape key to close the window

        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 0L;

            @Override
            public void actionPerformed(ActionEvent e) {
                window.setVisible(false);
            }
        };
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", escapeAction);
    }

    /**
     * Adds the dispose action with escape key.
     * 
     * @param window
     *            the window
     * @param rootPane
     *            the root pane
     */
    public static void addDisposeActionWithEscapeKey(final Window window, JRootPane rootPane) {
        // Handle escape key to close the window

        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action disposeAction = new AbstractAction() {
            private static final long serialVersionUID = 0L;

            @Override
            public void actionPerformed(ActionEvent e) {
                window.dispose();
            }
        };
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", disposeAction);
    }

    public static void addAppIcons(Window w) {
        w.setIconImages(Arrays.asList(ImageLoader.getImage(ImageLoader.APP_ICON).getImage(), ImageLoader.getImage(ImageLoader.APP_ICON_MEDIUM).getImage(), ImageLoader.getImage(
                ImageLoader.APP_ICON_BIG).getImage()));
    }

    /**
     * Applies Locale specific component orientation to containers.
     * 
     * @param containers
     *            One or more containers
     */
    public static void applyComponentOrientation(Container... containers) {
        if (componentOrientation == null) {
            setComponentOrientation();
        }
        for (Container container : containers) {
            container.applyComponentOrientation(componentOrientation);
        }
    }

    /**
     * Collapses all nodes in a tree.
     * 
     * @param tree
     *            the tree
     */
    public static void collapseTree(JTree tree) {
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
    public static void expandTree(JTree tree) {
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
     * Returns the component orientation.
     * 
     * @return The component orientation
     */
    public static ComponentOrientation getComponentOrientation() {
        if (componentOrientation == null) {
            setComponentOrientation();
        }
        return componentOrientation;
    }

    /**
     * Returns the component orientation as a SwingConstant.
     * 
     * @return The component orientation as a SwingConstant
     */
    public static int getComponentOrientationAsSwingConstant() {
        if (componentOrientation == null) {
            setComponentOrientation();
        }
        return componentOrientation.isLeftToRight() ? SwingConstants.LEFT : SwingConstants.RIGHT;
    }

    /**
     * Returns a proportional width according to screenWidth and desiredSize for
     * the current screen resolution.
     * 
     * @param screenWidth
     *            the screen width
     * @param desiredWidth
     *            the desired width
     * 
     * @return the component width for resolution
     */
    public static int getComponentWidthForResolution(int screenWidth, int desiredWidth) {
        int currentScreenWidth = mainDeviceBounds.width > MAX_COMPONENTS_WIDTH ? MAX_COMPONENTS_WIDTH : mainDeviceBounds.width;
        int result = desiredWidth * currentScreenWidth / screenWidth;
        return result;
    }

    /**
     * Returns a proportional height according to screenHeight and desiredHeight
     * for the current screen resolution.
     * 
     * @param screenHeight
     *            the screen height
     * @param desiredHeight
     *            the desired height
     * 
     * @return the component height for resolution
     */
    public static int getComponentHeightForResolution(int screenHeight, int desiredHeight) {
        int currentScreenHeight = mainDeviceBounds.height;
        int result = desiredHeight * currentScreenHeight / screenHeight;
        return result;
    }

    /**
     * Sets the border color.
     * 
     * @param borderColor
     *            the borderColor to set
     */
    public static void setBorderColor(Color borderColor) {
        GuiUtils.borderColor = borderColor;
    }

    /**
     * Sets the component orientation.
     */
    private static void setComponentOrientation() {
        if (Kernel.getInstance() == null) {
            componentOrientation = ComponentOrientation.getOrientation(Locale.getDefault());
        } else {
            if ("ug".equalsIgnoreCase(ApplicationState.getInstance().getLocale().getLocale().getLanguage())) {
                componentOrientation = ComponentOrientation.RIGHT_TO_LEFT;
            } else {
                componentOrientation = ComponentOrientation.getOrientation(ApplicationState.getInstance().getLocale().getLocale());
            }

        }
    }

    /**
     * Sets the window shape if possible.
     * 
     * @param window
     *            A mindow
     * @param mask
     *            A mask
     */
    public static void setWindowShape(Window window, Shape mask) {
        if (setWindowShapeMethod != null) {
            try {
                setWindowShapeMethod.invoke(null, window, mask);
            } catch (SecurityException e) {
                logger.error(LogCategories.DESKTOP, e);
            } catch (IllegalArgumentException e) {
                logger.info(LogCategories.DESKTOP, "shaped windows not supported");
            } catch (IllegalAccessException e) {
                logger.error(LogCategories.DESKTOP, e);
            } catch (InvocationTargetException e) {
                logger.error(LogCategories.DESKTOP, e);
            } catch (UnsupportedOperationException e) {
                logger.error(LogCategories.DESKTOP, e);
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

    public static void setWindowOpacity(Window window, float opacity) {
        if (setWindowOpacityMethod != null) {
            try {
                setWindowOpacityMethod.invoke(null, window, opacity);
            } catch (SecurityException e) {
                logger.error(LogCategories.DESKTOP, e);
            } catch (IllegalArgumentException e) {
                logger.info(LogCategories.DESKTOP, "opaque windows not supported");
            } catch (IllegalAccessException e) {
                logger.error(LogCategories.DESKTOP, e);
            } catch (InvocationTargetException e) {
                logger.info(LogCategories.DESKTOP, "opaque windows not supported");
                // In some systems where window opacity is not supported
                // This method launches InvocationTargetException continuosly
                // So the first time exception is thrown, we disable
                // call to setWindowOpacityMethod
                setWindowOpacityMethod = null;
            } catch (UnsupportedOperationException e) {
                logger.error(LogCategories.DESKTOP, e);
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
    public static void setWindowOpaque(Window window, boolean opaque) {
        if (setWindowOpaqueMethod != null) {
            try {
                setWindowOpaqueMethod.invoke(null, window, opaque);
            } catch (SecurityException e) {
                logger.error(LogCategories.DESKTOP, e);
            } catch (IllegalArgumentException e) {
                logger.info(LogCategories.DESKTOP, "opaque windows not supported");
            } catch (IllegalAccessException e) {
                logger.error(LogCategories.DESKTOP, e);
            } catch (InvocationTargetException e) {
                logger.info(LogCategories.DESKTOP, "opaque windows not supported");
                // In some systems where window opacity is not supported
                // This method launches InvocationTargetException continuosly
                // So the first time exception is thrown, we disable
                // call to setWindowOpaqueMethod
                setWindowOpaqueMethod = null;
            } catch (UnsupportedOperationException e) {
                logger.error(LogCategories.DESKTOP, e);
            }
        }
    }

    /**
     * Returns number of screens in current machine
     * 
     * @return
     */
    public static int getNumberOfScreenDevices() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
    }

    /**
     * Returns the screen where given point is or the default screen if it
     * doesn't fit in any screen
     * 
     * @param x
     * @param y
     * @return
     */
    public static GraphicsDevice getGraphicsDeviceForLocation(int x, int y) {
        GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (int i = 0; i < getNumberOfScreenDevices(); i++) {
            GraphicsDevice graphicsDevice = localGraphicsEnvironment.getScreenDevices()[i];
            if (graphicsDevice.getDefaultConfiguration().getBounds().contains(x, y)) {
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
    public static GraphicsDevice getGraphicsDeviceForLocation(Point p) {
        return getGraphicsDeviceForLocation(p.x, p.y);
    }
}
