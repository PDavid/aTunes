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

import java.util.List;

import net.sourceforge.atunes.kernel.AbstractStateRetrieveTask;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IStateService;

/**
 * Reads radios
 * 
 * @author alex
 * 
 */
public class RadioInitializationTask extends AbstractStateRetrieveTask {

	private List<IRadio> radios;
	private List<IRadio> presetRadios;

	@Override
	public void retrieveData(final IStateService stateService,
			final IBeanFactory beanFactory) {
		/*
		 * Read radio stations lists. We use different files, one for presets
		 * which is not modified by the user and a second one for all the user
		 * modifications.
		 */
		this.radios = stateService.retrieveRadioCache();
		this.presetRadios = stateService.retrieveRadioPreset();
	}

	@Override
	public void setData(final IBeanFactory beanFactory) {
		if (this.radios != null) {
			beanFactory.getBean(RadioHandler.class).setRadios(this.radios);
		}
		if (this.presetRadios != null) {
			beanFactory.getBean(RadioHandler.class).setPresetRadios(
					this.presetRadios);
		}
		beanFactory.getBean(INavigationHandler.class).refreshView(
				beanFactory.getBean("radioNavigationView",
						INavigationView.class));
	}
}
