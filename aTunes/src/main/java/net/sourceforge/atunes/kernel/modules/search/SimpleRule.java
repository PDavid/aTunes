/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.kernel.modules.search;

import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class represents a simple rule.
 */
public class SimpleRule {

    /** The attribute. */
    private String attribute;

    /** The operator. */
    private String operator;

    /** The value. */
    private String value;

    /**
     * Gets the attribute.
     * 
     * @return the attribute
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * Sets the attribute.
     * 
     * @param attribute
     *            the attribute to set
     */
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    /**
     * Gets the operator.
     * 
     * @return the operator
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Sets the operator.
     * 
     * @param operator
     *            the operator to set
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * Gets the value.
     * 
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     * 
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Instantiates a new simple rule.
     * 
     * @param attribute
     *            the attribute
     * @param operator
     *            the operator
     * @param value
     *            the value
     */
    public SimpleRule(String attribute, String operator, String value) {
        super();
        this.attribute = attribute;
        this.operator = operator;
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (attribute.equalsIgnoreCase(ISearchHandler.DEFAULT_INDEX)) {
            return value;
        }
        return StringUtils.getString(attribute, operator, " \"", value, "\"");
    }

}
