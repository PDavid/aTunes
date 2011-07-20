/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.actions;

public class ActionInstance {

    private Class<? extends AbstractAction> actionClass;
    private String actionId;

    public ActionInstance(Class<? extends AbstractAction> actionClass, String actionId) {
        super();
        this.actionClass = actionClass;
        this.actionId = actionId;
    }

    /**
     * @return the actionClass
     */
    protected Class<? extends AbstractAction> getActionClass() {
        return actionClass;
    }

    /**
     * @param actionClass
     *            the actionClass to set
     */
    protected void setActionClass(Class<? extends AbstractAction> actionClass) {
        this.actionClass = actionClass;
    }

    /**
     * @return the actionId
     */
    protected String getActionId() {
        return actionId;
    }

    /**
     * @param actionId
     *            the actionId to set
     */
    protected void setActionId(String actionId) {
        this.actionId = actionId;
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((actionClass == null) ? 0 : actionClass.hashCode());
		result = prime * result
				+ ((actionId == null) ? 0 : actionId.hashCode());
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
		if (!(obj instanceof ActionInstance)) {
			return false;
		}
		ActionInstance other = (ActionInstance) obj;
		if (actionClass == null) {
			if (other.actionClass != null) {
				return false;
			}
		} else if (!actionClass.equals(other.actionClass)) {
			return false;
		}
		if (actionId == null) {
			if (other.actionId != null) {
				return false;
			}
		} else if (!actionId.equals(other.actionId)) {
			return false;
		}
		return true;
	}

}
