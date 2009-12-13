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
package net.sourceforge.atunes.kernel.modules.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.frame.Frame;
import net.sourceforge.atunes.gui.frame.MultipleFrame;
import net.sourceforge.atunes.gui.frame.StandardFrame;
import net.sourceforge.atunes.gui.popup.FadingPopupFactory;
import net.sourceforge.atunes.gui.views.bars.ToolBar;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable.PlayState;
import net.sourceforge.atunes.gui.views.dialogs.AboutDialog;
import net.sourceforge.atunes.gui.views.dialogs.AddPodcastFeedDialog;
import net.sourceforge.atunes.gui.views.dialogs.AddRadioDialog;
import net.sourceforge.atunes.gui.views.dialogs.ColumnSetSelectorDialog;
import net.sourceforge.atunes.gui.views.dialogs.CoverNavigatorFrame;
import net.sourceforge.atunes.gui.views.dialogs.CustomSearchDialog;
import net.sourceforge.atunes.gui.views.dialogs.EditTagDialog;
import net.sourceforge.atunes.gui.views.dialogs.EditTitlesDialog;
import net.sourceforge.atunes.gui.views.dialogs.EqualizerDialog;
import net.sourceforge.atunes.gui.views.dialogs.ExportOptionsDialog;
import net.sourceforge.atunes.gui.views.dialogs.FileSelectionDialog;
import net.sourceforge.atunes.gui.views.dialogs.ImageDialog;
import net.sourceforge.atunes.gui.views.dialogs.IndeterminateProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.InputDialog;
import net.sourceforge.atunes.gui.views.dialogs.MultiFolderSelectionDialog;
import net.sourceforge.atunes.gui.views.dialogs.OSDDialog;
import net.sourceforge.atunes.gui.views.dialogs.ProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.RadioBrowserDialog;
import net.sourceforge.atunes.gui.views.dialogs.RepositoryProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.RepositorySelectionInfoDialog;
import net.sourceforge.atunes.gui.views.dialogs.ReviewImportDialog;
import net.sourceforge.atunes.gui.views.dialogs.RipCdDialog;
import net.sourceforge.atunes.gui.views.dialogs.RipperProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.SearchDialog;
import net.sourceforge.atunes.gui.views.dialogs.SearchResultsDialog;
import net.sourceforge.atunes.gui.views.dialogs.SplashScreenDialog;
import net.sourceforge.atunes.gui.views.dialogs.StatsDialog;
import net.sourceforge.atunes.gui.views.dialogs.TransferProgressDialog;
import net.sourceforge.atunes.gui.views.dialogs.UpdateDialog;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.EditPreferencesDialog;
import net.sourceforge.atunes.gui.views.dialogs.fullScreen.FullScreenWindow;
import net.sourceforge.atunes.gui.views.dialogs.properties.PropertiesDialog;
import net.sourceforge.atunes.gui.views.menus.ApplicationMenuBar;
import net.sourceforge.atunes.gui.views.panels.AudioObjectPropertiesPanel;
import net.sourceforge.atunes.gui.views.panels.ContextPanel;
import net.sourceforge.atunes.gui.views.panels.NavigationTablePanel;
import net.sourceforge.atunes.gui.views.panels.NavigationTreePanel;
import net.sourceforge.atunes.gui.views.panels.PlayListPanel;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Handler;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.ShowToolbarAction;
import net.sourceforge.atunes.kernel.controllers.coverNavigator.CoverNavigatorController;
import net.sourceforge.atunes.kernel.modules.cdripper.RipperHandler;
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.draganddrop.PlayListTableTransferHandler;
import net.sourceforge.atunes.kernel.modules.draganddrop.PlayListToDeviceDragAndDropListener;
import net.sourceforge.atunes.kernel.modules.player.PlaybackState;
import net.sourceforge.atunes.kernel.modules.player.PlaybackStateListener;
import net.sourceforge.atunes.kernel.modules.playlist.PlayList;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.tray.SystemTrayHandler;
import net.sourceforge.atunes.kernel.modules.updates.ApplicationVersion;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public final class GuiHandler extends Handler implements PlaybackStateListener {

    private static GuiHandler instance = new GuiHandler();

    Frame frame;
    private OSDDialog osdDialog;
    private EditTagDialog editTagDialog;
    private ExportOptionsDialog exportDialog;
    private StatsDialog statsDialog;
    private SearchDialog searchDialog;
    private RipCdDialog ripCdDialog;
    private RipperProgressDialog ripperProgressDialog;
    private IndeterminateProgressDialog indeterminateProgressDialog;
    private EditTitlesDialog editTitlesDialog;
    private EditPreferencesDialog editPreferencesDialog;
    private EqualizerDialog equalizerDialog;
    private AboutDialog aboutDialog;
    private SplashScreenDialog splashScreenDialog;
    private CustomSearchDialog customSearchDialog;
    private SearchResultsDialog searchResultsDialog;
    private RadioBrowserDialog radioBrowserDialog;
    private ReviewImportDialog reviewImportDialog;
    private FullScreenWindow fullScreenWindow;

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
    public void applicationStarted() {
    }

    /**
     * Finish.
     */
    public void finish() {
        if (!ApplicationState.getInstance().isShowSystemTray()) {
            Kernel.getInstance().finish();
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
     * Gets the edits the preferences dialog.
     * 
     * @return the edits the preferences dialog
     */
    public EditPreferencesDialog getEditPreferencesDialog() {
        if (editPreferencesDialog == null) {
            editPreferencesDialog = new EditPreferencesDialog(frame.getFrame());
        }
        return editPreferencesDialog;
    }

    /**
     * Gets the edits the tag dialog.
     * 
     * @return the edits the tag dialog
     */
    public EditTagDialog getEditTagDialog() {
        if (editTagDialog == null) {
            editTagDialog = new EditTagDialog(frame.getFrame());
        }
        return editTagDialog;
    }

    /**
     * Gets the edits the titles dialog.
     * 
     * @return the edits the titles dialog
     */
    public EditTitlesDialog getEditTitlesDialog() {
        if (editTitlesDialog == null) {
            editTitlesDialog = new EditTitlesDialog(frame.getFrame());
        }
        return editTitlesDialog;
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
            if (!ApplicationState.getInstance().isMultipleWindow()) {
                frame = new StandardFrame();
            } else {
                frame = new MultipleFrame();
            }
        }
        return frame;
    }

    /**
     * Gets the full screen frame.
     * 
     * @return the fullScreenFrame
     */
    public FullScreenWindow getFullScreenWindow() {
        if (fullScreenWindow == null) {
            JDialog.setDefaultLookAndFeelDecorated(false);
            fullScreenWindow = new FullScreenWindow(frame.getFrame());
            JDialog.setDefaultLookAndFeelDecorated(true);
        }
        return fullScreenWindow;
    }

    /**
     * Gets the indeterminate progress dialog.
     * 
     * @return the indeterminate progress dialog
     */
    IndeterminateProgressDialog getIndeterminateProgressDialog() {
        if (indeterminateProgressDialog == null) {
            indeterminateProgressDialog = new IndeterminateProgressDialog(frame.getFrame());
        }
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
     * Gets the oSD dialog.
     * 
     * @return the oSD dialog
     */
    public OSDDialog getOSDDialog() {
        if (osdDialog == null) {
            JDialog.setDefaultLookAndFeelDecorated(false);
            osdDialog = new OSDDialog(ApplicationState.getInstance().getOsdWidth());
            JDialog.setDefaultLookAndFeelDecorated(true);
        }
        return osdDialog;
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
     * Gets the properties panel.
     * 
     * @return the properties panel
     */
    public AudioObjectPropertiesPanel getPropertiesPanel() {
        return frame.getPropertiesPanel();
    }

    /**
     * Gets the radio browser dialog.
     * 
     * @return the radio browser dialog
     */
    public RadioBrowserDialog getRadioBrowserDialog() {
        if (radioBrowserDialog == null) {
            radioBrowserDialog = new RadioBrowserDialog(frame.getFrame());
        }
        return radioBrowserDialog;
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
     * Gets the rip cd dialog.
     * 
     * @return the rip cd dialog
     */
    public RipCdDialog getRipCdDialog() {
        if (ripCdDialog == null) {
            ripCdDialog = new RipCdDialog(frame.getFrame());
        }
        return ripCdDialog;
    }

    /**
     * Gets the ripper progress dialog.
     * 
     * @return the ripper progress dialog
     */
    public RipperProgressDialog getRipperProgressDialog() {
        if (ripperProgressDialog == null) {
            ripperProgressDialog = new RipperProgressDialog(frame.getFrame());
            ripperProgressDialog.addCancelAction(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    RipperHandler.getInstance().cancelProcess();
                }
            });
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
     * Gets the stats dialog.
     * 
     * @return the stats dialog
     */
    public StatsDialog getStatsDialog() {
        if (statsDialog == null) {
            statsDialog = new StatsDialog(frame.getFrame());
        }
        return statsDialog;
    }

    /**
     * Gets the tool bar.
     * 
     * @return the tool bar
     */
    public ToolBar getToolBar() {
        return frame.getToolBar();
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
        try {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    getIndeterminateProgressDialog().setVisible(false);
                }
            });
        } catch (Exception e) {
            getLogger().internalError(e);
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
     * Checks if is multiple window.
     * 
     * @return true, if is multiple window
     */
    public boolean isMultipleWindow() {
        return frame instanceof MultipleFrame;
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
     * Attribute to call setWindowSize of StandardFram on first call to
     * setFullFrameVisible This workaround fixes setExtendedState bug in Linux
     * systems:
     * 
     * See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6365898
     */
    private boolean firstTimeShow = true;

    /**
     * Sets the full frame visible.
     * 
     * @param visible
     *            the new full frame visible
     */
    public void setFullFrameVisible(boolean visible) {
        frame.setVisible(visible);

        // Workaround fot setExtendedState bug: setExtendedState must be called after
        // setVisible in Linux systems
        if (firstTimeShow && frame instanceof StandardFrame) {
            ((StandardFrame) frame).setWindowSize();
        }
        firstTimeShow = false;
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
        ControllerProxy.getInstance().getPlayerControlsController().setPlaying(playing);
        GuiHandler.getInstance().getFullScreenWindow().setPlaying(playing);
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
     * Gets the custom search dialog.
     * 
     * @return the custom search dialog
     */
    public CustomSearchDialog getCustomSearchDialog() {
        if (customSearchDialog == null) {
            customSearchDialog = new CustomSearchDialog(frame.getFrame());
        }
        return customSearchDialog;
    }

    /**
     * Gets the search results dialog.
     * 
     * @return the search results dialog
     */
    public SearchResultsDialog getSearchResultsDialog() {
        if (searchResultsDialog == null) {
            searchResultsDialog = new SearchResultsDialog(frame.getFrame());
        }
        return searchResultsDialog;
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
        AddRadioDialog dialog = new AddRadioDialog(frame.getFrame());
        dialog.setVisible(true);
        return dialog.getRadio();
    }

    /**
     * Show context information panel.
     * 
     * @param show
     *            the show
     * @param changeSize
     *            the change size
     */
    public void showContextPanel(boolean show, boolean changeSize) {
        ApplicationState.getInstance().setUseContext(show);
        frame.showContextPanel(show, changeSize);
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
     * Show cover navigator.
     */
    public void showCoverNavigator() {
        CoverNavigatorFrame coverNavigator = new CoverNavigatorFrame(RepositoryHandler.getInstance().getArtists(), frame.getFrame());
        new CoverNavigatorController(coverNavigator);
        coverNavigator.setVisible(true);
    }

    /**
     * Show error dialog.
     * 
     * @param message
     *            the message
     */
    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(frame.getFrame(), message, I18nUtils.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
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
     * Switches playlist filter on and off. It will just show (if hidden) or
     * hide (if visible) the filter each time called.
     * 
     */
    public void togglePlayListFiler() {
        if (frame.getPlayListPanel().getPlayListFilter().isVisible()) {
            frame.getPlayListPanel().getPlayListFilter().setVisible(false);
        } else {
            frame.getPlayListPanel().getPlayListFilter().setVisible(true);
        }
        // If show, then set focus to text field
        if (frame.getPlayListPanel().getPlayListFilter().isVisible()) {
            frame.getPlayListPanel().getPlayListFilter().setFocusToTextField();
        }
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
     * Show image dialog.
     * 
     * @param audioObject
     *            the audio object
     */
    public void showImageDialog(AudioObject audioObject) {
        new ImageDialog(frame.getFrame(), audioObject);
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
                    getIndeterminateProgressDialog().setTitle(text);
                    getIndeterminateProgressDialog().setVisible(true);
                }
            });
        } catch (Exception e) {
            getLogger().internalError(e);
        }
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
     * Show message.
     * 
     * @param message
     *            the message
     */
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(frame.getFrame(), message);
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
     * Show navigation table.
     * 
     * @param show
     *            the show
     */
    public void showNavigationTable(boolean show) {
        frame.showNavigationTable(show);
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

    /**
     * Show repository selection info dialog.
     */
    public void showRepositorySelectionInfoDialog() {
        new RepositorySelectionInfoDialog(frame.getFrame()).setVisible(true);
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
     * Show song properties.
     * 
     * @param show
     *            the show
     * @param update
     *            the update
     */
    public void showSongProperties(boolean show, boolean update) {
        ApplicationState.getInstance().setShowAudioObjectProperties(show);
        frame.showSongProperties(show);
        if (show && update) {
            if (PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList() != null) {
                ControllerProxy.getInstance().getFilePropertiesController().updateValues(PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList());
            } else {
                ControllerProxy.getInstance().getFilePropertiesController().onlyShowPropertiesPanel();
            }
        }
    }

    /**
     * Show splash screen.
     */
    public void showSplashScreen() {
        if (ApplicationState.getInstance().isShowTitle()) {
            JDialog.setDefaultLookAndFeelDecorated(false);
            splashScreenDialog = new SplashScreenDialog();
            JDialog.setDefaultLookAndFeelDecorated(true);
            // For multiple screens
            if (GuiUtils.getNumberOfScreenDevices() > 1) {
                // Get screen where application is shown
                GraphicsDevice screen = GuiUtils.getGraphicsDeviceForLocation(ApplicationState.getInstance().getWindowXPosition(), ApplicationState.getInstance()
                        .getWindowYPosition());
                GuiUtils.setLocationInScreen(splashScreenDialog, screen);
            }
            splashScreenDialog.setVisible(true);
            splashScreenDialog.toFront();
        }
    }

    /**
     * Show status bar.
     * 
     * @param show
     *            the show
     */
    public void showStatusBar(boolean show) {
        ApplicationState.getInstance().setShowStatusBar(show);
        frame.showStatusBar(show);
        repaint();
    }

    public void showToolBar(boolean show) {
        Actions.getAction(ShowToolbarAction.class).putValue(Action.SELECTED_KEY, show);
        ApplicationState.getInstance().setShowToolBar(show);
        frame.showToolBar(show);
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
        getLogger().debug(LogCategories.START, "Starting visualization");

        if (SystemProperties.IS_JAVA_6_UPDATE_10_OR_LATER) {
            FadingPopupFactory.install();
        }

        getFrame().create(ApplicationState.getInstance().getFrameState());

        // Create drag and drop listener
        PlayListTableTransferHandler playListTransferHandler = new PlayListTableTransferHandler();
        ControllerProxy.getInstance().getPlayListController().getMainPlayListTable().setTransferHandler(playListTransferHandler);
        ControllerProxy.getInstance().getPlayListController().getMainPlayListScrollPane().setTransferHandler(playListTransferHandler);

        new PlayListToDeviceDragAndDropListener();

        JProgressBar progressBar = getProgressBar();
        if (progressBar != null) {
            progressBar.setVisible(false);
        }

        hideDeviceInfoOnStatusBar();

        getLogger().debug(LogCategories.START, "Start visualization done");
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
            updateStatusBar(StringUtils.getString("<html>", I18nUtils.getString("BUFFERING"), " <b>", song.getTitle(), "</b>...</html>"));
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
            if (((AudioFile) song).getTag() == null) {
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
    public void playbackStateChanged(PlaybackState newState, AudioObject currentAudioObject) {
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
        showContextPanel(newState.isUseContext(), true);

        // Set location for navigator tabs (left or top)
        getNavigationTreePanel().getTabbedPane().setTabPlacement(newState.isShowNavigatorTabsAtLeft() ? SwingConstants.LEFT : SwingConstants.TOP);

        // Set OSD width
        getOSDDialog().setWidth(newState.getOsdWidth());

        // Set tabs and text for navigator
        getNavigationTreePanel().updateTabs();

        // Set text for context tabs
        getContextPanel().updateContextTabsText();

        // Set ticks for the player progress bar
        getPlayerControls().setShowTicksAndLabels(newState.isShowTicks());

        // Once done graphic changes, repaint the window
        repaint();
    }
}
