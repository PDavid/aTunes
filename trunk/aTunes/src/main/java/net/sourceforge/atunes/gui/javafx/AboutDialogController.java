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
import java.util.concurrent.ScheduledFuture;

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
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.utils.StringUtils;

import org.joda.time.DateMidnight;

/**
 * Controller for About dialog
 * 
 * @author alex
 * 
 */
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

	private ScheduledFuture<?> future;

	private ITaskService taskService;

	/**
	 * @param taskService
	 */
	public void setTaskService(final ITaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
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
	public void setDesktop(final IDesktop desktop) {
		this.desktop = desktop;
	}

	@Override
	public void initialize(final URL fxmlFileLocation,
			final ResourceBundle resources) {
		this.version.setText(StringUtils.getString(Constants.APP_NAME, " ",
				Constants.VERSION.toString()));
		this.version.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(final ActionEvent ev) {
				AboutDialogController.this.desktop.openURL(Constants.APP_WEB);
			}
		});

		this.contributors.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent ev) {
				AboutDialogController.this.desktop
						.openURL(Constants.CONTRIBUTORS_WEB);
			}
		});

		this.licenseText.setText(getLicenseText());

		this.okButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				AboutDialogController.this.future.cancel(false);
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

		propertyColumn.prefWidthProperty().bind(
				this.propertiesTable.widthProperty().divide(2).add(-20));
		valueColumn.prefWidthProperty().bind(
				this.propertiesTable.widthProperty().divide(2).add(-20));

		this.properties = FXCollections.observableArrayList();
		this.propertiesTable.setItems(this.properties);

		this.propertiesTable.getColumns().addAll(propertyColumn, valueColumn);

		this.future = this.taskService.submitPeriodically("Get JVM statistics",
				0, 4, new Runnable() {
					@Override
					public void run() {
						updatePropertiesTable();
					}
				});
	}

	private void updatePropertiesTable() {
		for (IJavaVirtualMachineStatistic statistic : this.beanFactory
				.getBeans(IJavaVirtualMachineStatistic.class)) {
			Property property = new Property(statistic.getDescription(),
					statistic.getValue());
			int indexToRemove = -1;
			for (int i = 0; i < this.properties.size() && indexToRemove == -1; i++) {
				if (this.properties.get(i).getDescription()
						.equals(property.getDescription())) {
					indexToRemove = i;
				}
			}
			if (indexToRemove != -1) {
				this.properties.remove(indexToRemove);
			}
			this.properties.add(property);
		}
	}

	@Override
	protected void dialogClosed() {
		this.future.cancel(false);
	}
}
