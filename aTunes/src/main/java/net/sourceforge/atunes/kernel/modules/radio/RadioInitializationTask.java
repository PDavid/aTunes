/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.kernel.modules.radio;

import java.util.List;

import net.sourceforge.atunes.model.IHandlerBackgroundInitializationTask;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IStateHandler;

/**
 * Reads radios
 * @author alex
 *
 */
public class RadioInitializationTask implements IHandlerBackgroundInitializationTask {

	private IStateHandler stateHandler;
	
	private RadioHandler radioHandler;
	
	private INavigationHandler navigationHandler;
	
	private INavigationView radioNavigationView;
	
	/**
	 * @param radioNavigationView
	 */
	public void setRadioNavigationView(INavigationView radioNavigationView) {
		this.radioNavigationView = radioNavigationView;
	}
	
	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
	
	/**
	 * @param radioHandler
	 */
	public void setRadioHandler(RadioHandler radioHandler) {
		this.radioHandler = radioHandler;
	}
	
	/**
	 * @param stateHandler
	 */
	public void setStateHandler(IStateHandler stateHandler) {
		this.stateHandler = stateHandler;
	}
	
	@Override
	public Runnable getInitializationTask() {
        return new Runnable() {
            /**
             * Read radio stations lists. We use different files, one for
             * presets which is not modified by the user and a second one for
             * all the user modifications.
             */
            @Override
            public void run() {
            	List<IRadio> radios = stateHandler.retrieveRadioCache();
            	if (radios != null) {
            		radioHandler.setRadios(radios);
            	}
            	List<IRadio> presetRadios = stateHandler.retrieveRadioPreset();
            	if (presetRadios != null) {
            		radioHandler.setPresetRadios(presetRadios);
            	}
            }
        };
	}
	
	@Override
	public Runnable getInitializationCompletedTask() {
		return new Runnable() {
			@Override
			public void run() {
				navigationHandler.refreshView(radioNavigationView);
			}
		};
	}
}
