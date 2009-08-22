/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.controllers.osd;

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
import net.sourceforge.atunes.kernel.controllers.model.WindowController;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.utils.GuiUtils;

/**
 * The Class OSDDialogController.
 */
public class OSDDialogController extends WindowController<OSDDialog> {

    WindowFader windowFader;
    private Point location;
    private Timer timer;

    /**
     * Instantiates a new oSD dialog controller.
     * 
     * @param dialogControlled
     *            the dialog controlled
     */
    public OSDDialogController(OSDDialog dialogControlled) {
        super(dialogControlled);
        addBindings();
        windowFader = new WindowFader(dialogControlled, 50);
    }

    @Override
    protected void addBindings() {
        MouseListener listener = new OSDDialogMouseListener(getWindowControlled(), this);
        getWindowControlled().addMouseListener(listener);
    }

    @Override
    protected void addStateBindings() {
        // Nothing to do
    }

    @Override
    protected OSDDialog getWindowControlled() {
        return super.getWindowControlled();
    }

    /**
     * Stops animation and disposes osd dialog
     */
    public void stopAnimation() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        windowFader.clear();
    }

    @Override
    protected void notifyReload() {
        // Nothing to do
    }

    /**
     * Show osd.
     * 
     * @param audioObject
     *            the audio object
     */
    public void showOSD(AudioObject audioObject) {

        if (audioObject == null || VisualHandler.getInstance().getFullScreenWindow().isVisible()) {
            return;
        }

        // If the OSD is already visible stop animation
        stopAnimation();

        windowFader = new WindowFader(getWindowControlled(), 50);

        int x = 0;
        if (ApplicationState.getInstance().getOsdHorizontalAlignment() == SwingConstants.CENTER) {
            x = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - ApplicationState.getInstance().getOsdWidth() / 2;
        } else if (ApplicationState.getInstance().getOsdHorizontalAlignment() == SwingConstants.LEFT) {
            x = 50;
        } else {
            x = Toolkit.getDefaultToolkit().getScreenSize().width - ApplicationState.getInstance().getOsdWidth() - 50;
        }
        x = Math.max(x, 0);

        int y = 0;
        if (ApplicationState.getInstance().getOsdVerticalAlignment() == SwingConstants.CENTER) {
            y = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - getWindowControlled().getHeight() / 2;
        } else if (ApplicationState.getInstance().getOsdVerticalAlignment() == SwingConstants.TOP) {
            y = 20;
        } else {
            y = Toolkit.getDefaultToolkit().getScreenSize().height - 20 - getWindowControlled().getHeight();
        }

        location = new Point(x, y);

        // By default OSD image has shadow, unless it's a generic image
        getWindowControlled().setShadowBorder(true);
        
        ImageIcon i;
        if (audioObject.canHaveCustomImages()) {
            i = audioObject.getCustomImage(-1, -1);
            if (i == null) {
                i = audioObject.getGenericImage(GenericImageSize.MEDIUM);
                getWindowControlled().setShadowBorder(false);
            }
        } else {
            i = audioObject.getGenericImage(GenericImageSize.MEDIUM);
            getWindowControlled().setShadowBorder(false);
        }
        getWindowControlled().setImage(i);
        getWindowControlled().setLine1(audioObject.getTitle());
        getWindowControlled().setLine2(audioObject.getAlbum());
        getWindowControlled().setLine3(audioObject.getArtist());

        getWindowControlled().setLocation(location);
        GuiUtils.setWindowOpacity(getWindowControlled(), 0);
        getWindowControlled().setRoundedBorders(true);

        getWindowControlled().setVisible(true);
        // see bug 1864517
        getWindowControlled().repaint();

        windowFader.fadeIn();
        timer = new Timer(ApplicationState.getInstance().getOsdDuration() * 1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                windowFader.fadeOut();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
}
