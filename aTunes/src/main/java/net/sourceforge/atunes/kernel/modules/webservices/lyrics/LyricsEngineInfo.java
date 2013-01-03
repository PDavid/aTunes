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

package net.sourceforge.atunes.kernel.modules.webservices.lyrics;

import java.beans.ConstructorProperties;

import net.sourceforge.atunes.model.ILyricsEngineInfo;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Class with info about lyrics engines.
 */
public class LyricsEngineInfo implements ILyricsEngineInfo {

	private static final long serialVersionUID = -8014747196952195246L;

	private final String name;
	private final String clazz;
	private boolean enabled;

	/**
	 * @param name
	 * @param clazz
	 * @param enabled
	 */
	@ConstructorProperties({ "name", "clazz", "enabled" })
	public LyricsEngineInfo(final String name, final String clazz,
			final boolean enabled) {
		this.enabled = enabled;
		this.clazz = clazz;
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getClazz() {
		return this.clazz;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.clazz == null) ? 0 : this.clazz.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		LyricsEngineInfo other = (LyricsEngineInfo) obj;
		if (this.clazz == null) {
			if (other.clazz != null) {
				return false;
			}
		} else if (!this.clazz.equals(other.clazz)) {
			return false;
		}
		return true;
	}

	@Override
	public ILyricsEngineInfo copy() {
		return new LyricsEngineInfo(getName(), getClazz(), isEnabled());
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
