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

package net.sourceforge.atunes.gui.lookandfeel.substance;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTree;

import net.sourceforge.atunes.model.ITreeCellRendererCode;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTreeCellRenderer;

final class SubstanceLookAndFeelTreeCellRenderer extends SubstanceDefaultTreeCellRenderer {
    private final ITreeCellRendererCode code;

    private static final long serialVersionUID = 3830003466764008228L;

    SubstanceLookAndFeelTreeCellRenderer(ITreeCellRendererCode code) {
        this.code = code;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    	JComponent c = (JComponent) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        return code.getComponent(c, tree, value, sel, expanded, leaf, row, hasFocus);
    }
}