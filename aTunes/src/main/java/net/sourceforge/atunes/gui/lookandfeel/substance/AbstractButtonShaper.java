/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.gui.lookandfeel.substance;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.border.Border;

import org.pushingpixels.substance.api.shaper.StandardButtonShaper;
import org.pushingpixels.substance.api.shaper.SubstanceButtonShaper;
import org.pushingpixels.substance.internal.ui.SubstanceButtonUI;
import org.pushingpixels.substance.internal.utils.border.SubstanceButtonBorder;

/**
 * based on code from Xtreme Media Player
 * @author alex
 *
 */
public abstract class AbstractButtonShaper implements SubstanceButtonShaper {

	private static final class CustomSubstanceButtonBorder extends
	SubstanceButtonBorder {
		private CustomSubstanceButtonBorder(final Class<?> buttonShaperClass) {
			super(buttonShaperClass);
		}

		@Override
		public Insets getBorderInsets(final Component c) {
			return new Insets(0, 0, 0, 0);
		}
	}

	@Override
	public Dimension getPreferredSize(final AbstractButton button,
			final Dimension uiPreferredSize) {
		if (button.getClientProperty(SubstanceButtonUI.BORDER_COMPUTED) == null) {
			boolean isBorderComputing = (button
					.getClientProperty(SubstanceButtonUI.BORDER_COMPUTING) != null);
			Border border = button.getBorder();
			int uiw = uiPreferredSize.width;
			int uih = uiPreferredSize.height;
			Insets bi = border.getBorderInsets(button);
			if (!isBorderComputing) {
				button.setBorder(null);
			}
			uiPreferredSize.setSize(uiw - bi.left - bi.right, uih - bi.top
					- bi.bottom);

			if (!isBorderComputing) {
				button.setBorder(this.getButtonBorder(button));
				button.putClientProperty(SubstanceButtonUI.BORDER_COMPUTED, "");
			}
		}
		return uiPreferredSize;
	}

	@Override
	public Border getButtonBorder(final AbstractButton button) {
		return new CustomSubstanceButtonBorder(StandardButtonShaper.class);
	}

	@Override
	public boolean isProportionate() {
		return true;
	}

}
