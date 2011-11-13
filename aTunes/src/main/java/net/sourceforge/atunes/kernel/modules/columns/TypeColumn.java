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

package net.sourceforge.atunes.kernel.modules.columns;

import javax.swing.SwingConstants;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectGenericImageFactory;
import net.sourceforge.atunes.model.IColorMutableImageIcon;

public class TypeColumn extends AbstractColumn {

    /**
     * 
     */
    private static final long serialVersionUID = -3060341777429113749L;
    
    private IAudioObjectGenericImageFactory audioObjectGenericImageFactory;

    public TypeColumn() {
        super("TYPE", IColorMutableImageIcon.class);
        setResizable(false);
        setWidth(20);
        setVisible(true);
        setAlignment(SwingConstants.CENTER);
        this.audioObjectGenericImageFactory = Context.getBean(IAudioObjectGenericImageFactory.class);
    }

    @Override
    protected int ascendingCompare(IAudioObject ao1, IAudioObject ao2) {
        return 0;
    }

    @Override
    public boolean isSortable() {
        return false;
    }

    @Override
    public Object getValueFor(IAudioObject audioObject) {
    	return this.audioObjectGenericImageFactory.getGenericImage(audioObject, GenericImageSize.SMALL);
    }

    @Override
    public String getHeaderText() {
        return "";
    }
    
    @Override
    public boolean isPlayListExclusive() {
        return true;
    }
}
