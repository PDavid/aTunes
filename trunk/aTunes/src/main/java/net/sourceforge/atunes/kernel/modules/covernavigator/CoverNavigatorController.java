/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.covernavigator;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.dialogs.CoverNavigatorFrame;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.kernel.modules.process.ProcessListener;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.GetCoversProcess;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.utils.GuiUtils;

import org.jdesktop.swingx.border.DropShadowBorder;

public final class CoverNavigatorController extends AbstractSimpleController<CoverNavigatorFrame> {

    private static final int COVER_PANEL_WIDTH = Constants.COVER_NAVIGATOR_IMAGE_SIZE.getSize() + 20;
    private static final int COVER_PANEL_HEIGHT = Constants.COVER_NAVIGATOR_IMAGE_SIZE.getSize() + 40;

    private final class GenerateAndShowAlbumPanelsSwingWorker extends
			SwingWorker<Void, IntermediateResult> {
		private final Artist artistSelected;

		private GenerateAndShowAlbumPanelsSwingWorker(Artist artistSelected) {
			this.artistSelected = artistSelected;
		}

		@Override
		protected Void doInBackground() throws Exception {

		    final List<Album> albums = new ArrayList<Album>(artistSelected.getAlbums().values());
		    Collections.sort(albums);

		    int coversAdded = 0;
		    for (Album album : albums) {
		        ImageIcon cover = album.getPicture(Constants.COVER_NAVIGATOR_IMAGE_SIZE);
		        publish(new IntermediateResult(album, cover));
		        coversAdded++;
		    }
		    return null;
		}

		@Override
		protected void done() {
		    getComponentControlled().setCursor(Cursor.getDefaultCursor());
		    getComponentControlled().getList().setEnabled(true);
		    getComponentControlled().getCoversButton().setEnabled(true);
		}

		@Override
		protected void process(List<IntermediateResult> intermediateResults) {
		    for (IntermediateResult intermediateResult : intermediateResults) {
		        getComponentControlled().getCoversPanel().add(getPanelForAlbum(intermediateResult.getAlbum(), intermediateResult.getCover()));
		        getComponentControlled().getCoversPanel().revalidate();
		        getComponentControlled().getCoversPanel().repaint();
		        getComponentControlled().getCoversPanel().validate();
		    }
		}
	}

	private final class GetCoversButtonActionListener implements ActionListener {
		private final class GetCoversProcessListener implements ProcessListener {
			@Override
			public void processCanceled() {
			    update();
			}

			@Override
			public void processFinished(boolean ok) {
			    update();
			}

			private void update() {
			    try {
			        SwingUtilities.invokeAndWait(new Runnable() {
			            @Override
			            public void run() {
			                updateCovers();
			            }
			        });
			    } catch (Exception e) {
			        // Nothing to do
			    }
			}
		}

		private final CoverNavigatorFrame frame;

		private GetCoversButtonActionListener(CoverNavigatorFrame frame) {
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		    Artist selectedArtist = (Artist) frame.getList().getSelectedValue();
		    if (selectedArtist != null) {
		        GetCoversProcess process = new GetCoversProcess(selectedArtist, getComponentControlled());
		        process.addProcessListener(new GetCoversProcessListener());
		        process.execute();
		    }
		}
	}

	private static class IntermediateResult {

        private Album album;
        private ImageIcon cover;

        public IntermediateResult(Album album, ImageIcon cover) {
            this.album = album;
            this.cover = cover;
        }

        public Album getAlbum() {
            return album;
        }

        public ImageIcon getCover() {
            return cover;
        }

    }

    /**
     * Instantiates a new cover navigator controller.
     * 
     * @param frame
     *            the frame
     */
    public CoverNavigatorController(CoverNavigatorFrame frame) {
        super(frame);
        addBindings();
    }

    @Override
    protected void addBindings() {
        final CoverNavigatorFrame frame = getComponentControlled();
        frame.getList().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!((JList) e.getSource()).getValueIsAdjusting()) {
                    updateCovers();
                }
            }

        });
        frame.getCoversButton().addActionListener(new GetCoversButtonActionListener(frame));
    }

    @Override
    protected void addStateBindings() {
        // Nothing to do
    }

    /**
     * Gets the panel for album.
     * 
     * @param album
     *            the album
     * @param cover
     *            the cover
     * @param coversSize
     *            the covers size
     * 
     * @return the panel for album
     */
    JPanel getPanelForAlbum(Album album, ImageIcon cover) {
        JPanel panel = new JPanel(new FlowLayout());

        JLabel coverLabel = new JLabel(cover);
        coverLabel.setToolTipText(album.getName());
        if (cover == null) {
            coverLabel.setPreferredSize(new Dimension(Constants.COVER_NAVIGATOR_IMAGE_SIZE.getSize(), Constants.COVER_NAVIGATOR_IMAGE_SIZE.getSize()));
            coverLabel.setBorder(BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
        } else {
            coverLabel.setBorder(new DropShadowBorder());
        }

        JLabel label = new JLabel(album.getName(), SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(Constants.COVER_NAVIGATOR_IMAGE_SIZE.getSize(), 20));

        panel.add(coverLabel);
        panel.add(label);
        panel.setPreferredSize(new Dimension(COVER_PANEL_WIDTH, COVER_PANEL_HEIGHT));
        panel.setOpaque(false);

        GuiUtils.applyComponentOrientation(panel);
        return panel;
    }

    @Override
    protected void notifyReload() {
        // Nothing to do
    }

    /**
     * Update covers.
     */
    protected void updateCovers() {
        final Artist artistSelected = (Artist) getComponentControlled().getList().getSelectedValue();
        if (artistSelected == null) {
            return;
        }

        getComponentControlled().getCoversPanel().removeAll();

        getComponentControlled().getList().setEnabled(false);
        getComponentControlled().getCoversButton().setEnabled(false);
        getComponentControlled().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<Void, IntermediateResult> generateAndShowAlbumPanels = new GenerateAndShowAlbumPanelsSwingWorker(artistSelected);
        generateAndShowAlbumPanels.execute();
    }

}
