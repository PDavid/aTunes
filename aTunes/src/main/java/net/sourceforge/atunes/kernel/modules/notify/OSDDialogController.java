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

package net.sourceforge.atunes.kernel.modules.notify;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import net.sourceforge.atunes.gui.WindowFader;
import net.sourceforge.atunes.gui.views.dialogs.OSDDialog;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectGenericImageFactory;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.GuiUtils;

final class OSDDialogController extends AbstractSimpleController<OSDDialog> {

    private WindowFader windowFader;
    private Point location;
    private Timer timer;
    private IOSManager osManager;
    private IAudioObjectGenericImageFactory audioObjectGenericImageFactory;
    private ILookAndFeelManager lookAndFeelManager;

    /**
     * Instantiates a new oSD dialog controller.
     * @param dialogControlled
     * @param state
     * @param osManager
     * @param audioObjectGenericImageFactory
     * @param lookAndFeelManager
     */
    OSDDialogController(OSDDialog dialogControlled, IState state, IOSManager osManager, IAudioObjectGenericImageFactory audioObjectGenericImageFactory, ILookAndFeelManager lookAndFeelManager) {
        super(dialogControlled, state);
        addBindings();
        this.audioObjectGenericImageFactory = audioObjectGenericImageFactory;
        this.osManager = osManager;
        this.windowFader = new WindowFader(dialogControlled, 50);
        this.lookAndFeelManager = lookAndFeelManager;
    }

    @Override
	public void addBindings() {
        MouseListener listener = new OSDDialogMouseListener(getComponentControlled(), this);
        getComponentControlled().addMouseListener(listener);
    }

    /**
     * Stops animation and disposes osd dialog
     */
    void stopAnimation() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        windowFader.clear();
    }

    /**
     * Show osd.
     * 
     * @param audioObject
     *            the audio object
     */
    void showOSD(IAudioObject audioObject) {
        if (audioObject == null) {
            return;
        }

        // If the OSD is already visible stop animation
        stopAnimation();

        windowFader = new WindowFader(getComponentControlled(), 50);

        int x = 0;
        if (getState().getOsdHorizontalAlignment() == SwingConstants.CENTER) {
            x = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - getState().getOsdWidth() / 2;
        } else if (getState().getOsdHorizontalAlignment() == SwingConstants.LEFT) {
            x = 50;
        } else {
            x = Toolkit.getDefaultToolkit().getScreenSize().width - getState().getOsdWidth() - 50;
        }
        x = Math.max(x, 0);

        int y = 0;
        if (getState().getOsdVerticalAlignment() == SwingConstants.CENTER) {
            y = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - getComponentControlled().getHeight() / 2;
        } else if (getState().getOsdVerticalAlignment() == SwingConstants.TOP) {
            y = 20;
        } else {
            y = Toolkit.getDefaultToolkit().getScreenSize().height - 20 - getComponentControlled().getHeight();
        }

        location = new Point(x, y);

        // By default OSD image has shadow, unless it's a generic image
        getComponentControlled().setShadowBorder(true);

        ImageIcon i = audioObject.getImage(ImageSize.SIZE_MAX, osManager);
        if (i == null) {
            i = audioObjectGenericImageFactory.getGenericImage(audioObject, GenericImageSize.MEDIUM).getIcon(lookAndFeelManager.getCurrentLookAndFeel().getPaintForSpecialControls());
            getComponentControlled().setShadowBorder(false);
        }
        getComponentControlled().setImage(i);
        getComponentControlled().setLine1(audioObject.getTitleOrFileName());
        getComponentControlled().setLine2(audioObject.getAlbum());
        getComponentControlled().setLine3(audioObject.getArtist());

        getComponentControlled().setLocation(location);
        GuiUtils.setWindowOpacity(getComponentControlled(), 0);
        getComponentControlled().setRoundedBorders(true);

        getComponentControlled().setVisible(true);
        // see bug 1864517
        getComponentControlled().repaint();

        windowFader.fadeIn();
        timer = new Timer(getState().getOsdDuration() * 1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                windowFader.fadeOut();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
}
