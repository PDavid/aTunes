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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomWindow;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.IGenre;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.IYear;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class ExtendedToolTip. This is a special window shown as tooltip for
 * navigator tree objects
 */
public final class ExtendedToolTip extends AbstractCustomWindow {

    private static final String SONGS2 = "SONGS";

	private static final long serialVersionUID = -5041702404982493070L;

    private static final Dimension IMAGE_DIMENSION = new Dimension(Constants.TOOLTIP_IMAGE_WIDTH + 200, Constants.TOOLTIP_IMAGE_HEIGHT + 10);
    private static final Dimension NO_IMAGE_DIMENSION = new Dimension(200, 65);

    private JLabel image;
    private JLabel line1;
    private JLabel line2;
    private JLabel line3;

    /**
     * Instantiates a new extended tool tip.
     * @param lookAndFeel
     */
    public ExtendedToolTip(ILookAndFeel lookAndFeel) {
        super(null, IMAGE_DIMENSION.width, IMAGE_DIMENSION.height);

        setFocusableWindowState(false);
        JPanel container = new JPanel(new GridBagLayout());
        image = new JLabel();
        line1 = new JLabel();
        line2 = new JLabel();
        line3 = new JLabel();

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 3;
        c.insets = new Insets(0, 5, 0, 0);
        container.add(image, c);
        c.gridx = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;
        //c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 0, 10);
        container.add(line1, c);
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(0, 10, 0, 10);
        container.add(line2, c);
        c.gridx = 1;
        c.gridy = 2;
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(0, 10, 0, 10);
        container.add(line3, c);
        // Use scroll pane to draw a border consistent with look and feel
        JScrollPane scrollPane = lookAndFeel.getScrollPane(container);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        add(scrollPane);
    }

    /**
     * Sets the text of line 1
     * 
     * @param text
     * 
     */
    public void setLine1(String text) {
        line1.setText(text);
    }

    /**
     * Sets the text of line 2
     * 
     * @param text
     * 
     */
    public void setLine2(String text) {
        line2.setText(text);
    }

    /**
     * Sets the image
     * 
     * @param img
     *            the new image
     */
    public void setImage(ImageIcon img) {
        if (img != null) {
            // Add 50 to width to force images to fit height of tool tip as much as possible
            image.setIcon(ImageUtils.scaleImageBicubic(img.getImage(), Constants.TOOLTIP_IMAGE_WIDTH + 50, Constants.TOOLTIP_IMAGE_HEIGHT));
            image.setVisible(true);
        } else {
            image.setIcon(null);
            image.setVisible(false);
        }
    }

    /**
     * Sets the text of line 3
     * 
     * @param text
     * 
     */
    public void setLine3(String text) {
        line3.setText(text);
    }

    /**
     * Returns <code>true</code> if given object can be shown in extended
     * tooltip
     * 
     * @param object
     * @return
     */
    @SuppressWarnings("unchecked")
	public static boolean canObjectBeShownInExtendedToolTip(Object object) {
        if (object instanceof ITreeObject) {
            return ((ITreeObject<? extends IAudioObject>) object).isExtendedToolTipSupported();
        }
        return false;
    }

    /**
     * Fills tool tip with data from object
     * 
     * @param obj
     * @param repository
     */
    @SuppressWarnings("unchecked")
	public void setToolTipContent(Object obj) {
        // Picture is set asynchronously 
        setImage(null);
        if (obj instanceof ITreeObject) {
        	setExtendedToolTipFromTreeObject((ITreeObject<? extends IAudioObject>) obj);
        }
    }

    /**
     * Fills tool tip from tree object
     * @param object
     */
    private void setExtendedToolTipFromTreeObject(ITreeObject<? extends IAudioObject> object) {
    	if (object instanceof IAlbum) {
    		setFromAlbum(object);
    	} else if (object instanceof IPodcastFeed) {
    		setFromPodcast(object);
    	} else if (object instanceof IFolder) {
    		setFromFolder(object);
    	} else if (object instanceof IGenre) {
    		setFromGenre(object);
    	} else if (object instanceof IYear) {
    		setFromYear(object);
    	} else if (object instanceof IArtist) {
    		setFromArtist(object);
    	}
	}

	/**
	 * @param object
	 */
	private void setFromArtist(ITreeObject<? extends IAudioObject> object) {
		IArtist a = (IArtist) object;
		setLine1(a.getName());
		int albumNumber = a.getAlbums().size();
		setLine2(StringUtils.getString(albumNumber, " ", (albumNumber > 1 ? I18nUtils.getString("ALBUMS") : I18nUtils.getString("ALBUM"))));
		setLine3(StringUtils.getString(I18nUtils.getString("TIMES_PLAYED"), ": ", Context.getBean(IStatisticsHandler.class).getArtistTimesPlayed(a)));
	}

	/**
	 * @param object
	 */
	private void setFromYear(ITreeObject<? extends IAudioObject> object) {
		IYear y = (IYear) object;
		setLine1(y.getName());
		int artistNumber = y.getArtistSet().size();
		setLine2(StringUtils.getString(artistNumber, " ", (artistNumber > 1 ? I18nUtils.getString("ARTISTS") : I18nUtils.getString("ARTIST"))));
		int songs = y.size();
		setLine3(StringUtils.getString(songs, " ", (songs > 1 ? I18nUtils.getString(SONGS2) : I18nUtils.getString("SONG"))));
	}

	/**
	 * @param object
	 */
	private void setFromGenre(ITreeObject<? extends IAudioObject> object) {
		IGenre g = (IGenre) object;
		setLine1(g.getName());
		int artistNumber = g.getArtistSet().size();
		setLine2(StringUtils.getString(artistNumber, " ", (artistNumber > 1 ? I18nUtils.getString("ARTISTS") : I18nUtils.getString("ARTIST"))));
		int songs = g.size();
		setLine3(StringUtils.getString(songs, " ", (songs > 1 ? I18nUtils.getString(SONGS2) : I18nUtils.getString("SONG"))));
	}

	/**
	 * @param object
	 */
	private void setFromFolder(ITreeObject<? extends IAudioObject> object) {
		IFolder f = (IFolder) object;
		setLine1(f.getName());
		int folderNumber = f.getFolders().size();
		if (folderNumber > 0) {
		    setLine2(StringUtils.getString(folderNumber, " ", (folderNumber > 1 ? I18nUtils.getString("FOLDERS") : I18nUtils.getString("FOLDER"))));
		} else {
		    setLine2(null);
		}
		int songs = f.getAudioObjects().size();
		setLine3(StringUtils.getString(songs, " ", (songs > 1 ? I18nUtils.getString(SONGS2) : I18nUtils.getString("SONG"))));
	}

	/**
	 * @param object
	 */
	private void setFromPodcast(ITreeObject<? extends IAudioObject> object) {
		IPodcastFeed p = (IPodcastFeed) object;
		setLine1(p.getName());
		setLine2(StringUtils.getString(I18nUtils.getString("PODCAST_ENTRIES"), ": ", p.getPodcastFeedEntries().size()));
		setLine3(StringUtils.getString(I18nUtils.getString("NEW_PODCAST_ENTRIES_TOOLTIP"), ": ", p.getNewEntriesCount()));
	}

	/**
	 * @param object
	 */
	private void setFromAlbum(ITreeObject<? extends IAudioObject> object) {
		IAlbum a = (IAlbum) object;
		setLine1(a.getName());
		setLine2(a.getArtist().getName());
		int songNumber = a.size();
		setLine3(StringUtils.getString(songNumber, " ", (songNumber > 1 ? I18nUtils.getString(SONGS2) : I18nUtils.getString("SONG"))));
	}

    /**
     * Adjust size of extended tool tip if it's going to show an image or not
     * 
     * @param image
     */
    public void setSizeToFitImage(boolean image) {
        setSize(image ? IMAGE_DIMENSION : NO_IMAGE_DIMENSION);
    }
}
