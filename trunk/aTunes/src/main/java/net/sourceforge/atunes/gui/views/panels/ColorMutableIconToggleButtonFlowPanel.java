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

import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.ILookAndFeelChangeListener;
import net.sourceforge.atunes.model.ILookAndFeelManager;

public class ColorMutableIconToggleButtonFlowPanel extends JPanel implements ILookAndFeelChangeListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3228209071274604067L;
	
	private List<ColorMutableIconToggleButton> buttons;
	
	private Map<String, JToggleButton> toggles;
	
	private ButtonGroup group;
	
	private ILookAndFeelManager lookAndFeelManager;
	
	/**
	 * Creates a new panel
	 * @param lookAndFeelManager
	 */
	public ColorMutableIconToggleButtonFlowPanel(ILookAndFeelManager lookAndFeelManager) {
		super();
		this.lookAndFeelManager = lookAndFeelManager;
		this.group = new ButtonGroup();
		this.buttons = new ArrayList<ColorMutableIconToggleButtonFlowPanel.ColorMutableIconToggleButton>();
		this.toggles = new HashMap<String, JToggleButton>();
		ComponentOrientation orientation = GuiUtils.getComponentOrientation();
		setLayout(new FlowLayout(orientation == ComponentOrientation.LEFT_TO_RIGHT ? FlowLayout.LEFT : FlowLayout.RIGHT, 0, 0));
		lookAndFeelManager.addLookAndFeelChangeListener(this);
	}
	
	/**
	 * Adds a new button to this panel
	 * @param icon
	 * @param tooltip
	 * @param action
	 */
	public void addButton(String name, IColorMutableImageIcon icon, String tooltip, Action action, Object userObject) {
		ColorMutableIconToggleButton button = new ColorMutableIconToggleButton(name, icon, tooltip, action, userObject);
		buttons.add(button);
		addButtonToPanel(button);
	}
	
	/**
	 * Removes all buttons 
	 */
	public void clear() {
		removeAll();
		this.group = new ButtonGroup();
		this.buttons = new ArrayList<ColorMutableIconToggleButtonFlowPanel.ColorMutableIconToggleButton>();
		this.toggles = new HashMap<String, JToggleButton>();
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
	 * Returns user object of selected toggle
	 * @return
	 */
	public Object getSelectedItem() {
		for (String itemKey : toggles.keySet()) {
			if (toggles.get(itemKey).isSelected()) {
				for (ColorMutableIconToggleButton button : buttons) {
					if (button.getButtonName().equals(itemKey)) {
						return button.getUserObject();
					}
				}
			}
		}
		return null;
	}
	
	private void addButtonToPanel(final ColorMutableIconToggleButton button) {
		JToggleButton toggle = new JToggleButton(button.getIcon().getIcon(lookAndFeelManager.getCurrentLookAndFeel().getPaintForSpecialControls()));
		toggle.setToolTipText(button.getTooltip());
		// Use action listener to encapsulate action to avoid toggle button use text and icon from action object
		toggle.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				button.getAction().actionPerformed(e);
			}
		});
		group.add(toggle);
		this.toggles.put(button.getButtonName(), toggle);
		add(toggle);
	}
	
	
	@Override
	public void lookAndFeelChanged() {
		removeAll();
		group = new ButtonGroup();
		for (ColorMutableIconToggleButton button : buttons) {
			addButtonToPanel(button);
		}
	}

	private static class ColorMutableIconToggleButton {
		
		private String buttonName;
		
		private IColorMutableImageIcon icon;
		
		private String tooltip;
		
		private Action action;
		
		private Object userObject;

		ColorMutableIconToggleButton(String buttonName, IColorMutableImageIcon icon, String tooltip, Action action, Object userObject) {
			super();
			this.buttonName = buttonName;
			this.icon = icon;
			this.tooltip = tooltip;
			this.action = action;
			this.userObject = userObject;
		}
		
		/**
		 * @return
		 */
		public String getButtonName() {
			return buttonName;
		}
		
		/**
		 * @return
		 */
		public IColorMutableImageIcon getIcon() {
			return icon;
		}
		
		/**
		 * @return
		 */
		public String getTooltip() {
			return tooltip;
		}
		
		/**
		 * @return
		 */
		public Action getAction() {
			return action;
		}
		
		/**
		 * @return
		 */
		public Object getUserObject() {
			return userObject;
		}
	}
}
