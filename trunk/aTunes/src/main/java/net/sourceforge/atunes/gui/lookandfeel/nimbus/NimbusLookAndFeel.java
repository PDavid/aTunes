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

package net.sourceforge.atunes.gui.lookandfeel.nimbus;

import java.util.List;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.sourceforge.atunes.gui.lookandfeel.AbstractLookAndFeel;
import net.sourceforge.atunes.misc.log.Logger;

public class NimbusLookAndFeel extends AbstractLookAndFeel {

    @Override
    public String getName() {
        return "Nimbus";
    }

    @Override
    public String getDescription() {
        return "Nimbus Look And Feel";
    }

    @Override
    public List<String> getSkins() {
        return null;
    }

    @Override
    public void initializeLookAndFeel() {
    }

    @Override
    public String getDefaultSkin() {
        return null;
    }

    @Override
    public void setLookAndFeel(String skin) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException e) {
            new Logger().internalError(e);
        } catch (InstantiationException e) {
            new Logger().internalError(e);
        } catch (IllegalAccessException e) {
            new Logger().internalError(e);
        } catch (UnsupportedLookAndFeelException e) {
            new Logger().internalError(e);
        }
    }
}
