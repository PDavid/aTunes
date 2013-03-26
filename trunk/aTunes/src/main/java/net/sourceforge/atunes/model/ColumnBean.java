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

package net.sourceforge.atunes.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * This class represents information about a column to be saved into application
 * settings.
 * 
 * @author alex
 */
public final class ColumnBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3224708329958757496L;

	/** The order. */
	private int order;

	/** The visible. */
	private boolean visible;

	/** The width. */
	private int width;

	/** The sort */
	private ColumnSort sort;

	/**
	 * Gets the order.
	 * 
	 * @return the order
	 */
	public int getOrder() {
		return this.order;
	}

	/**
	 * Gets the width.
	 * 
	 * @return the width
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Checks if is visible.
	 * 
	 * @return the visible
	 */
	public boolean isVisible() {
		return this.visible;
	}

	/**
	 * Sets the order.
	 * 
	 * @param order
	 *            the order to set
	 */
	public void setOrder(final int order) {
		this.order = order;
	}

	/**
	 * Sets the visible.
	 * 
	 * @param visible
	 *            the visible to set
	 */
	public void setVisible(final boolean visible) {
		this.visible = visible;
	}

	/**
	 * Sets the width.
	 * 
	 * @param width
	 *            the width to set
	 */
	public void setWidth(final int width) {
		this.width = width;
	}

	/**
	 * @return the sort
	 */
	public ColumnSort getSort() {
		return this.sort;
	}

	/**
	 * @param sort
	 *            the sort to set
	 */
	public void setSort(final ColumnSort sort) {
		this.sort = sort;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.order;
		result = prime * result
				+ ((this.sort == null) ? 0 : this.sort.hashCode());
		result = prime * result + (this.visible ? 1231 : 1237);
		result = prime * result + this.width;
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
		ColumnBean other = (ColumnBean) obj;
		if (this.order != other.order) {
			return false;
		}
		if (this.sort != other.sort) {
			return false;
		}
		if (this.visible != other.visible) {
			return false;
		}
		if (this.width != other.width) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
