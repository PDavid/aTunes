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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.ClipboardFacade;

/**
 * Factory to build UI components
 * 
 * @author alex
 * 
 */
public class ControlsBuilder implements IControlsBuilder {

	private static final JTextComponent.KeyBinding[] MAC_OS_BINDINGS = {
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_C,
					InputEvent.CTRL_MASK),
					DefaultEditorKit.defaultKeyTypedAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_V,
					InputEvent.CTRL_MASK),
					DefaultEditorKit.defaultKeyTypedAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_X,
					InputEvent.CTRL_MASK),
					DefaultEditorKit.defaultKeyTypedAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_A,
					InputEvent.CTRL_MASK),
					DefaultEditorKit.defaultKeyTypedAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(
					KeyEvent.VK_LEFT, InputEvent.CTRL_MASK),
					DefaultEditorKit.defaultKeyTypedAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(
					KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK),
					DefaultEditorKit.defaultKeyTypedAction),

			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_C,
					InputEvent.META_MASK), DefaultEditorKit.copyAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_V,
					InputEvent.META_MASK), DefaultEditorKit.pasteAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_X,
					InputEvent.META_MASK), DefaultEditorKit.cutAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_A,
					InputEvent.META_MASK), DefaultEditorKit.selectAllAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(
					KeyEvent.VK_LEFT, InputEvent.META_MASK),
					DefaultEditorKit.beginLineAction),
			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(
					KeyEvent.VK_RIGHT, InputEvent.META_MASK),
					DefaultEditorKit.endLineAction) };

	private IOSManager osManager;

	private ILookAndFeelManager lookAndFeelManager;

	private ClipboardFacade clipboard;

	/**
	 * @param clipboard
	 */
	public void setClipboard(ClipboardFacade clipboard) {
		this.clipboard = clipboard;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}

	@Override
	public JTextArea createTextArea() {
		JTextArea textArea = new JTextArea();
		new EditionPopUpMenu(textArea, clipboard);
		return textArea;
	}

	@Override
	public JTextField createTextField() {
		JTextField textField = new JTextField();
		initializeTextField(textField);
		new EditionPopUpMenu(textField, clipboard);
		return textField;
	}

	@Override
	public JTextPane createTextPane(Integer alignJustified) {
		CustomTextPane textPane = new CustomTextPane(alignJustified);
		// Register look and feel change listener
		lookAndFeelManager.addLookAndFeelChangeListener(textPane);
		new EditionPopUpMenu(textPane, clipboard);
		return textPane;
	}

	@Override
	public JTextPane createReadOnlyTextPane(String text) {
		JTextPane textPane = createTextPane(null);
		textPane.setEditable(false);
		textPane.setBorder(BorderFactory.createEmptyBorder());
		textPane.setOpaque(false);
		textPane.setText(text);
		return textPane;
	}

	/**
	 * Sets custom properties to text fields
	 * 
	 * @param customTextField
	 */
	private void initializeTextField(final JTextField customTextField) {
		if (osManager.isMacOsX()) {
			JTextComponent.loadKeymap(customTextField.getKeymap(),
					MAC_OS_BINDINGS, customTextField.getActions());
		}
	}
}
