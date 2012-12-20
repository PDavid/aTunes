/*
 * aTunes 3.1.0
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

package net.sourceforge.atunes.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.utils.I18nUtils;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

/**
 * The Class RadioBrowserTableModel.
 */
public final class RadioBrowserTreeTableModel extends AbstractTreeTableModel {

    private static final long serialVersionUID = 1997644065009669746L;

    /** The radios map */
    private Map<String, List<IRadio>> radiosMap;

    /** List of radio labels */
    private List<String> radioLabels;

    /**
     * Root node object
     */
    private static final String ROOT = "ROOT";

    /**
     * @param radios
     */
    public RadioBrowserTreeTableModel(final List<IRadio> radios) {
	super(new DefaultMutableTreeTableNode(ROOT));
	setRadios(radios);
    }

    /**
     * Sets the radio map and labels list
     * 
     * @param radios
     */
    private void setRadios(final List<IRadio> radios) {
	radiosMap = new HashMap<String, List<IRadio>>();
	radioLabels = new ArrayList<String>();
	for (IRadio r : radios) {
	    if (radiosMap.containsKey(r.getLabel())) {
		radiosMap.get(r.getLabel()).add(r);
	    } else {
		List<IRadio> radioList = new ArrayList<IRadio>();
		radioList.add(r);
		radiosMap.put(r.getLabel(), radioList);
		radioLabels.add(r.getLabel());
	    }
	}
	Collections.sort(radioLabels);
    }

    /**
     * 
     * @param node
     * @return <code>true</code> if node is root
     */
    private boolean isRoot(final DefaultMutableTreeTableNode node) {
	return node.getUserObject().equals(ROOT);
    }

    /**
     * 
     * @param node
     * @return <code>true</code> if node is a radio label
     */
    private boolean isLabel(final DefaultMutableTreeTableNode node) {
	return node.getUserObject() instanceof String;
    }

    @Override
    public Object getChild(final Object parent, final int index) {
	if (isRoot((DefaultMutableTreeTableNode) parent)) {
	    return new DefaultMutableTreeTableNode(radioLabels.get(index));
	}
	if (isLabel((DefaultMutableTreeTableNode) parent)) {
	    String label = (String) ((DefaultMutableTreeTableNode) parent)
		    .getUserObject();
	    return new DefaultMutableTreeTableNode(radiosMap.get(label).get(
		    index));
	}
	return "jj";
    }

    @Override
    public int getChildCount(final Object parent) {
	if (isRoot((DefaultMutableTreeTableNode) parent)) {
	    return radioLabels.size();
	}
	if (isLabel((DefaultMutableTreeTableNode) parent)) {
	    String label = (String) ((DefaultMutableTreeTableNode) parent)
		    .getUserObject();
	    return radiosMap.get(label).size();
	}
	return 0;
    }

    @Override
    public int getIndexOfChild(final Object parent, final Object child) {
	if (isLabel((DefaultMutableTreeTableNode) child)) {
	    String label = (String) ((DefaultMutableTreeTableNode) child)
		    .getUserObject();
	    return radioLabels.indexOf(label);
	}
	// Is a radio
	String label = (String) ((DefaultMutableTreeTableNode) parent)
		.getUserObject();
	IRadio r = (IRadio) ((DefaultMutableTreeTableNode) child)
		.getUserObject();
	return radiosMap.get(label).indexOf(r);
    }

    @Override
    public int getColumnCount() {
	return 3;
    }

    @Override
    public String getColumnName(final int column) {
	if (column == 0) {
	    return I18nUtils.getString("LABEL");
	} else if (column == 1) {
	    return I18nUtils.getString("NAME");
	} else {
	    return I18nUtils.getString("URL");
	}
    }

    @Override
    public Object getValueAt(final Object node, final int column) {
	if (isLabel((DefaultMutableTreeTableNode) node)) {
	    return column == 0 ? node : null;
	}

	// Is a radio
	IRadio r = (IRadio) ((DefaultMutableTreeTableNode) node)
		.getUserObject();
	if (column == 0) {
	    return null;
	} else if (column == 1) {
	    return r.getName();
	} else if (column == 2) {
	    return r.getUrl();
	}
	return null;
    }

}
