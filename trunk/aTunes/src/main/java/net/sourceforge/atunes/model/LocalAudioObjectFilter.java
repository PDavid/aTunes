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

package net.sourceforge.atunes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocalAudioObjectFilter {

    /**
     * Gets the local audio objects from a list of audio objects
     * 
     * @param audioObjects
     * @return
     */
    public List<ILocalAudioObject> getLocalAudioObjects(List<IAudioObject> audioObjects) {
        if (audioObjects == null || audioObjects.isEmpty()) {
            return Collections.emptyList();
        }
        List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
        for (IAudioObject audioObject : audioObjects) {
            if (audioObject instanceof ILocalAudioObject) {
                result.add((ILocalAudioObject) audioObject);
            }
        }
        return result;
    }

    /**
     * Returns a list where there are no repeated audio objects (same title and artist)
     * 
     * @param list
     * @return
     */
    public List<ILocalAudioObject> filterRepeatedObjects(List<ILocalAudioObject> list) {
    	return filterWithHash(list, new IHashCalculator() {
    		@Override
    		public int getHash(ILocalAudioObject lao) {
    			return (lao.getAlbumArtist() != null && !lao.getAlbumArtist().trim().equals("") ? lao.getAlbumArtist() : lao.getArtist()).hashCode() * 
    					lao.getTitle().hashCode();
    		}
    	});
    }
    
    /**
     * Returns a list where there are no repeated audio objects (same title and album and artist)
     * 
     * @param list
     * @return
     */
    public List<ILocalAudioObject> filterRepeatedObjectsWithAlbums(List<ILocalAudioObject> list) {
    	return filterWithHash(list, new IHashCalculator() {
    		@Override
    		public int getHash(ILocalAudioObject lao) {
    			return (lao.getAlbumArtist() != null && !lao.getAlbumArtist().trim().equals("") ? lao.getAlbumArtist() : lao.getArtist()).hashCode() * 
    					lao.getAlbum().hashCode() * 
    					lao.getTitle().hashCode();
    		}
    	});
    }

    /**
     * Returns a list where there are no repeated audio objects (same title and album and artist)
     * 
     * @param list
     * @return
     */
    private List<ILocalAudioObject> filterWithHash(List<ILocalAudioObject> list, IHashCalculator hashCalculator) {
        List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>(list);
        Set<Integer> artistAndTitles = new HashSet<Integer>();
        for (ILocalAudioObject af : list) {
            // Build a set of strings of type artist_hash * album_hash * title_hash
            Integer hash = hashCalculator.getHash(af);
            if (artistAndTitles.contains(hash)) {
                // Repeated artist + album + title, remove from result list
                result.remove(af);
            } else {
                artistAndTitles.add(hash);
            }
        }
        return result;
    }
    
    private interface IHashCalculator {
    	/**
    	 * Gets hash for a lao
    	 * @param lao
    	 * @return
    	 */
    	public int getHash(ILocalAudioObject lao);
    }
}
