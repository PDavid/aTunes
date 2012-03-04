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

package net.sourceforge.atunes.kernel.modules.state;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.kernel.StateChangeListeners;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.IIndeterminateProgressDialogFactory;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

public final class EditPreferencesDialogController extends AbstractSimpleController<EditPreferencesDialog> {

    /** The panels of the edit preferences dialog */
    private List<AbstractPreferencesPanel> preferencesPanels;
    
    private StateChangeListeners stateChangeListeners;
    
    /**
     * @param preferencesPanels
     */
    public void setPreferencesPanels(List<AbstractPreferencesPanel> preferencesPanels) {
		this.preferencesPanels = preferencesPanels;
	}
    
    /**
     * @param stateChangeListeners
     */
    public void setStateChangeListeners(StateChangeListeners stateChangeListeners) {
		this.stateChangeListeners = stateChangeListeners;
	}
    
    /**
     * Instantiates a new edits the preferences dialog controller.
     * @param dialog
     * @param state
     */
    public EditPreferencesDialogController(EditPreferencesDialog dialog, IState state) {
        super(dialog, state);
    }
    
    /**
     * Initializes controller
     */
    public void initialize() {
        for (AbstractPreferencesPanel panel : preferencesPanels) {
        	panel.setState(getState());
        }
        
        getComponentControlled().setPanels(preferencesPanels);
        buildList();
        addBindings();
        
        getComponentControlled().pack();
    }

    @Override
	public void addBindings() {
        EditPreferencesDialogListener listener = new EditPreferencesDialogListener(getComponentControlled(), this, stateChangeListeners);
        getComponentControlled().getList().addListSelectionListener(listener);
        getComponentControlled().getCancel().addActionListener(listener);
        getComponentControlled().getOk().addActionListener(listener);
        getComponentControlled().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                // Call dialogVisibilityChanged
                for (AbstractPreferencesPanel panel : preferencesPanels) {
                    panel.dialogVisibilityChanged(true);
                }
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                // Call dialogVisibilityChanged
                for (AbstractPreferencesPanel panel : preferencesPanels) {
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

        for (AbstractPreferencesPanel p : preferencesPanels) {
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
    		protected Void doInBackground() throws PreferencesValidationException {
    	        for (AbstractPreferencesPanel p : preferencesPanels) {
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
        for (AbstractPreferencesPanel p : preferencesPanels) {
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
        for (AbstractPreferencesPanel p : preferencesPanels) {
            p.resetImmediateChanges(getState());
        }
    }

    /**
     * Start.
     */
    public void start() {
        // Update panels
        for (AbstractPreferencesPanel panel : preferencesPanels) {
            panel.updatePanel(getState());
        }

        // Call dialogVisibilityChanged
        for (AbstractPreferencesPanel panel : preferencesPanels) {
            panel.dialogVisibilityChanged(true);
        }

        getComponentControlled().resetPanels();

        // Set first panel (selected) dirty
        preferencesPanels.get(0).setDirty(true);
        getComponentControlled().getList().setSelectedIndex(0);
        
        getComponentControlled().setVisible(true);
    }
}
