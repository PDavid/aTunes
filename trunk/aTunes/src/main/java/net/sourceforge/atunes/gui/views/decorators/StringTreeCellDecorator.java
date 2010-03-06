package net.sourceforge.atunes.gui.views.decorators;

import java.awt.Component;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.TreeCellDecorator;
import net.sourceforge.atunes.utils.I18nUtils;

public class StringTreeCellDecorator extends TreeCellDecorator {

    @Override
    public Component decorateTreeCellComponent(Component component, Object userObject) {
        if (userObject instanceof String) {
            String text = ((String) userObject);
            if (text.equals(I18nUtils.getString("REPOSITORY"))) {
                ((JLabel) component).setIcon(Images.getImage(Images.AUDIO_FILE_LITTLE));
            } else if (text.equals(I18nUtils.getString("DEVICE"))) {
                ((JLabel) component).setIcon(Images.getImage(Images.DEVICE));
            } else if (text.equals(I18nUtils.getString("ARTISTS"))) {
                ((JLabel) component).setIcon(Images.getImage(Images.ARTIST));
            } else if (text.equals(I18nUtils.getString("ALBUMS"))) {
                ((JLabel) component).setIcon(Images.getImage(Images.ALBUM));
            } else if (text.equals(I18nUtils.getString("SONGS"))) {
                ((JLabel) component).setIcon(Images.getImage(Images.AUDIO_FILE_LITTLE));
            } else if (text.equals(I18nUtils.getString("FAVORITES"))) {
                ((JLabel) component).setIcon(Images.getImage(Images.FAVORITE));
            } else if (text.equals(I18nUtils.getString("PODCAST_FEEDS"))) {
                ((JLabel) component).setIcon(Images.getImage(Images.RSS_LITTLE));
            } else if (text.equals(I18nUtils.getString("RADIO"))) {
                ((JLabel) component).setIcon(Images.getImage(Images.RADIO_LITTLE));
            } else {
                // For radio view
                ((JLabel) component).setIcon(Images.getImage(Images.FOLDER));
            }

            ((JLabel) component).setToolTipText(null);
        }
        return component;
    }

}
