/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.util.List;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.I18nUtils;

public class ExtractPictureAction extends ActionOverSelectedObjects<AudioFile> {

    private static final long serialVersionUID = -8618297820141610193L;

    ExtractPictureAction() {
        super(I18nUtils.getString("EXTRACT_PICTURE"), ImageLoader.getImage(ImageLoader.EXPORT_PICTURE), AudioFile.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("EXTRACT_PICTURE"));
    }

    @Override
    protected void performAction(List<AudioFile> objects) {
        // Export only first picture
        AudioFilePictureUtils.exportPicture(objects.get(0), VisualHandler.getInstance().getFrame().getFrame());
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<AudioObject> selection) {
        return selection.size() == 1 && selection.get(0) instanceof AudioFile && ((AudioFile) selection.get(0)).hasInternalPicture();
    }
}
