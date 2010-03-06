package net.sourceforge.atunes.gui.views.decorators;

import java.awt.Component;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.TreeCellDecorator;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;

public class PodcastFeedTreeCellDecorator extends TreeCellDecorator {

    @Override
    public Component decorateTreeCellComponent(Component component, Object userObject) {
        if (userObject instanceof PodcastFeed) {
            ((JLabel) component).setIcon(Images.getImage(Images.RSS_LITTLE));
        }
        return component;
    }

}
