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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;

import net.sourceforge.atunes.model.IButtonPanel;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeelChangeListener;
import net.sourceforge.atunes.model.ILookAndFeelManager;

/**
 * A panel with toggle buttons
 * 
 * @author alex
 * 
 */
public class ToggleButtonFlowPanel extends JPanel implements
		ILookAndFeelChangeListener, IButtonPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3228209071274604067L;

	private List<ToggleButtonOfFlowPanel> buttons;

	private Map<ToggleButtonOfFlowPanel, JToggleButton> toggles;

	private ButtonGroup group;

	private JScrollPane scrollPane;

	private JPanel buttonContainer;

	private JButton leftButton;

	private JButton rightButton;

	private List<ItemListener> itemListeners;

	private ILookAndFeelManager lookAndFeelManager;

	private boolean iconOnly;

	private IIconFactory arrowLeftImageIcon;

	private IIconFactory arrowRightImageIcon;

	/**
	 * @param iconOnly
	 */
	@Override
	public void setIconOnly(final boolean iconOnly) {
		this.iconOnly = iconOnly;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * @param arrowLeftImageIcon
	 */
	public void setArrowLeftImageIcon(final IIconFactory arrowLeftImageIcon) {
		this.arrowLeftImageIcon = arrowLeftImageIcon;
	}

	/**
	 * @param arrowRightImageIcon
	 */
	public void setArrowRightImageIcon(final IIconFactory arrowRightImageIcon) {
		this.arrowRightImageIcon = arrowRightImageIcon;
	}

	/**
	 * Initializes component
	 */
	public void initialize() {
		this.itemListeners = new ArrayList<ItemListener>();
		this.group = new ButtonGroup();
		this.buttons = new ArrayList<ToggleButtonOfFlowPanel>();
		this.toggles = new HashMap<ToggleButtonOfFlowPanel, JToggleButton>();
		this.buttonContainer = new JPanel(new GridBagLayout());
		this.scrollPane = new JScrollPane(this.buttonContainer);
		this.scrollPane.setBorder(BorderFactory.createEmptyBorder());
		this.scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		setLayout(new GridBagLayout());

		this.leftButton = new JButton();
		this.rightButton = new JButton();

		setArrows();
		arrangeComponents();
		addListeners();
		setMinimumSize(new Dimension(10,
				this.leftButton.getMinimumSize().height + 3));
	}

	/**
	 * 
	 */
	private void addListeners() {
		this.scrollPane.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(final ComponentEvent e) {
				checkButtonsVisible();
			}

			@Override
			public void componentShown(final ComponentEvent arg0) {
				checkButtonsVisible();
			}
		});

		this.leftButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				moveToLeft();
			}
		});

		this.rightButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				moveToRight();
			}
		});
	}

	/**
	 * 
	 */
	private void arrangeComponents() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 1;
		c.fill = GridBagConstraints.VERTICAL;
		c.insets = new Insets(0, 0, 0, 1);
		add(this.leftButton, c);

		c.gridx = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 0, 0);
		add(this.scrollPane, c);

		c.gridx = 2;
		c.weightx = 0;
		c.weighty = 1;
		c.fill = GridBagConstraints.VERTICAL;
		c.insets = new Insets(0, 1, 0, 0);
		add(this.rightButton, c);
	}

	/**
	 * Adds a new button to this panel
	 * 
	 * @param name
	 * @param tooltip
	 * @param icon
	 * @param action
	 * @param userObject
	 */
	@Override
	public void addButton(final String name, final String tooltip,
			final IColorMutableImageIcon icon, final Action action,
			final Object userObject) {
		ToggleButtonOfFlowPanel button = new ToggleButtonOfFlowPanel(name,
				tooltip, icon, action, userObject);
		this.buttons.add(button);
		addButtonToPanel(button);
		checkButtonsVisible();
	}

	/**
	 * Removes all buttons
	 */
	@Override
	public void clear() {
		this.buttonContainer.removeAll();
		this.group = new ButtonGroup();
		this.buttons = new ArrayList<ToggleButtonOfFlowPanel>();
		this.toggles = new HashMap<ToggleButtonOfFlowPanel, JToggleButton>();
		invalidate();
		revalidate();
		repaint();
		for (ItemListener l : this.itemListeners) {
			l.itemStateChanged(null);
		}
	}

	/**
	 * Removes a button
	 * 
	 * @param index
	 */
	@Override
	public void removeButton(final int index) {
		ToggleButtonOfFlowPanel button = this.buttons.get(index);
		this.buttons.remove(index);
		this.toggles.remove(button);
		rearrangeButtons();
		invalidate();
		revalidate();
		repaint();
		for (ItemListener l : this.itemListeners) {
			l.itemStateChanged(null);
		}
	}

	/**
	 * Sets button identified by name as selected
	 * 
	 * @param buttonName
	 */
	@Override
	public void setSelectedButton(final String buttonName) {
		for (ToggleButtonOfFlowPanel button : this.toggles.keySet()) {
			if (button.getButtonName().equals(buttonName)) {
				this.toggles.get(button).setSelected(true);
				break;
			}
		}
	}

	/**
	 * Sets button identified by index
	 * 
	 * @param index
	 */
	@Override
	public void setSelectedButton(final int index) {
		setSelectedButton(this.buttons.get(index).getButtonName());
	}

	/**
	 * Returns user object of selected toggle
	 * 
	 * @return
	 */
	@Override
	public Object getSelectedItem() {
		for (ToggleButtonOfFlowPanel button : this.toggles.keySet()) {
			if (this.toggles.get(button).isSelected()) {
				return button.getUserObject();
			}
		}
		return null;
	}

	private void addButtonToPanel(final ToggleButtonOfFlowPanel button) {
		JToggleButton toggle = new JToggleButton(this.iconOnly ? ""
				: button.getButtonName());
		toggle.setToolTipText(button.getTooltip());
		if (button.getIcon() != null) {
			toggle.setIcon(button.getIcon().getIcon(
					this.lookAndFeelManager.getCurrentLookAndFeel()
							.getPaintForSpecialControls()));
		}
		// Use action listener to encapsulate action to avoid toggle button use
		// text and icon from action object
		toggle.addActionListener(new ToggleButtonActionListener(button));
		this.group.add(toggle);
		this.toggles.put(button, toggle);

		rearrangeButtons();

		invalidate();
		revalidate();
		repaint();
	}

	private void rearrangeButtons() {
		this.buttonContainer.removeAll();
		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 1;
		c.insets = new Insets(0, 1, 0, 0);
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		for (int i = 0; i < this.buttons.size(); i++) {
			c.gridx = i;
			if (i == this.buttons.size() - 1) {
				c.weightx = 1;
			}
			this.buttonContainer.add(this.toggles.get(this.buttons.get(i)), c);
		}
	}

	@Override
	public void lookAndFeelChanged() {
		for (Map.Entry<ToggleButtonOfFlowPanel, JToggleButton> button : this.toggles
				.entrySet()) {
			IColorMutableImageIcon icon = button.getKey().getIcon();
			if (icon != null) {
				button.getValue().setIcon(
						icon.getIcon(this.lookAndFeelManager
								.getCurrentLookAndFeel()
								.getPaintForSpecialControls()));
			}
		}
		setArrows();
	}

	private void setArrows() {
		this.leftButton.setIcon(this.arrowLeftImageIcon
				.getIcon(this.lookAndFeelManager.getCurrentLookAndFeel()
						.getPaintForSpecialControls()));
		this.rightButton.setIcon(this.arrowRightImageIcon
				.getIcon(this.lookAndFeelManager.getCurrentLookAndFeel()
						.getPaintForSpecialControls()));
	}

	/**
	 * Adds an item listener
	 * 
	 * @param l
	 */
	@Override
	public void addItemListener(final ItemListener l) {
		this.itemListeners.add(l);
	}

	/**
	 * Renames button
	 * 
	 * @param index
	 * @param newName
	 */
	@Override
	public void renameButton(final int index, final String newName) {
		ToggleButtonOfFlowPanel button = this.buttons.get(index);
		if (button != null) {
			JToggleButton toggle = this.toggles.get(button);
			this.toggles.remove(button);
			button.setButtonName(newName);
			this.toggles.put(button, toggle);
			toggle.setText(newName);
			toggle.setToolTipText(newName);
		}
	}

	/**
	 * 
	 */
	private void moveToLeft() {
		move(-30);
	}

	/**
	 * 
	 */
	private void moveToRight() {
		move(30);
	}

	/**
	 * 
	 */
	private void move(final int move) {
		Rectangle r = this.scrollPane.getVisibleRect();
		r = new Rectangle(r.x + move, r.y, r.width, r.height);
		this.scrollPane.getViewport().scrollRectToVisible(r);
	}

	/**
	 * 
	 */
	private void checkButtonsVisible() {
		Rectangle r1 = this.buttonContainer.getBounds();
		Rectangle r2 = this.scrollPane.getVisibleRect();
		boolean allButtonsVisible = r1.width <= r2.width;
		this.leftButton.setVisible(!allButtonsVisible);
		this.rightButton.setVisible(!allButtonsVisible);
	}

	/**
	 * @param event
	 * @return index of button selected in an action event
	 */
	@Override
	public int getIndexOfButtonSelected(final ActionEvent event) {
		JToggleButton toggle = (JToggleButton) event.getSource();
		for (Entry<ToggleButtonOfFlowPanel, JToggleButton> entry : this.toggles
				.entrySet()) {
			if (entry.getValue().equals(toggle)) {
				return this.buttons.indexOf(entry.getKey());
			}
		}
		return -1;
	}
}
