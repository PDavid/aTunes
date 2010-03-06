package net.sourceforge.atunes.gui.views.decorators;

import java.awt.Component;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.gui.lookandfeel.TreeCellDecorator;
import net.sourceforge.atunes.kernel.modules.repository.tags.IncompleteTagsChecker;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.TreeObject;

public class IncompleteTagsTreeCellDecorator extends TreeCellDecorator {

    @Override
    public Component decorateTreeCellComponent(Component component, Object userObject) {
        if (ApplicationState.getInstance().isHighlightIncompleteTagElements() && userObject instanceof TreeObject) {
            if (IncompleteTagsChecker.hasIncompleteTags((TreeObject) userObject)) {
                ((JLabel) component).setForeground(ColorDefinitions.GENERAL_UNKNOWN_ELEMENT_FOREGROUND_COLOR);
            }
        }
        return component;
    }

}
