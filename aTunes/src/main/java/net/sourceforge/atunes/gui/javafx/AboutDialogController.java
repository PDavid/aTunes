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

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.IJavaVirtualMachineStatistic;
import net.sourceforge.atunes.utils.StringUtils;

import org.joda.time.DateMidnight;

public class AboutDialogController extends JavaFXDialogController implements
		Initializable {

	@FXML
	private Hyperlink contributors;

	@FXML
	private Hyperlink version;

	@FXML
	private Label licenseText;

	@FXML
	private TableView<Property> propertiesTable;

	@FXML
	private Button okButton;

	private IDesktop desktop;

	private IBeanFactory beanFactory;

	private ObservableList<Property> properties;

	private Timer timer;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Gets the license text.
	 * 
	 * @return the license text
	 */
	private String getLicenseText() {
		return StringUtils
				.getString(
						"Copyright (C) 2006-",
						DateMidnight.now().year().get(),
						"  The aTunes Team\n\n",
						"This program is free software; you can redistribute it and/or ",
						"modify it under the terms of the GNU General Public License ",
						"as published by the Free Software Foundation; either version 2 ",
						"of the License, or (at your option) any later version.\n\n",
						"This program is distributed in the hope that it will be useful, ",
						"but WITHOUT ANY WARRANTY; without even the implied warranty of ",
						"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the ",
						"GNU General Public License for more details.\n\n",
						"You should have received a copy of the GNU General Public License ",
						"along with this program; if not, write to the\n\nFree Software ",
						"Foundation, Inc.\n51 Franklin Street, Fifth Floor\nBoston, MA\n02110-1301, USA");
	}

	/**
	 * @param desktop
	 */
	public void setDesktop(IDesktop desktop) {
		this.desktop = desktop;
	}

	@Override
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		version.setText(StringUtils.getString(Constants.APP_NAME, " ",
				Constants.VERSION.toString()));
		version.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent ev) {
				desktop.openURL(Constants.APP_WEB);
			}
		});

		contributors.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ev) {
				desktop.openURL(Constants.CONTRIBUTORS_WEB);
			}
		});

		licenseText.setText(getLicenseText());

		okButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				timer.cancel();
				getDialog().close();
			}
		});

		TableColumn<Property, String> propertyColumn = new TableColumn<Property, String>(
				"Property");

		TableColumn<Property, String> valueColumn = new TableColumn<Property, String>(
				"Value");

		propertyColumn
				.setCellValueFactory(new PropertyValueFactory<Property, String>(
						"description"));

		valueColumn
				.setCellValueFactory(new PropertyValueFactory<Property, String>(
						"value"));

		this.properties = FXCollections.observableArrayList();
		propertiesTable.setItems(this.properties);

		propertiesTable.getColumns().addAll(propertyColumn, valueColumn);

		updatePropertiesTable();

		this.timer = new Timer();
		this.timer.schedule(new TimerTask() {

			@Override
			public void run() {
				updatePropertiesTable();
			}
		}, 0, 2000);
	}

	private void updatePropertiesTable() {
		properties.clear();
		for (IJavaVirtualMachineStatistic statistic : beanFactory
				.getBeans(IJavaVirtualMachineStatistic.class)) {
			properties.add(new Property(statistic.getDescription(), statistic
					.getValue()));
		}
	}

	@Override
	protected void dialogClosed() {
		timer.cancel();
	}
}
