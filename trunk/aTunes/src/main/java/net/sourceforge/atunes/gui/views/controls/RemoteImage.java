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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * A panel that loads image from an URL
 * 
 * @author alex
 * 
 */
public class RemoteImage extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6219312867578803780L;

	private static final String INDETERMINATE = "indeterminate";

	private static final String IMAGE = "image";

	private INetworkHandler networkHandler;

	private IBackgroundWorkerFactory backgroundWorkerFactory;

	private ITaskService taskService;

	private JLabel imageLabel;

	private JPanel panelWithProgressBar;

	private JProgressBar progressBar;

	private Future<?> future;

	/**
	 * @param taskService
	 */
	public void setTaskService(ITaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * @param backgroundWorkerFactory
	 */
	public void setBackgroundWorkerFactory(
			IBackgroundWorkerFactory backgroundWorkerFactory) {
		this.backgroundWorkerFactory = backgroundWorkerFactory;
	}

	/**
	 * @param networkHandler
	 */
	public void setNetworkHandler(INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}

	public void initialize() {
		this.imageLabel = new JLabel();
		this.panelWithProgressBar = new JPanel(new GridBagLayout());
		this.progressBar = new JProgressBar();
		this.progressBar.setIndeterminate(true);
		GridBagConstraints c = new GridBagConstraints();
		this.panelWithProgressBar.add(this.progressBar, c);
		setLayout(new CardLayout());
		add(INDETERMINATE, this.panelWithProgressBar);
		add(IMAGE, this.imageLabel);
		this.progressBar.setVisible(false);
		this.panelWithProgressBar.setVisible(false);
		this.imageLabel.setVisible(false);
	}

	/**
	 * Starts loading image
	 * 
	 * @param url
	 */
	public void loadImage(final String url) {
		if (future != null) {
			future.cancel(true);
		}
		IBackgroundWorker<ImageIcon, Void> worker = this.backgroundWorkerFactory
				.getWorker();
		worker.setActionsBeforeBackgroundStarts(new Runnable() {
			@Override
			public void run() {
				RemoteImage.this.progressBar.setVisible(true);
				RemoteImage.this.panelWithProgressBar.setVisible(true);
				RemoteImage.this.imageLabel.setVisible(false);
				((CardLayout) getLayout())
						.show(RemoteImage.this, INDETERMINATE);
			}
		});

		worker.setBackgroundActions(new Callable<ImageIcon>() {

			@Override
			public ImageIcon call() {
				if (!StringUtils.isEmpty(url)) {
					try {
						Image image = RemoteImage.this.networkHandler
								.getImage(RemoteImage.this.networkHandler
										.getConnection(url));
						if (image != null) {
							return new ImageIcon(image);
						}
					} catch (IOException e) {
						Logger.error(e);
					}
				}
				return null;
			}
		});

		worker.setActionsWhenDone(new IBackgroundWorker.IActionsWithBackgroundResult<ImageIcon>() {
			@Override
			public void call(final ImageIcon image) {
				RemoteImage.this.progressBar.setVisible(false);
				RemoteImage.this.panelWithProgressBar.setVisible(false);
				((CardLayout) getLayout()).show(RemoteImage.this, IMAGE);
				RemoteImage.this.imageLabel.setIcon(image);
				RemoteImage.this.imageLabel.setVisible(true);
			}
		});

		this.future = worker.execute(this.taskService);
	}

}
