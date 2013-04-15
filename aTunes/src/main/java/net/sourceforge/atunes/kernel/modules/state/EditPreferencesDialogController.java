/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import javax.swing.DefaultListModel;

import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.IBeanFactory;

/**
 * Controller for edit preferences dialog
 * 
 * @author alex
 * 
 */
public final class EditPreferencesDialogController extends
		AbstractSimpleController<EditPreferencesDialog> {

	/** The panels of the edit preferences dialog */
	private List<AbstractPreferencesPanel> preferencesPanels;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param preferencesPanels
	 */
	public void setPreferencesPanels(
			final List<AbstractPreferencesPanel> preferencesPanels) {
		this.preferencesPanels = preferencesPanels;
	}

	/**
	 * Instantiates a new edits the preferences dialog controller.
	 * 
	 * @param dialog
	 */
	public EditPreferencesDialogController(final EditPreferencesDialog dialog) {
		super(dialog);
	}

	/**
	 * Initializes controller
	 */
	public void initialize() {
		getComponentControlled().setPanels(preferencesPanels);
		buildList();
		addBindings();
		getComponentControlled().pack();
	}

	@Override
	public void addBindings() {
		EditPreferencesDialogListener listener = new EditPreferencesDialogListener(
				getComponentControlled(), this);
		getComponentControlled().getList().addListSelectionListener(listener);
		getComponentControlled().getCancel().addActionListener(listener);
		getComponentControlled().getOk().addActionListener(listener);
		getComponentControlled().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(final ComponentEvent e) {
				// Call dialogVisibilityChanged
				for (AbstractPreferencesPanel panel : preferencesPanels) {
					panel.dialogVisibilityChanged(true);
				}
			}

			@Override
			public void componentHidden(final ComponentEvent e) {
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
	 * reset immediate changes of panels
	 */
	void resetImmediateChanges() {
		for (AbstractPreferencesPanel p : preferencesPanels) {
			p.resetImmediateChanges();
		}
	}

	/**
	 * Start.
	 */
	public void start() {
		// Update panels
		for (AbstractPreferencesPanel panel : preferencesPanels) {
			panel.updatePanel();
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

	void validateAndProcessPreferences(EditPreferencesDialog preferencesDialog) {
		beanFactory
				.getBean(ValidateAndProcessPreferencesBackgroundWorker.class)
				.validateAndProcessPreferences(preferencesDialog,
						preferencesPanels);
	}
}
