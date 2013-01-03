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

package net.sourceforge.atunes.kernel.modules.process;

import net.sourceforge.atunes.model.IProcess;
import net.sourceforge.atunes.model.IProcessFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Implements a IProcessFactory using Spring beans
 * 
 * @author alex
 * 
 */
public class ProcessFactory implements IProcessFactory, ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(
	    final ApplicationContext applicationContext) {
	this.context = applicationContext;
    }

    @Override
    public IProcess<?> getProcessByName(final String processName) {
	return this.context.getBean(processName, IProcess.class);
    }
}
