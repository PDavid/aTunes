/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Calls desktop to open a file
 * 
 * @author alex
 * 
 */
final class OpenFile implements Callable<Void> {
    private final File fileToOpen;

    OpenFile(final File fileToOpen) {
        this.fileToOpen = fileToOpen;
    }

    @Override
    public Void call() {
        try {
    	Desktop.getDesktop().open(fileToOpen);
        } catch (IOException e) {
    	Logger.error(e);
        }
        return null;
    }
}