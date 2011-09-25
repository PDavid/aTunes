/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.ui;

import java.awt.ComponentOrientation;
import java.awt.EventQueue;
import java.awt.Window;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.frame.FrameState;
import net.sourceforge.atunes.gui.popup.FadingPopupFactory;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.state.beans.LocaleBean;
import net.sourceforge.atunes.model.IAboutDialog;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFrameState;
import net.sourceforge.atunes.model.IFullScreenHandler;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ISystemTrayHandler;
import net.sourceforge.atunes.model.IUIHandler;
import net.sourceforge.atunes.model.PlayState;
import net.sourceforge.atunes.model.PlaybackState;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.JVMProperties;
import net.sourceforge.atunes.utils.Logger;

public final class UIHandler extends AbstractHandler implements IUIHandler {

 
    @Override
    public void applicationStarted(List<IAudioObject> playList) {
        getFrame().setVisible(true);
    	
    	IState state = getState();
    	IFrameState frameState = state.getFrameState(getFrame().getClass());
    	getFrame().applicationStarted(frameState);
    	
        showStatusBar(state.isShowStatusBar(), false);
        
        if (!getState().isShowSystemTray() && getOsManager().isClosingMainWindowClosesApplication()) {
        	getFrame().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        }        
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.ui.IUIHandler#finish()
	 */
    @Override
	public void finish() {
        if (!getState().isShowSystemTray() && getOsManager().isClosingMainWindowClosesApplication()) {
            Kernel.finish();
        }
    }

    @Override
    public void applicationFinish() {
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            window.setVisible(false);
        }
    }

    /**
     * Repaint.
     */
    private void repaint() {
        getFrame().getFrame().invalidate();
        getFrame().getFrame().validate();
        getFrame().getFrame().repaint();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.ui.IUIHandler#showFullFrame()
	 */
    @Override
	public void showFullFrame() {
    	getFrame().setVisible(true);
    }

    /**
     * Sets the playing.
     * 
     * @param playing
     *            the new playing
     */
    private void setPlaying(boolean playing) {
        PlayerHandler.getInstance().setPlaying(playing);
        getBean(IFullScreenHandler.class).setPlaying(playing);
        getBean(ISystemTrayHandler.class).setPlaying(playing);
    }

    /**
     * Sets title bar text, adding app name and version.
     * 
     * @param text
     *            the text
     */
    private void setTitleBar(String text) {
        StringBuilder strBuilder = new StringBuilder();
        if (!text.equals("")) {
            strBuilder.append(text);
            strBuilder.append(" - ");
        }
        strBuilder.append(Constants.APP_NAME);
        strBuilder.append(" ");
        strBuilder.append(Constants.VERSION.toShortString());

        String result = strBuilder.toString();

        getFrame().setTitle(result);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.ui.IUIHandler#showAboutDialog()
	 */
    @Override
	public void showAboutDialog() {
    	getBean(IAboutDialog.class).showDialog();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.ui.IUIHandler#showStatusBar(boolean, boolean)
	 */
    @Override
	public void showStatusBar(boolean show, boolean save) {
    	if (save) {
    		getState().setShowStatusBar(show);
    	}
        getFrame().showStatusBar(show);
        repaint();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.ui.IUIHandler#startVisualization()
	 */
    @Override
	public void startVisualization() {
        Logger.debug("Starting visualization");

        if (new JVMProperties().isJava6Update10OrLater()) {
            FadingPopupFactory.install(getOsManager(), getBean(ILookAndFeelManager.class).getCurrentLookAndFeel());
        }

		getFrame().setState(getState());
		getFrame().setOsManager(getOsManager());
		getFrame().setPlayListHandler(getBean(IPlayListHandler.class));
		getFrame().setNavigationHandler(getBean(INavigationHandler.class));
        
        IFrameState frameState = getState().getFrameState(getFrame().getClass());
        LocaleBean locale = getState().getLocale();
        LocaleBean oldLocale = getState().getOldLocale();
        // Reset fame state if no frame state in state or if component orientation of locale has changed
        if (frameState == null || locale == null || locale != null && oldLocale != null
                && !(ComponentOrientation.getOrientation(locale.getLocale()).equals(ComponentOrientation.getOrientation(oldLocale.getLocale())))) {
            frameState = new FrameState();
            getState().setFrameState(getFrame().getClass(), frameState);
        }
        getFrame().create(frameState);

        JProgressBar progressBar = getFrame().getProgressBar();
        if (progressBar != null) {
            progressBar.setVisible(false);
        }

        getFrame().showDeviceInfo(false);
        setTitleBar("");
        
        Logger.debug("Start visualization done");
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.ui.IUIHandler#toggleWindowVisibility()
	 */
    @Override
	public void toggleWindowVisibility() {
        getFrame().setVisible(!getFrame().isVisible());
        getFrame().getFrame().toFront();
        getFrame().getFrame().setState(java.awt.Frame.NORMAL);
    }

    /**
     * Update status bar.
     * 
     * @param text
     *            the text
     */
    private void updateStatusBar(String text) {
    	getFrame().setLeftStatusBarText(text, text);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.ui.IUIHandler#updateTitleBar(net.sourceforge.atunes.model.IAudioObject)
	 */
    @Override
	public void updateTitleBar(IAudioObject song) {
    	setTitleBar(song != null ? song.getAudioObjectDescription() : "");
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
        if (newState == PlaybackState.PAUSED) {
            // Pause
            setPlaying(false);
            updateStatusBar(I18nUtils.getString("PAUSED"));
            setTitleBar("");
            getFrame().getPlayListTable().setPlayState(PlayState.PAUSED);

        } else if (newState == PlaybackState.RESUMING) {
            // Resume
            setPlaying(true);
            getFrame().updateStatusBarWithObjectBeingPlayed(getBean(IPlayListHandler.class).getCurrentAudioObjectFromCurrentPlayList());
            updateTitleBar(getBean(IPlayListHandler.class).getCurrentAudioObjectFromCurrentPlayList());
            getFrame().getPlayListTable().setPlayState(PlayState.PLAYING);

        } else if (newState == PlaybackState.PLAYING) {
            // Playing
        	getFrame().updateStatusBarWithObjectBeingPlayed(currentAudioObject);
            updateTitleBar(currentAudioObject);
            setPlaying(true);
            getFrame().getPlayListTable().setPlayState(PlayState.PLAYING);

        } else if (newState == PlaybackState.STOPPED) {
            // Stop
            setPlaying(false);
            updateStatusBar(I18nUtils.getString("STOPPED"));
            setTitleBar("");
            getFrame().getPlayListTable().setPlayState(PlayState.STOPPED);
        }
    }

    @Override
    public void applicationStateChanged(IState newState) {
        // Once done graphic changes, repaint the window
        repaint();
    }
}
