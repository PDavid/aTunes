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

package net.sourceforge.atunes.kernel.modules.state;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.IProxyBean;
import net.sourceforge.atunes.model.IProxyBeanFactory;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The preferences panel for internet settings.
 */
public final class InternetPanel extends AbstractPreferencesPanel {

	private static final long serialVersionUID = -1872565673079044088L;

	private JRadioButton noProxyRadioButton;
	private JRadioButton httpProxyRadioButton;
	private JRadioButton socksProxyRadioButton;
	private JLabel proxyURLLabel;
	private JTextField proxyURL;
	private JLabel proxyPortLabel;
	private JTextField proxyPort;
	private JLabel proxyUserLabel;
	private JTextField proxyUser;
	private JLabel proxyPasswordLabel;
	private JPasswordField proxyPassword;

	private INetworkHandler networkHandler;

	private transient IProxyBeanFactory proxyBeanFactory;

	private IStateCore stateCore;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param stateCore
	 */
	public void setStateCore(IStateCore stateCore) {
		this.stateCore = stateCore;
	}

	/**
	 * @param networkHandler
	 */
	public void setNetworkHandler(INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}

	/**
	 * @param proxyBeanFactory
	 */
	public void setProxyBeanFactory(IProxyBeanFactory proxyBeanFactory) {
		this.proxyBeanFactory = proxyBeanFactory;
	}

	/**
	 * Instantiates a new internet panel.
	 */
	public InternetPanel() {
		super(I18nUtils.getString("INTERNET"));
	}

	/**
	 * Initializes panel
	 */
	public void initialize() {
		noProxyRadioButton = new JRadioButton(I18nUtils.getString("NO_PROXY"));
		noProxyRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enableProxySettings(false);
			}
		});
		httpProxyRadioButton = new JRadioButton(
				I18nUtils.getString("HTTP_PROXY"));
		httpProxyRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enableProxySettings(true);
			}
		});
		socksProxyRadioButton = new JRadioButton(
				I18nUtils.getString("SOCKS_PROXY"));
		socksProxyRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enableProxySettings(true);
			}
		});

		ButtonGroup group = new ButtonGroup();
		group.add(noProxyRadioButton);
		group.add(httpProxyRadioButton);
		group.add(socksProxyRadioButton);

		proxyURLLabel = new JLabel(I18nUtils.getString("HOST"));
		proxyURLLabel.setEnabled(false);
		proxyURL = controlsBuilder.createTextField();
		proxyURL.setColumns(15);

		proxyPortLabel = new JLabel(I18nUtils.getString("PORT"));
		proxyPortLabel.setEnabled(false);
		proxyPort = controlsBuilder.createTextField();
		proxyPort.setColumns(15);
		proxyPort.setEnabled(false);

		proxyUserLabel = new JLabel(I18nUtils.getString("USER"));
		proxyUserLabel.setEnabled(false);
		proxyUser = controlsBuilder.createTextField();
		proxyUser.setColumns(15);
		proxyUser.setEnabled(false);

		proxyPasswordLabel = new JLabel(I18nUtils.getString("PASSWORD"));
		proxyPasswordLabel.setEnabled(false);
		proxyPassword = new JPasswordField(15);
		proxyPassword.setEnabled(false);

		arrangePanel();
	}

	/**
	 * 
	 */
	private void arrangePanel() {
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.insets = new Insets(10, 0, 10, 10);
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(2, 5, 2, 5);
		c.anchor = GridBagConstraints.LINE_START;
		add(noProxyRadioButton, c);
		c.gridy = 1;
		add(httpProxyRadioButton, c);
		c.gridy = 2;
		add(socksProxyRadioButton, c);
		c.gridy = 3;
		c.weightx = 0;
		c.gridwidth = 1;
		add(proxyURLLabel, c);
		c.gridx = 1;
		c.weightx = 1;
		add(proxyURL, c);
		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 0;
		add(proxyPortLabel, c);
		c.gridx = 1;
		c.weightx = 1;
		add(proxyPort, c);
		c.gridx = 0;
		c.gridy = 5;
		c.weightx = 0;
		add(proxyUserLabel, c);
		c.gridx = 1;
		c.weightx = 1;
		add(proxyUser, c);
		c.gridx = 0;
		c.gridy = 6;
		c.weightx = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		add(proxyPasswordLabel, c);
		c.gridx = 1;
		c.weightx = 1;
		c.weighty = 1;
		add(proxyPassword, c);
	}

	/**
	 * Enable proxy settings.
	 * 
	 * @param v
	 *            the v
	 */
	void enableProxySettings(boolean v) {
		proxyURLLabel.setEnabled(v);
		proxyURL.setEnabled(v);
		proxyPortLabel.setEnabled(v);
		proxyPort.setEnabled(v);
		proxyUserLabel.setEnabled(v);
		proxyUser.setEnabled(v);
		proxyPasswordLabel.setEnabled(v);
		proxyPassword.setEnabled(v);
	}

	/**
	 * Gets the proxy.
	 * 
	 * @return the proxy
	 */
	private IProxyBean getProxy() {
		if (noProxyRadioButton.isSelected()) {
			return null;
		}

		int port = Integer.parseInt(proxyPort.getText());
		String type;
		if (httpProxyRadioButton.isSelected()) {
			type = IProxyBean.HTTP_PROXY;
		} else {
			type = IProxyBean.SOCKS_PROXY;
		}
		return proxyBeanFactory.getProxy(type, proxyURL.getText(), port,
				proxyUser.getText(), new String(proxyPassword.getPassword()));
	}

	@Override
	public boolean applyPreferences() {
		IProxyBean proxy = getProxy();
		stateCore.setProxy(proxy);
		networkHandler.updateProxy(proxy);
		return false;
	}

	@Override
	public void validatePanel() throws PreferencesValidationException {
		if (!noProxyRadioButton.isSelected()) {
			int port = 0;
			try {
				port = Integer.parseInt(proxyPort.getText());
			} catch (NumberFormatException e) {
				throw new PreferencesValidationException(
						I18nUtils.getString("INCORRECT_PORT"), e);
			}

			Socket s = null;
			try {
				// Test proxy
				s = new Socket(proxyURL.getText(), port);
			} catch (UnknownHostException e) {
				throw new PreferencesValidationException(
						I18nUtils.getString("INCORRECT_PROXY"), e);
			} catch (IOException e) {
				throw new PreferencesValidationException(
						I18nUtils.getString("INCORRECT_PROXY"), e);
			} finally {
				ClosingUtils.close(s);
			}
		}
	}

	/**
	 * Sets the configuration.
	 * 
	 * @param proxy
	 *            the new configuration
	 */
	private void setConfiguration(IProxyBean proxy) {
		enableProxySettings(proxy != null);
		if (proxy == null) {
			noProxyRadioButton.setSelected(true);
		} else if (proxy.getType().equals(IProxyBean.HTTP_PROXY)) {
			httpProxyRadioButton.setSelected(true);
		} else {
			socksProxyRadioButton.setSelected(true);
		}
		proxyURL.setText(proxy != null ? proxy.getUrl() : "");
		proxyPort.setText(proxy != null ? Integer.toString(proxy.getPort())
				: "");
		proxyUser.setText(proxy != null ? proxy.getUser() : "");
		proxyPassword.setText(proxy != null ? proxy.getPassword() : "");
	}

	@Override
	public void updatePanel() {
		setConfiguration(stateCore.getProxy());
	}

	@Override
	public void resetImmediateChanges() {
		// Do nothing
	}

	@Override
	public void dialogVisibilityChanged(boolean visible) {
		// Do nothing
	}
}
