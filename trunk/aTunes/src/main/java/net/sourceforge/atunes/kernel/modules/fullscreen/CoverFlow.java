/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.fullscreen;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IOSManager;

public final class CoverFlow extends JPanel {

    private static final long serialVersionUID = -5982158797052430789L;

    private List<Cover> covers;
    
    private int coverSize;
    
    CoverFlow(int coverSize) {
        super(new GridBagLayout());
        this.coverSize = coverSize;
        covers = new ArrayList<Cover>(5);
        covers.add(new Cover());
        covers.add(new Cover());
        covers.add(new Cover());
        covers.add(new Cover());
        covers.add(new Cover());

        setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.2;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        add(covers.get(0), c);
        c.gridx = 1;
        add(covers.get(1), c);
        c.gridx = 2;
        c.weightx = 0.3;
        add(covers.get(2), c);
        c.gridx = 3;
        c.weightx = 0.2;
        add(covers.get(3), c);
        c.gridx = 4;
        add(covers.get(4), c);
    }

    /**
     * Paint.
     * 
     * @param objects
     * @param osManager
     */
    void paint(final List<IAudioObject> objects, IOSManager osManager) {
        int i = 0;
        for (IAudioObject ao : objects) {
        	covers.get(i).paint(ao, i, osManager, coverSize);
            i++;
        }
    }
}
