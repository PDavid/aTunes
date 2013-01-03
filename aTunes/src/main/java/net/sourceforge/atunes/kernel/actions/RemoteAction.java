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

package net.sourceforge.atunes.kernel.actions;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ICommand;
import net.sourceforge.atunes.model.ICommandHandler;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Remote action can be called from outside application
 * @author alex
 *
 */
public abstract class RemoteAction implements ICommand {

	private static final long serialVersionUID = 34587011817746442L;

	private boolean synchronousResponse = true; // all commands are synchronous by default

	private ICommandHandler commandHandler;

	private String commandName;

	private IBeanFactory beanFactory;

	/**
	 * String to return when command completes successfully
	 */
	protected static final String OK = "OK";

	/**
	 * @param commandName
	 */
	public void setCommandName(final String commandName) {
		this.commandName = commandName;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Initializes action if needed
	 * All initialization needed retrieving values from <code>getState</code> must be done here
	 * Must call super.initialize() when overriding
	 * @param state
	 */
	protected void initialize() {
		if (StringUtils.isEmpty(commandName)) {
			throw new IllegalArgumentException("Remote action must have a command");
		}
		commandHandler.registerCommand(this);
	}

	/**
	 * @param commandHandler
	 */
	public void setCommandHandler(final ICommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}

	@Override
	public final boolean isSynchronousResponse() {
		return synchronousResponse;
	}

	/**
	 * Sets if action needs a synchronous response
	 * @param sync
	 */
	public void setSynchronousResponse(final boolean sync) {
		this.synchronousResponse = sync;
	}

	/**
	 * Calls an action of application
	 * @param actionClass
	 */
	protected final void callAction(final Class<? extends CustomAbstractAction> actionClass) {
		beanFactory.getBean(actionClass).actionPerformed(null);
	}

	/**
	 * Calls an action of application
	 * @param actionClass
	 */
	protected final void callAction(final CustomAbstractAction action) {
		action.actionPerformed(null);
	}

	/**
	 * Calls an action of application
	 * @param actionClass
	 */
	protected final void callToggleAction(final Class<? extends CustomAbstractAction> actionClass, final boolean value) {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				CustomAbstractAction action = beanFactory.getBean(actionClass);
				action.putValue(CustomAbstractAction.SELECTED_KEY, value);
				callAction(action);
			}
		});
	}

	@Override
	public final String getCommandName() {
		return commandName;
	}

	/**
	 * @return optional parameters text
	 */
	protected abstract String getOptionalParameters();

	/**
	 * @return help text of command
	 */
	protected abstract String getHelpText();
}
