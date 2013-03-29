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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.atunes.gui.views.controls.LookAndFeelAwareButton;
import net.sourceforge.atunes.gui.views.controls.LookAndFeelAwareLabel;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFilterPanel;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Panel to show a filter
 * 
 * @author alex
 * 
 */
public class FilterPanel extends JPanel implements IFilterPanel {

	private final class ClearButton extends LookAndFeelAwareButton {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8082456165867297443L;
		private final Dimension size = new Dimension(16, 16);

		private ClearButton(final ILookAndFeelManager lookAndFeelManager) {
			super(lookAndFeelManager);
		}

		@Override
		public Dimension getPreferredSize() {
			return this.size;
		}

		@Override
		public Dimension getMinimumSize() {
			return this.size;
		}

		@Override
		public Dimension getMaximumSize() {
			return this.size;
		}

		@Override
		protected void paintComponent(final java.awt.Graphics g) {
			if (!FilterPanel.this.filterApplied) {
				return;
			}
			Ellipse2D.Float e = new Ellipse2D.Float(-7, -7, 14, 14);
			Polygon p = new Polygon();
			p.addPoint(-4, -2);
			p.addPoint(-2, -4);
			p.addPoint(0, -2);
			p.addPoint(2, -4);
			p.addPoint(4, -2);
			p.addPoint(2, 0);
			p.addPoint(4, 2);
			p.addPoint(2, 4);
			p.addPoint(0, 2);
			p.addPoint(-2, 4);
			p.addPoint(-4, 2);
			p.addPoint(-2, 0);

			Graphics2D g2 = (Graphics2D) g;
			g2.translate(getBounds().width / 2, getBounds().height / 2);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setPaint(FilterPanel.this.lookAndFeelManager
					.getCurrentLookAndFeel().getPaintForSpecialControls());
			Area a = new Area(e);
			a.subtract(new Area(p));
			g2.fill(a);
			g2.dispose();
		}
	}

	private static final long serialVersionUID = 1801321624657098000L;

	private JTextField filterTextField;
	private JButton clearButton;

	private boolean filterApplied;

	private ILookAndFeelManager lookAndFeelManager;

	private IIconFactory filterIcon;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param filterIcon
	 */
	public void setFilterIcon(final IIconFactory filterIcon) {
		this.filterIcon = filterIcon;
	}

	/**
	 * @param state
	 * @param lookAndFeelManager
	 */
	public FilterPanel() {
		super(new BorderLayout(3, 0));
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * Initializes panel
	 */
	public void initialize() {
		setMinimumSize(new Dimension(120, 0));
		this.filterTextField = this.controlsBuilder.createTextField();
		this.filterTextField.setColumns(8);
		this.filterTextField.setToolTipText(I18nUtils
				.getString("FILTER_TEXTFIELD_TOOLTIP"));
		this.clearButton = new ClearButton(this.lookAndFeelManager);

		LookAndFeelAwareLabel icon = new LookAndFeelAwareLabel(
				this.lookAndFeelManager, this.filterIcon.getColorMutableIcon());

		icon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

		add(icon, BorderLayout.WEST);
		add(this.filterTextField, BorderLayout.CENTER);
		add(this.clearButton, BorderLayout.EAST);
	}

	/**
	 * @return the filterTextField
	 */
	@Override
	public JTextField getFilterTextField() {
		return this.filterTextField;
	}

	/**
	 * @return the clearButton
	 */
	@Override
	public JButton getClearButton() {
		return this.clearButton;
	}

	/**
	 * Called to update filter panel
	 * 
	 * @param filterApplied
	 */
	@Override
	public void setFilterApplied(final boolean filterApplied) {
		this.filterApplied = filterApplied;
		this.clearButton.repaint();
	}

	@Override
	public JPanel getSwingComponent() {
		return this;
	}
}