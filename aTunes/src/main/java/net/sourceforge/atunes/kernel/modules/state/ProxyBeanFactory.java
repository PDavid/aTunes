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

package net.sourceforge.atunes.kernel.modules.state;

import net.sourceforge.atunes.model.IProxyBean;
import net.sourceforge.atunes.model.IProxyBeanFactory;

/**
 * Creates proxy beans
 * 
 * @author alex
 * 
 */
public class ProxyBeanFactory implements IProxyBeanFactory {

    @Override
    public IProxyBean getProxy(final String type, final String url,
	    final int port, final String user, final String password) {
	return new ProxyBean(type, url, port, user, password);
    }
}
