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

package net.sourceforge.atunes.kernel.modules.tray;

import java.awt.CheckboxMenuItem;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;

import net.sourceforge.atunes.kernel.actions.ExitAction;
import net.sourceforge.atunes.kernel.actions.MuteAction;
import net.sourceforge.atunes.kernel.actions.PlayAction;
import net.sourceforge.atunes.kernel.actions.PlayNextAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.PlayPreviousAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.RepeatModeAction;
import net.sourceforge.atunes.kernel.actions.ShowAboutAction;
import net.sourceforge.atunes.kernel.actions.ShuffleModeAction;
import net.sourceforge.atunes.kernel.actions.StopCurrentAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.ToggleOSDSettingAction;
import net.sourceforge.atunes.model.ITrayIconFiller;
import net.sourceforge.atunes.utils.I18nUtils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class MacTrayIconFiller implements ITrayIconFiller, ApplicationContextAware {
	
	private ApplicationContext context;
	
	private MenuItem playMenuItem;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
		
	@Override
	public void fillTrayIcon(TrayIcon trayIcon) {
		PopupMenu popupmenu = trayIcon.getPopupMenu();

		popupmenu.add(getPlayMenuItem());
		popupmenu.add(getStopMenuItem());
		popupmenu.add(getPreviousMenuItem());
		popupmenu.add(getNextMenuItem());
		popupmenu.addSeparator();
		popupmenu.add(getMuteCheckBoxMenuItem());
		popupmenu.addSeparator();
		popupmenu.add(getShuffleCheckBoxMenuItem());
		popupmenu.add(getRepeatCheckBoxMenuItem());
		popupmenu.addSeparator();
		popupmenu.add(getShowOSDCheckBoxMenuItem());
		popupmenu.addSeparator();
		popupmenu.add(getAboutMenuItem());
		popupmenu.addSeparator();
		popupmenu.add(getExitMenuItem());
	}
	
	@Override
	public void setPlayMenuItemText(String text) {
		playMenuItem.setLabel(text);
	}
	
	/**
     * Getter of play menu item
     * 
     * @return
     */
    private MenuItem getPlayMenuItem() {
        if (playMenuItem == null) {
            playMenuItem = new MenuItem(I18nUtils.getString("PLAY"));
            playMenuItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					context.getBean(PlayAction.class).actionPerformed(null);
				}
			});
        }
        return playMenuItem;
    }
	
    /**
     * Getter of stop menu item
     * 
     * @return
     */
    private MenuItem getStopMenuItem() {
        MenuItem mi = new MenuItem(I18nUtils.getString("STOP"));
        mi.addActionListener(context.getBean(StopCurrentAudioObjectAction.class));
        return mi;
    }

    /**
     * Getter of previous menu item
     * 
     * @return
     */
    private MenuItem getPreviousMenuItem() {
        MenuItem mi = new MenuItem(I18nUtils.getString("PREVIOUS"));
        mi.addActionListener(context.getBean(PlayPreviousAudioObjectAction.class));
        return mi;
    }

    /**
     * Getter for next menu item
     * 
     * @return
     */
    private MenuItem getNextMenuItem() {
        MenuItem mi = new MenuItem(I18nUtils.getString("NEXT"));
        mi.addActionListener(context.getBean(PlayNextAudioObjectAction.class));
        return mi;
    }

    /**
     * Getter for mute menu item
     * 
     * @return
     */
    private CheckboxMenuItem getMuteCheckBoxMenuItem() {
        CheckboxMenuItem mute = new CheckboxMenuItem(I18nUtils.getString("MUTE"));
        mute.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				AbstractAction action = context.getBean(MuteAction.class);
				action.putValue(AbstractAction.SELECTED_KEY, !(Boolean) action.getValue(AbstractAction.SELECTED_KEY));
				action.actionPerformed(null);
			}
		});
        boolean selected = (Boolean) context.getBean(MuteAction.class).getValue(AbstractAction.SELECTED_KEY);
        mute.setState(selected);
        return mute;
    }

    /**
     * Getter for shuffle menu item
     * 
     * @return
     */
    private CheckboxMenuItem getShuffleCheckBoxMenuItem() {
        CheckboxMenuItem mi = new CheckboxMenuItem(I18nUtils.getString("SHUFFLE"));
        mi.addItemListener(new ItemListener() {
        	@Override
        	public void itemStateChanged(ItemEvent e) {
				AbstractAction action = context.getBean(ShuffleModeAction.class);
				action.putValue(AbstractAction.SELECTED_KEY, !(Boolean) action.getValue(AbstractAction.SELECTED_KEY));
				action.actionPerformed(null);
        	}
        });
        boolean selected = (Boolean) context.getBean(ShuffleModeAction.class).getValue(AbstractAction.SELECTED_KEY);
        mi.setState(selected);
        return mi;
    }

    /**
     * Getter for repeat menu item
     */
    private CheckboxMenuItem getRepeatCheckBoxMenuItem() {
        CheckboxMenuItem mi = new CheckboxMenuItem(I18nUtils.getString("REPEAT"));
        mi.addItemListener(new ItemListener() {
        	@Override
        	public void itemStateChanged(ItemEvent e) {
				AbstractAction action = context.getBean(RepeatModeAction.class);
				action.putValue(AbstractAction.SELECTED_KEY, !(Boolean) action.getValue(AbstractAction.SELECTED_KEY));
				action.actionPerformed(null);
        	}
        });
        boolean selected = (Boolean) context.getBean(RepeatModeAction.class).getValue(AbstractAction.SELECTED_KEY);
        mi.setState(selected);
        return mi;
    }

    /**
     * Getter for showOSD menu item
     * 
     * @return
     */
    private CheckboxMenuItem getShowOSDCheckBoxMenuItem() {
        CheckboxMenuItem mi = new CheckboxMenuItem(I18nUtils.getString("SHOW_OSD"));
        mi.addItemListener(new ItemListener() {
        	@Override
        	public void itemStateChanged(ItemEvent e) {
				AbstractAction action = context.getBean(ToggleOSDSettingAction.class);
				action.putValue(AbstractAction.SELECTED_KEY, !(Boolean) action.getValue(AbstractAction.SELECTED_KEY));
				action.actionPerformed(null);
        	}
        });
        boolean selected = (Boolean) context.getBean(ToggleOSDSettingAction.class).getValue(AbstractAction.SELECTED_KEY);
        mi.setState(selected);
        return mi;
    }

    /**
     * Getter for about menu item
     * 
     * @return
     */
    private MenuItem getAboutMenuItem() {
        MenuItem mi = new MenuItem(I18nUtils.getString("ABOUT"));
        mi.addActionListener(context.getBean(ShowAboutAction.class));
        return mi;
    }

    /**
     * Getter for exit menu item
     * 
     * @return
     */
    private MenuItem getExitMenuItem() {
        MenuItem mi = new MenuItem(I18nUtils.getString("EXIT"));
        mi.addActionListener(context.getBean(ExitAction.class));
        return mi;
    }
}
