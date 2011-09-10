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

import java.util.List;

import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.I18nUtils;

public class ExtractPictureAction extends AbstractActionOverSelectedObjects<ILocalAudioObject> {

    private static final long serialVersionUID = -8618297820141610193L;

    ExtractPictureAction() {
        super(I18nUtils.getString("EXTRACT_PICTURE"), ILocalAudioObject.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("EXTRACT_PICTURE"));
    }

    @Override
    protected void performAction(List<ILocalAudioObject> objects) {
        // Export only first picture
        AudioFilePictureUtils.exportPicture(objects.get(0), GuiHandler.getInstance().getFrame().getFrame());
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<AudioObject> selection) {
        return selection.size() == 1 && selection.get(0) instanceof ILocalAudioObject && ((ILocalAudioObject) selection.get(0)).hasInternalPicture();
    }
}
