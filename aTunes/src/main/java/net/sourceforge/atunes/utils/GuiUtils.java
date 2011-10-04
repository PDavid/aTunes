/*
 * aTunes 2.2.0-SNAPSHOT
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
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.StyleConstants;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.AbstractTableCellRendererCode;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IState;

import org.commonjukebox.plugins.model.PluginApi;

/**
 * GUI related utility methods.
 */
@PluginApi
public final class GuiUtils {

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

    private static IState state = Context.getBean(IState.class);
    
    /**
     * Bounds of the main screen device, used to calculate sizes
     */
    private static Rectangle mainDeviceBounds;

    public static final int MAX_COMPONENTS_WIDTH = 3280;

    static {
        try {
            Class<?> awtUtilities = Class.forName("com.sun.awt.AWTUtilities");
            setWindowShapeMethod = awtUtilities.getDeclaredMethod("setWindowShape", Window.class, Shape.class);
            setWindowOpacityMethod = awtUtilities.getDeclaredMethod("setWindowOpacity", Window.class, float.class);
            setWindowOpaqueMethod = awtUtilities.getDeclaredMethod("setWindowOpaque", Window.class, boolean.class);
        } catch (ClassNotFoundException e) {
            Logger.info("class com.sun.awt.AWTUtilities not found");
        } catch (SecurityException e) {
            Logger.error(e);
        } catch (NoSuchMethodException e) {
            Logger.info("method in class com.sun.awt.AWTUtilities not found");
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
        w.setIconImages(Arrays.asList(Images.getImage(Images.APP_LOGO_16).getImage(), Images.getImage(Images.APP_LOGO_32).getImage(), Images.getImage(Images.APP_LOGO_90)
                .getImage()));
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
     * Returns the component orientation as a text style constant.
     * 
     * @return The component orientation as a SwingConstant
     */
    public static int getComponentOrientationAsTextStyleConstant() {
        if (componentOrientation == null) {
            setComponentOrientation();
        }
        return componentOrientation.isLeftToRight() ? StyleConstants.ALIGN_LEFT : StyleConstants.ALIGN_RIGHT;
    }

    /**
     * Returns a proportional width according to given factor for the current screen resolution.
     * @param screenWidthFactor
     * @return the component width for resolution
     */
    public static int getComponentWidthForResolution(float screenWidthFactor) {
        int currentScreenWidth = mainDeviceBounds.width > MAX_COMPONENTS_WIDTH ? MAX_COMPONENTS_WIDTH : mainDeviceBounds.width;
        return (int) (currentScreenWidth * screenWidthFactor);
    }

    /**
     * Returns a proportional width according to given factor for the current screen resolution or the given minimum width if
     * calculated value is lower
     * @param screenWidthFactor
     * @param minimumWidth
     * @return
     */
    public static int getComponentWidthForResolution(float screenWidthFactor, int minimumWidth) {
    	return Math.max(getComponentWidthForResolution(screenWidthFactor), minimumWidth);
    }

    /**
     * Returns a proportional height according to given screen height factor for the current screen resolution.
     * 
     * @param screenHeight
     *            the screen height
     * @param desiredHeight
     *            the desired height
     * 
     * @return the component height for resolution
     */
    public static int getComponentHeightForResolution(float screenHeightFactor) {
        int currentScreenHeight = mainDeviceBounds.height;
        return (int) (currentScreenHeight * screenHeightFactor);
    }

    /**
     * Returns a proportional height according to given screen height factor for the current screen resolution or the given minimum height
     * if calculated value is lower
     * 
     * @param screenHeightFactor
     * @param minimumHeight
     * @return
     */
    public static int getComponentHeightForResolution(float screenHeightFactor, int minimumHeight) {
    	return Math.max(getComponentHeightForResolution(screenHeightFactor), minimumHeight);
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
    	componentOrientation = ComponentOrientation.LEFT_TO_RIGHT;
    	if (state.getLocale() != null) {
    		if ("ug".equalsIgnoreCase(state.getLocale().getLocale().getLanguage())) {
    			componentOrientation = ComponentOrientation.RIGHT_TO_LEFT;
    		} else {
    			componentOrientation = ComponentOrientation.getOrientation(state.getLocale().getLocale());
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
                // any exception will disable call to method
            } catch (SecurityException e) {
                Logger.info("shaped windows not supported: ", e.getMessage());
                setWindowShapeMethod = null;
            } catch (IllegalArgumentException e) {
                Logger.info("shaped windows not supported: ", e.getMessage());
                setWindowShapeMethod = null;
            } catch (IllegalAccessException e) {
                Logger.info("shaped windows not supported: ", e.getMessage());
                setWindowShapeMethod = null;
            } catch (InvocationTargetException e) {
                Logger.info("shaped windows not supported: ", e.getMessage());
                setWindowShapeMethod = null;
            } catch (UnsupportedOperationException e) {
                Logger.info("shaped windows not supported: ", e.getMessage());
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

    public static void setWindowOpacity(Window window, float opacity) {
        if (setWindowOpacityMethod != null) {
            try {
                setWindowOpacityMethod.invoke(null, window, opacity);
                // any exception will disable call to method
            } catch (SecurityException e) {
                Logger.info("opaque windows not supported: ", e.getMessage());
                setWindowOpacityMethod = null;
            } catch (IllegalArgumentException e) {
                Logger.info("opaque windows not supported: ", e.getMessage());
                setWindowOpacityMethod = null;
            } catch (IllegalAccessException e) {
                Logger.info("opaque windows not supported: ", e.getMessage());
                setWindowOpacityMethod = null;
            } catch (InvocationTargetException e) {
                Logger.info("opaque windows not supported: ", e.getMessage());
                setWindowOpacityMethod = null;
            } catch (UnsupportedOperationException e) {
                Logger.info("opaque windows not supported: ", e.getMessage());
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
    public static void setWindowOpaque(Window window, boolean opaque) {
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

    public static ComponentOrientationTableCellRendererCode getComponentOrientationTableCellRendererCode(ILookAndFeel lookAndFeel) {
        return new ComponentOrientationTableCellRendererCode(lookAndFeel);
    }

    public static class ComponentOrientationTableCellRendererCode extends AbstractTableCellRendererCode {
        public ComponentOrientationTableCellRendererCode(ILookAndFeel lookAndFeel) {
			super(lookAndFeel);
		}

		@Override
        public JComponent getComponent(JComponent superComponent, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        	if (superComponent instanceof JLabel) {
        		GuiUtils.applyComponentOrientation((JLabel) superComponent);
        	}
            return superComponent;
        }
    }
    
    /**
     * Returns true if mouse event is from primary mouse button (left-click or not Ctrl-click in Mac)
     * @param e
     * @return
     */
    public static boolean isPrimaryMouseButton(MouseEvent e) {
    	return SwingUtilities.isLeftMouseButton(e) && !e.isControlDown();
    }
    
    /**
     * Returns true if mouse event is from secondary mouse button (right-click or Ctrl-click in Mac)
     * @param e
     * @return
     */
    public static boolean isSecondaryMouseButton(MouseEvent e) {
    	return SwingUtilities.isRightMouseButton(e) || e.isControlDown();
    }
}
