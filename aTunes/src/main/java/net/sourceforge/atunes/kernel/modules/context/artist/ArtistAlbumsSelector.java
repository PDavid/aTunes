/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.context.artist;

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IContextPanelContent;
import net.sourceforge.atunes.model.IStateContext;

/**
 * Selects between albums list or albums flow
 * 
 * @author alex
 * 
 */
public final class ArtistAlbumsSelector {

    private ArtistAlbumsSelector() {
    }

    /**
     * Selects albums content
     * 
     * @param beanFactory
     * @param stateContext
     * @return
     */
    public static IContextPanelContent<?> getContextPanelContent(
	    final IBeanFactory beanFactory, final IStateContext stateContext) {
	return beanFactory
		.getBean(
			stateContext.isShowContextAlbumsInGrid() ? "artistAlbumsFlowContent"
				: "artistAlbumsContent",
			IContextPanelContent.class);
    }
}
