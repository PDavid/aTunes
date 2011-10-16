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

package net.sourceforge.atunes.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {

	private ReflectionUtils() {}
	
	/**
	 * Returns class or null if any exception happens
	 * @param className
	 * @return
	 */
	public static Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			Logger.error(e);
		}
		return null;
	}
	
	/**
	 * Creates a new instance of a class with a default constructor or returns null if any exception happens
	 * @param className
	 * @return
	 */
	public static Object newInstance(String className) {
        Class<?> clazz;
		try {
			clazz = Class.forName(className);
	       	return clazz.getConstructor((Class[]) null).newInstance((Object[]) null);
		} catch (ClassNotFoundException e) {
			Logger.error(e);
		} catch (IllegalArgumentException e) {
			Logger.error(e);
		} catch (SecurityException e) {
			Logger.error(e);
		} catch (InstantiationException e) {
			Logger.error(e);
		} catch (IllegalAccessException e) {
			Logger.error(e);
		} catch (InvocationTargetException e) {
			Logger.error(e);
		} catch (NoSuchMethodException e) {
			Logger.error(e);
		}
		return null;
	}
	
	/**
	 * Returns method object or null if any error happens
	 * @param clazz
	 * @param methodName
	 * @param arguments
	 * @return
	 */
	public static Method getMethod(Class<?> clazz, String methodName, Class<?>...arguments) {
		try {
	    	return clazz.getDeclaredMethod(methodName, arguments);
		} catch (IllegalArgumentException e) {
        	Logger.error(e);
		} catch (SecurityException e) {
        	Logger.error(e);
		} catch (NoSuchMethodException e) {
        	Logger.error(e);
		}
		return null;
	}
	
	/**
	 * Invokes a method of an object with given arguments
	 * @param instance
	 * @param method
	 * @param arguments
	 * @return result of invoke
	 */
	public static Object invoke(Object instance, Method method, Object...arguments) {
		if (method != null) {
			try {
				return method.invoke(instance, arguments);
			} catch (IllegalArgumentException e) {
				Logger.error(e);
			} catch (IllegalAccessException e) {
				Logger.error(e);
			} catch (InvocationTargetException e) {
				Logger.error(e);
			}
		}
		return null;
	}
}
