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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * A panel to choose an item given a list of images
 * 
 * @author alex
 * 
 * @param <T>
 */
public final class ByImageChoosingPanel<T> extends ScrollableFlowPanel {

	/**
	 * An image representing an object
	 * 
	 * @author alex
	 * 
	 * @param <U>
	 */
	public static class ImageEntry<U> {

		private final U object;
		private final ImageIcon image;

		/**
		 * @param object
		 * @param image
		 */
		public ImageEntry(final U object, final ImageIcon image) {
			super();
			this.object = object;
			this.image = image;
		}

		/**
		 * @return object
		 */
		public U getObject() {
			return this.object;
		}

		/**
		 * @return image
		 */
		public ImageIcon getImage() {
			return this.image;
		}

	}

	private static class CustomJRadioButton<T> extends JRadioButton {

		private static final long serialVersionUID = -6933585654529381134L;

		private final T object;

		/**
		 * @param object
		 */
		public CustomJRadioButton(final T object) {
			this.object = object;
		}

		public T getObject() {
			return this.object;
		}
	}

	private static final long serialVersionUID = -3541046889511348904L;

	private final Set<CustomJRadioButton<T>> buttons = new HashSet<CustomJRadioButton<T>>();
	private final ButtonGroup buttonGroup = new ButtonGroup();

	/**
	 * @param entries
	 */
	public ByImageChoosingPanel(final List<? extends ImageEntry<T>> entries) {
		setLayout(new FlowLayout(FlowLayout.LEADING, 20, 20));
		init(entries);
		setOpaque(false);
	}

	private void init(final List<? extends ImageEntry<T>> entries) {
		for (ImageEntry<T> entry : entries) {
			JLabel imageLabel = new JLabel();
			final CustomJRadioButton<T> button = new CustomJRadioButton<T>(
					entry.getObject());
			button.setFocusPainted(false);
			imageLabel.setIcon(entry.getImage());
			this.buttons.add(button);
			this.buttonGroup.add(button);

			imageLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					button.setSelected(!button.isSelected());
				}
			});

			JPanel panel = createPanel(imageLabel, button);
			add(panel);
		}
	}

	private JPanel createPanel(final JLabel imageLabel,
			final CustomJRadioButton<T> button) {
		JPanel p = new JPanel(new GridBagLayout());
		p.setOpaque(false);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		p.add(imageLabel, c);
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		p.add(button, c);
		return p;
	}

	/**
	 * @param object
	 */
	public void setSelectedItem(final T object) {
		for (CustomJRadioButton<T> button : this.buttons) {
			if (button.getObject().equals(object)) {
				button.setSelected(true);
			}
		}
	}

	/**
	 * @return
	 */
	public T getSelectedItem() {
		for (CustomJRadioButton<T> button : this.buttons) {
			if (button.isSelected()) {
				return button.getObject();
			}
		}
		return null;
	}

}
