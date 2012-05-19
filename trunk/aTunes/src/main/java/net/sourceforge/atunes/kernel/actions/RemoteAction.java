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

package net.sourceforge.atunes.kernel.actions;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.ICommand;
import net.sourceforge.atunes.model.ICommandHandler;
import net.sourceforge.atunes.utils.StringUtils;

public abstract class RemoteAction implements ICommand {

    private static final class CallToggleAction implements Runnable {
    	
		private final Class<? extends CustomAbstractAction> actionClass;
		private final boolean value;

		private CallToggleAction(Class<? extends CustomAbstractAction> actionClass, boolean value) {
			this.actionClass = actionClass;
			this.value = value;
		}

		@Override
		public void run() {
			CustomAbstractAction action = Context.getBean(actionClass);
			action.putValue(CustomAbstractAction.SELECTED_KEY, value);
			action.actionPerformed(null);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 34587011817746442L;

	private boolean synchronousResponse = false;
    
    private ICommandHandler commandHandler;
    
    private String commandName;
    
    /**
     * @param commandName
     */
    public RemoteAction(String commandName) {
    	this.commandName = commandName;
    	setSynchronousResponse(true); // all commands are synchronous by default
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
    public void setCommandHandler(ICommandHandler commandHandler) {
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
    protected final void setSynchronousResponse(boolean sync) {
    	this.synchronousResponse = sync;
    }
    
    /**
     * Calls an action of application
     * @param actionClass
     */
    protected final void callAction(Class<? extends CustomAbstractAction> actionClass) {
    	Context.getBean(actionClass).actionPerformed(null);
    }

    /**
     * Calls an action of application
     * @param actionClass
     */
    protected final void callToggleAction(final Class<? extends CustomAbstractAction> actionClass, final boolean value) {
    	GuiUtils.callInEventDispatchThread(new CallToggleAction(actionClass, value));
    }

    @Override
    public final String getCommandName() {
    	return commandName;
    }
}
