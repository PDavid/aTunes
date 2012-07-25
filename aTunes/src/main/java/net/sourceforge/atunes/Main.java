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

package net.sourceforge.atunes;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Main class to launch aTunes.
 */
public final class Main {
	
	private Main() {}
	
    /**
     * Main method for calling aTunes.
     * @param args
     */
    public static void main(String[] args) {
        // Initialize Spring
    	Context.initialize();

        // Enable uncaught exception catching
        Thread.setDefaultUncaughtExceptionHandler(Context.getBean(UncaughtExceptionHandler.class));
        
    	List<String> arguments = StringUtils.fromStringArrayToList(args);
    	
        // Save arguments, if application is restarted they will be necessary
    	Context.getBean(IApplicationArguments.class).saveArguments(arguments);

    	// Now start application
    	Context.getBean(ApplicationStarter.class).start(arguments);
    }    
}
