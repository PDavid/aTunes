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
package net.sourceforge.atunes.kernel.modules.tray;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.JTrayIcon;
import net.sourceforge.atunes.gui.views.controls.JTrayIcon.JTrayIconPopupMenu;
import net.sourceforge.atunes.kernel.Handler;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.ExitAction;
import net.sourceforge.atunes.kernel.actions.MuteAction;
import net.sourceforge.atunes.kernel.actions.OSDSettingAction;
import net.sourceforge.atunes.kernel.actions.RepeatModeAction;
import net.sourceforge.atunes.kernel.actions.ShowAboutAction;
import net.sourceforge.atunes.kernel.actions.ShuffleModeAction;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
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
    private JMenuItem playMenu;
    JCheckBoxMenuItem mute;
    JCheckBoxMenuItem shuffle;
    JCheckBoxMenuItem repeat;
    JCheckBoxMenuItem showOSD;

    /**
     * Instantiates a new system tray handler.
     */
    private SystemTrayHandler() {
    }
    
    @Override
    protected void initHandler() {
    	// TODO Auto-generated method stub
    	
    }
    
    @Override
    public void applicationStarted() {
    	// TODO Auto-generated method stub
    	
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
        playMenu = new JMenuItem(I18nUtils.getString("PLAY"), ImageLoader.getImage(ImageLoader.PLAY_TRAY_MENU));
        playMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlayerHandler.getInstance().playCurrentAudioObject(true);
            }
        });
        menu.add(playMenu);

        JMenuItem stop = new JMenuItem(I18nUtils.getString("STOP"), ImageLoader.getImage(ImageLoader.STOP_TRAY_MENU));
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlayerHandler.getInstance().stopCurrentAudioObject(true);
            }
        });
        menu.add(stop);

        JMenuItem previous = new JMenuItem(I18nUtils.getString("PREVIOUS"), ImageLoader.getImage(ImageLoader.PREVIOUS_TRAY_MENU));
        previous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlayerHandler.getInstance().playPreviousAudioObject();
            }
        });
        menu.add(previous);

        JMenuItem next = new JMenuItem(I18nUtils.getString("NEXT"), ImageLoader.getImage(ImageLoader.NEXT_TRAY_MENU));
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PlayerHandler.getInstance().playNextAudioObject();
            }
        });
        menu.add(next);

        menu.add(new JSeparator());

        mute = new JCheckBoxMenuItem(I18nUtils.getString("MUTE"), ImageLoader.getImage(ImageLoader.VOLUME_MUTE_TRAY_MENU));
        mute.setAction(Actions.getAction(MuteAction.class));
        menu.add(mute);

        menu.add(new JSeparator());

        shuffle = new JCheckBoxMenuItem(Actions.getAction(ShuffleModeAction.class));
        menu.add(shuffle);

        repeat = new JCheckBoxMenuItem(Actions.getAction(RepeatModeAction.class));
        menu.add(repeat);

        menu.add(new JSeparator());

        showOSD = new JCheckBoxMenuItem(Actions.getAction(OSDSettingAction.class));
        menu.add(showOSD);

        menu.add(new JSeparator());

        menu.add(new JMenuItem(Actions.getAction(ShowAboutAction.class)));

        menu.add(new JSeparator());

        menu.add(new JMenuItem(Actions.getAction(ExitAction.class)));

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
            trayIcon = new JTrayIcon(ImageLoader.getImage(ImageLoader.APP_ICON_TRAY).getImage(), SystemProperties.OS == OperatingSystem.LINUX);
            trayIcon.setToolTip(StringUtils.getString(Constants.APP_NAME, " ", Constants.VERSION.toShortString()));
            trayIcon.setJTrayIconJPopupMenu(fillMenu(trayIcon.new JTrayIconPopupMenu()));
            trayIcon.setImageAutoSize(true);

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                getLogger().error(LogCategories.TRAY, e);
            }

            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        VisualHandler.getInstance().toggleWindowVisibility();
                    }
                }
            });
            VisualHandler.getInstance().setFrameDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
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
            nextIcon = new TrayIcon(ImageLoader.getImage(ImageLoader.NEXT_TRAY).getImage());
            nextIcon.setImageAutoSize(true);
            nextIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        PlayerHandler.getInstance().playNextAudioObject();
                    }
                }
            });
            try {
                tray.add(nextIcon);
            } catch (AWTException e) {
                getLogger().error(LogCategories.TRAY, e);
            }

            stopIcon = new TrayIcon(ImageLoader.getImage(ImageLoader.STOP_TRAY).getImage());
            stopIcon.setImageAutoSize(true);
            stopIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        PlayerHandler.getInstance().stopCurrentAudioObject(true);
                    }
                }
            });
            try {
                tray.add(stopIcon);
            } catch (AWTException e) {
                getLogger().error(LogCategories.TRAY, e);
            }

            playIcon = new TrayIcon(ImageLoader.getImage(ImageLoader.PLAY_TRAY).getImage());
            playIcon.setImageAutoSize(true);
            playIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        PlayerHandler.getInstance().playCurrentAudioObject(true);
                    }
                }
            });
            try {
                tray.add(playIcon);
            } catch (AWTException e) {
                getLogger().error(LogCategories.TRAY, e);
            }

            previousIcon = new TrayIcon(ImageLoader.getImage(ImageLoader.PREVIOUS_TRAY).getImage());
            previousIcon.setImageAutoSize(true);
            previousIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        PlayerHandler.getInstance().playPreviousAudioObject();
                    }
                }
            });
            try {
                tray.add(previousIcon);
            } catch (AWTException e) {
                getLogger().error(LogCategories.TRAY, e);
            }
        } else {
            getLogger().error(LogCategories.TRAY, "No system tray supported");
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
            if (trayIcon != null) {
                playMenu.setText(I18nUtils.getString("PAUSE"));
                playMenu.setIcon(ImageLoader.getImage(ImageLoader.PAUSE_TRAY_MENU));
            }
            if (playIcon != null) {
                playIcon.setImage(ImageLoader.getImage(ImageLoader.PAUSE_TRAY).getImage());
            }
        } else {
            if (trayIcon != null) {
                playMenu.setText(I18nUtils.getString("PLAY"));
                playMenu.setIcon(ImageLoader.getImage(ImageLoader.PLAY_TRAY_MENU));
            }
            if (playIcon != null) {
                playIcon.setImage(ImageLoader.getImage(ImageLoader.PLAY_TRAY).getImage());
            }
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
                tray.remove(trayIcon);
                VisualHandler.getInstance().setFrameDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
                tray.remove(previousIcon);
                tray.remove(playIcon);
                tray.remove(stopIcon);
                tray.remove(nextIcon);
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
        if (trayIcon != null) {
            trayIcon.setToolTip(msg);
        }
    }

    /**
     * Tray icon advice.
     */
    private void trayIconAdvice() {
        // For some reason, in Linux systems display message causes Swing freeze
        if (SystemProperties.OS != OperatingSystem.LINUX) {
            trayIcon.displayMessage(Constants.APP_NAME, I18nUtils.getString("TRAY_ICON_MESSAGE"), TrayIcon.MessageType.INFO);
        }
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
        setTrayIconVisible(newState.isShowSystemTray());
        setTrayPlayerVisible(newState.isShowTrayPlayer());
    }
}
