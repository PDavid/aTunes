/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.tray;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.ActionTrayIcon;
import net.sourceforge.atunes.gui.views.controls.JTrayIcon;
import net.sourceforge.atunes.gui.views.controls.JTrayIcon.JTrayIconPopupMenu;
import net.sourceforge.atunes.kernel.Handler;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.ExitAction;
import net.sourceforge.atunes.kernel.actions.MuteAction;
import net.sourceforge.atunes.kernel.actions.OSDSettingAction;
import net.sourceforge.atunes.kernel.actions.PlayAction;
import net.sourceforge.atunes.kernel.actions.PlayNextAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.PlayPreviousAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.RepeatModeAction;
import net.sourceforge.atunes.kernel.actions.ShowAboutAction;
import net.sourceforge.atunes.kernel.actions.ShuffleModeAction;
import net.sourceforge.atunes.kernel.actions.StopCurrentAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.ToggleWindowVisibilityAction;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The system tray handler.
 */
public final class SystemTrayHandler extends Handler {

    private static SystemTrayHandler instance = new SystemTrayHandler();

    private boolean trayInitialized;
    private boolean trayIconVisible;
    private boolean trayPlayerVisible;
    private SystemTray tray;
    private JTrayIcon trayIcon;
    private TrayIcon previousIcon;
    private TrayIcon playIcon;
    private TrayIcon stopIcon;
    private TrayIcon nextIcon;
    private JMenuItem playMenuItem;

    /**
     * Instantiates a new system tray handler.
     */
    private SystemTrayHandler() {
    }

    @Override
    protected void initHandler() {

    }

    @Override
    public void applicationStarted() {
    }

    /**
     * Gets the single instance of SystemTrayHandler.
     * 
     * @return single instance of SystemTrayHandler
     */
    public static SystemTrayHandler getInstance() {
        return instance;
    }

    /**
     * Fill menu.
     * 
     * @param menu
     *            the menu
     * 
     * @return the j tray icon popup menu
     */
    private JTrayIconPopupMenu fillMenu(JTrayIconPopupMenu menu) {
        menu.add(getPlayMenuItem());
        menu.add(getStopMenuItem());
        menu.add(getPreviousMenuItem());
        menu.add(getNextMenuItem());
        menu.add(new JSeparator());
        menu.add(getMuteCheckBoxMenuItem());
        menu.add(new JSeparator());
        menu.add(getShuffleCheckBoxMenuItem());
        menu.add(getRepeatCheckBoxMenuItem());
        menu.add(new JSeparator());
        menu.add(getShowOSDCheckBoxMenuItem());
        menu.add(new JSeparator());
        menu.add(getAboutMenuItem());
        menu.add(new JSeparator());
        menu.add(getExitMenuItem());

        GuiUtils.applyComponentOrientation(menu);

        return menu;
    }

    /**
     * Finish.
     */
    public void applicationFinish() {
        setTrayIconVisible(false);
        setTrayPlayerVisible(false);
    }

    /**
     * Inits the system tray.
     */
    private void initSystemTray() {
        if (!trayInitialized && SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
            trayInitialized = true;
        }
    }

    /**
     * Inits the tray icon.
     */
    public void initTrayIcon() {
        initSystemTray();
        if (tray != null) {
            trayIconVisible = true;
            addTrayIcon(getTrayIcon());
            GuiHandler.getInstance().setFrameDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        } else {
            getLogger().error(LogCategories.TRAY, "No system tray supported");
        }
    }

    /**
     * Inits the tray player icons.
     */
    public void initTrayPlayerIcons() {
        initSystemTray();
        if (tray != null) {
            trayPlayerVisible = true;
            addTrayIcon(getPreviousTrayIcon());
            addTrayIcon(getStopTrayIcon());
            addTrayIcon(getPlayTrayIcon());
            addTrayIcon(getNextTrayIcon());
        } else {
            getLogger().error(LogCategories.TRAY, "No system tray supported");
        }
    }

    /**
     * Adds given tray icon
     * 
     * @param icon
     */
    private void addTrayIcon(TrayIcon icon) {
        try {
            tray.add(icon);
        } catch (AWTException e) {
            getLogger().error(LogCategories.TRAY, e);
        }
    }

    /**
     * Sets the playing.
     * 
     * @param playing
     *            the new playing
     */
    public void setPlaying(boolean playing) {
        if (playing) {
            getPlayMenuItem().setText(I18nUtils.getString("PAUSE"));
            getPlayMenuItem().setIcon(Images.getImage(Images.PAUSE_TRAY_MENU));
            getPlayTrayIcon().setImage(Images.getImage(Images.PAUSE_TRAY).getImage());
        } else {
            getPlayMenuItem().setText(I18nUtils.getString("PLAY"));
            getPlayMenuItem().setIcon(Images.getImage(Images.PLAY_TRAY_MENU));
            getPlayTrayIcon().setImage(Images.getImage(Images.PLAY_TRAY).getImage());
        }

    }

    /**
     * Sets the tray icon visible.
     * 
     * @param visible
     *            the new tray icon visible
     */
    private void setTrayIconVisible(boolean visible) {
        if (visible && !trayIconVisible) {
            initTrayIcon();
            if (tray != null) {
                trayIconAdvice();
            }
        } else {
            if (!visible && trayIconVisible) {
                tray.remove(getTrayIcon());
                GuiHandler.getInstance().setFrameDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                trayIconVisible = false;
            }
        }
    }

    /**
     * Sets the tray player visible.
     * 
     * @param visible
     *            the new tray player visible
     */
    private void setTrayPlayerVisible(boolean visible) {
        if (visible && !trayPlayerVisible) {
            initTrayPlayerIcons();
        } else {
            if (!visible && trayPlayerVisible) {
                tray.remove(getPreviousTrayIcon());
                tray.remove(getPlayTrayIcon());
                tray.remove(getStopTrayIcon());
                tray.remove(getNextTrayIcon());
                trayPlayerVisible = false;
            }
        }
    }

    /**
     * Sets the tray tool tip.
     * 
     * @param msg
     *            the new tray tool tip
     */
    public void setTrayToolTip(String msg) {
        getTrayIcon().setToolTip(msg);
    }

    /**
     * Tray icon advice.
     */
    private void trayIconAdvice() {
        // For some reason, in Linux systems display message causes Swing freeze
        if (SystemProperties.OS != OperatingSystem.LINUX) {
            getTrayIcon().displayMessage(Constants.APP_NAME, I18nUtils.getString("TRAY_ICON_MESSAGE"), TrayIcon.MessageType.INFO);
        }
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
        setTrayIconVisible(newState.isShowSystemTray());
        setTrayPlayerVisible(newState.isShowTrayPlayer());
    }

    /**
     * Getter of play menu item
     * 
     * @return
     */
    private JMenuItem getPlayMenuItem() {
        if (playMenuItem == null) {
            playMenuItem = new JMenuItem(Actions.getAction(PlayAction.class));
            playMenuItem.setIcon(Images.getImage(Images.PLAY_TRAY_MENU));
        }
        return playMenuItem;
    }

    /**
     * Getter of stop menu item
     * 
     * @return
     */
    private JMenuItem getStopMenuItem() {
        JMenuItem stop = new JMenuItem(Actions.getAction(StopCurrentAudioObjectAction.class));
        stop.setText(I18nUtils.getString("STOP"));
        stop.setIcon(Images.getImage(Images.STOP_TRAY_MENU));
        return stop;
    }

    /**
     * Getter of previous menu item
     * 
     * @return
     */
    private JMenuItem getPreviousMenuItem() {
        JMenuItem previous = new JMenuItem(Actions.getAction(PlayPreviousAudioObjectAction.class));
        previous.setText(I18nUtils.getString("PREVIOUS"));
        previous.setIcon(Images.getImage(Images.PREVIOUS_TRAY_MENU));
        return previous;
    }

    /**
     * Getter for next menu item
     * 
     * @return
     */
    private JMenuItem getNextMenuItem() {
        JMenuItem next = new JMenuItem(Actions.getAction(PlayNextAudioObjectAction.class));
        next.setText(I18nUtils.getString("NEXT"));
        next.setIcon(Images.getImage(Images.NEXT_TRAY_MENU));
        return next;
    }

    /**
     * Getter for mute menu item
     * 
     * @return
     */
    private JCheckBoxMenuItem getMuteCheckBoxMenuItem() {
        JCheckBoxMenuItem mute = new JCheckBoxMenuItem(Actions.getAction(MuteAction.class));
        mute.setText(I18nUtils.getString("MUTE"));
        mute.setIcon(Images.getImage(Images.VOLUME_MUTE_TRAY_MENU));
        return mute;
    }

    /**
     * Getter for shuffle menu item
     * 
     * @return
     */
    private JCheckBoxMenuItem getShuffleCheckBoxMenuItem() {
        return new JCheckBoxMenuItem(Actions.getAction(ShuffleModeAction.class));
    }

    /**
     * Getter for repeat menu item
     */
    private JCheckBoxMenuItem getRepeatCheckBoxMenuItem() {
        return new JCheckBoxMenuItem(Actions.getAction(RepeatModeAction.class));
    }

    /**
     * Getter for showOSD menu item
     * 
     * @return
     */
    private JCheckBoxMenuItem getShowOSDCheckBoxMenuItem() {
        return new JCheckBoxMenuItem(Actions.getAction(OSDSettingAction.class));
    }

    /**
     * Getter for about menu item
     * 
     * @return
     */
    private JMenuItem getAboutMenuItem() {
        return new JMenuItem(Actions.getAction(ShowAboutAction.class));
    }

    /**
     * Getter for exit menu item
     * 
     * @return
     */
    private JMenuItem getExitMenuItem() {
        return new JMenuItem(Actions.getAction(ExitAction.class));
    }

    /**
     * Getter for trayIcon
     * 
     * @return
     */
    private JTrayIcon getTrayIcon() {
        if (trayIcon == null) {
            trayIcon = new JTrayIcon(Images.getImage(Images.APP_ICON_TRAY).getImage(), SystemProperties.OS == OperatingSystem.LINUX, Actions
                    .getAction(ToggleWindowVisibilityAction.class));
            trayIcon.setToolTip(StringUtils.getString(Constants.APP_NAME, " ", Constants.VERSION.toShortString()));
            trayIcon.setJTrayIconJPopupMenu(fillMenu(trayIcon.new JTrayIconPopupMenu()));
            trayIcon.setImageAutoSize(true);
        }
        return trayIcon;
    }

    /**
     * Getter for nextIcon
     * 
     * @return
     */
    private TrayIcon getNextTrayIcon() {
        if (nextIcon == null) {
            nextIcon = new ActionTrayIcon(Images.getImage(Images.NEXT_TRAY).getImage(), Actions.getAction(PlayNextAudioObjectAction.class));
            nextIcon.setImageAutoSize(true);
        }
        return nextIcon;
    }

    /**
     * Getter for stopIcon
     * 
     * @return
     */
    private TrayIcon getStopTrayIcon() {
        if (stopIcon == null) {
            stopIcon = new ActionTrayIcon(Images.getImage(Images.STOP_TRAY).getImage(), Actions.getAction(StopCurrentAudioObjectAction.class));
            stopIcon.setImageAutoSize(true);
        }
        return stopIcon;
    }

    /**
     * Getter for playIcon
     * 
     * @return
     */
    private TrayIcon getPlayTrayIcon() {
        if (playIcon == null) {
            playIcon = new ActionTrayIcon(Images.getImage(Images.PLAY_TRAY).getImage(), Actions.getAction(PlayAction.class));
            playIcon.setImageAutoSize(true);
        }
        return playIcon;
    }

    /**
     * Getter for previousIcon
     * 
     * @return
     */
    private TrayIcon getPreviousTrayIcon() {
        previousIcon = new ActionTrayIcon(Images.getImage(Images.PREVIOUS_TRAY).getImage(), Actions.getAction(PlayPreviousAudioObjectAction.class));
        previousIcon.setImageAutoSize(true);
        return previousIcon;
    }

}
