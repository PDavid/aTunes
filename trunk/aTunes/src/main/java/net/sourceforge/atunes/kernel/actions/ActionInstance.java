/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

    private Class<? extends Action> actionClass;
    private String actionId;

    public ActionInstance(Class<? extends Action> actionClass, String actionId) {
        super();
        this.actionClass = actionClass;
        this.actionId = actionId;
    }

    /**
     * @return the actionClass
     */
    protected Class<? extends Action> getActionClass() {
        return actionClass;
    }

    /**
     * @param actionClass
     *            the actionClass to set
     */
    protected void setActionClass(Class<? extends Action> actionClass) {
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

    @Override
    public int hashCode() {
        int hash = 0;
        if (actionId != null) {
            hash = hash + actionId.hashCode();
        }
        if (actionClass != null) {
            hash = hash + actionClass.hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == ((ActionInstance) obj).hashCode();
    }
}
