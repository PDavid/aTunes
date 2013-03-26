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

import java.io.IOException;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.util.Callback;
import net.sourceforge.atunes.model.IBeanFactory;

/**
 * Loads FXML Controller using Spring so we can use dependency injection
 * 
 * @author alex
 * 
 */
public class SpringFXMLLoader {

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Loads FXML with given bundle for i18n
	 * 
	 * @param dialog
	 * @param fxml
	 * @param bundle
	 * @return
	 * @throws IOException
	 */
	public Object load(final JavaFXDialog dialog, String fxml,
			ResourceBundle bundle) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml), bundle);
		loader.setControllerFactory(new Callback<Class<?>, Object>() {

			@Override
			public Object call(Class<?> clazz) {
				if (JavaFXDialogController.class.isAssignableFrom(clazz)) {
					JavaFXDialogController controller = (JavaFXDialogController) beanFactory
							.getBean(clazz);
					controller.setDialog(dialog);
					return controller;
				} else {
					return beanFactory.getBean(clazz);
				}
			}
		});
		return loader.load();
	}
}
