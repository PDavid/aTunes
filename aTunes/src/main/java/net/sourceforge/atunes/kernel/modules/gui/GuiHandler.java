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

package net.sourceforge.atunes.kernel.modules.gui;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Window;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.frame.FrameState;
import net.sourceforge.atunes.gui.popup.FadingPopupFactory;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable.PlayState;
import net.sourceforge.atunes.gui.views.dialogs.AboutDialog;
import net.sourceforge.atunes.gui.views.dialogs.AddArtistDragDialog;
import net.sourceforge.atunes.gui.views.dialogs.AddPodcastFeedDialog;
import net.sourceforge.atunes.gui.views.dialogs.IndeterminateProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.InputDialog;
import net.sourceforge.atunes.gui.views.dialogs.RadioDialog;
import net.sourceforge.atunes.gui.views.dialogs.RepositoryProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.ReviewImportDialog;
import net.sourceforge.atunes.gui.views.dialogs.SearchDialog;
import net.sourceforge.atunes.gui.views.dialogs.properties.PropertiesDialog;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.PlaybackState;
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayList;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListLocalAudioObjectFilter;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListPodcastFeedEntryFilter;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListRadioFilter;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.state.beans.LocaleBean;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFrameState;
import net.sourceforge.atunes.model.IFullScreenHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ISystemTrayHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

public final class GuiHandler extends AbstractHandler {

 
    private static GuiHandler instance = new GuiHandler();

    private SearchDialog searchDialog;
    private IndeterminateProgressDialog indeterminateProgressDialog;
    private AboutDialog aboutDialog;
    private ReviewImportDialog reviewImportDialog;

    /**
     * Instantiates a new gui handler.
     */
    private GuiHandler() {
    }

    @Override
    protected void initHandler() {
    }

    /**
     * Gets the single instance of this class.
     * 
     * @return single instance of this class
     */
    public static GuiHandler getInstance() {
        return instance;
    }

    @Override
    public void applicationStarted(List<IAudioObject> playList) {
        getFrame().setVisible(true);
    	
    	IState state = getState();
    	IFrameState frameState = state.getFrameState(getFrame().getClass());
    	getFrame().applicationStarted(frameState);
    	
        showStatusBar(state.isShowStatusBar(), false);
        showContextPanel(state.isUseContext());
        
        if (!getState().isShowSystemTray() && getOsManager().isClosingMainWindowClosesApplication()) {
        	getFrame().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        }        
    }

    /**
     * Finish.
     * 
     * NOTE: This method is called using reflection from MACOSXAdapter. Refactoring will break code!
     */
    public void finish() {
        if (!getState().isShowSystemTray() && getOsManager().isClosingMainWindowClosesApplication()) {
            Kernel.finish();
        }
    }

    /**
     * Creates a new indeterminate dialog
     * 
     * @return the indeterminate progress dialog
     */
    private IndeterminateProgressDialog getNewIndeterminateProgressDialog(Window parent) {
        indeterminateProgressDialog = new IndeterminateProgressDialog(parent != null ? parent : getFrame().getFrame());
        return indeterminateProgressDialog;
    }

    /**
     * Gets indeterminate dialog
     * 
     * @return the indeterminate progress dialog
     */
    private IndeterminateProgressDialog getIndeterminateProgressDialog() {
        return indeterminateProgressDialog;
    }

    /**
     * Gets a progress dialog for repository load
     * 
     * @return the progress dialog
     */
    public RepositoryProgressDialog getProgressDialog() {
        return new RepositoryProgressDialog(getFrame().getFrame());
    }

    /**
     * Gets the review import dialog.
     * 
     * @return the review import dialog
     */
    public ReviewImportDialog getReviewImportDialog() {
        if (reviewImportDialog == null) {
            reviewImportDialog = new ReviewImportDialog(getFrame().getFrame(), getState());
        }
        return reviewImportDialog;
    }

    /**
     * Gets the search dialog.
     * 
     * @return the search dialog
     */
    public SearchDialog getSearchDialog() {
        if (searchDialog == null) {
            searchDialog = new SearchDialog(getFrame().getFrame());
        }
        return searchDialog;
    }

    /**
     * Hide indeterminate progress dialog.
     */
    public void hideIndeterminateProgressDialog() {
    	if (getIndeterminateProgressDialog() != null) {
    		getIndeterminateProgressDialog().setVisible(false);
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

    /**
     * Convenience method, called from MacOSXAdapter
     */
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
        Context.getBean(IFullScreenHandler.class).setPlaying(playing);
        Context.getBean(ISystemTrayHandler.class).setPlaying(playing);
    }

    /**
     * Sets title bar text, adding app name and version.
     * 
     * @param text
     *            the text
     */
    public void setTitleBar(String text) {
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
        Context.getBean(ISystemTrayHandler.class).setTrayToolTip(result);
    }

    /**
     * Show about dialog.
     * 
     * NOTE: This method is called using reflection from MACOSXAdapter. Refactoring will break code!
     */
    public void showAboutDialog() {
        if (aboutDialog == null) {
            aboutDialog = new AboutDialog(getFrame().getFrame());
        }
        aboutDialog.setVisible(true);
    }

    /**
     * Show add podcast feed dialog.
     * 
     * @return the podcast feed
     */
    public PodcastFeed showAddPodcastFeedDialog() {
        AddPodcastFeedDialog dialog = new AddPodcastFeedDialog(getFrame().getFrame());
        dialog.setVisible(true);
        return dialog.getPodcastFeed();
    }
    
    public void showAddArtistDragDialog(Artist currentArtist){
    	AddArtistDragDialog dialog = new AddArtistDragDialog(getFrame().getFrame(),currentArtist);
    	dialog.setVisible(true);
    	
    }

    /**
     * Show add radio dialog.
     * 
     * @return the radio
     */
    public Radio showAddRadioDialog() {
        RadioDialog dialog = new RadioDialog(getFrame().getFrame());
        dialog.setVisible(true);
        return dialog.getRadio();
    }

    /**
     * Show  radio dialog for edition
     * 
     * @param radio to edit
     * 
     * @return the radio
     */
    public Radio showEditRadioDialog(Radio radio) {
        RadioDialog dialog = new RadioDialog(getFrame().getFrame(), radio);
        dialog.setVisible(true);
        return dialog.getRadio();
    }

    /**
     * Show context information panel.
     * 
     * @param show
     *            the show
     */
    public void showContextPanel(boolean show) {
        getState().setUseContext(show);
        getFrame().showContextPanel(show);
        if (show) {
            ContextHandler.getInstance().retrieveInfoAndShowInPanel(PlayListHandler.getInstance().getCurrentAudioObjectFromVisiblePlayList());
        }
    }

    /**
     * Show confirmation dialog.
     * 
     * @param message
     *            the message
     * @param title
     *            the title
     * 
     * @return the int
     */
    public int showConfirmationDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(getFrame().getFrame(), message, title, JOptionPane.YES_NO_OPTION);
    }

    /**
     * Show confirmation dialog.
     * 
     * @param message
     *            the message
     * @return the int
     */
    public int showConfirmationDialog(String message) {
        return showConfirmationDialog(message, I18nUtils.getString("CONFIRMATION"));
    }

    /**
     * Show error dialog.
     * 
     * @param message
     *            the message
     */
    public void showErrorDialog(final String message) {
    	if (SwingUtilities.isEventDispatchThread()) {
    		JOptionPane.showMessageDialog(getFrame().getFrame(), message, I18nUtils.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
    	} else {
    		SwingUtilities.invokeLater(new Runnable() {
    			@Override
    			public void run() {
    				JOptionPane.showMessageDialog(getFrame().getFrame(), message, I18nUtils.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
    			}
    		});
    	}
    }

    /**
     * Show error dialog.
     * 
     * @param message
     *            the message
     * @param parent
     *            the parent
     */
    public void showErrorDialog(String message, Component parent) {
        JOptionPane.showMessageDialog(parent, message, I18nUtils.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a exception report dialog
     * 
     * @param message
     * @param t
     */
    public void showExceptionDialog(String message, Exception t) {
        showExceptionDialog(I18nUtils.getString("ERROR"), message, t);
    }

    /**
     * Shows a exception report dialog
     * 
     * @param title
     * @param message
     * @param t
     */
    public void showExceptionDialog(String title, String message, Exception t) {
        JXErrorPane pane = new JXErrorPane();
        StringBuilder sb = new StringBuilder();
        sb.append(t.getClass().getName());
        sb.append(": ");
        sb.append(t.getMessage());
        sb.append("<br/>");
        sb.append("<br/>");
        for (StackTraceElement s : t.getStackTrace()) {
            sb.append(s.toString());
            sb.append("<br/>");
        }
        pane.setErrorInfo(new ErrorInfo(title, message, sb.toString(), null, t, Level.SEVERE, null));
        JXErrorPane.showDialog(null, pane);

    }

    /**
     * Show indeterminate progress dialog.
     * 
     * @param text
     *            the text
     */
    public void showIndeterminateProgressDialog(final String text) {
        try {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                	getNewIndeterminateProgressDialog(null);
                    getIndeterminateProgressDialog().setTitle(text);
                    getIndeterminateProgressDialog().setVisible(true);
                }
            });
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    /**
     * Show indeterminate progress dialog.
     * 
     * @param text
     *            the text
     */
    public void showIndeterminateProgressDialog(final Window parent, final String text) {
    	getNewIndeterminateProgressDialog(parent);
    	getIndeterminateProgressDialog().setTitle(text);
    	getIndeterminateProgressDialog().setVisible(true);
    }

    /**
     * Show input dialog.
     * 
     * @param title
     *            the title
     * @param text
     *            the text
     * 
     * @return the string
     */
    public String showInputDialog(String title, String text) {
        return showInputDialog(title, text, null);
    }

    /**
     * Show input dialog.
     * 
     * @param title
     *            the title
     * @param text
     *            the text
     * @param icon
     *            the icon
     * 
     * @return the string
     */
    public String showInputDialog(String title, String text, Image icon) {
        InputDialog id = new InputDialog(getFrame().getFrame(), title, icon);
        id.show(text);
        return id.getResult();
    }

    /**
     * Show message for principal frame
     * 
     * @param message
     *            the message
     */
    public void showMessage(String message) {
        showMessage(message, getFrame().getFrame());
    }
    
    /**
     * Show message
     * 
     * @param message
     *            the message
     */
    public void showMessage(String message, Component owner) {
        JOptionPane.showMessageDialog(owner, message);
    }
    
    /**
     * Shows a custom message dialog.
     * 
     * @param message
     *            the message
     * @param title
     *            the title
     * @param messageType
     *            the JOptionPane integer constant which determines type of
     *            message
     * @param options
     *            array of objects to be shown on buttons
     */
    public Object showMessage(String message, String title, int messageType, Object[] options) {
        JOptionPane pane = new JOptionPane(message, messageType, JOptionPane.OK_CANCEL_OPTION, null, options);
        JDialog dialog = pane.createDialog(getFrame().getFrame(), title);
        dialog.setLocationRelativeTo(getFrame().getFrame());
        dialog.setVisible(true);
        return pane.getValue();
    }

    /**
     * Show open dialog.
     * 
     * @param fileChooser
     *            the file chooser
     * @param filter
     *            the filter
     * 
     * @return the int
     */
    public int showOpenDialog(JFileChooser fileChooser, FileFilter filter) {
        fileChooser.setFileFilter(filter);
        return fileChooser.showOpenDialog(getFrame().getFrame());
    }

    /**
     * Show play list information.
     * 
     * @param playList
     *            the play list
     */
    public void showPlayListInformation(PlayList playList) {
        int audioFiles = new PlayListLocalAudioObjectFilter().getObjects(playList).size();
        int radios = new PlayListRadioFilter().getObjects(playList).size();
        int podcastFeedEntries = new PlayListPodcastFeedEntryFilter().getObjects(playList).size();
        int audioObjects = playList.size();

        Object[] strs = new Object[20];
        strs[0] = I18nUtils.getString("PLAYLIST");
        strs[1] = ": ";
        strs[2] = audioObjects;
        strs[3] = " ";
        strs[4] = I18nUtils.getString("SONGS");
        strs[5] = " (";
        strs[6] = playList.getLength();
        strs[7] = ") ";
        strs[8] = " - ";
        strs[9] = audioFiles;
        strs[10] = " ";
        strs[11] = I18nUtils.getString("SONGS");
        strs[12] = " / ";
        strs[13] = radios;
        strs[14] = " ";
        strs[15] = I18nUtils.getString("RADIOS");
        strs[16] = " / ";
        strs[17] = podcastFeedEntries;
        strs[18] = " ";
        // Check if differenciation is required (needed by some slavic languages)
        if (I18nUtils.getString("PODCAST_ENTRIES_COUNTER").isEmpty()) {
            strs[19] = I18nUtils.getString("PODCAST_ENTRIES");
        } else {
            strs[19] = I18nUtils.getString("PODCAST_ENTRIES_COUNTER");
        }

        Object[] strs2 = new Object[9];
        strs2[0] = I18nUtils.getString("PLAYLIST");
        strs2[1] = ": ";
        strs2[2] = audioObjects;
        strs2[3] = " - ";
        strs2[4] = audioFiles;
        strs2[5] = " / ";
        strs2[6] = radios;
        strs2[7] = " / ";
        strs2[8] = podcastFeedEntries;

        String toolTip = StringUtils.getString(strs);
        String text = StringUtils.getString(strs2);
        getFrame().setRightStatusBarText(text, toolTip);
    }

    /**
     * Show properties dialog.
     * 
     * @param audioObject
     *            the audio object
     */
    public void showPropertiesDialog(IAudioObject audioObject) {
        PropertiesDialog dialog = PropertiesDialog.newInstance(audioObject, getFrame().getFrame(), getState(), getFrame(), getOsManager());
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
    }

    //TODO RTL component orientation
    /**
     * Show repository song number.
     * 
     * @param size
     *            the size
     * @param sizeInBytes
     *            the size in bytes
     * @param duration
     *            the duration
     */

    public void showRepositoryAudioFileNumber(long size, long sizeInBytes, long duration) {
        // Check if differenciation is required (needed by some slavic languages)
        if (I18nUtils.getString("SONGS_IN_REPOSITORY").isEmpty()) {
            String text = StringUtils.getString(I18nUtils.getString("REPOSITORY"), ": ", size, " ", I18nUtils.getString("SONGS"));
            String toolTip = StringUtils.getString(I18nUtils.getString("REPOSITORY"), ": ", size, " ", I18nUtils.getString("SONGS"), " - ", StringUtils
                    .fromByteToMegaOrGiga(sizeInBytes), " - ", StringUtils.fromSecondsToHoursAndDays(duration));
            getFrame().setCenterStatusBarText(text, toolTip);
        } else {
            String text = StringUtils.getString(I18nUtils.getString("REPOSITORY"), ": ", size, " ", I18nUtils.getString("SONGS_IN_REPOSITORY"));
            String toolTip = StringUtils.getString(I18nUtils.getString("REPOSITORY"), ": ", size, " ", I18nUtils.getString("SONGS_IN_REPOSITORY"), " - ", StringUtils
                    .fromByteToMegaOrGiga(sizeInBytes), " - ", StringUtils.fromSecondsToHoursAndDays(duration));
            getFrame().setCenterStatusBarText(text, toolTip);
        }
    }

    /**
     * Show save dialog.
     * 
     * @param fileChooser
     *            the file chooser
     * @param filter
     *            the filter
     * 
     * @return the int
     */
    public int showSaveDialog(JFileChooser fileChooser, FileFilter filter) {
        fileChooser.setFileFilter(filter);
        return fileChooser.showSaveDialog(getFrame().getFrame());
    }

    /**
     * Show status bar.
     * 
     * @param show
     * @param save
     */
    public void showStatusBar(boolean show, boolean save) {
    	if (save) {
    		getState().setShowStatusBar(show);
    	}
        getFrame().showStatusBar(show);
        repaint();
    }

    /**
     * Sets progress bar visible and with given tooltip
     * 
     * @param indeterminate
     * @param text
     */
    public void showProgressBar(boolean indeterminate, String text) {
        if (getFrame().getProgressBar() != null) {
            getFrame().getProgressBar().setVisible(true);
            getFrame().getProgressBar().setIndeterminate(indeterminate);
            if (!indeterminate) {
                getFrame().getProgressBar().setMinimum(0);
                getFrame().getProgressBar().setValue(0);
            }
            getFrame().getProgressBar().setToolTipText(text);
        }
    }

    /**
     * Hides progress bar
     */
    public void hideProgressBar() {
        if (getFrame().getProgressBar() != null) {
            getFrame().getProgressBar().setVisible(false);
        }
    }

    /**
     * Start visualization.
     */
    public void startVisualization() {
        Logger.debug("Starting visualization");

        if (SystemProperties.IS_JAVA_6_UPDATE_10_OR_LATER) {
            FadingPopupFactory.install(getOsManager());
        }

		getFrame().setState(getState());
		getFrame().setOsManager(getOsManager());
        
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

        Logger.debug("Start visualization done");
    }

    /**
     * Toggle window visibility.
     */
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

    /**
     * Update title bar.
     * 
     * @param song
     *            the song
     */
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
            getFrame().updateStatusBarWithObjectBeingPlayed(PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList());
            updateTitleBar(PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList());
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
        // Show or hide context panel
        showContextPanel(newState.isUseContext());

        // Once done graphic changes, repaint the window
        repaint();
    }
}
