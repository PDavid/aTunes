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

package net.sourceforge.atunes.kernel.modules.state;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.AbstractPreferencesPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.ContextPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.DevicePanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.EditPreferencesDialog;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.GeneralPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.ImportExportPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.InternetPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.LastFmPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.NavigatorPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.OSDPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.PlayListPrefPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.PlayerPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.PluginsPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.PodcastFeedPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.PreferencesValidationException;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.RadioPanel;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.RepositoryPanel;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.IIndeterminateProgressDialogFactory;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.utils.I18nUtils;

final class EditPreferencesDialogController extends AbstractSimpleController<EditPreferencesDialog> {

    /** The panels of the edit preferences dialog */
    private List<AbstractPreferencesPanel> panels;
    
    private IStateHandler stateHandler;

    /**
     * Instantiates a new edits the preferences dialog controller.
     * @param dialog
     * @param state
     * @param osManager
     * @param frame
     * @param stateHandler
     */
    EditPreferencesDialogController(EditPreferencesDialog dialog, IState state, IOSManager osManager, IFrame frame, IStateHandler stateHandler) {
        super(dialog, state);
        this.stateHandler = stateHandler;
        panels = new ArrayList<AbstractPreferencesPanel>();
        panels.add(new GeneralPanel(osManager));
        panels.add(new RepositoryPanel()); 
        panels.add(new PlayerPanel(osManager)); 
        panels.add(new NavigatorPanel()); 
        panels.add(new PlayListPrefPanel());
        panels.add(new OSDPanel()); 
        panels.add(new ContextPanel()); 
        panels.add(new InternetPanel()); 
        panels.add(new LastFmPanel()); 
        panels.add(new DevicePanel(osManager)); 
        panels.add(new RadioPanel()); 
        panels.add(new PodcastFeedPanel(osManager)); 
        panels.add(new ImportExportPanel());
        
        for (AbstractPreferencesPanel panel : panels) {
        	panel.setState(state);
        }
        
        if (Kernel.isEnablePlugins()) {
        	panels.add(new PluginsPanel(dialog, frame));
        }
        getComponentControlled().setPanels(panels);
        buildList();
        addBindings();
    }

    @Override
	public void addBindings() {
        EditPreferencesDialogListener listener = new EditPreferencesDialogListener(getComponentControlled(), this, stateHandler);
        getComponentControlled().getList().addListSelectionListener(listener);
        getComponentControlled().getCancel().addActionListener(listener);
        getComponentControlled().getOk().addActionListener(listener);
        getComponentControlled().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                // Call dialogVisibilityChanged
                for (AbstractPreferencesPanel panel : panels) {
                    panel.dialogVisibilityChanged(true);
                }
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                // Call dialogVisibilityChanged
                for (AbstractPreferencesPanel panel : panels) {
                    panel.dialogVisibilityChanged(false);
                }
            }
        });
    }

    /**
     * Builds the list.
     */
    private void buildList() {
        DefaultListModel listModel = new DefaultListModel();

        for (AbstractPreferencesPanel p : panels) {
            listModel.addElement(p);
        }

        getComponentControlled().setListModel(listModel);
    }

    /**
     * Checks if preferences of all panels are valid
     * 
     * @return
     */
    void validatePreferences() throws PreferencesValidationException {
		final IIndeterminateProgressDialog dialog = Context.getBean(IIndeterminateProgressDialogFactory.class).newDialog(getComponentControlled());

    	final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
    		
    		@Override
    		protected Void doInBackground() throws Exception {
    	        for (AbstractPreferencesPanel p : panels) {
    	            p.validatePanel();
    	        }
    	        return null;
    		}

    		@Override
    		protected void done() {
    			super.done();
    			dialog.hideDialog();
    		}
    	};

    	try {
    		worker.execute();
    		dialog.setTitle(I18nUtils.getString("VALIDATING_PREFERENCES"));
	    	dialog.showDialog();
			worker.get();
		} catch (InterruptedException e) {
			Logger.error(e);
		} catch (ExecutionException e) {
			if (e.getCause() instanceof PreferencesValidationException) {
				throw (PreferencesValidationException) e.getCause();
			}
		} finally {
			dialog.hideDialog();
		}
    }

    /**
     * Process preferences.
     * 
     * @return true if application needs to be restarted to apply some changes
     */
    boolean processPreferences() {
        boolean needRestart = false;
        // Apply preferences from panels
        for (AbstractPreferencesPanel p : panels) {
        	if (p.isDirty()) {
        		Logger.debug("Panel ", p.getTitle(), " is dirty");
        		// WARNING: There was a bug when call to applyPreferences was made as second operand of OR due to shortcut
        		// So call method and after do OR (method call as first operand is also valid)
        		// See bug https://sourceforge.net/tracker/?func=detail&aid=2999531&group_id=161929&atid=821812 for more information
        		boolean panelNeedRestart = p.applyPreferences(getState());
        		needRestart = needRestart || panelNeedRestart;
        	} else {
        		Logger.debug("Panel ", p.getTitle(), " is clean");
        	}
        }
        return needRestart;
    }

    /**
     * reset immediate changes of panels
     */
    void resetImmediateChanges() {
        for (AbstractPreferencesPanel p : panels) {
            p.resetImmediateChanges(getState());
        }
    }

    /**
     * Start.
     */
    public void start() {
        // Update panels
        for (AbstractPreferencesPanel panel : panels) {
            panel.updatePanel(getState());
        }

        // Call dialogVisibilityChanged
        for (AbstractPreferencesPanel panel : panels) {
            panel.dialogVisibilityChanged(true);
        }

        getComponentControlled().resetPanels();

        // Set first panel (selected) dirty
        panels.get(0).setDirty(true);
        getComponentControlled().getList().setSelectedIndex(0);
        
        getComponentControlled().setVisible(true);
    }
}
