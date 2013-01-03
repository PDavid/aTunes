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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.io.File;
import java.util.Arrays;

import net.sourceforge.atunes.model.CDMetadata;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFactory;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.model.ITagHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Abstract encoder
 * 
 * @author alex
 * 
 */
public abstract class AbstractEncoder implements Encoder {

    private String extensionOfEncodedFiles;

    private ProgressListener listener;

    private String quality;

    private String[] availableQualities;

    private String defaultQuality;

    private String formatName;

    private ILocalAudioObjectFactory localAudioObjectFactory;

    private IOSManager osManager;

    private ITagHandler tagHandler;

    private IUnknownObjectChecker unknownObjectChecker;

    /**
     * Empty constructor
     */
    public AbstractEncoder() {

    }

    /**
     * @param extensionOfEncodedFiles
     * @param availableQualities
     * @param defaultQuality
     * @param formatName
     */
    public AbstractEncoder(final String extensionOfEncodedFiles,
	    final String[] availableQualities, final String defaultQuality,
	    final String formatName) {
	this.extensionOfEncodedFiles = extensionOfEncodedFiles;
	this.availableQualities = Arrays.copyOf(availableQualities,
		availableQualities.length);
	this.defaultQuality = defaultQuality;
	this.formatName = formatName;
    }

    /**
     * @param unknownObjectChecker
     */
    public void setUnknownObjectChecker(
	    final IUnknownObjectChecker unknownObjectChecker) {
	this.unknownObjectChecker = unknownObjectChecker;
    }

    /**
     * @param tagHandler
     */
    public void setTagHandler(final ITagHandler tagHandler) {
	this.tagHandler = tagHandler;
    }

    /**
     * @param osManager
     */
    public void setOsManager(final IOSManager osManager) {
	this.osManager = osManager;
    }

    /**
     * @return os manager
     */
    public IOSManager getOsManager() {
	return osManager;
    }

    /**
     * @param localAudioObjectFactory
     */
    public void setLocalAudioObjectFactory(
	    final ILocalAudioObjectFactory localAudioObjectFactory) {
	this.localAudioObjectFactory = localAudioObjectFactory;
    }

    @Override
    public final String getExtensionOfEncodedFiles() {
	return extensionOfEncodedFiles;
    }

    @Override
    public final void setListener(final ProgressListener listener) {
	this.listener = listener;
    }

    @Override
    public final void setQuality(final String quality) {
	this.quality = quality;
    }

    /**
     * @return progress listener
     */
    public final ProgressListener getListener() {
	return listener;
    }

    /**
     * @return quality
     */
    public final String getQuality() {
	return quality;
    }

    @Override
    public final String[] getAvailableQualities() {
	return Arrays.copyOf(availableQualities, availableQualities.length);
    }

    @Override
    public final String getDefaultQuality() {
	return defaultQuality;
    }

    @Override
    public final String getFormatName() {
	return formatName;
    }

    @Override
    public boolean setTag(final File file, final int trackNumber,
	    final CDMetadata metadata) {
	try {
	    ILocalAudioObject audiofile = localAudioObjectFactory
		    .getLocalAudioObject(file);
	    ITag tag = tagHandler.getNewTag();

	    tag.setAlbum(metadata.getAlbum());
	    String albumArtist = metadata.getAlbumArtist();
	    String artist = metadata.getArtist(trackNumber);
	    tag.setAlbumArtist(albumArtist);
	    if (!StringUtils.isEmpty(artist)) {
		tag.setArtist(artist);
	    } else if (!StringUtils.isEmpty(albumArtist)) {
		tag.setArtist(albumArtist);
	    } else {
		tag.setArtist(unknownObjectChecker.getUnknownArtist());
	    }
	    tag.setYear(metadata.getYear());
	    tag.setGenre(metadata.getGenre());
	    tag.setTitle(metadata.getTitle(trackNumber));
	    tag.setComposer(metadata.getComposer(trackNumber));
	    tag.setTrackNumber(trackNumber);
	    tag.setDiscNumber(metadata.getDisc());

	    tagHandler.setTag(audiofile, tag);

	    return true;
	} catch (Exception e) {
	    Logger.error(StringUtils.getString(
		    "Tag Process execution caused exception ", e));
	    return false;
	}
    }
}
