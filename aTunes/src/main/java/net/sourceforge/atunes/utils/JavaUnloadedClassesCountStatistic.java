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

import java.lang.management.ManagementFactory;

import net.sourceforge.atunes.model.IJavaVirtualMachineStatistic;

/**
 * Number of classes unloaded
 * 
 * @author alex
 * 
 */
public final class JavaUnloadedClassesCountStatistic implements
		IJavaVirtualMachineStatistic {
	@Override
	public String getValue() {
		return String.valueOf(ManagementFactory.getClassLoadingMXBean()
				.getUnloadedClassCount());
	}

	@Override
	public String getDescription() {
		return "Unloaded Classes Count";
	}
}