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

package net.sourceforge.atunes.kernel.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.Timer;

import net.sourceforge.atunes.gui.images.NormalizationImageIcon;
import net.sourceforge.atunes.gui.images.WarningImageIcon;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.utils.I18nUtils;

public class NormalizeModeAction extends ActionWithColorMutableIcon {

    private static final long serialVersionUID = 6993968558006979367L;

    private Timer timer;
    
    private IPlayerHandler playerHandler;
    
    /**
     * @param playerHandler
     */
    public void setPlayerHandler(IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

    public NormalizeModeAction() {
        super(I18nUtils.getString("NORMALIZE"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("NORMALIZE"));
        timer = new Timer(1000, new ActionListener() {
            boolean showWarning;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (showWarning) {
                    putValue(SMALL_ICON, NormalizationImageIcon.getIcon(getLookAndFeel().getPaintForSpecialControls(), getLookAndFeel()));
                } else {
                    putValue(SMALL_ICON, WarningImageIcon.getIcon(getLookAndFeel().getPaintForSpecialControls(), getLookAndFeel()));
                }
                showWarning = !showWarning;
            }
        });
    }
    
    @Override
    protected void initialize() {
    	super.initialize();
        putValue(SELECTED_KEY, getState().isUseNormalisation());
        if (getState().isUseNormalisation()) {
            timer.start();
        }
    }

    @Override
    protected void executeAction() {
        boolean isNormalized = !getState().isUseNormalisation();
        getState().setUseNormalisation(isNormalized);
        playerHandler.applyNormalization();
        if (timer.isRunning()) {
            timer.stop();
            putValue(SMALL_ICON, NormalizationImageIcon.getIcon(getLookAndFeel().getPaintForSpecialControls(), getLookAndFeel()));
        } else {
            timer.start();
        }
    }
    
    @Override
    public IColorMutableImageIcon getIcon(final ILookAndFeel lookAndFeel) {
    	return new IColorMutableImageIcon() {
			
			@Override
			public ImageIcon getIcon(Color paint) {
				return  NormalizationImageIcon.getIcon(paint, lookAndFeel);
			}
		};
    }

}
