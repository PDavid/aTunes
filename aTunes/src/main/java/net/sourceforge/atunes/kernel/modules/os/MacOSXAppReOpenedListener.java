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

package net.sourceforge.atunes.kernel.modules.os;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.ReflectionUtils;

/**
 * Listener to be called when application is reopened, usually from dock
 * 
 * @author alex
 * 
 */
final class MacOSXAppReOpenedListener implements InvocationHandler {

    private final Object targetObject;
    private final Method targetMethod;

    /**
     * @param target
     * @param methodName
     */
    public MacOSXAppReOpenedListener(final Object target,
	    final String methodName) {
	this.targetObject = target;
	this.targetMethod = ReflectionUtils.getMethod(target.getClass(),
		methodName);
    }

    @Override
    public Object invoke(final Object proxy, final Method method,
	    final Object[] args) {
	try {
	    targetMethod.invoke(targetObject, (Object[]) null);
	} catch (IllegalArgumentException e) {
	    Logger.error(e);
	} catch (IllegalAccessException e) {
	    Logger.error(e);
	} catch (InvocationTargetException e) {
	    Logger.error(e);
	}
	return null;
    }
}