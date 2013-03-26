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

package net.sourceforge.atunes.gui.javafx;

import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * Abstract dialog encapsulating a JavaFX scene
 * 
 * While migrating from Swing to JavaFX scenes will be integrated in JFXPanel
 * objects
 * 
 * @author alex
 * 
 */
public abstract class JavaFXDialog implements IDialog {

	private static final String ESCAPE = "ESCAPE";

	private IFrame frame;

	private String title;

	private JDialog dialog;

	private boolean modal;

	private int width;

	private int height;

	private SpringFXMLLoader fxmlLoader;

	/**
	 * @param fxmlLoader
	 */
	public void setFxmlLoader(SpringFXMLLoader fxmlLoader) {
		this.fxmlLoader = fxmlLoader;
	}

	/**
	 * @param width
	 */
	public final void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return
	 */
	public final int getWidth() {
		return width;
	}

	/**
	 * @param height
	 */
	public final void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return height
	 */
	public final int getHeight() {
		return height;
	}

	/**
	 * @param modal
	 */
	public final void setModal(boolean modal) {
		this.modal = modal;
	}

	/**
	 * @param frame
	 */
	public final void setFrame(IFrame frame) {
		this.frame = frame;
	}

	@Override
	public void initialize() {
		if (Platform.isImplicitExit()) {
			Platform.setImplicitExit(false);
		}
	}

	@Override
	public void showDialog() {
		dialog = new JDialog(frame != null ? frame.getFrame() : null,
				modal ? ModalityType.DOCUMENT_MODAL : ModalityType.MODELESS);
		dialog.setTitle(title);
		dialog.setSize(width, height);
		dialog.setLocationRelativeTo(frame == null
				|| frame.getFrame().getWidth() == 0 ? null : frame.getFrame());
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addDisposeActionWithEscapeKey(dialog, dialog.getRootPane());

		final JFXPanel panel = new JFXPanel();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					panel.setScene(createScene());
				} catch (IOException e) {
					Logger.error(e);
				}
			}
		});
		dialog.setContentPane(panel);
		dialog.setVisible(true);
	}

	@Override
	public void hideDialog() {
		dialog.setVisible(false);
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
		if (dialog != null) {
			dialog.setTitle(title);
		}
	}

	/**
	 * Adds the dispose action with escape key.
	 * 
	 * @param window
	 *            the window
	 * @param rootPane
	 *            the root pane
	 */
	private void addDisposeActionWithEscapeKey(final Window window,
			final JRootPane rootPane) {
		// Handle escape key to close the window

		KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action disposeAction = new AbstractAction() {
			private static final long serialVersionUID = 0L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				window.dispose();
			}
		};
		rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape,
				ESCAPE);
		rootPane.getActionMap().put(ESCAPE, disposeAction);
	}

	/**
	 * Loads an fxml and creates a scene
	 * 
	 * @param fxml
	 * @return
	 * @throws IOException
	 */
	protected Scene loadScene(String fxml) throws IOException {
		Parent root = (Parent) fxmlLoader.load(this, fxml,
				I18nUtils.getLanguageBundle());
		Scene scene = new Scene(root, getWidth(), getHeight());
		scene.getStylesheets().add("styles/style.css");
		return scene;
	}

	/**
	 * @return JavaFX scene to show in this dialog
	 * @throws IOException
	 */
	protected abstract Scene createScene() throws IOException;

	/**
	 * Closes dialog
	 */
	public void close() {
		GuiUtils.callInEventDispatchThreadLater(new Runnable() {
			@Override
			public void run() {
				if (dialog != null) {
					dialog.dispose();
				}
			}
		});
	}

	/**
	 * Sets a callback to be called when dialog is closed
	 * 
	 * @param javaFXDialogController
	 */
	public void setCloseDialogCallback(
			final JavaFXDialogController javaFXDialogController) {
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				javaFXDialogController.dialogClosed();
			}
		});
	}
}
