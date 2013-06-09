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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.AbstractTableCellRendererCode;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.RemoteImage;
import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IEvent;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.joda.time.format.DateTimeFormat;

/**
 * A dialog to show events
 * 
 * @author alex
 * 
 */
public final class RecommendedEventsDialog extends AbstractCustomDialog {

	private static final long serialVersionUID = -1744765531225480303L;

	private JTable table;

	private JLabel eventTitle;

	private JLabel eventDate;

	private JLabel eventVenue;

	private JLabel eventCity;

	private JLabel eventCountry;

	private JLabel eventArtists;

	private UrlLabel readMore;

	private RemoteImage eventImage;

	/**
	 * @param frame
	 * @param controlsBuilder
	 */
	public RecommendedEventsDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 800, 600, controlsBuilder);
	}

	@Override
	public void initialize() {
		setTitle(I18nUtils.getString("RECOMMENDED_EVENTS"));
		setContent(getLookAndFeel());
	}

	@Override
	public void hideDialog() {
		setVisible(false);
	}

	@Override
	public void showDialog() {
		setVisible(true);
	}

	/**
	 * Sets events to show
	 * 
	 * @param events
	 */
	public void setEvents(List<IEvent> events) {
		Object[][] data = new Object[events.size()][1];
		for (int i = 0; i < events.size(); i++) {
			data[i][0] = events.get(i);
		}
		this.table.setModel(new DefaultTableModel(data,
				new Object[] { "EVENTS" }));
	}

	/**
	 * Sets the content.
	 * 
	 * @param iLookAndFeel
	 */
	private void setContent(final ILookAndFeel iLookAndFeel) {
		JPanel panel = new JPanel(new GridBagLayout());

		JLabel textLabel = new JLabel(
				I18nUtils.getString("RECOMMENDED_EVENTS_DIALOG_TEXT"));

		this.table = getControlsBuilder().createTable();
		this.table.setRowHeight(Constants.THUMB_IMAGE_HEIGHT + 10);
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.setTableHeader(null);
		this.table.setDefaultRenderer(
				Object.class,
				getLookAndFeel().getTableCellRenderer(
						new RecommendedEventCellRendererCode(
								getControlsBuilder())));
		this.table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							IEvent event = (IEvent) ((DefaultTableModel) table
									.getModel()).getValueAt(
									table.getSelectedRow(), 0);
							showEventDetails(event);
						}
					}
				});

		JScrollPane scrollPane = getControlsBuilder().createScrollPane(
				this.table);
		scrollPane.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));
		scrollPane.setPreferredSize(new Dimension(400, Integer.MAX_VALUE));
		scrollPane.setMinimumSize(new Dimension(400, 0));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		panel.add(textLabel, c);
		c.gridy++;
		c.gridwidth = 1;
		c.weighty = 1;
		panel.add(scrollPane, c);
		c.gridx++;
		c.weightx = 1;
		c.insets = new Insets(10, 0, 10, 10);
		panel.add(
				getControlsBuilder().createScrollPane(getEventDetailsPanel()),
				c);

		add(panel);
	}

	private JPanel getEventDetailsPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		this.eventTitle = new JLabel();
		this.eventTitle.setFont(getLookAndFeelManager().getCurrentLookAndFeel()
				.getContextInformationBigFont());
		this.eventImage = getControlsBuilder().createRemoteImage();
		this.eventDate = new JLabel();
		this.eventVenue = new JLabel();
		this.eventCity = new JLabel();
		this.eventCountry = new JLabel();
		this.eventArtists = new JLabel();

		this.readMore = (UrlLabel) getControlsBuilder().getUrlLabel();

		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		panel.add(eventImage, c);
		c.gridy++;
		panel.add(eventTitle, c);
		c.gridy++;
		panel.add(eventDate, c);
		c.gridy++;
		panel.add(eventVenue, c);
		c.gridy++;
		panel.add(eventCity, c);
		c.gridy++;
		panel.add(eventCountry, c);
		c.gridy++;
		panel.add(eventArtists, c);
		c.gridy++;
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTH;
		panel.add(readMore, c);

		return panel;
	}

	private void showEventDetails(IEvent event) {
		this.eventImage.loadImage(event.getOriginalImageUrl());
		this.eventTitle.setText(event.getTitle());
		this.eventDate.setText(DateTimeFormat.longDate().print(
				event.getStartDate()));
		this.eventVenue.setText(event.getVenue());
		this.eventCity.setText(event.getCity());
		this.eventCountry.setText(event.getCountry());
		StringBuilder artists = new StringBuilder();
		artists.append("<html>");
		for (String artist : event.getArtists()) {
			artists.append(artist);
			artists.append("<br>");
		}
		artists.append("</html>");
		this.eventArtists.setText(artists.toString());
		this.readMore.setText(I18nUtils.getString("READ_MORE"), event.getUrl());
	}

	private static class RecommendedEventCellRendererCode extends
			AbstractTableCellRendererCode<JComponent, IEvent> {

		private final IControlsBuilder controlsBuilder;

		public RecommendedEventCellRendererCode(IControlsBuilder controlsBuilder) {
			this.controlsBuilder = controlsBuilder;
		}

		@Override
		public JComponent getComponent(JComponent superComponent, JTable t,
				IEvent value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (value != null) {
				return new RecommendedEventPanel(value, controlsBuilder,
						superComponent.getBackground(),
						superComponent.getForeground());
			} else {
				return superComponent;
			}
		}

	}

	private static class RecommendedEventPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3503596525626448268L;

		/**
		 * @param event
		 * @param controlsBuilder
		 * @param backgroundColor
		 * @param foregroundColor
		 */
		public RecommendedEventPanel(IEvent event,
				IControlsBuilder controlsBuilder, Color backgroundColor,
				Color foregroundColor) {
			super(new GridBagLayout());
			setContent(event, controlsBuilder, backgroundColor, foregroundColor);
		}

		private void setContent(IEvent event, IControlsBuilder controlsBuilder,
				Color backgroundColor, Color foregroundColor) {
			JLabel imageLabel = new JLabel();
			imageLabel.setIcon(event.getImage());
			JLabel textLabel = new JLabel();
			textLabel.setText(StringUtils.getString("<html>", event.getTitle(),
					"<br>",
					DateTimeFormat.longDate().print(event.getStartDate()),
					"</html>"));

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.anchor = GridBagConstraints.WEST;
			c.insets = new Insets(2, (Constants.THUMB_IMAGE_WIDTH + 20)
					/ 2
					- (imageLabel.getIcon() != null ? imageLabel.getIcon()
							.getIconWidth() : 0) / 2, 0, 0);
			add(imageLabel, c);
			c.gridx = 1;
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(0, (Constants.THUMB_IMAGE_WIDTH + 20)
					/ 2
					- (imageLabel.getIcon() != null ? imageLabel.getIcon()
							.getIconWidth() : 0) / 2, 0, 0);
			add(textLabel, c);

			controlsBuilder.applyComponentOrientation(this);

			if (backgroundColor != null) {
				textLabel.setBackground(backgroundColor);
				setBackground(backgroundColor);
				imageLabel.setBackground(backgroundColor);
			}
			if (foregroundColor != null) {
				textLabel.setForeground(foregroundColor);
			}
		}
	}
}
