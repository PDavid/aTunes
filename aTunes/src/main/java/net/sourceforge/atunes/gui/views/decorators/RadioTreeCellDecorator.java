package net.sourceforge.atunes.gui.views.decorators;

import java.awt.Component;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.TreeCellDecorator;
import net.sourceforge.atunes.kernel.modules.radio.Radio;

public class RadioTreeCellDecorator extends TreeCellDecorator {
	
	@Override
	public Component decorateTreeCellComponent(Component component, Object userObject) {
        if (userObject instanceof Radio) {
            ((JLabel)component).setIcon(Images.getImage(Images.RADIO_LITTLE));
        }
        return component;
	}

}
