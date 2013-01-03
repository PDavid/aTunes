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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Enables or disables normalization
 * 
 * @author alex
 * 
 */
public class NormalizeModeAction extends ActionWithColorMutableIcon {

    private final class WarningActionListener implements ActionListener {

	private boolean showWarning;

	@Override
	public void actionPerformed(final ActionEvent arg0) {
	    if (showWarning) {
		putValue(SMALL_ICON, normalizationIcon.getIcon(getLookAndFeel()
			.getPaintForSpecialControls()));
	    } else {
		putValue(SMALL_ICON, warningIcon.getIcon(getLookAndFeel()
			.getPaintForSpecialControls()));
	    }
	    showWarning = !showWarning;
	}
    }

    private static final long serialVersionUID = 6993968558006979367L;

    private Timer timer;

    private IPlayerHandler playerHandler;

    private IIconFactory normalizationIcon;

    private IIconFactory warningIcon;

    private IStatePlayer statePlayer;

    /**
     * @param statePlayer
     */
    public void setStatePlayer(final IStatePlayer statePlayer) {
	this.statePlayer = statePlayer;
    }

    /**
     * @param warningIcon
     */
    public void setWarningIcon(final IIconFactory warningIcon) {
	this.warningIcon = warningIcon;
    }

    /**
     * @param normalizationIcon
     */
    public void setNormalizationIcon(final IIconFactory normalizationIcon) {
	this.normalizationIcon = normalizationIcon;
    }

    /**
     * @param playerHandler
     */
    public void setPlayerHandler(final IPlayerHandler playerHandler) {
	this.playerHandler = playerHandler;
    }

    /**
     * Default constructor
     */
    public NormalizeModeAction() {
	super(I18nUtils.getString("NORMALIZE"));
    }

    @Override
    protected void initialize() {
	super.initialize();
	putValue(SELECTED_KEY, statePlayer.isUseNormalisation());
	timer = new Timer(1000, new WarningActionListener());
	if (statePlayer.isUseNormalisation()) {
	    timer.start();
	}
    }

    @Override
    protected void executeAction() {
	boolean isNormalized = !statePlayer.isUseNormalisation();
	statePlayer.setUseNormalisation(isNormalized);
	playerHandler.applyNormalization();
	if (timer.isRunning()) {
	    timer.stop();
	    putValue(SMALL_ICON, normalizationIcon.getIcon(getLookAndFeel()
		    .getPaintForSpecialControls()));
	} else {
	    timer.start();
	}
    }

    @Override
    public IColorMutableImageIcon getIcon(final ILookAndFeel lookAndFeel) {
	return normalizationIcon.getColorMutableIcon();
    }
}
