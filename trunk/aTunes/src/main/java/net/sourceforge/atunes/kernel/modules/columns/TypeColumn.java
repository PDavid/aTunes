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

package net.sourceforge.atunes.kernel.modules.columns;

import java.awt.Paint;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.images.AudioFileImageIcon;
import net.sourceforge.atunes.gui.images.ColorMutableImageIcon;
import net.sourceforge.atunes.gui.images.RadioImageIcon;
import net.sourceforge.atunes.gui.images.RssImageIcon;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;

public class TypeColumn extends AbstractColumn {

    /**
     * 
     */
    private static final long serialVersionUID = -3060341777429113749L;

    public TypeColumn() {
        super("TYPE", ColorMutableImageIcon.class);
        setResizable(false);
        setWidth(20);
        setVisible(true);
        setAlignment(SwingConstants.CENTER);
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
        if (audioObject instanceof AudioFile) {
            return new ColorMutableImageIcon() {
            	@Override
            	public ImageIcon getIcon(Paint paint) {
            		return AudioFileImageIcon.getSmallImageIcon(paint);
            	}
            };
        } else if (audioObject instanceof IRadio) {
            return new ColorMutableImageIcon() {
            	@Override
            	public ImageIcon getIcon(Paint paint) {
            		return RadioImageIcon.getSmallIcon(paint);
            	}
            };
        } else if (audioObject instanceof IPodcastFeedEntry) {
            return new ColorMutableImageIcon() {
				
				@Override
				public ImageIcon getIcon(Paint paint) {
					return RssImageIcon.getSmallIcon(paint);
				}
			};
        } else {
            return null;
        }
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
