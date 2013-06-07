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

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.context.ContextTable;
import net.sourceforge.atunes.kernel.modules.context.ContextTableAction;
import net.sourceforge.atunes.model.IEvent;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Content to show events for the artist
 * 
 * @author alex
 * 
 */
public class EventsContent extends
		AbstractContextPanelContent<EventsDataSource> {

	private ContextTable eventsResultTable;

	@Override
	public String getContentName() {
		return I18nUtils.getString("EVENTS");
	}

	@Override
	public void updateContentFromDataSource(final EventsDataSource source) {
		((EventsResultTableModel) this.eventsResultTable.getModel())
				.setEntries(source.getEvents());
	}

	@Override
	public void clearContextPanelContent() {
		super.clearContextPanelContent();
		((EventsResultTableModel) this.eventsResultTable.getModel())
				.setEntries(null);
	}

	@Override
	public Component getComponent() {
		// Create components
		this.eventsResultTable = getBeanFactory().getBean(ContextTable.class);
		this.eventsResultTable.setModel(new EventsResultTableModel());
		this.eventsResultTable.addContextRowPanel(getBeanFactory().getBean(
				EventsResultsTableCellRendererCode.class));

		ContextTableAction<IEvent> action = getBeanFactory().getBean(
				OpenEventAction.class);
		List<ContextTableAction<?>> list = new ArrayList<ContextTableAction<?>>();
		list.add(action);

		this.eventsResultTable.setRowActions(list);
		return this.eventsResultTable;
	}

	@Override
	public List<Component> getOptions() {
		return null;
	}
}
