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
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.gui.frame.DefaultSingleFrame;
import net.sourceforge.atunes.gui.frame.Frame;
import net.sourceforge.atunes.gui.frame.FrameState;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.popup.FadingPopupFactory;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable.PlayState;
import net.sourceforge.atunes.gui.views.dialogs.AboutDialog;
import net.sourceforge.atunes.gui.views.dialogs.AddPodcastFeedDialog;
import net.sourceforge.atunes.gui.views.dialogs.ColumnSetSelectorDialog;
import net.sourceforge.atunes.gui.views.dialogs.EqualizerDialog;
import net.sourceforge.atunes.gui.views.dialogs.ExportOptionsDialog;
import net.sourceforge.atunes.gui.views.dialogs.FileSelectionDialog;
import net.sourceforge.atunes.gui.views.dialogs.IndeterminateProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.InputDialog;
import net.sourceforge.atunes.gui.views.dialogs.MultiFolderSelectionDialog;
import net.sourceforge.atunes.gui.views.dialogs.ProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.RadioDialog;
import net.sourceforge.atunes.gui.views.dialogs.RepositoryProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.ReviewImportDialog;
import net.sourceforge.atunes.gui.views.dialogs.RipperProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.SearchDialog;
import net.sourceforge.atunes.gui.views.dialogs.SplashScreenDialog;
import net.sourceforge.atunes.gui.views.dialogs.TransferProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.UpdateDialog;
import net.sourceforge.atunes.gui.views.dialogs.properties.PropertiesDialog;
import net.sourceforge.atunes.gui.views.menus.ApplicationMenuBar;
import net.sourceforge.atunes.gui.views.panels.ContextPanel;
import net.sourceforge.atunes.gui.views.panels.NavigationTablePanel;
import net.sourceforge.atunes.gui.views.panels.NavigationTreePanel;
import net.sourceforge.atunes.gui.views.panels.PlayListPanel;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.PlaybackState;
import net.sourceforge.atunes.kernel.PlaybackStateListener;
import net.sourceforge.atunes.kernel.modules.cdripper.RipperHandler;
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.fullscreen.FullScreenHandler;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayList;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.beans.LocaleBean;
import net.sourceforge.atunes.kernel.modules.tray.SystemTrayHandler;
import net.sourceforge.atunes.kernel.modules.updates.ApplicationVersion;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.LocalAudioObject;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

public final class GuiHandler extends AbstractHandler implements PlaybackStateListener {

    private static class RipperCancelAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            RipperHandler.getInstance().cancelProcess();
        }
    }

    private static GuiHandler instance = new GuiHandler();

    private Frame frame;
    private ExportOptionsDialog exportDialog;
    private SearchDialog searchDialog;
    private RipperProgressDialog ripperProgressDialog;
    private IndeterminateProgressDialog indeterminateProgressDialog;
    private EqualizerDialog equalizerDialog;
    private AboutDialog aboutDialog;
    private SplashScreenDialog splashScreenDialog;
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
    public void applicationStarted(List<AudioObject> playList) {
        GuiHandler.getInstance().setFullFrameVisible(true);
        //Hide title dialog
        GuiHandler.getInstance().hideSplashScreen();

    	
    	ApplicationState state = ApplicationState.getInstance();
    	FrameState frameState = state.getFrameState(getFrame().getClass());
    	getFrame().applicationStarted(frameState);
    	
        showStatusBar(state.isShowStatusBar(), false);
        showContextPanel(state.isUseContext());
        
        if (!ApplicationState.getInstance().isShowSystemTray()) {
            GuiHandler.getInstance().setFrameDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        }        
    }

    /**
     * Finish.
     */
    public void finish() {
        if (!ApplicationState.getInstance().isShowSystemTray()) {
            Kernel.finish();
        }
    }

    /**
     * Gets the context information panel.
     * 
     * @return the context information panel
     */
    public ContextPanel getContextPanel() {
        return frame.getContextPanel();
    }

    /**
     * Gets the equalizer dialog.
     * 
     * @return the equalizer dialog
     */
    public EqualizerDialog getEqualizerDialog() {
        if (equalizerDialog == null) {
            equalizerDialog = new EqualizerDialog(frame.getFrame());
        }
        return equalizerDialog;
    }

    /**
     * Gets the export dialog.
     * 
     * @return the export dialog
     */
    public ExportOptionsDialog getExportDialog() {
        if (exportDialog == null) {
            exportDialog = new ExportOptionsDialog(frame.getFrame());
        }
        return exportDialog;
    }

    /**
     * Gets the file selection dialog.
     * 
     * @param dirOnly
     *            the dir only
     * 
     * @return the file selection dialog
     */
    public FileSelectionDialog getFileSelectionDialog(boolean dirOnly) {
        return new FileSelectionDialog(frame.getFrame(), dirOnly);
    }

    /**
     * Gets the frame.
     * 
     * @return the frame
     */
    public Frame getFrame() {
        if (frame == null) {
            Class<? extends Frame> clazz = ApplicationState.getInstance().getFrameClass();
            if (clazz != null) {
                try {
                    frame = clazz.newInstance();
                } catch (InstantiationException e) {
                    Logger.error(LogCategories.HANDLER, e);
                    constructDefaultFrame();
                } catch (IllegalAccessException e) {
                    Logger.error(LogCategories.HANDLER, e);
                    constructDefaultFrame();
                }
            } else {
                constructDefaultFrame();
            }
        }
        return frame;
    }

    private void constructDefaultFrame() {
        frame = new DefaultSingleFrame();
        ApplicationState.getInstance().setFrameClass(frame.getClass());
    }

    /**
     * Creates a new indeterminate dialog
     * 
     * @return the indeterminate progress dialog
     */
    private IndeterminateProgressDialog getNewIndeterminateProgressDialog(JFrame parent) {
        indeterminateProgressDialog = new IndeterminateProgressDialog(parent != null ? parent : frame.getFrame());
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
     * Gets the menu bar.
     * 
     * @return the menu bar
     */
    public ApplicationMenuBar getMenuBar() {
        return frame.getAppMenuBar();
    }

    /**
     * Gets the multi folder selection dialog.
     * 
     * @return the multi folder selection dialog
     */
    public MultiFolderSelectionDialog getMultiFolderSelectionDialog() {
        return new MultiFolderSelectionDialog(frame.getFrame());
    }

    /**
     * Gets the navigation tree panel.
     * 
     * @return the navigation tree panel
     */
    public NavigationTreePanel getNavigationTreePanel() {
        return frame.getNavigationTreePanel();
    }

    /**
     * Gets the navigation table panel.
     * 
     * @return the navigation table panel
     */
    public NavigationTablePanel getNavigationTablePanel() {
        return frame.getNavigationTablePanel();
    }

    /**
     * Gets a new progress dialog
     * 
     * @param title
     * @param owner
     * @return
     */
    public ProgressDialog getNewProgressDialog(String title, Component owner) {
        return new ProgressDialog(title, owner == null ? frame.getFrame() : owner);
    }

    /**
     * Gets a new transfer progress dialog
     * 
     * @param title
     * @param owner
     * @return
     */
    public TransferProgressDialog getNewTransferProgressDialog(String title, Component owner) {
        return new TransferProgressDialog(title, owner == null ? frame.getFrame() : owner);
    }

    /**
     * Gets the player controls.
     * 
     * @return the player controls
     */
    public PlayerControlsPanel getPlayerControls() {
        return frame.getPlayerControls();
    }

    /**
     * Gets the dialog to select columns
     * 
     * @return the play list column selector
     */
    public ColumnSetSelectorDialog getColumnSelector() {
        return new ColumnSetSelectorDialog(frame.getFrame());
    }

    /**
     * Gets the play list panel.
     * 
     * @return the play list panel
     */
    public PlayListPanel getPlayListPanel() {
        return frame.getPlayListPanel();
    }

    /**
     * Gets the play list table.
     * 
     * @return the play list table
     */
    public PlayListTable getPlayListTable() {
        return frame.getPlayListTable();
    }

    /**
     * Gets a progress dialog for repository load
     * 
     * @return the progress dialog
     */
    public RepositoryProgressDialog getProgressDialog() {
        return new RepositoryProgressDialog(frame.getFrame());
    }

    /**
     * Gets the review import dialog.
     * 
     * @return the review import dialog
     */
    public ReviewImportDialog getReviewImportDialog() {
        if (reviewImportDialog == null) {
            reviewImportDialog = new ReviewImportDialog(frame.getFrame());
        }
        return reviewImportDialog;
    }

    /**
     * Gets the ripper progress dialog.
     * 
     * @return the ripper progress dialog
     */
    public RipperProgressDialog getRipperProgressDialog() {
        if (ripperProgressDialog == null) {
            ripperProgressDialog = new RipperProgressDialog(frame.getFrame());
            ripperProgressDialog.addCancelAction(new RipperCancelAction());
        }
        return ripperProgressDialog;
    }

    /**
     * Gets the search dialog.
     * 
     * @return the search dialog
     */
    public SearchDialog getSearchDialog() {
        if (searchDialog == null) {
            searchDialog = new SearchDialog(frame.getFrame());
        }
        return searchDialog;
    }

    /**
     * Gets the window location.
     * 
     * @return the window location
     */
    public Point getWindowLocation() {
        return frame.getLocation();
    }

    /**
     * Gets the window size.
     * 
     * @return the window size
     */
    public Dimension getWindowSize() {
        return frame.getSize();
    }

    /**
     * Hide indeterminate progress dialog.
     */
    public void hideIndeterminateProgressDialog() {
    	if (getIndeterminateProgressDialog() != null) {
    		getIndeterminateProgressDialog().setVisible(false);
    	}
    }

    /**
     * Hide splash screen.
     */
    public void hideSplashScreen() {
        if (splashScreenDialog != null) {
            splashScreenDialog.setVisible(false);
            splashScreenDialog.dispose();
            splashScreenDialog = null;
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
     * Checks if is maximized.
     * 
     * @return true, if is maximized
     */
    public boolean isMaximized() {
        return frame.getExtendedState() == java.awt.Frame.MAXIMIZED_BOTH;
    }

    /**
     * Repaint.
     */
    private void repaint() {
        frame.getFrame().invalidate();
        frame.getFrame().validate();
        frame.getFrame().repaint();
    }

    /**
     * Sets the center status bar text.
     * 
     * @param text
     *            the text
     * @param toolTip
     *            the tool tip
     */
    public void setCenterStatusBarText(String text, String toolTip) {
        frame.setCenterStatusBarText(text, toolTip);
    }

    /**
     * Sets the frame default close operation.
     * 
     * @param op
     *            the new frame default close operation
     */
    public void setFrameDefaultCloseOperation(int op) {
        frame.setDefaultCloseOperation(op);
    }

    /**
     * Sets the full frame extended state.
     * 
     * @param state
     *            the new full frame extended state
     */
    public void setFullFrameExtendedState(int state) {
        frame.setExtendedState(state);
    }

    /**
     * Sets the full frame location.
     * 
     * @param location
     *            the new full frame location
     */
    public void setFullFrameLocation(Point location) {
        frame.setLocation(location);
    }

    /**
     * Sets the full frame location relative to.
     * 
     * @param c
     *            the new full frame location relative to
     */
    public void setFullFrameLocationRelativeTo(Component c) {
        frame.setLocationRelativeTo(c);
    }

    /**
     * Sets the full frame visible.
     * 
     * @param visible
     *            the new full frame visible
     */
    public void setFullFrameVisible(boolean visible) {
        frame.setVisible(visible);
    }

    /**
     * Sets the left status bar text.
     * 
     * @param text
     *            the text
     * @param toolTip
     *            the tool tip
     */
    public void setLeftStatusBarText(String text, String toolTip) {
        frame.setLeftStatusBarText(text, toolTip);
    }

    /**
     * Sets the playing.
     * 
     * @param playing
     *            the new playing
     */
    private void setPlaying(boolean playing) {
        PlayerHandler.getInstance().setPlaying(playing);
        FullScreenHandler.getInstance().setPlaying(playing);
        SystemTrayHandler.getInstance().setPlaying(playing);
    }

    /**
     * Sets the right status bar text.
     * 
     * @param text
     *            the text
     * @param toolTip
     *            the tool tip
     */
    public void setRightStatusBarText(String text, String toolTip) {
        frame.setRightStatusBarText(text, toolTip);
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

        frame.setTitle(result);
        SystemTrayHandler.getInstance().setTrayToolTip(result);
    }

    /**
     * Show about dialog.
     */
    public void showAboutDialog() {
        if (aboutDialog == null) {
            aboutDialog = new AboutDialog(frame.getFrame());
        }
        aboutDialog.setVisible(true);
    }

    /**
     * Show add podcast feed dialog.
     * 
     * @return the podcast feed
     */
    public PodcastFeed showAddPodcastFeedDialog() {
        AddPodcastFeedDialog dialog = new AddPodcastFeedDialog(frame.getFrame());
        dialog.setVisible(true);
        return dialog.getPodcastFeed();
    }

    /**
     * Show add radio dialog.
     * 
     * @return the radio
     */
    public Radio showAddRadioDialog() {
        RadioDialog dialog = new RadioDialog(frame.getFrame());
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
        RadioDialog dialog = new RadioDialog(frame.getFrame(), radio);
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
        ApplicationState.getInstance().setUseContext(show);
        frame.showContextPanel(show);
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
        return JOptionPane.showConfirmDialog(frame.getFrame(), message, title, JOptionPane.YES_NO_OPTION);
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
    		JOptionPane.showMessageDialog(frame.getFrame(), message, I18nUtils.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
    	} else {
    		SwingUtilities.invokeLater(new Runnable() {
    			@Override
    			public void run() {
    				JOptionPane.showMessageDialog(frame.getFrame(), message, I18nUtils.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
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
     * Show icon on status bar.
     * 
     * @param img
     *            the img
     * @param text
     *            the text
     */
    public void showDeviceInfoOnStatusBar(String text) {
        frame.setStatusBarDeviceLabelText(text);
        frame.showDeviceInfo(true);
    }

    public void hideDeviceInfoOnStatusBar() {
        frame.showDeviceInfo(false);
    }

    public void showNewPodcastFeedEntriesInfo() {
        if (!ApplicationState.getInstance().isShowStatusBar()) {
            showMessage(I18nUtils.getString("NEW_PODCAST_ENTRIES"));
        } else {
            frame.showNewPodcastFeedEntriesInfo(true);
        }
    }

    public void showNewVersionInfo(ApplicationVersion version, boolean alwaysInDialog) {
        if (alwaysInDialog || !ApplicationState.getInstance().isShowStatusBar()) {
            new UpdateDialog(version, frame.getFrame()).setVisible(true);
        } else {
            frame.showNewVersionInfo(true, version);
        }
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
            Logger.internalError(e);
        }
    }

    /**
     * Show indeterminate progress dialog.
     * 
     * @param text
     *            the text
     */
    public void showIndeterminateProgressDialog(final JFrame parent, final String text) {
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
        InputDialog id = new InputDialog(frame.getFrame(), title, icon);
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
        showMessage(message,frame.getFrame());
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
        JDialog dialog = pane.createDialog(frame.getFrame(), title);
        dialog.setLocationRelativeTo(frame.getFrame());
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
        return fileChooser.showOpenDialog(frame.getFrame());
    }

    /**
     * Show play list information.
     * 
     * @param playList
     *            the play list
     */
    public void showPlayListInformation(PlayList playList) {
        int audioFiles = playList.getObjectsOfType(AudioFile.class).size();
        int radios = playList.getObjectsOfType(Radio.class).size();
        int podcastFeedEntries = playList.getObjectsOfType(PodcastFeedEntry.class).size();
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
        setRightStatusBarText(text, toolTip);
    }

    /**
     * Returns progress bar
     * 
     * @return
     */
    public JProgressBar getProgressBar() {
        return frame.getProgressBar();
    }

    /**
     * Show properties dialog.
     * 
     * @param audioObject
     *            the audio object
     */
    public void showPropertiesDialog(AudioObject audioObject) {
        PropertiesDialog dialog = PropertiesDialog.newInstance(audioObject, frame.getFrame());
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
            setCenterStatusBarText(text, toolTip);
        } else {
            String text = StringUtils.getString(I18nUtils.getString("REPOSITORY"), ": ", size, " ", I18nUtils.getString("SONGS_IN_REPOSITORY"));
            String toolTip = StringUtils.getString(I18nUtils.getString("REPOSITORY"), ": ", size, " ", I18nUtils.getString("SONGS_IN_REPOSITORY"), " - ", StringUtils
                    .fromByteToMegaOrGiga(sizeInBytes), " - ", StringUtils.fromSecondsToHoursAndDays(duration));
            setCenterStatusBarText(text, toolTip);
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
        return fileChooser.showSaveDialog(frame.getFrame());
    }

    /**
     * Show splash screen.
     */
    public void showSplashScreen() {
        if (ApplicationState.getInstance().isShowTitle()) {
            JDialog.setDefaultLookAndFeelDecorated(false);
            splashScreenDialog = new SplashScreenDialog(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getAppVersionLittleFont(), ColorDefinitions.TITLE_DIALOG_FONT_COLOR);
            JDialog.setDefaultLookAndFeelDecorated(true);
            // For multiple screens
            if (GuiUtils.getNumberOfScreenDevices() > 1) {
            	// This method is called before starting visualization so maybe frame state is not yet created
            	FrameState state = ApplicationState.getInstance().getFrameState(getFrame().getClass());
                // Get screen where application is shown
                GraphicsDevice screen = state != null ? GuiUtils.getGraphicsDeviceForLocation(state.getXPosition(), state.getYPosition()) : null;
                if (screen != null) {
                	Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
                	splashScreenDialog.setLocation(screenBounds.width / 2 - splashScreenDialog.getWidth() / 2 + screenBounds.x, screenBounds.height / 2 - splashScreenDialog.getHeight() / 2 + screenBounds.y);
                } else {
                	splashScreenDialog.setLocation(GuiUtils.getDeviceWidth() / 2 - splashScreenDialog.getWidth() / 2, GuiUtils.getDeviceHeight() / 2 - splashScreenDialog.getHeight() / 2);
                }
            }
            splashScreenDialog.setVisible(true);
            splashScreenDialog.toFront();
        }
    }

    /**
     * Show status bar.
     * 
     * @param show
     * @param save
     */
    public void showStatusBar(boolean show, boolean save) {
    	if (save) {
    		ApplicationState.getInstance().setShowStatusBar(show);
    	}
        frame.showStatusBar(show);
        repaint();
    }

    /**
     * Sets progress bar visible and with given tooltip
     * 
     * @param indeterminate
     * @param text
     */
    public void showProgressBar(boolean indeterminate, String text) {
        if (frame.getProgressBar() != null) {
            frame.getProgressBar().setVisible(true);
            frame.getProgressBar().setIndeterminate(indeterminate);
            if (!indeterminate) {
                frame.getProgressBar().setMinimum(0);
                frame.getProgressBar().setValue(0);
            }
            frame.getProgressBar().setToolTipText(text);
        }
    }

    /**
     * Hides progress bar
     */
    public void hideProgressBar() {
        if (frame.getProgressBar() != null) {
            frame.getProgressBar().setVisible(false);
        }
    }

    /**
     * Start visualization.
     */
    public void startVisualization() {
        Logger.debug(LogCategories.START, "Starting visualization");

        if (SystemProperties.IS_JAVA_6_UPDATE_10_OR_LATER) {
            FadingPopupFactory.install();
        }

        FrameState frameState = ApplicationState.getInstance().getFrameState(getFrame().getClass());
        LocaleBean locale = ApplicationState.getInstance().getLocale();
        LocaleBean oldLocale = ApplicationState.getInstance().getOldLocale();
        // Reset fame state if no frame state in state or if component orientation of locale has changed
        if (frameState == null || locale == null || locale != null && oldLocale != null
                && !(ComponentOrientation.getOrientation(locale.getLocale()).equals(ComponentOrientation.getOrientation(oldLocale.getLocale())))) {
            frameState = new FrameState();
            ApplicationState.getInstance().setFrameState(getFrame().getClass(), frameState);
        }
        getFrame().create(frameState);

        JProgressBar progressBar = getProgressBar();
        if (progressBar != null) {
            progressBar.setVisible(false);
        }

        hideDeviceInfoOnStatusBar();

        Logger.debug(LogCategories.START, "Start visualization done");
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
     * @param song
     *            the song
     */
    public void updateStatusBar(AudioObject song) {
    	// TODO: Refactor this method
        if (!(song instanceof Radio || song instanceof PodcastFeedEntry)) {
            if (GuiUtils.getComponentOrientation().isLeftToRight()) {
                String text = StringUtils.getString(I18nUtils.getString("PLAYING"), ": ");
                if (song instanceof Radio || song instanceof PodcastFeedEntry || ((AudioFile) song).getTag() == null) {
                    text = StringUtils.getString(text, song.getTitleOrFileName());
                } else {
                    if (((AudioFile) song).getTag().getTitle() == null || ((AudioFile) song).getTag().getTitle().equals("")) {
                        text = StringUtils.getString(text, ((AudioFile) song).getFile().getName(), " - ");
                    } else {
                        text = StringUtils.getString(text, ((AudioFile) song).getTag().getTitle(), " - ");
                    }
                    if (((AudioFile) song).getTag().getArtist() == null || ((AudioFile) song).getTag().getArtist().equals("")) {
                        text = StringUtils.getString(text, Artist.getUnknownArtist(), " ");
                    } else {
                        text = StringUtils.getString(text, ((AudioFile) song).getTag().getArtist(), " ");
                    }
                    text = StringUtils.getString(text, "(", StringUtils.seconds2String(song.getDuration()), ")");
                }
                setLeftStatusBarText(text, text);
            } else {
                String text = "";
                if (song instanceof Radio || song instanceof PodcastFeedEntry || ((AudioFile) song).getTag() == null) {
                    text = StringUtils.getString(song.getTitleOrFileName());
                } else {
                    text = StringUtils.getString("(", StringUtils.seconds2String(song.getDuration()), ") ");
                    if (((AudioFile) song).getTag().getArtist() == null || ((AudioFile) song).getTag().getArtist().equals("")) {
                        text = StringUtils.getString(text, Artist.getUnknownArtist(), " ");
                    } else {
                        text = StringUtils.getString(text, ((AudioFile) song).getTag().getArtist(), " ");
                    }
                    if (((AudioFile) song).getTag().getTitle() == null || ((AudioFile) song).getTag().getTitle().equals("")) {
                        text = StringUtils.getString(text, " - ", ((AudioFile) song).getFile().getName());
                    } else {
                        text = StringUtils.getString(text, " - ", ((AudioFile) song).getTag().getTitle());
                    }
                }
                text = StringUtils.getString(text, " :", I18nUtils.getString("PLAYING"));
                setLeftStatusBarText(text, text);
            }
        } else {
        	if (song instanceof PodcastFeedEntry && ((PodcastFeedEntry)song).isDownloaded()) {
        		updateStatusBar(StringUtils.getString(I18nUtils.getString("PLAYING"), ": ", song.getTitle()));
        	} else {
        		updateStatusBar(StringUtils.getString(I18nUtils.getString("BUFFERING"), " ", song.getTitle(), "..."));
        	}
        }
    }

    /**
     * Update status bar.
     * 
     * @param text
     *            the text
     */
    private void updateStatusBar(String text) {
        setLeftStatusBarText(text, text);
    }

    /**
     * Update title bar.
     * 
     * @param song
     *            the song
     */
    public void updateTitleBar(AudioObject song) {
        if (song != null) {
            if (song instanceof Radio) {
                setTitleBar(StringUtils.getString(((Radio) song).getName(), " (", ((Radio) song).getUrl(), ")"));
                return;
            }

            if (song instanceof PodcastFeedEntry) {
                setTitleBar(StringUtils.getString(((PodcastFeedEntry) song).getTitle(), " (", ((PodcastFeedEntry) song).getUrl(), ")"));
                return;
            }

            StringBuilder strBuilder = new StringBuilder();
            if (song instanceof LocalAudioObject && ((AudioFile) song).getTag() == null) {
                strBuilder.append(((AudioFile) song).getFile().getName());
            } else {
                strBuilder.append(song.getTitleOrFileName());
                strBuilder.append(" - ");
                strBuilder.append(song.getArtist());
                strBuilder.append(" - ");
                strBuilder.append(song.getAlbum());
            }
            strBuilder.append(" (");
            strBuilder.append(StringUtils.seconds2String(song.getDuration()));
            strBuilder.append(")");
            setTitleBar(strBuilder.toString());
        } else {
            setTitleBar("");
        }
    }

    @Override
    public void playbackStateChanged(final PlaybackState newState, final AudioObject currentAudioObject) {
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

    private void playbackStateChangedEDT(PlaybackState newState, AudioObject currentAudioObject) {
        if (newState == PlaybackState.PAUSED) {
            // Pause
            setPlaying(false);
            updateStatusBar(I18nUtils.getString("PAUSED"));
            setTitleBar("");
            getPlayListTable().setPlayState(PlayState.PAUSED);

        } else if (newState == PlaybackState.RESUMING) {
            // Resume
            setPlaying(true);
            updateStatusBar(PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList());
            updateTitleBar(PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList());
            getPlayListTable().setPlayState(PlayState.PLAYING);

        } else if (newState == PlaybackState.PLAYING) {
            // Playing
            updateStatusBar(currentAudioObject);
            updateTitleBar(currentAudioObject);
            setPlaying(true);
            getPlayListTable().setPlayState(PlayState.PLAYING);

        } else if (newState == PlaybackState.STOPPED) {
            // Stop
            setPlaying(false);
            updateStatusBar(I18nUtils.getString("STOPPED"));
            setTitleBar("");
            getPlayListTable().setPlayState(PlayState.STOPPED);
        }
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
        // Show or hide context panel
        showContextPanel(newState.isUseContext());

        // Once done graphic changes, repaint the window
        repaint();
    }
    
	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(AudioObject audioObject) {}

	@Override
	public void deviceDisconnected(String location) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
		        hideDeviceInfoOnStatusBar();
		        showMessage(I18nUtils.getString("DEVICE_DISCONNECTION_DETECTED"));		        
			}
		});
	}
	
}
