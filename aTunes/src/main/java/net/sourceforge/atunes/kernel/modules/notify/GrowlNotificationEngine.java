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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * Notification engine for Mac OS X using Growl
 * @author alex
 *
 */
public class GrowlNotificationEngine extends CommonNotificationEngine {

	private static final String HELP_ARG = "--help";
	private static final String IMAGE_ARG = "--image";
	private static final String MESSAGE_ARG = "-m";
	private static final String TITLE_ARG = "-t";
	private static final String GROWLNOTIFY = "/usr/local/bin/growlnotify";

	private IOSManager osManager;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	@Override
	public String getName() {
		return "Growl";
	}

	@Override
	public void showNotification(final IAudioObject audioObject) {
		String image = getTemporalImage(audioObject, osManager);
		List<String> command = getCommand(audioObject, image);
		ProcessBuilder pb = new ProcessBuilder(command);
		try {
			Logger.debug("Executing growl notification: ", Arrays.toString(command.toArray()));
			Process process = pb.start();
			int code = process.waitFor();
			Logger.debug("growl notification ended with code: ", code);
		} catch (IOException e) {
			Logger.error("growl notification error: ", e.getMessage());
		} catch (InterruptedException e) {
			Logger.error("growl notification error: ", e.getMessage());
		} finally {
			removeTemporalImage(image);
		}
	}

	@Override
	public void disposeNotifications() {
	}

	@Override
	public void updateNotification(final IStateUI newState) {
	}

	@Override
	public boolean testEngineAvailable() {
		if (!osManager.isMacOsX()) {
			return false;
		}

		List<String> args = new ArrayList<String>();
		args.add(GROWLNOTIFY);
		args.add(HELP_ARG);
		ProcessBuilder pb = new ProcessBuilder(args);
		int code = -1;
		try {
			Logger.debug("Testing growl: ", Arrays.toString(args.toArray()));
			Process process = pb.start();
			code = process.waitFor();
			Logger.debug("growl test ended with code: ", code);
		} catch (IOException e) {
			Logger.error("growl test error: ", e.getMessage());
		} catch (InterruptedException e) {
			Logger.error("growl test error: ", e.getMessage());
		}

		return code == 0;
	}

	/**
	 * Arguments for growl command
	 * @param audioObject
	 * @param image
	 * @return
	 */
	private List<String> getCommand(final IAudioObject audioObject, final String image) {
		List<String> args = new ArrayList<String>();
		args.add(GROWLNOTIFY);
		args.add(TITLE_ARG);
		args.add(audioObject.getTitleOrFileName());
		args.add(MESSAGE_ARG);
		args.add(new StringBuilder(audioObject.getAlbum(unknownObjectChecker)).append("\n").append(audioObject.getAlbumArtistOrArtist(unknownObjectChecker)).toString());
		args.add(IMAGE_ARG);
		args.add(image);
		return args;
	}

	@Override
	public String getDescription() {
		return I18nUtils.getString("NOTIFICATION_ENGINE_GROWL_DESCRIPTION");
	}

	@Override
	public String getUrl() {
		return "http://growl.info/";
	}

}
