/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.kernel.modules.context;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.FadeInPanel;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IContextPanel;
import net.sourceforge.atunes.model.IContextPanelContent;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.utils.Logger;

import org.commonjukebox.plugins.model.PluginApi;

/**
 * This class represents a context panel shown in a context tab. Context panel
 * shows information related to the current audio object active in the
 * application
 * 
 * @author fleax
 */
@PluginApi
public abstract class AbstractContextPanel implements IContextPanel {

	private static final long serialVersionUID = 7870512266932745272L;

	/**
	 * Last AudioObject used to update context panel
	 */
	private IAudioObject audioObject;

	private Component component;

	private List<IContextPanelContent<?>> contents;

	private IContextHandler contextHandler;

	private ILookAndFeel lookAndFeel;

	private JProgressBar indeterminateProgressBar;

	@Override
	public final void updateContextPanel(final IAudioObject newAudioObject, final boolean forceUpdate) {
		// If the AudioObject is the same as used before to update panel then do nothing if forceUpdate is false
		if (!forceUpdate && this.audioObject != null && this.audioObject.equals(newAudioObject)) {
			return;
		}

		if (panelNeedsToBeUpdated(this.audioObject, newAudioObject)) {
			GuiUtils.callInEventDispatchThreadAndWait(new Runnable() {
				@Override
				public void run() {
					indeterminateProgressBar.setVisible(true);
				}
			});
			Logger.debug("Updating panel: ", getContextPanelName());
			ContextPanelContentUpdated updatesController = new ContextPanelContentUpdated(getContents().size(), new Runnable() {
				@Override
				public void run() {
					GuiUtils.callInEventDispatchThreadAndWait(new Runnable() {
						@Override
						public void run() {
							indeterminateProgressBar.setVisible(false);
						}
					});
					contextHandler.finishedContextPanelUpdate();
				}
			});
			for (IContextPanelContent<?> content : getContents()) {
				clearContextPanelContent(content);
				content.updateContextPanelContent(newAudioObject, updatesController);
			}
		}

		this.audioObject = newAudioObject;
	}

	private void clearContextPanelContent(final IContextPanelContent<?> content) {
		GuiUtils.callInEventDispatchThreadAndWait(new ClearContextPanelContent(content));
	}

	@Override
	public final void clearContextPanel() {
		Logger.debug("Clearing panel: ", getContextPanelName());
		for (IContextPanelContent<?> content : getContents()) {
			clearContextPanelContent(content);
		}
		audioObject = null;
	}

	@Override
	public final Component getUIComponent(final ILookAndFeel lookAndFeel) {
		if (component == null) {
			JPanel panel = new ContextPanelContainer(new GridBagLayout());
			indeterminateProgressBar = new JProgressBar();
			indeterminateProgressBar.setIndeterminate(true);
			indeterminateProgressBar.setVisible(false);
			indeterminateProgressBar.setBorder(BorderFactory.createEmptyBorder());
			panel.setOpaque(false);
			GridBagConstraints c = new GridBagConstraints();
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(10, 10, 10, 10);
			c.gridy = 1;
			for (IContextPanelContent<?> content : getContents()) {
				addContextPanelContent(lookAndFeel, panel, c, content);
			}

			c.insets = new Insets(20, 30, 20, 30);
			panel.add(indeterminateProgressBar, c);

			// Add a dummy panel at the end
			c.weighty = 1;
			c.anchor = GridBagConstraints.NORTH;
			c.gridy++;
			panel.add(new JPanel(), c);

			JScrollPane scrollPane = lookAndFeel.getScrollPane(panel);
			scrollPane.getVerticalScrollBar().setUnitIncrement(50);
			component = scrollPane;
		}
		return component;
	}

	/**
	 * @param lookAndFeel
	 * @param panel
	 * @param c
	 * @param content
	 */
	private void addContextPanelContent(final ILookAndFeel lookAndFeel, final JPanel panel, final GridBagConstraints c, final IContextPanelContent<?> content) {
		Component componentToAdd = content.getComponent();
		if (componentToAdd instanceof JComponent) {
			((JComponent) componentToAdd).setOpaque(false);
		}
		if (content.isScrollNeeded()) {
			componentToAdd = getContentWithScroll(lookAndFeel, componentToAdd);
		}

		FadeInPanel fadePanel = new FadeInPanel();
		fadePanel.setLayout(new GridLayout(1, 1));
		fadePanel.add(componentToAdd);
		content.setParentPanel(fadePanel);

		panel.add(fadePanel, c);
		c.gridy++;
	}

	/**
	 * @param lookAndFeel
	 * @param componentToAdd
	 * @return
	 */
	private JScrollPane getContentWithScroll(final ILookAndFeel lookAndFeel, final Component componentToAdd) {
		JScrollPane scroll = null;
		if (componentToAdd instanceof JTable) {
			scroll = lookAndFeel.getTableScrollPane((JTable)componentToAdd);
		} else if (componentToAdd instanceof JTree) {
			scroll = lookAndFeel.getTreeScrollPane((JTree)componentToAdd);
		} else if (componentToAdd instanceof JList) {
			scroll = lookAndFeel.getListScrollPane((JList)componentToAdd);
		} else {
			scroll = lookAndFeel.getScrollPane(componentToAdd);
		}
		// Set a minimum height
		scroll.setMinimumSize(new Dimension(0, 200));
		return scroll;
	}

	@Override
	public final String getTitle() {
		return getContextPanelTitle(contextHandler.getCurrentAudioObject());
	}

	@Override
	public final IColorMutableImageIcon getIcon() {
		return getContextPanelIcon(contextHandler.getCurrentAudioObject());
	}

	@Override
	public final boolean isEnabled() {
		if (contextHandler.getCurrentAudioObject() == null) {
			return false;
		}
		return true;
	}

	@Override
	public final boolean isVisible() {
		if (contextHandler.getCurrentAudioObject() == null) {
			return true;
		}
		return isPanelVisibleForAudioObject(contextHandler.getCurrentAudioObject());
	}

	@Override
	public List<Component> getOptions() {
		List<Component> components = new ArrayList<Component>();
		for (IContextPanelContent<?> content : getContents()) {
			List<Component> options = content.getOptions();
			if (options != null && !options.isEmpty()) {
				components.addAll(options);
			}
		}
		return components;
	}

	@Override
	public final Action getAction() {
		return new AbstractAction() {

			private static final long serialVersionUID = -9078018024869169623L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				contextHandler.setContextTab(getContextPanelName());
			}
		};
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result	+ ((getContextPanelName() == null) ? 0 : getContextPanelName().hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		IContextPanel other = (IContextPanel) obj;
		if (getContextPanelName() == null) {
			if (other.getContextPanelName() != null) {
				return false;
			}
		} else if (!getContextPanelName().equals(other.getContextPanelName())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return getTitle();
	}

	final List<IContextPanelContent<?>> getContents() {
		return contents;
	}

	@Override
	public final void setContents(final List<IContextPanelContent<?>> contents) {
		this.contents = contents;
	}

	/**
	 * @param contextHandler
	 */
	public void setContextHandler(final IContextHandler contextHandler) {
		this.contextHandler = contextHandler;
	}

	/**
	 * @param lookAndFeel
	 */
	public void setLookAndFeel(final ILookAndFeel lookAndFeel) {
		this.lookAndFeel = lookAndFeel;
	}

	/**
	 * @return
	 */
	protected ILookAndFeel getLookAndFeel() {
		return lookAndFeel;
	}
}
