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

package net.sourceforge.atunes.gui.frame;

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
import javax.swing.JProgressBar;
import javax.swing.Timer;

import net.sourceforge.atunes.gui.views.bars.ToolBar;
import net.sourceforge.atunes.gui.views.controls.CustomDialog;
import net.sourceforge.atunes.gui.views.controls.CustomFrame;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.dialogs.UpdateDialog;
import net.sourceforge.atunes.gui.views.menus.ApplicationMenuBar;
import net.sourceforge.atunes.gui.views.panels.AudioObjectPropertiesPanel;
import net.sourceforge.atunes.gui.views.panels.ContextPanel;
import net.sourceforge.atunes.gui.views.panels.NavigationTablePanel;
import net.sourceforge.atunes.gui.views.panels.NavigationTreePanel;
import net.sourceforge.atunes.gui.views.panels.PlayListPanel;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.updates.ApplicationVersion;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The multiple window frame
 */
public final class MultipleFrame implements Frame {

    private static class FrameWindowAdapter extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            GuiHandler.getInstance().finish();
        }
    }

    private static final class FrameComponentAdapter extends ComponentAdapter {
        private final Timer t;

        private FrameComponentAdapter(Timer t) {
            this.t = t;
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            t.start();
        }
    }

    private static final class CustomDialogComponentAdapter extends ComponentAdapter {
        private final Timer t;

        private CustomDialogComponentAdapter(Timer t) {
            this.t = t;
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            t.start();
        }
    }

    private static final int STICKY_INSET = 30;

    private static int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;

    private static final int NONE = -1;
    private static final int NORTH = 0;
    private static final int SOUTH = 1;
    private static final int WEST = 2;
    private static final int EAST = 3;

    private static final Dimension frameDimension = new Dimension(500, 400);
    private static final Dimension navigatorDimension = new Dimension(300, 300);
    private static final Dimension filePropertiesDimension = new Dimension(500, 130);
    private static final Dimension contextDimension = new Dimension(300, 689);

    private CustomFrame frame;

    private FrameState frameState;

    private CustomDialog navigationTreeDialog;
    private CustomDialog navigationTableDialog;
    private CustomDialog audioObjectPropertiesDialog;
    private CustomDialog contextDialog;
    private ApplicationMenuBar menuBar;
    private ToolBar toolBar;
    private NavigationTreePanel navigationTreePanel;
    private NavigationTablePanel navigationTablePanel;
    private PlayListPanel playListPanel;
    private PlayerControlsPanel playerControlsPanel;
    private AudioObjectPropertiesPanel audioObjectPropertiesPanel;
    private ContextPanel contextPanel;

    private List<Window> windows;

    /**
     * Instantiates a new multiple frame.
     */
    public MultipleFrame() {
        this.windows = new ArrayList<Window>();
    }

    /**
     * Adds the content to audio object properties.
     */
    private void addContentToAudioObjectProperties() {
        audioObjectPropertiesPanel = new AudioObjectPropertiesPanel();
        audioObjectPropertiesDialog.add(audioObjectPropertiesPanel);
        GuiUtils.applyComponentOrientation(audioObjectPropertiesPanel);
    }

    /**
     * Adds the content to frame.
     */
    private void addContentToFrame() {
        GuiUtils.addAppIcons(frame);
        playerControlsPanel = new PlayerControlsPanel();

        JPanel auxPanel = new JPanel(new BorderLayout());
        auxPanel.add(getToolBar(), BorderLayout.NORTH);
        auxPanel.add(getPlayListPanel(), BorderLayout.CENTER);
        auxPanel.add(playerControlsPanel, BorderLayout.SOUTH);

        GuiUtils.applyComponentOrientation(auxPanel);

        frame.add(auxPanel);
    }

    @Override
    public NavigationTreePanel getNavigationTreePanel() {
        if (navigationTreePanel == null) {
            navigationTreePanel = new NavigationTreePanel();
        }
        return navigationTreePanel;
    }

    @Override
    public NavigationTablePanel getNavigationTablePanel() {
        if (navigationTablePanel == null) {
            navigationTablePanel = new NavigationTablePanel();
        }
        return navigationTablePanel;
    }

    /**
     * Adds the content to open strands.
     */
    private void addContentToContext() {
        contextPanel = new ContextPanel();
        contextDialog.add(contextPanel);
        GuiUtils.applyComponentOrientation(contextPanel);
    }

    @Override
    public void create(FrameState frameState) {
        this.frameState = frameState;

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

        navigationTreePanel = new NavigationTreePanel();
        navigationTablePanel = new NavigationTablePanel();

        navigationTreeDialog = getNewDialog(frame, I18nUtils.getString("NAVIGATION_TREE"), navigatorDimension.width, navigatorDimension.height, frame, WEST, navigatorDimension);
        navigationTreeDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // When closing navigator dialog, perform the same actions as deselecting tool bar button
                toolBar.getShowNavigationTree().setSelected(false);
                toolBar.getShowNavigationTree().getAction().actionPerformed(null);
            }
        });
        navigationTreeDialog.add(navigationTreePanel);
        GuiUtils.applyComponentOrientation(navigationTreeDialog);

        navigationTableDialog = getNewDialog(frame, I18nUtils.getString("NAVIGATION_TABLE"), navigatorDimension.width, navigatorDimension.height, frame, SOUTH, navigatorDimension);
        navigationTableDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // When closing navigator dialog, perform the same actions as deselecting tool bar button
                toolBar.getShowNavigationTable().setSelected(false);
                toolBar.getShowNavigationTable().getAction().actionPerformed(null);
            }
        });
        navigationTableDialog.add(navigationTablePanel);
        GuiUtils.applyComponentOrientation(navigationTableDialog);

        audioObjectPropertiesDialog = getNewDialog(frame, I18nUtils.getString("PROPERTIES"), filePropertiesDimension.width, filePropertiesDimension.height, frame, SOUTH,
                filePropertiesDimension);
        addContentToAudioObjectProperties();
        audioObjectPropertiesDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // When closing navigator dialog, perform the same actions as deselecting tool bar button
                toolBar.getShowAudioObjectProperties().setSelected(false);
                toolBar.getShowAudioObjectProperties().getAction().actionPerformed(null);
            }
        });

        contextDialog = getNewDialog(frame, I18nUtils.getString("CONTEXT_INFORMATION"), contextDimension.width, contextDimension.height, frame, EAST, contextDimension);
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

    @Override
    public ApplicationMenuBar getAppMenuBar() {
        return menuBar;
    }

    @Override
    public ContextPanel getContextPanel() {
        return contextPanel;
    }

    @Override
    public int getExtendedState() {
        return frame.getExtendedState();
    }

    @Override
    public JFrame getFrame() {
        return frame;
    }

    @Override
    public Point getLocation() {
        return frame.getLocation();
    }

    @Override
    public PlayerControlsPanel getPlayerControls() {
        return playerControlsPanel;
    }

    @Override
    public PlayListPanel getPlayListPanel() {
        if (playListPanel == null) {
            playListPanel = new PlayListPanel();
        }
        return playListPanel;
    }

    @Override
    public PlayListTable getPlayListTable() {
        return getPlayListPanel().getPlayListTable();
    }

    @Override
    public AudioObjectPropertiesPanel getPropertiesPanel() {
        return audioObjectPropertiesPanel;
    }

    @Override
    public Dimension getSize() {
        return frame.getSize();
    }

    @Override
    public ToolBar getToolBar() {
        return toolBar;
    }

    @Override
    public boolean isVisible() {
        return frame.isVisible();
    }

    @Override
    public void setCenterStatusBarText(String text, String toolTip) {
        // Nothing to do
    }

    @Override
    public void setDefaultCloseOperation(int op) {
        frame.setDefaultCloseOperation(op);
        navigationTreeDialog.setDefaultCloseOperation(op);
        navigationTableDialog.setDefaultCloseOperation(op);
        audioObjectPropertiesDialog.setDefaultCloseOperation(op);
        contextDialog.setDefaultCloseOperation(op);
    }

    @Override
    public void setExtendedState(int state) {
        //frame.setExtendedState(state);
    }

    @Override
    public void setLeftStatusBarText(String text, String toolTip) {
        //	Nothing to do
    }

    @Override
    public void setLocation(Point location) {
        //frame.setLocation(location.x, location.y);
    }

    @Override
    public void setLocationRelativeTo(Component c) {
        frame.setLocationRelativeTo(c);
    }

    @Override
    public void setRightStatusBarText(String text, String toolTip) {
        // Nothing to do
    }

    @Override
    public void setStatusBarDeviceLabelText(String text) {
        // Nothing to do
    }

    @Override
    public void setTitle(String title) {
        frame.setTitle(title);
    }

    @Override
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
        if (!visible) {
            navigationTreeDialog.setVisible(visible);
            navigationTableDialog.setVisible(visible);
        }
        if (!visible || ApplicationState.getInstance().isShowAudioObjectProperties()) {
            audioObjectPropertiesDialog.setVisible(visible);
        }
        if (!visible || ApplicationState.getInstance().isUseContext()) {
            contextDialog.setVisible(visible);
        }
    }

    @Override
    public void showContextPanel(boolean show) {
        contextDialog.setVisible(show);
    }

    @Override
    public void showNavigationTree(boolean show) {
        navigationTreeDialog.setVisible(show);
    }

    @Override
    public void showNavigationTable(boolean show) {
        navigationTableDialog.setVisible(show);
    }

    @Override
    public JProgressBar getProgressBar() {
        // Nothing to do
        return null;
    }

    @Override
    public void showSongProperties(boolean show) {
        audioObjectPropertiesDialog.setVisible(show);
    }

    @Override
    public void showStatusBar(boolean show) {
        // Nothing to do
    }

    @Override
    public void showToolBar(boolean show) {
        getToolBar().setVisible(show);
    }

    @Override
    public void showDeviceInfo(boolean show) {
        // Nothing to do
    }

    @Override
    public void showNewPodcastFeedEntriesInfo(boolean show) {
        if (show) {
            JOptionPane.showMessageDialog(frame, I18nUtils.getString("NEW_PODCAST_ENTRIES"));
        }
    }

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
        final CustomDialog dialog = new CustomDialog(frame, width, height);
        dialog.setTitle(title);
        dialog.setMinimumSize(minSize);
        final Timer t = new Timer(400, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                positionChanged(dialog);
            }
        });
        t.setRepeats(false);
        dialog.addComponentListener(new CustomDialogComponentAdapter(t));

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
        final CustomFrame newFrame = new CustomFrame(title, width, height, this.frame == null ? null : this.frame);
        final Timer t = new Timer(400, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                positionChanged(newFrame);
            }
        });
        t.setRepeats(false);
        newFrame.addComponentListener(new FrameComponentAdapter(t));

        if (this.frame == null) {
            this.frame = newFrame;
            this.frame.addWindowListener(new FrameWindowAdapter());
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
        x = (SCREEN_WIDTH - (x + window.getSize().width) < STICKY_INSET) ? SCREEN_WIDTH - window.getSize().width : x;
        y = (y < STICKY_INSET) ? 0 : y;
        y = (SCREEN_HEIGHT - (y + window.getSize().height) < STICKY_INSET) ? SCREEN_HEIGHT - window.getSize().height : y;

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

    @Override
    public FrameState getFrameState() {
        return frameState;
    }
    
    @Override
    public void applicationStarted(FrameState frameState) {
    }

}
