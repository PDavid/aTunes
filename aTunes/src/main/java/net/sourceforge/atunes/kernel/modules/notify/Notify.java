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

package net.sourceforge.atunes.kernel.modules.notify;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.atunes.utils.Logger;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * Use this class for JNA access to libnotify.
 * 
 * @author Marco Biscaro
 */
public final class Notify {

	/**
	 * Uses default value for expire time.
	 * 
	 * @see #setTimeout(NotifyNotification, int)
	 */
	public static final int NOTIFY_EXPIRES_DEFAULT = -1;
	/**
	 * Keep a notify visible until the user or the applications closes it.
	 * 
	 * @see #setTimeout(NotifyNotification, int)
	 */
	public static final int NOTIFY_EXPIRES_NEVER = 0;

	/*
	 * Determines if libnotify is present.
	 */
	private static boolean notifyPresent;

	/**
	 * Register the libnotify and sets the <code>notifyPresent</code> flag to
	 * true if everything occurs successfully.
	 */
	static {
		Logger.info("Starting libnotify...");
		try {
			Native.register("notify");
			notifyPresent = true;
			Logger.info("libnotify started");
		} catch (UnsatisfiedLinkError e) {
			Logger.info("libnotify is not present");
		}
	}

	private Notify() {

	}

	/**
	 * Class that model a NotifyNotification object, used by libnotify.
	 * 
	 * @author Marco Biscaro
	 */
	public static final class NotifyNotification extends Structure {

		/**
		 * Must be public in order to JNA work
		 */
		public Pointer parent_object;

		/**
		 * Must be public in order to JNA work
		 */
		public Pointer priv;

		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] { "parent_object", "priv" });
		}
	}

	/**
	 * Determines if the libnotify is present or not.
	 * 
	 * @return <code>true</code> if libnotify is present, <code>false</code>
	 *         otherwise
	 */
	public static boolean isNotifyPresent() {
		return notifyPresent;
	}

	/**
	 * Initializes the libnotify API for use.
	 * 
	 * @param appName
	 *            the program name
	 * @return <code>true</code> if the libnotify was successfully started,
	 *         <code>false</code> otherwise
	 * @see #uninit()
	 */
	public static boolean init(final String appName) {
		return notify_init(appName);
	}

	/**
	 * Informs to libnotify that we will not use it again.
	 * 
	 * @see #init(String)
	 */
	public static void uninit() {
		notify_uninit();
	}

	/**
	 * Determines if the libnotify was initialized.
	 * 
	 * @return <code>true</code> if libnotify was already initialized,
	 *         <code>false</code> otherwise
	 * @see #init(String)
	 * @see #uninit()
	 */
	public static boolean isInitted() {
		return notify_is_initted();
	}

	/**
	 * Gets the name registered by libnotify.
	 * 
	 * @return the application name registered by libnotify
	 * @see #init(String)
	 */
	public static String getAppName() {
		return notify_get_app_name();
	}

	/**
	 * Creates a new notification with specified parameters. The notification is
	 * invisible by default. To show it, call {@link #show(NotifyNotification)}.
	 * 
	 * @param summary
	 *            the notification title
	 * @param body
	 *            the notification message
	 * @param icon
	 *            the path to icon or null if it will not be used
	 * @return a {@link NotifyNotification} containing the given information
	 */
	public static NotifyNotification newNotification(final String summary,
			final String body, final String icon) {
		return notify_notification_new(summary, body, icon, null);
	}

	/**
	 * Changes the timeout to notification. The value can be
	 * {@link #NOTIFY_EXPIRES_DEFAULT} (use default value),
	 * {@link #NOTIFY_EXPIRES_NEVER} (still visible until the user or the
	 * program closes it) or a number greater than zero, representing the time
	 * in milliseconds.
	 * <p>
	 * <b>Note:</b> not all operational systems respect this value.
	 * 
	 * @param notification
	 *            the {@link NotifyNotification} object to set the timeout
	 * @param timeout
	 *            {@link #NOTIFY_EXPIRES_DEFAULT}, {@link #NOTIFY_EXPIRES_NEVER}
	 *            or the time in milliseconds
	 */
	public static void setTimeout(final NotifyNotification notification,
			final int timeout) {
		notify_notification_set_timeout(notification, timeout);
	}

	/**
	 * Show the given {@link NotifyNotification} on screen.
	 * 
	 * @param notification
	 *            the {@link NotifyNotification} to show
	 * @return <code>true</code> if everything occurs without errors,
	 *         <code>false</code> otherwise
	 * @see #setTimeout(NotifyNotification, int)
	 */
	public static boolean show(final NotifyNotification notification) {
		return notify_notification_show(notification, null);
	}

	/**
	 * Hides the given {@link NotifyNotification}, if applicable.
	 * <p>
	 * <b>Note:</b> not all operational systems honors this method call.
	 * 
	 * @param notification
	 *            the {@link NotifyNotification} to hide
	 * @return <code>true</code> if everything occurs without errors,
	 *         <code>false</code> otherwise
	 */
	public static boolean close(final NotifyNotification notification) {
		return notify_notification_close(notification, null);
	}

	// private native methods

	private static native boolean notify_init(String appName);

	private static native void notify_uninit();

	private static native boolean notify_is_initted();

	private static native String notify_get_app_name();

	private static native NotifyNotification notify_notification_new(
			String summary, String body, String icon, Structure attach);

	private static native void notify_notification_set_timeout(
			NotifyNotification notification, int timeout);

	private static native boolean notify_notification_show(
			NotifyNotification notification, Structure error);

	private static native boolean notify_notification_close(
			NotifyNotification notification, Structure error);

}
