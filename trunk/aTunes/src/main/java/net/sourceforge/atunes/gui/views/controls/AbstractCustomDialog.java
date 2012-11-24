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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JDialog;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;

/**
 * Abstract dialog
 * 
 * @author alex
 * 
 */
public abstract class AbstractCustomDialog extends JDialog implements IDialog {

	private static final long serialVersionUID = -4593025984520110706L;

	private ILookAndFeelManager lookAndFeelManager;

	private IControlsBuilder controlsBuilder;

	private final boolean modal;

	private final CloseAction closeAction;

	/**
	 * Instantiates a new custom modal dialog.
	 * 
	 * @param frame
	 * @param width
	 * @param height
	 */
	public AbstractCustomDialog(final IFrame frame, final int width,
			final int height) {
		this(frame, width, height, true, CloseAction.DISPOSE);
	}

	/**
	 * Instantiates a new custom dialog.
	 * 
	 * @param frame
	 * @param width
	 * @param height
	 * @param modal
	 * @param closeAction
	 */
	public AbstractCustomDialog(final IFrame frame, final int width,
			final int height, final boolean modal, final CloseAction closeAction) {
		super(frame.getFrame());
		setSize(width, height);
		setLocationRelativeTo(frame.getFrame().getWidth() == 0 ? null : frame
				.getFrame());
		this.modal = modal;
		this.closeAction = closeAction;
	}

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * Initializes dialog
	 */
	public void initializeDialog() {
		setUndecorated(getLookAndFeel().isDialogUndecorated());
		setModalityType(this.modal ? ModalityType.APPLICATION_MODAL
				: ModalityType.MODELESS);
		setDefaultCloseOperation(this.closeAction.getConstant());
		if (this.closeAction == CloseAction.DISPOSE) {
			GuiUtils.addDisposeActionWithEscapeKey(this, getRootPane());
		} else if (this.closeAction == CloseAction.HIDE) {
			GuiUtils.addCloseActionWithEscapeKey(this, getRootPane());
		}
		initialize();
	}

	/**
	 * @return look and feel
	 */
	protected ILookAndFeel getLookAndFeel() {
		return this.lookAndFeelManager.getCurrentLookAndFeel();
	}

	/**
	 * @return look and feel manager
	 */
	protected ILookAndFeelManager getLookAndFeelManager() {
		return this.lookAndFeelManager;
	}

	@Override
	public Component add(final Component comp) {
		if (comp instanceof JComponent) {
			((JComponent) comp).setOpaque(false);
		}
		Component c = super.add(comp);
		this.controlsBuilder.applyComponentOrientation(this);
		return c;
	}

	@Override
	public void hideDialog() {
		setVisible(false);
	}

	@Override
	public void showDialog() {
		setVisible(true);
	}

	@Override
	public void initialize() {
		// Empty method to override
	}

	/**
	 * @return controls builder
	 */
	protected final IControlsBuilder getControlsBuilder() {
		return this.controlsBuilder;
	}
}
