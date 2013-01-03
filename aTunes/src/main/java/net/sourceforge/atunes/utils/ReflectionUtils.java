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

package net.sourceforge.atunes.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * Utilities for reflection
 * 
 * @author alex
 * 
 */
public final class ReflectionUtils {

	private ReflectionUtils() {
	}

	/**
	 * Returns class or null if any exception happens
	 * 
	 * @param className
	 * @return
	 */
	public static Class<?> getClass(final String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			Logger.error(e);
		}
		return null;
	}

	/**
	 * Creates a new instance of a class with a default constructor or returns
	 * null if any exception happens
	 * 
	 * @param className
	 * @return
	 */
	public static Object newInstance(final String className) {
		Class<?> clazz;
		try {
			clazz = Class.forName(className);
			return clazz.getConstructor((Class[]) null).newInstance(
					(Object[]) null);
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
	 * 
	 * @param clazz
	 * @param methodName
	 * @param arguments
	 * @return
	 */
	public static Method getMethod(final Class<?> clazz,
			final String methodName, final Class<?>... arguments) {
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
	 * 
	 * @param instance
	 * @param method
	 * @param arguments
	 * @return result of invoke
	 */
	public static Object invoke(final Object instance, final Method method,
			final Object... arguments) {
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

	/**
	 * Returns type arguments of a clazz
	 * 
	 * @param clazz
	 * @return
	 */
	public static Type[] getTypeArgumentsOfParameterizedType(
			final Class<?> clazz) {
		return ((ParameterizedType) clazz.getGenericSuperclass())
				.getActualTypeArguments();
	}

	/**
	 * Returns map with properties and values of given object
	 * 
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> describe(final Object bean) {
		try {
			Map<String, String> description = PropertyUtils.describe(bean);
			description.remove("class");
			return description;
		} catch (IllegalAccessException e) {
			Logger.error(e);
		} catch (InvocationTargetException e) {
			Logger.error(e);
		} catch (NoSuchMethodException e) {
			Logger.error(e);
		}
		return null;
	}
}
