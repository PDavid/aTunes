package net.sourceforge.atunes.gui.views.decorators;

import java.awt.Component;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.gui.lookandfeel.TreeCellDecorator;
import net.sourceforge.atunes.kernel.modules.repository.data.Album;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;
import net.sourceforge.atunes.kernel.modules.repository.data.Genre;

public class UnknownElementTreeCellDecorator extends TreeCellDecorator {
	
	@Override
	public Component decorateTreeCellComponent(Component component, Object userObject) {
        if (userObject.toString() != null) {
            if (Artist.isUnknownArtist(userObject.toString()) || Album.isUnknownAlbum(userObject.toString()) || Genre.isUnknownGenre(userObject.toString())) {
                ((JLabel)component).setForeground(ColorDefinitions.GENERAL_UNKNOWN_ELEMENT_FOREGROUND_COLOR);
            }
        }
        return component;
	}

}
