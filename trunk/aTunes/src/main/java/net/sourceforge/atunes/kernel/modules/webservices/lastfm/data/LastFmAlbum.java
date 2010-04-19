/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;

import net.roarsoftware.lastfm.Album;
import net.roarsoftware.lastfm.ImageSize;
import net.roarsoftware.lastfm.Playlist;
import net.roarsoftware.lastfm.Track;
import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.TrackInfo;
import net.sourceforge.atunes.utils.StringUtils;

public class LastFmAlbum implements AlbumInfo {

    private static final long serialVersionUID = -8021357529697065642L;

    private static final ThreadLocal<SimpleDateFormat> df = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("d MMM yyyy, HH:mm", Locale.US);
        }
    };

    private String artist;
    private String title;
    private String url;
    private String releaseDateString;
    private String bigCoverURL;
    private String coverURL;
    private String smallCoverURL;
    private List<TrackInfo> tracks;
    // Used by renderers   
    private ImageIcon cover;

    /**
     * Gets the album.
     * 
     * @return the album
     */
    public static AlbumInfo getAlbum(Album a, Playlist pl) {
        LastFmAlbum album = new LastFmAlbum();

        album.artist = a.getArtist();
        album.title = a.getName();
        album.url = a.getUrl();
        album.releaseDateString = a.getReleaseDate() != null ? a.getReleaseDate().toString() : "";
        album.bigCoverURL = a.getImageURL(ImageSize.LARGE);
        album.coverURL = a.getImageURL(ImageSize.ORIGINAL);
        album.smallCoverURL = a.getImageURL(ImageSize.SMALL);

        if (pl != null) {
            List<TrackInfo> ts = new ArrayList<TrackInfo>();
            for (Track t : pl.getTracks()) {
                ts.add(LastFmTrack.getTrack(t));
            }

            // Process track list: if all tracks have a common string between (), [], {} as "(Live)" then it's removed from all of them
            // In this way track names are more accurate
            if (!ts.isEmpty()) {
                String firstTrackTitle = ts.get(0).getTitle();
                // Get all text between () [] {}
                List<String> tokensOfFirstTrackTitle = StringUtils.getTextBetweenChars(firstTrackTitle, '(', ')');
                tokensOfFirstTrackTitle.addAll(StringUtils.getTextBetweenChars(firstTrackTitle, '[', ']'));
                tokensOfFirstTrackTitle.addAll(StringUtils.getTextBetweenChars(firstTrackTitle, '{', '}'));

                // Check what tokens are present in all track titles
                List<String> commonTokens = new ArrayList<String>();
                for (String token : tokensOfFirstTrackTitle) {
                    boolean common = true;
                    for (int i = 1; i < ts.size() && common; i++) {
                        if (!ts.get(i).getTitle().contains(token)) {
                            common = false;
                        }
                    }
                    if (common) {
                        commonTokens.add(token);
                    }
                }

                // Then remove common tokens from all titles
                for (TrackInfo ti : ts) {
                    for (String token : commonTokens) {
                        ti.setTitle(ti.getTitle().replace(token, ""));
                    }
                    ti.setTitle(ti.getTitle().trim());
                }
            }

            album.tracks = ts;
        }

        return album;
    }

    /**
     * Gets the artist.
     * 
     * @return the artist
     */
    @Override
    public String getArtist() {
        return artist;
    }

    /**
     * Gets the artist url.
     * 
     * @return the artist url
     */
    @Override
    public String getArtistUrl() {
        return url.substring(0, url.lastIndexOf('/'));
    }

    /**
     * Gets the big cover url.
     * 
     * @return the bigCoverURL
     */
    @Override
    public String getBigCoverURL() {
        return bigCoverURL;
    }

    /**
     * Gets the cover.
     * 
     * @return the cover
     */
    @Override
    public ImageIcon getCover() {
        return cover;
    }

    /**
     * Gets the cover url.
     * 
     * @return the cover url
     */
    @Override
    public String getCoverURL() {
        return coverURL;
    }

    /**
     * Gets the release date.
     * 
     * @return the release date
     */
    @Override
    public Date getReleaseDate() {
        try {
            return df.get().parse(releaseDateString);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Gets the release date string.
     * 
     * @return the releaseDateString
     */
    @Override
    public String getReleaseDateString() {
        return releaseDateString;
    }

    /**
     * Gets the small cover url.
     * 
     * @return the small cover url
     */
    @Override
    public String getSmallCoverURL() {
        return smallCoverURL;
    }

    /**
     * Gets the title.
     * 
     * @return the title
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Gets the tracks.
     * 
     * @return the tracks
     */
    @Override
    public List<TrackInfo> getTracks() {
        return tracks;
    }

    /**
     * Gets the url.
     * 
     * @return the url
     */
    @Override
    public String getUrl() {
        return url;
    }

    /**
     * Gets the year.
     * 
     * @return the year
     */
    @Override
    public String getYear() {
        Date releaseDate = getReleaseDate();
        if (releaseDate == null) {
            return "";
        }
        Calendar c = Calendar.getInstance();
        c.setTime(releaseDate);
        return Integer.toString(c.get(Calendar.YEAR));
    }

    /**
     * Sets the artist.
     * 
     * @param artist
     *            the artist to set
     */
    @Override
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Sets the big cover url.
     * 
     * @param bigCoverURL
     *            the bigCoverURL to set
     */
    @Override
    public void setBigCoverURL(String bigCoverURL) {
        this.bigCoverURL = bigCoverURL;
    }

    /**
     * Sets the cover.
     * 
     * @param cover
     *            the cover to set
     */
    @Override
    public void setCover(ImageIcon cover) {
        this.cover = cover;
    }

    /**
     * Sets the cover url.
     * 
     * @param coverURL
     *            the coverURL to set
     */
    @Override
    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    /**
     * Sets the release date string.
     * 
     * @param releaseDateString
     *            the releaseDateString to set
     */
    @Override
    public void setReleaseDateString(String releaseDateString) {
        this.releaseDateString = releaseDateString;
    }

    /**
     * Sets the small cover url.
     * 
     * @param smallCoverURL
     *            the smallCoverURL to set
     */
    @Override
    public void setSmallCoverURL(String smallCoverURL) {
        this.smallCoverURL = smallCoverURL;
    }

    /**
     * Sets the title.
     * 
     * @param title
     *            the title to set
     */
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the tracks.
     * 
     * @param tracks
     *            the tracks to set
     */
    @Override
    public void setTracks(List<? extends TrackInfo> tracks) {
        this.tracks = tracks != null ? new ArrayList<TrackInfo>(tracks) : null;
    }

    /**
     * Sets the url.
     * 
     * @param url
     *            the url to set
     */
    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return StringUtils.getString(artist, " - ", title);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bigCoverURL == null) ? 0 : bigCoverURL.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        LastFmAlbum other = (LastFmAlbum) obj;
        if (bigCoverURL == null) {
            if (other.bigCoverURL != null) {
                return false;
            }
        } else if (!bigCoverURL.equals(other.bigCoverURL)) {
            return false;
        }
        return true;
    }

}
