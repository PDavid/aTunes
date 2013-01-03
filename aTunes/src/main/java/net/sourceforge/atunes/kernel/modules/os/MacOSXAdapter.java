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

import net.sourceforge.atunes.utils.ReflectionUtils;

/**
 * Each adapter has the name of the EAWT method it intends to listen for (handleAbout, for example),
 * the Object that will ultimately perform the task, and the Method to be called on that Object
 * @author alex
 *
 */
class MacOSXAdapter implements InvocationHandler {

    /**
     * Mac OS EAWT method to listen
     */
    private String proxySignature;
    
    /**
     * Instance of class to call
     */
	private Object targetObject;
	
	/**
	 * Method to call
	 */
    private Method targetMethod;

    /**
     * @param proxySignature
     * @param target
     * @param methodName
     */
    MacOSXAdapter(String proxySignature, Object target, String methodName) {
        this.proxySignature = proxySignature;
        this.targetObject = target;
        this.targetMethod = ReflectionUtils.getMethod(target.getClass(), methodName);
    }

    /**
     * Call to method of target class
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private boolean callTarget() throws InvocationTargetException, IllegalAccessException {
        Object result = targetMethod.invoke(targetObject, (Object[]) null);
        if (result == null) {
            return true;
        }
        return Boolean.valueOf(result.toString()).booleanValue();
    }

    /**
     * InvocationHandler implementation(non-Javadoc)
     * This is the entry point for our proxy object; it is called every time an ApplicationListener method is invoked
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        if (isCorrectMethod(method, args)) {
            boolean handled = callTarget();
            setApplicationEventHandled(args[0], handled);
        }
        // All of the ApplicationListener methods are void; return null regardless of what happens
        return null;
    }

    /**
     * Compare the method that was called to the intended method when the OSXAdapter instance was created
     * (e.g. handleAbout, handleQuit, handleOpenFile, etc.)
     * @param method
     * @param args
     * @return
     */
    private boolean isCorrectMethod(Method method, Object[] args) {
        return (targetMethod != null && proxySignature.equals(method.getName()) && args.length == 1);
    }

    /**
     * It is important to mark the ApplicationEvent as handled and cancel the default behavior
     * This method checks for a boolean result from the proxy method and sets the event accordingly
     * @param event
     * @param handled
     */
    private void setApplicationEventHandled(Object event, boolean handled) {
        if (event != null) {
        	Method method = ReflectionUtils.getMethod(event.getClass(), "setHandled", boolean.class);
        	// If the target method returns a boolean, use that as a hint
        	ReflectionUtils.invoke(event, method, Boolean.valueOf(handled));
        }
    }
}
