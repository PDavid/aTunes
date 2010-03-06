package net.sourceforge.atunes.gui.views.decorators;

import java.awt.Component;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.TreeCellDecorator;
import net.sourceforge.atunes.kernel.modules.repository.data.Album;
import net.sourceforge.atunes.kernel.modules.repository.favorites.FavoritesHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;

public class AlbumTreeCellDecorator extends TreeCellDecorator {

    @Override
    public Component decorateTreeCellComponent(Component component, Object userObject) {
        if (userObject instanceof Album) {
            if (!ApplicationState.getInstance().isShowFavoritesInNavigator() || !FavoritesHandler.getInstance().getFavoriteAlbumsInfo().containsValue(userObject)) {
                ((JLabel) component).setIcon(Images.getImage(Images.ALBUM));
            } else {
                ((JLabel) component).setIcon(Images.getImage(Images.ALBUM_FAVORITE));
            }
        }
        return component;
    }

}
