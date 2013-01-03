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

package net.sourceforge.atunes.kernel.modules.process;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.ImageUtils;

/**
 * The Class SetGenresProcess.
 */
public class SetCoversProcess extends AbstractChangeTagProcess {

	private Map<ILocalAudioObject, ImageIcon> filesAndCovers;

	private IWebServicesHandler webServicesHandler;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	@Override
	protected void retrieveInformationBeforeChangeTags() {
		super.retrieveInformationBeforeChangeTags();
		this.filesAndCovers = getCoversForFiles(this.getFilesToChange());
	}

	@Override
	protected void changeTag(final ILocalAudioObject file) throws IOException {
		ImageIcon imageOfFile = this.filesAndCovers.get(file);
		if (imageOfFile != null) {
			BufferedImage bufferedCover = ImageUtils.toBufferedImage(imageOfFile.getImage());
			ITag newTag = getTagHandler().getNewTag(file, new HashMap<String, Object>());
			newTag.setInternalImage(true);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedCover, "PNG", byteArrayOutputStream);
			getTagHandler().setTag(file, newTag, true, byteArrayOutputStream.toByteArray());
		}
	}

	/**
	 * Gets the covers for files.
	 * 
	 * @param files
	 *            the files
	 * 
	 * @return the covers for files
	 */
	private Map<ILocalAudioObject, ImageIcon> getCoversForFiles(final Collection<ILocalAudioObject> files) {
		Map<ILocalAudioObject, ImageIcon> result = new HashMap<ILocalAudioObject, ImageIcon>();

		Map<Integer, ImageIcon> coverCache = new HashMap<Integer, ImageIcon>();

		for (ILocalAudioObject f : files) {
			if (!unknownObjectChecker.isUnknownArtist(f.getArtist(unknownObjectChecker)) && !unknownObjectChecker.isUnknownAlbum(f.getAlbum(unknownObjectChecker))) {
				ImageIcon cover = null;
				int cacheKey = f.getArtist(unknownObjectChecker).hashCode() + f.getAlbum(unknownObjectChecker).hashCode();
				if (coverCache.containsKey(cacheKey)) {
					cover = coverCache.get(cacheKey);
				} else {
					IAlbumInfo albumInfo = webServicesHandler.getAlbum(f.getArtist(unknownObjectChecker), f.getAlbum(unknownObjectChecker));
					if (albumInfo == null) {
						continue;
					}
					cover = webServicesHandler.getAlbumImage(albumInfo);
					if (cover == null) {
						continue;
					}
					coverCache.put(cacheKey, cover);
					// Wait one second to avoid IP banning
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// Nothing to do
					}
				}
				if (cover != null) {
					result.put(f, cover);
				}
			}
		}
		return result;
	}

}
