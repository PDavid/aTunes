package net.sourceforge.atunes.gui.views.decorators;

import java.awt.Component;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.TreeCellDecorator;
import net.sourceforge.atunes.kernel.modules.repository.data.Genre;

public class GenreTreeCellDecorator extends TreeCellDecorator {

    @Override
    public Component decorateTreeCellComponent(Component component, Object userObject) {
        if (userObject instanceof Genre) {
            ((JLabel) component).setIcon(Images.getImage(Images.GENRE));
        }
        return component;
    }

}
