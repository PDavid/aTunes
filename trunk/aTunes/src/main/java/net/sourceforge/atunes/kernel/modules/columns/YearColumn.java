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

package net.sourceforge.atunes.kernel.modules.columns;

import javax.swing.SwingConstants;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

/**
 * Column to show year
 * 
 * @author alex
 * 
 */
public class YearColumn extends AbstractColumn<String> {

	private static final long serialVersionUID = -8016584284036796639L;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * Constructor
	 */
	public YearColumn() {
		super("YEAR");
		setWidth(100);
		setAlignment(SwingConstants.CENTER);
		setVisible(false);
		setUsedForFilter(true);
	}

	@Override
	protected int ascendingCompare(final IAudioObject ao1,
			final IAudioObject ao2) {
		return compare(ao1.getYear(this.unknownObjectChecker),
				ao2.getYear(this.unknownObjectChecker));
	}

	@Override
	protected int descendingCompare(final IAudioObject ao1,
			final IAudioObject ao2) {
		return -ascendingCompare(ao1, ao2);
	}

	@Override
	public String getValueFor(final IAudioObject audioObject, final int row) {
		// Return year
		return audioObject.getYear(this.unknownObjectChecker);
	}

	@Override
	public String getValueForFilter(final IAudioObject audioObject,
			final int row) {
		return audioObject.getYear(this.unknownObjectChecker);
	}
}
