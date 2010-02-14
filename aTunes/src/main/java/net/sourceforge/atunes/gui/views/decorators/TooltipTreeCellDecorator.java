package net.sourceforge.atunes.gui.views.decorators;

import java.awt.Component;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.lookandfeel.TreeCellDecorator;
import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.TreeObject;

public class TooltipTreeCellDecorator extends TreeCellDecorator {
	
	@Override
	public Component decorateTreeCellComponent(Component component, Object userObject) {
        if (!ApplicationState.getInstance().isShowExtendedTooltip() || !ExtendedToolTip.canObjectBeShownInExtendedToolTip(userObject)) {
            if (userObject instanceof TreeObject) {
                ((JLabel)component).setToolTipText(((TreeObject) userObject).getToolTip());
            } else {
            	((JLabel)component).setToolTipText(userObject.toString());
            }
        } else {
            // If using extended tooltip we must set tooltip to null. If not will appear the tooltip of the parent node
        	((JLabel)component).setToolTipText(null);
        }
        return component;
	}

}
