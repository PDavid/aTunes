package net.sourceforge.atunes.gui.views.controls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Creates JToggleButton for a flow panel
 * 
 * @author alex
 * 
 */
public class ToggleButtonOfFlowPanelCreator {

	private ILookAndFeelManager lookAndFeelManager;

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	JToggleButton createButton(final boolean iconOnly,
			final ToggleButtonOfFlowPanel button) {

		final ILookAndFeel laf = this.lookAndFeelManager
				.getCurrentLookAndFeel();
		JToggleButton toggle = new JToggleButton(iconOnly ? ""
				: StringUtils.getFirstChars(button.getButtonName(), 30, true));
		toggle.setToolTipText(button.getTooltip());
		toggle.setFocusPainted(false);
		toggle.setForeground(laf.getPaintForSpecialControls());

		laf.putClientProperties(toggle);

		if (button.getIcon() != null) {
			toggle.setIcon(button.getIcon().getIcon(
					laf.getPaintForSpecialControls()));
			toggle.setRolloverIcon(button.getIcon().getIcon(
					laf.getPaintForSpecialControlsRollover()));
			toggle.setSelectedIcon(button.getIcon().getIcon(
					laf.getPaintForSpecialControlsRollover()));
		}

		// Use action listener to encapsulate action to avoid toggle button use
		// text and icon from action object
		toggle.addActionListener(new ToggleButtonActionListener(button));

		if (!iconOnly) {
			toggle.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(final ChangeEvent evt) {
					updateForeground(false, laf, evt);
				}
			});
			toggle.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(final MouseEvent evt) {
					updateForeground(true, laf, evt);
				}

				@Override
				public void mouseExited(final MouseEvent evt) {
					updateForeground(false, laf, evt);
				}
			});
		}

		return toggle;
	}

	private void updateForeground(final boolean rollover,
			final ILookAndFeel laf, final EventObject evt) {
		if (((JToggleButton) evt.getSource()).isSelected() || rollover) {
			((JToggleButton) evt.getSource()).setForeground(laf
					.getPaintForSpecialControlsRollover());
		} else {
			((JToggleButton) evt.getSource()).setForeground(laf
					.getPaintForSpecialControls());
		}
	}
}
