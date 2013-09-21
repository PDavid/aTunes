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

package net.sourceforge.atunes.kernel.modules.radio;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;

import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRadioHandler;
import net.sourceforge.atunes.utils.DefaultComparator;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;

/**
 * Controller for radio browser
 * 
 * @author alex
 * 
 */
public final class RadioBrowserDialogController extends
		AbstractSimpleController<RadioBrowserDialog> {

	private IRadioHandler radioHandler;

	private IBeanFactory beanFactory;

	private Collator collator;

	/**
	 * Initializes controller
	 */
	public void initialize() {
		setComponentControlled(this.beanFactory.getBean(IDialogFactory.class)
				.newDialog(RadioBrowserDialog.class));
		addBindings();
	}

	/**
	 * @param radioHandler
	 */
	public void setRadioHandler(final IRadioHandler radioHandler) {
		this.radioHandler = radioHandler;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param collator
	 */
	public void setCollator(final Collator collator) {
		this.collator = collator;
	}

	/**
	 * Show radio browser.
	 */
	void showRadioBrowser() {
		retrieveData();
	}

	/**
	 * Retrieve data.
	 */
	private void retrieveData() {
		this.beanFactory
				.getBean(RetrieveRadioBrowserDataBackgroundWorker.class)
				.retrieve(this);
	}

	@Override
	public void addBindings() {
		RadioBrowserDialogListener listener = new RadioBrowserDialogListener(
				getComponentControlled(), this.radioHandler);
		getComponentControlled().getList().addMouseListener(listener);
		getComponentControlled().getTable().addMouseListener(listener);
	}

	protected void show(final List<IRadio> radios) {
		ImmutableListMultimap<String, IRadio> labelsAndRadios = Multimaps
				.index(radios, new Function<IRadio, String>() {
					@Override
					public String apply(final IRadio input) {
						if (input != null && input.getLabel() != null) {
							return input.getLabel();
						}
						return "";
					}
				});
		List<String> labels = new ArrayList<String>(labelsAndRadios.keySet());
		Collections.sort(labels, new DefaultComparator(this.collator));
		DefaultListModel listModel = new DefaultListModel();
		for (String label : labels) {
			listModel.addElement(label);
		}
		getComponentControlled().getList().setModel(listModel);
		getComponentControlled().getTable().setModel(
				new RadioBrowserTableModel(labelsAndRadios));
		getComponentControlled().setVisible(true);
	}
}
