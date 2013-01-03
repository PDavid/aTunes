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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This action only returns help text which the handler can then output to the telnet client.
 * 
 */
public class PrintHelpToRemoteAction extends RemoteAction {

	private static final long serialVersionUID = -6161108283405050138L;

	private IBeanFactory beanFactory;

	@Override
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public String runCommand(final java.util.List<String> parameters) {
		StringBuilder sb = new StringBuilder();
		sb.append("\naTunes terminal line interface. This interface provides you with control of your player over a network.\n");
		sb.append("Each command has the format of command:\"command name [params]\", without \"\".\n");
		sb.append("Commands available are:\n\n");

		List<RemoteAction> actions = new ArrayList<RemoteAction>(beanFactory.getBeans(RemoteAction.class));
		Collections.sort(actions, new Comparator<RemoteAction>() {
			@Override
			public int compare(final RemoteAction o1, final RemoteAction o2) {
				return o1.getCommandName().compareTo(o2.getCommandName());
			}
		});
		int width = getWidth(actions) + 2;
		for (RemoteAction action : actions) {
			sb.append(String.format("%-" + width + "s", StringUtils.getString(action.getCommandName(), " ", getOptionalParameters(action))));
			sb.append(action.getHelpText());
			sb.append("\n");
		}

		return sb.toString();
	}

	private int getWidth(final List<RemoteAction> actions) {
		int max = 0;
		for (RemoteAction action : actions) {
			int actionWidth = action.getCommandName().length() + getOptionalParameters(action).length() + 1;
			max = Math.max(max, actionWidth);
		}
		return max;
	}

	private String getOptionalParameters(final RemoteAction action) {
		return action.getOptionalParameters() != null ? action.getOptionalParameters() : "";
	}

	@Override
	protected String getHelpText() {
		return "Prints this help message";
	}

	@Override
	protected String getOptionalParameters() {
		return null;
	}
}
