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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm;

import java.io.Serializable;

/**
 * An object to be used as key in last.fm cache
 * @author alex
 *
 */
public class CacheKey implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6858546645644279551L;

	private String keyType;
	
	private Object keyObject;
	
	/**
	 * @param keyType
	 * @param keyObject
	 */
	public CacheKey(String keyType, Serializable keyObject) {
		this.keyType = keyType;
		this.keyObject = keyObject;
	}
	
	/**
	 * @return
	 */
	public Object getKeyObject() {
		return keyObject;
	}
	
	/**
	 * @return
	 */
	public String getKeyType() {
		return keyType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((keyObject == null) ? 0 : keyObject.hashCode());
		result = prime * result + ((keyType == null) ? 0 : keyType.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CacheKey other = (CacheKey) obj;
		if (keyObject == null) {
			if (other.keyObject != null) {
				return false;
			}
		} else if (!keyObject.equals(other.keyObject)) {
			return false;
		}
		if (keyType == null) {
			if (other.keyType != null) {
				return false;
			}
		} else if (!keyType.equals(other.keyType)) {
			return false;
		}
		return true;
	}
}
