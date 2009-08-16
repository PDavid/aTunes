/*
 * aTunes 1.14.0
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.CustomDialog;
import net.sourceforge.atunes.gui.views.controls.CustomFrame;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.dialogs.UpdateDialog;
import net.sourceforge.atunes.gui.views.menus.ApplicationMenuBar;
import net.sourceforge.atunes.gui.views.panels.ContextPanel;
import net.sourceforge.atunes.gui.views.panels.AudioObjectPropertiesPanel;
import net.sourceforge.atunes.gui.views.panels.NavigationPanel;
import net.sourceforge.atunes.gui.views.panels.PlayListPanel;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.updates.ApplicationVersion;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * The Class MultipleFrame.
 */
public class MultipleFrame implements Frame {

    /** The Constant frameDimension. */
    private static final Dimension frameDimension = new Dimension(500, 400);

    /** The Constant navigatorDimension. */
    private static final Dimension navigatorDimension = new Dimension(300, 689);

    /** The Constant filePropertiesDimension. */
    private static final Dimension filePropertiesDimension = new Dimension(500, 130);

    /** The Constant contextDimension. */
    private static final Dimension contextDimension = new Dimension(300, 689);

    /** The frame. */
    private CustomFrame frame;

    /** The navigator dialog. */
    CustomDialog navigatorDialog;

    /** The file properties dialog. */
    private CustomDialog filePropertiesDialog;

    /** The audio scrobbler dialog. */
    private CustomDialog contextDialog;

    /** The menu bar. */
    private ApplicationMenuBar menuBar;

    /** The tool bar. */
    ToolBar toolBar;

    /** The navigation panel. */
    private NavigationPanel navigationPanel;

    /** The play list panel. */
    private PlayListPanel playListPanel;

    /** The player controls panel. */
    private PlayerControlsPanel playerControlsPanel;

    /** The file properties panel. */
    private AudioObjectPropertiesPanel filePropertiesPanel;

    /** The audio scrobbler panel. */
    private ContextPanel contextPanel;

    /** The Constant NONE. */
    static final int NONE = -1;

    /** The Constant NORTH. */
    static final int NORTH = 0;

    /** The Constant SOUTH. */
    static final int SOUTH = 1;

    /** The Constant WEST. */
    static final int WEST = 2;

    /** The Constant EAST. */
    static final int EAST = 3;

    /** The windows. */
    List<Window> windows;

    private static final int STICKY_INSET = 30;

    private static int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

    /**
     * Instantiates a new multiple frame.
     */
    public MultipleFrame() {
        this.windows = new ArrayList<Window>();
    }

    /**
     * Adds the content to file properties.
     */
    private void addContentToFileProperties() {
        filePropertiesPanel = new AudioObjectPropertiesPanel();
        filePropertiesDialog.add(filePropertiesPanel);
        GuiUtils.applyComponentOrientation(filePropertiesPanel);
    }

    /**
     * Adds the content to frame.
     */
    private void addContentToFrame() {
        frame.setIconImage(ImageLoader.APP_ICON.getImage());
        playerControlsPanel = new PlayerControlsPanel();

        JPanel auxPanel = new JPanel(new BorderLayout());
        auxPanel.add(getToolBar(), BorderLayout.NORTH);
        auxPanel.add(getPlayListPanel(), BorderLayout.CENTER);
        auxPanel.add(playerControlsPanel, BorderLayout.SOUTH);

        GuiUtils.applyComponentOrientation(auxPanel);

        frame.add(auxPanel);
    }

    /**
     * Adds the content to navigator.
     */
    private void addContentToNavigator() {
        navigationPanel = new NavigationPanel();
        navigatorDialog.add(navigationPanel);
        GuiUtils.applyComponentOrientation(navigationPanel);
    }

    /**
     * Adds the content to open strands.
     */
    private void addContentToContext() {
        contextPanel = new ContextPanel();
        contextDialog.add(contextPanel);
        GuiUtils.applyComponentOrientation(contextPanel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#create()
     */
    @Override
    public void create() {
        menuBar = new ApplicationMenuBar();
        toolBar = new ToolBar();

        frame = getNewFrame("", frameDimension.width, frameDimension.height, null, NONE, frameDimension);
        frame.setJMenuBar(menuBar);

        Point p = null;
        if (ApplicationState.getInstance().getMultipleViewXPosition() > 0 && ApplicationState.getInstance().getMultipleViewYPosition() > 0) {
            p = new Point(ApplicationState.getInstance().getMultipleViewXPosition(), ApplicationState.getInstance().getMultipleViewYPosition());
        }
        if (p == null) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation((screenSize.width - frameDimension.width) / 2, 100);
        } else {
            frame.setLocation(p.x, p.y);
        }

        Dimension d = null;
        if (ApplicationState.getInstance().getMultipleViewWidth() != 0 && ApplicationState.getInstance().getMultipleViewHeight() != 0) {
            d = new Dimension(ApplicationState.getInstance().getMultipleViewWidth(), ApplicationState.getInstance().getMultipleViewHeight());
        }
        if (d != null) {
            frame.setSize(d);
        }

        addContentToFrame();

        navigatorDialog = getNewDialog(frame, LanguageTool.getString("NAVIGATOR"), navigatorDimension.width, navigatorDimension.height, frame, WEST, navigatorDimension);
        addContentToNavigator();
        navigatorDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // When closing navigator dialog, perform the same actions as deselecting tool bar button
                toolBar.getShowNavigator().setSelected(false);
                toolBar.getShowNavigator().getAction().actionPerformed(null);
            }
        });

        filePropertiesDialog = getNewDialog(frame, LanguageTool.getString("PROPERTIES"), filePropertiesDimension.width, filePropertiesDimension.height, frame, SOUTH,
                filePropertiesDimension);
        addContentToFileProperties();
        filePropertiesDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // When closing navigator dialog, perform the same actions as deselecting tool bar button
                toolBar.getShowFileProperties().setSelected(false);
                toolBar.getShowFileProperties().getAction().actionPerformed(null);
            }
        });

        contextDialog = getNewDialog(frame, LanguageTool.getString("CONTEXT_INFORMATION"), contextDimension.width, contextDimension.height, frame, EAST, contextDimension);
        addContentToContext();
        contextDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // When closing navigator dialog, perform the same actions as deselecting tool bar button
                toolBar.getShowContext().setSelected(false);
                toolBar.getShowContext().getAction().actionPerformed(null);
            }
        });

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#getAppMenuBar()
     */
    @Override
    public ApplicationMenuBar getAppMenuBar() {
        return menuBar;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#getAudioScrobblerPanel()
     */
    @Override
    public ContextPanel getContextPanel() {
        return contextPanel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#getExtendedState()
     */
    @Override
    public int getExtendedState() {
        return frame.getExtendedState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#getFrame()
     */
    @Override
    public JFrame getFrame() {
        return frame;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#getLocation()
     */
    @Override
    public Point getLocation() {
        return frame.getLocation();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#getNavigationPanel()
     */
    @Override
    public NavigationPanel getNavigationPanel() {
        return navigationPanel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#getPlayerControls()
     */
    @Override
    public PlayerControlsPanel getPlayerControls() {
        return playerControlsPanel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#getPlayListPanel()
     */
    @Override
    public PlayListPanel getPlayListPanel() {
        if (playListPanel == null) {
            playListPanel = new PlayListPanel();
        }
        return playListPanel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#getPlayListTable()
     */
    @Override
    public PlayListTable getPlayListTable() {
        return getPlayListPanel().getPlayListTable();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#getPropertiesPanel()
     */
    @Override
    public AudioObjectPropertiesPanel getPropertiesPanel() {
        return filePropertiesPanel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#getSize()
     */
    @Override
    public Dimension getSize() {
        return frame.getSize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#getToolBar()
     */
    @Override
    public ToolBar getToolBar() {
        return toolBar;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#isVisible()
     */
    @Override
    public boolean isVisible() {
        return frame.isVisible();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.atunes.gui.Frame#setCenterStatusBar(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void setCenterStatusBar(String text, String toolTip) {
        // Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#setDefaultCloseOperation(int)
     */
    @Override
    public void setDefaultCloseOperation(int op) {
        frame.setDefaultCloseOperation(op);
        navigatorDialog.setDefaultCloseOperation(op);
        filePropertiesDialog.setDefaultCloseOperation(op);
        contextDialog.setDefaultCloseOperation(op);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#setExtendedState(int)
     */
    @Override
    public void setExtendedState(int state) {
        //frame.setExtendedState(state);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.atunes.gui.Frame#setLeftStatusBarText(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void setLeftStatusBarText(String text, String toolTip) {
        //	Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#setLocation(java.awt.Point)
     */
    @Override
    public void setLocation(Point location) {
        //frame.setLocation(location.x, location.y);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.atunes.gui.Frame#setLocationRelativeTo(java.awt.Component
     * )
     */
    @Override
    public void setLocationRelativeTo(Component c) {
        frame.setLocationRelativeTo(c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#setRightStatusBar(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void setRightStatusBar(String text, String toolTip) {
        // Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.atunes.gui.Frame#setStatusBarImageLabelIcon(javax.swing
     * .Icon, java.lang.String)
     */
    @Override
    public void setStatusBarDeviceLabelText(String text) {
        // Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#setTitle(java.lang.String)
     */
    @Override
    public void setTitle(String title) {
        frame.setTitle(title);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
        if (!visible || ApplicationState.getInstance().isShowNavigationPanel()) {
            navigatorDialog.setVisible(visible);
        }
        if (!visible || ApplicationState.getInstance().isShowAudioObjectProperties()) {
            filePropertiesDialog.setVisible(visible);
        }
        if (!visible || ApplicationState.getInstance().isUseContext()) {
            contextDialog.setVisible(visible);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#showContextPanel(boolean, boolean)
     */
    @Override
    public void showContextPanel(boolean show, boolean changeSize) {
        contextDialog.setVisible(show);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#showNavigationPanel(boolean,
     * boolean)
     */
    @Override
    public void showNavigationPanel(boolean show, boolean changeSize) {
        navigatorDialog.setVisible(show);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#showNavigationTable(boolean)
     */
    @Override
    public void showNavigationTable(boolean show) {
        navigationPanel.getNavigationTableContainer().setVisible(show);
        if (show) {
            navigationPanel.getSplitPane().setDividerLocation(ApplicationState.getInstance().getLeftHorizontalSplitPaneDividerLocation());
        } else {
            // Save location
            ApplicationState.getInstance().setLeftHorizontalSplitPaneDividerLocation(navigationPanel.getSplitPane().getDividerLocation());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#showProgressBar(boolean,
     * java.lang.String)
     */
    @Override
    public void showProgressBar(boolean visible, String tooltip) {
        // Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#showSongProperties(boolean)
     */
    @Override
    public void showSongProperties(boolean show) {
        filePropertiesDialog.setVisible(show);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#showStatusBar(boolean)
     */
    @Override
    public void showStatusBar(boolean show) {
        // Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#showToolBar(boolean)
     */
    @Override
    public void showToolBar(boolean show) {
        getToolBar().setVisible(show);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#showDeviceInfo(boolean)
     */
    @Override
    public void showDeviceInfo(boolean show) {
        // Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.atunes.gui.Frame#showNewPodcastFeedEntriesInfo(boolean)
     */
    @Override
    public void showNewPodcastFeedEntriesInfo(boolean show) {
        if (show) {
            JOptionPane.showMessageDialog(frame, LanguageTool.getString("NEW_PODCAST_ENTRIES"));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.gui.Frame#showNewVersionInfo(boolean,
     * net.sourceforge.atunes.kernel.modules.updates.ApplicationVersion)
     */
    @Override
    public void showNewVersionInfo(boolean show, ApplicationVersion version) {
        if (show) {
            new UpdateDialog(version, null).setVisible(true);
        }
    }

    /**
     * Gets the new dialog.
     * 
     * @param parent
     *            the parent
     * @param title
     *            the title
     * @param width
     *            the width
     * @param height
     *            the height
     * @param relative
     *            the relative
     * @param position
     *            the position
     * @param minSize
     *            the min size
     * 
     * @return the new dialog
     */
    private CustomDialog getNewDialog(CustomFrame parent, String title, int width, int height, Window relative, int position, Dimension minSize) {
        final CustomDialog dialog = new CustomDialog(frame, width, height) {
            private static final long serialVersionUID = 0L;
        };
        dialog.setTitle(title);
        dialog.setMinimumSize(minSize);
        final Timer t = new Timer(400, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                positionChanged(dialog);
            }
        });
        t.setRepeats(false);
        dialog.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                t.start();
            }
        });

        if (windows.isEmpty() || position == NONE) {
            dialog.setLocation(50, 50);
        } else {
            Rectangle relativeFrameBounds = relative.getBounds();
            if (position == NORTH) {
                dialog.setLocation(relativeFrameBounds.x, relativeFrameBounds.y - height - 1);
            } else if (position == SOUTH) {
                dialog.setLocation(relativeFrameBounds.x, relativeFrameBounds.y + relativeFrameBounds.height - 1);
            } else if (position == EAST) {
                dialog.setLocation(relativeFrameBounds.x + relativeFrameBounds.width - 1, relativeFrameBounds.y);
            } else {
                // Avoid x < 0
                int x = relativeFrameBounds.x - width + 1;
                dialog.setLocation(x >= 0 ? x : 0, relativeFrameBounds.y);
            }
        }

        windows.add(dialog);
        return dialog;
    }

    /**
     * Gets the new frame.
     * 
     * @param title
     *            the title
     * @param width
     *            the width
     * @param height
     *            the height
     * @param relative
     *            the relative
     * @param position
     *            the position
     * @param minSize
     *            the min size
     * 
     * @return the new frame
     */
    private CustomFrame getNewFrame(String title, int width, int height, Window relative, int position, Dimension minSize) {
        final CustomFrame newFrame = new CustomFrame(title, width, height, this.frame == null ? null : this.frame) {
            private static final long serialVersionUID = 7204427148108937993L;
        };
        final Timer t = new Timer(400, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                positionChanged(newFrame);
            }
        });
        t.setRepeats(false);
        newFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                t.start();
            }
        });

        if (this.frame == null) {
            this.frame = newFrame;
            this.frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    VisualHandler.getInstance().finish();
                }
            });
        }
        if (windows.isEmpty() || position == NONE) {
            newFrame.setLocationRelativeTo(null);
        } else {
            Rectangle relativeFrameBounds = relative.getBounds();
            if (position == NORTH) {
                newFrame.setLocation(relativeFrameBounds.x, relativeFrameBounds.y - height - 1);
            } else if (position == SOUTH) {
                newFrame.setLocation(relativeFrameBounds.x, relativeFrameBounds.y + relativeFrameBounds.height - 1);
            } else if (position == EAST) {
                newFrame.setLocation(relativeFrameBounds.x + relativeFrameBounds.width - 1, relativeFrameBounds.y);
            } else {
                newFrame.setLocation(relativeFrameBounds.x - width + 1, relativeFrameBounds.y);
            }
        }

        windows.add(newFrame);
        return newFrame;
    }

    protected void positionChanged(Window window) {
        int x = window.getBounds().x;
        int y = window.getBounds().y;

        // Sticky borders
        x = (x < STICKY_INSET) ? 0 : x;
        x = (screenWidth - (x + window.getSize().width) < STICKY_INSET) ? screenWidth - window.getSize().width : x;
        y = (y < STICKY_INSET) ? 0 : y;
        y = (screenHeight - (y + window.getSize().height) < STICKY_INSET) ? screenHeight - window.getSize().height : y;

        if (windows.size() > 1) {
            for (int i = 0; i < windows.size(); i++) {
                Window f = windows.get(i);
                if (window != f && f.isVisible()) {
                    int relX = x - f.getBounds().x;
                    int relY = y - f.getBounds().y;

                    int locationX = f.getBounds().x + (relX > 0 ? f.getBounds().width - 1 : -window.getBounds().width + 1);
                    int locationY = f.getBounds().y + (relY > 0 ? f.getBounds().height - 1 : -window.getBounds().height + 1);

                    int deltaX = locationX - x;
                    int deltaY = locationY - y;

                    if (Math.abs(deltaX) < STICKY_INSET || Math.abs(deltaY) < STICKY_INSET) {
                        if (Math.abs(deltaX) > Math.abs(deltaY)) {
                            y = locationY;
                        } else {
                            x = locationX;
                        }
                        break;
                    }
                }
            }
        }

        if (x != window.getBounds().x || y != window.getBounds().y) {
            window.setLocation(x, y);
        }
    }

}
