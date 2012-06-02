/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

import java.awt.FlowLayout;
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

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeelChangeListener;
import net.sourceforge.atunes.model.ILookAndFeelManager;

public class TextToggleButtonFlowPanel extends JPanel implements ILookAndFeelChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3228209071274604067L;

	private List<TextToggleButton> buttons;

	private Map<String, JToggleButton> toggles;

	private ButtonGroup group;

	private JScrollPane scrollPane;

	private JPanel buttonContainer;

	private JButton leftButton;

	private JButton rightButton;
	
	private List<ItemListener> itemListeners;

	private ILookAndFeelManager lookAndFeelManager;
	
	/**
	 * Creates a new panel
	 * @param lookAndFeelManager
	 */
	public TextToggleButtonFlowPanel(ILookAndFeelManager lookAndFeelManager) {
		super();
		this.lookAndFeelManager = lookAndFeelManager;
		this.itemListeners = new ArrayList<ItemListener>();
		this.group = new ButtonGroup();
		this.buttons = new ArrayList<TextToggleButton>();
		this.toggles = new HashMap<String, JToggleButton>();
		buttonContainer = new JPanel(new FlowLayout(GuiUtils.getComponentOrientation().isLeftToRight() ? FlowLayout.LEFT : FlowLayout.RIGHT, 0, 0));
		scrollPane = new JScrollPane(buttonContainer);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		setLayout(new GridBagLayout());

		leftButton = new JButton();
		rightButton = new JButton();
		
		setArrows();

		arrangeComponents();

		scrollPane.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				checkButtonsVisible();
			}
			
			@Override
			public void componentShown(ComponentEvent arg0) {
				checkButtonsVisible();
			}
		});

		leftButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				moveToLeft();
			}
		});

		rightButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
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
		c.insets = new Insets(1, 0, 1, 0);
		add(leftButton, c);

		c.gridx = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(0, 0, 0, 0);
		add(scrollPane, c);

		c.gridx = 2;
		c.weightx = 0;
		c.weighty = 1;
		c.insets = new Insets(1, 0, 1, 0);
		c.fill = GridBagConstraints.VERTICAL;
		add(rightButton, c);
	}

	/**
	 * Adds a new button to this panel
	 * @param icon
	 * @param tooltip
	 * @param action
	 */
	public void addButton(String name, String tooltip, Action action, Object userObject) {
		TextToggleButton button = new TextToggleButton(name, tooltip, action, userObject);
		buttons.add(button);
		addButtonToPanel(button);
	}

	/**
	 * Removes all buttons 
	 */
	public void clear() {
		buttonContainer.removeAll();
		this.group = new ButtonGroup();
		this.buttons = new ArrayList<TextToggleButton>();
		this.toggles = new HashMap<String, JToggleButton>();
		invalidate();
		revalidate();
		repaint();
		for (ItemListener l : itemListeners) {
			l.itemStateChanged(null);
		}
	}
	
	/**
	 * Removes a button
	 * @param index
	 */
	public void removeButton(int index) {
		TextToggleButton button = this.buttons.get(index);
		this.buttons.remove(index);
		JToggleButton toggle = this.toggles.get(button.getButtonName());
		this.toggles.remove(button.getButtonName());
		this.buttonContainer.remove(toggle);
		invalidate();
		revalidate();
		repaint();
		for (ItemListener l : itemListeners) {
			l.itemStateChanged(null);
		}
	}

	/**
	 * Sets button identified by name as selected
	 * @param buttonName
	 */
	public void setSelectedButton(String buttonName) {
		for (String name : toggles.keySet()) {
			if (name.equals(buttonName)) {
				toggles.get(name).setSelected(true);
				break;
			}
		}
	}

	/**
	 * Sets button identified by index
	 * @param buttonName
	 */
	public void setSelectedButton(int index) {
		TextToggleButton button = this.buttons.get(index);
		setSelectedButton(button.getButtonName());
	}

	/**
	 * Returns user object of selected toggle
	 * @return
	 */
	public Object getSelectedItem() {
		for (String itemKey : toggles.keySet()) {
			if (toggles.get(itemKey).isSelected()) {
				for (TextToggleButton button : buttons) {
					if (button.getButtonName().equals(itemKey)) {
						return button.getUserObject();
					}
				}
			}
		}
		return null;
	}

	private void addButtonToPanel(final TextToggleButton button) {
		JToggleButton toggle = new JToggleButton(button.getButtonName());
		toggle.setToolTipText(button.getTooltip());
		// Use action listener to encapsulate action to avoid toggle button use text and icon from action object
		toggle.addActionListener(new TextToggleButtonActionListener(button));
		group.add(toggle);
		this.toggles.put(button.getButtonName(), toggle);
		
		buttonContainer.add(toggle);
		
		invalidate();
		revalidate();
		repaint();
	}


	@Override
	public void lookAndFeelChanged() {
		removeAll();
		group = new ButtonGroup();
		for (TextToggleButton button : buttons) {
			addButtonToPanel(button);
		}
		setArrows();
	}
	
	private void setArrows() {
		leftButton.setIcon(Context.getBean("arrowLeftImageIcon", IIconFactory.class).getIcon(lookAndFeelManager.getCurrentLookAndFeel().getPaintForSpecialControls()));
		rightButton.setIcon(Context.getBean("arrowRightImageIcon", IIconFactory.class).getIcon(lookAndFeelManager.getCurrentLookAndFeel().getPaintForSpecialControls()));
	}

	/**
	 * Adds an item listener
	 * @param l
	 */
	public void addItemListener(ItemListener l) {
		itemListeners.add(l);
	}

	/**
	 * Returns index of button or -1
	 * @param name
	 * @return
	 */
	public int getIndexOfButton(String name) {
		for (TextToggleButton button : buttons) {
			if (button.getButtonName().equals(name)) {
				return buttons.indexOf(button); 
			}
		}
		return -1;
	}

	/**
	 * Renames button
	 * @param index
	 * @param newName
	 */
	public void renameButton(int index, String newName) {
		TextToggleButton button = this.buttons.get(index);
		if (button != null) {
			JToggleButton toggle = this.toggles.get(button.getButtonName());
			this.toggles.remove(button.getButtonName());
			button.setButtonName(newName);
			this.toggles.put(newName, toggle);
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
	private void move(int move) {
		Rectangle r = scrollPane.getVisibleRect();
		r = new Rectangle(r.x + move, r.y, r.width, r.height);
		scrollPane.getViewport().scrollRectToVisible(r);
	}

	/**
	 * 
	 */
	private void checkButtonsVisible() {
		Rectangle r1 = buttonContainer.getBounds();
		Rectangle r2 = scrollPane.getVisibleRect();
		boolean allButtonsVisible = r1.width <= r2.width;
		leftButton.setVisible(!allButtonsVisible);
		rightButton.setVisible(!allButtonsVisible);
	}


}
