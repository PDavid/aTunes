package net.sourceforge.atunes.gui.views.decorators;

import java.awt.Component;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.TreeCellDecorator;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;
import net.sourceforge.atunes.kernel.modules.repository.favorites.FavoritesHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;

public class ArtistTreeCellDecorator extends TreeCellDecorator {
	
	@Override
	public Component decorateTreeCellComponent(Component component, Object userObject) {
        if (userObject instanceof Artist) {
            if (!ApplicationState.getInstance().isShowFavoritesInNavigator() || !FavoritesHandler.getInstance().getFavoriteArtistsInfo().containsValue(userObject)) {
            	((JLabel)component).setIcon(Images.getImage(Images.ARTIST));
            } else {
            	((JLabel)component).setIcon(Images.getImage(Images.ARTIST_FAVORITE));
            }
        }
        return component;
	}

}
