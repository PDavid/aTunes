/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.AudioObject;

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
    protected int ascendingCompare(AudioObject ao1, AudioObject ao2) {
        return 0;
    }

    @Override
    public boolean isSortable() {
        return false;
    }

    @Override
    public Object getValueFor(AudioObject audioObject) {
        if (audioObject instanceof AudioFile) {
            return new ColorMutableImageIcon() {
            	@Override
            	public ImageIcon getIcon(Paint paint) {
            		return paint != null ? AudioFileImageIcon.getSmallImageIcon(paint) : AudioFileImageIcon.getSmallImageIcon(); // Use cached version if paint null
            	}
            };
        } else if (audioObject instanceof Radio) {
            return new ColorMutableImageIcon() {
            	@Override
            	public ImageIcon getIcon(Paint paint) {
            		return Images.getImage(Images.RADIO_LITTLE);
            	}
            };
        } else if (audioObject instanceof PodcastFeedEntry) {
            return new ColorMutableImageIcon() {
				
				@Override
				public ImageIcon getIcon(Paint paint) {
					return Images.getImage(Images.RSS_LITTLE);
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
