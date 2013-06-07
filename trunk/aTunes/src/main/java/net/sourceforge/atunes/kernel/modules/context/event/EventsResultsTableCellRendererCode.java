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

package net.sourceforge.atunes.kernel.modules.context.event;

import javax.swing.JComponent;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.context.ContextTableRowPanel;
import net.sourceforge.atunes.kernel.modules.context.ContextTableRowPanelRendererCode;
import net.sourceforge.atunes.model.IEvent;
import net.sourceforge.atunes.utils.StringUtils;

import org.joda.time.format.DateTimeFormat;

/**
 * Renderer for Youtube results in context panel
 * 
 * @author alex
 * 
 */
public class EventsResultsTableCellRendererCode extends
		ContextTableRowPanelRendererCode<IEvent> {

	@Override
	public ContextTableRowPanel<IEvent> createPanel(
			final JComponent superComponent, final IEvent value) {
		return getPanelForTableRenderer(value.getImage(),
				StringUtils.getString("<html>", value.getTitle(), "<br>",
						DateTimeFormat.longDate().print(value.getStartDate()),
						"<br>", value.getCity(), "<br>", value.getCountry(),
						"</html>"), Constants.THUMB_IMAGE_WIDTH);
	}

	@Override
	public String getCacheKeyControl(final IEvent v) {
		return v.getArtist();
	}
}
