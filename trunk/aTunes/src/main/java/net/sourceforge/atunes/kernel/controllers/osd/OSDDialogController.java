/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import net.sourceforge.atunes.kernel.controllers.model.AbstractSimpleController;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.GuiUtils;

public final class OSDDialogController extends AbstractSimpleController<OSDDialog> {

    private WindowFader windowFader;
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
        MouseListener listener = new OSDDialogMouseListener(getComponentControlled(), this);
        getComponentControlled().addMouseListener(listener);
    }

    @Override
    protected void addStateBindings() {
        // Nothing to do
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

        if (audioObject == null || GuiHandler.getInstance().getFullScreenWindow().isVisible()) {
            return;
        }

        // If the OSD is already visible stop animation
        stopAnimation();

        windowFader = new WindowFader(getComponentControlled(), 50);

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
            y = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - getComponentControlled().getHeight() / 2;
        } else if (ApplicationState.getInstance().getOsdVerticalAlignment() == SwingConstants.TOP) {
            y = 20;
        } else {
            y = Toolkit.getDefaultToolkit().getScreenSize().height - 20 - getComponentControlled().getHeight();
        }

        location = new Point(x, y);

        // By default OSD image has shadow, unless it's a generic image
        getComponentControlled().setShadowBorder(true);

        ImageIcon i = audioObject.getImage(ImageSize.SIZE_MAX);
        if (i == null) {
            i = audioObject.getGenericImage(GenericImageSize.MEDIUM);
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
