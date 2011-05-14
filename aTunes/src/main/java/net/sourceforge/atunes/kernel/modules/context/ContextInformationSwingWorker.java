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

package net.sourceforge.atunes.kernel.modules.context;

import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

/**
 * This class implements a special SwingWorker used to retrieve data from a
 * ContextInformationDataSource and show it in a ContextPanelContent
 * 
 * @author alex
 * 
 */
class ContextInformationSwingWorker extends SwingWorker<Map<String, ?>, Void> {

    /**
     * The context panel content where information must be shown after
     * retrieving data
     */
    private AbstractContextPanelContent content;

    /**
     * The context information data source used to retrieve information
     */
    private ContextInformationDataSource dataSource;

    /**
     * Parameters used to call data source
     */
    private Map<String, ?> parameters;

    /**
     * Constructor used to create a new ContextInformationSwingWorker
     * 
     * @param content
     * @param dataSource
     * @param parameters
     */
    ContextInformationSwingWorker(AbstractContextPanelContent content, ContextInformationDataSource dataSource, Map<String, ?> parameters) {
        this.content = content;
        this.dataSource = dataSource;
        this.parameters = parameters;
    }

    @Override
    protected Map<String, ?> doInBackground() throws Exception {
   		return dataSource.getData(parameters);
    }

    @Override
    protected void done() {
        super.done();
        try {
            content.updateContentWithDataSourceResult(get());
            // Enable task pane so user can expand or collapse
            content.getParentTaskPane().setEnabled(true);
            // After update data expand content
            content.getParentTaskPane().setCollapsed(false);
        } catch (CancellationException e) {
            // thrown when cancelled
        } catch (InterruptedException e) {
            new Logger().error(LogCategories.CONTEXT, e);
        } catch (ExecutionException e) {
            new Logger().error(LogCategories.CONTEXT, e);
        }
    }
}
