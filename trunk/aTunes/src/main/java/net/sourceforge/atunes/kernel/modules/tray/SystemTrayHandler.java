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

package net.sourceforge.atunes.kernel.modules.tray;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayerTrayIconsHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ISystemTrayHandler;
import net.sourceforge.atunes.model.ITrayIcon;
import net.sourceforge.atunes.model.PlaybackState;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * The system tray handler.
 */
public final class SystemTrayHandler extends AbstractHandler implements ISystemTrayHandler {

    private boolean trayInitialized;
    private boolean trayIconVisible;
    private boolean trayPlayerVisible;
    private SystemTray tray;
    private TrayIcon trayIcon;
    
    private TrayIcon previousIcon;
    private TrayIcon playIcon;
    private TrayIcon stopIcon;
    private TrayIcon nextIcon;
    
    private boolean playing;
    
    private ITrayIcon customTrayIcon;
    
    private PlayerTrayIconsBuilder playerTrayIconsBuilder;
    
    private IPlayerTrayIconsHandler iconsHandler;
    
    /**
     * @param playerTrayIconsBuilder
     */
    public void setPlayerTrayIconsBuilder(PlayerTrayIconsBuilder playerTrayIconsBuilder) {
		this.playerTrayIconsBuilder = playerTrayIconsBuilder;
	}
    
    @Override
    public void allHandlersInitialized() {
    	if (getOsManager().areTrayIconsSupported()) {
    		iconsHandler = getOsManager().getPlayerTrayIcons();
    		customTrayIcon = getOsManager().getTrayIcon();
    		// System tray player
    		if (getState().isShowTrayPlayer()) {
    			initTrayPlayerIcons();
    		}

    		// System tray
    		if (getState().isShowSystemTray()) {
    			initTrayIcon();
    		}
    	}    	
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
    private void initTrayIcon() {
        initSystemTray();
        if (isTrayInitialized()) {
            trayIconVisible = true;
            addTrayIcon(getTrayIcon());
            getFrame().setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        } else {
            Logger.error("No system tray supported");
        }
    }

    /**
     * Inits the tray player icons.
     */
    private void initTrayPlayerIcons() {
        initSystemTray();
        if (isTrayInitialized()) {
            trayPlayerVisible = true;
            // Icons must be added in reverse order
            addTrayIcon(getNextTrayIcon());
            addTrayIcon(getPlayTrayIcon());
            addTrayIcon(getStopTrayIcon());
            addTrayIcon(getPreviousTrayIcon());
        } else {
            Logger.error("No system tray supported");
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
            Logger.error(e);
        }
    }

    @Override
	public void setPlaying(boolean playing) {
    	this.playing = playing;
    	if (isTrayInitialized()) {
        	Image icon = null;
            if (playing) {
                customTrayIcon.setPlayMenuItemText(I18nUtils.getString("PAUSE"));
            	icon = iconsHandler.getPauseIcon(tray.getTrayIconSize());
            } else {
                customTrayIcon.setPlayMenuItemText(I18nUtils.getString("PLAY"));
            	icon = iconsHandler.getPlayIcon(tray.getTrayIconSize());
            }
            getPlayTrayIcon().setImage(icon);
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
            if (isTrayInitialized()) {
                trayIconAdvice();
            }
        } else {
            if (!visible && trayIconVisible && isTrayInitialized()) {
                tray.remove(getTrayIcon());
                getFrame().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
            if (!visible && trayPlayerVisible && isTrayInitialized()) {
                tray.remove(getPreviousTrayIcon());
                tray.remove(getPlayTrayIcon());
                tray.remove(getStopTrayIcon());
                tray.remove(getNextTrayIcon());
                trayPlayerVisible = false;
            }
        }
    }

	private void setTrayToolTip(String msg) {
    	if (isTrayInitialized()) {
    		getTrayIcon().setToolTip(msg);
    	}
    }

    /**
     * Tray icon advice.
     */
    private void trayIconAdvice() {
        // For some reason, in Linux systems display message causes Swing freeze
        if (!getOsManager().isLinux() && isTrayInitialized()) {
            getTrayIcon().displayMessage(Constants.APP_NAME, I18nUtils.getString("TRAY_ICON_MESSAGE"), TrayIcon.MessageType.INFO);
        }
    }

    @Override
    public void applicationStateChanged(IState newState) {
        setTrayIconVisible(newState.isShowSystemTray());
        setTrayPlayerVisible(newState.isShowTrayPlayer());
        updateTrayPlayerIconsColor();
    }

    /**
     * Changes color of tray player icons
     */
    private void updateTrayPlayerIconsColor() {
    	if (trayPlayerVisible) {
    		getStopTrayIcon().setImage(iconsHandler.getStopIcon(tray.getTrayIconSize()));
    		if (playing) {
    			getPlayTrayIcon().setImage(iconsHandler.getPauseIcon(tray.getTrayIconSize()));
    		} else {
    			getPlayTrayIcon().setImage(iconsHandler.getPlayIcon(tray.getTrayIconSize()));
    		}
    		getNextTrayIcon().setImage(iconsHandler.getNextIcon(tray.getTrayIconSize()));
    		getPreviousTrayIcon().setImage(iconsHandler.getPreviousIcon(tray.getTrayIconSize()));
    	}
	}

    /**
     * Getter for trayIcon
     * 
     * @return
     */
    private TrayIcon getTrayIcon() {
        if (trayIcon == null) {
        	trayIcon = customTrayIcon.getTrayIcon(Images.getImage(Images.APP_LOGO_32).getImage(), tray.getTrayIconSize().width);
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
        	nextIcon = playerTrayIconsBuilder.getNextTrayIcon(tray.getTrayIconSize());
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
        	stopIcon = playerTrayIconsBuilder.getStopTrayIcon(tray.getTrayIconSize());
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
        	playIcon = playerTrayIconsBuilder.getPlayTrayIcon(tray.getTrayIconSize());
        }
        return playIcon;
    }
    
    /**
     * Getter for previousIcon
     * 
     * @return
     */
    private TrayIcon getPreviousTrayIcon() {
    	if (previousIcon == null) {
    		previousIcon = playerTrayIconsBuilder.getPreviousTrayIcon(tray.getTrayIconSize());
    	}
        return previousIcon;
    }

	/**
	 * @return the trayInitialized
	 */
	protected boolean isTrayInitialized() {
		return trayInitialized;
	}
	
	@Override
	public void playbackStateChanged(final PlaybackState newState, final IAudioObject currentAudioObject) {
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    playbackStateChangedEDT(newState, currentAudioObject);
                }
            });
        } else {
            playbackStateChangedEDT(newState, currentAudioObject);
        }
	}
	
    private void playbackStateChangedEDT(PlaybackState newState, IAudioObject currentAudioObject) {
    	String text = currentAudioObject != null ? currentAudioObject.getAudioObjectDescription() : "";
        StringBuilder strBuilder = new StringBuilder();
        if (!text.equals("")) {
            strBuilder.append(text);
            strBuilder.append(" - ");
        }
        strBuilder.append(Constants.APP_NAME);
        strBuilder.append(" ");
        strBuilder.append(Constants.VERSION.toShortString());
        setTrayToolTip(strBuilder.toString());
    }
}
