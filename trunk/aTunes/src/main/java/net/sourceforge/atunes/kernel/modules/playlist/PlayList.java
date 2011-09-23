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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import net.sourceforge.atunes.kernel.PlayListEventListeners;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.PointedList;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class represents a play list.
 * 
 * @author fleax
 */
public class PlayList implements IPlayList {

    private static final long serialVersionUID = 2756513776762920794L;

    /**
     * Play list mode to select audio objects
     */
    private transient PlayListMode mode;

    /**
     * Name of play list as shown on play list tabs.
     */
    private String name;

    /**
     * Pointed List of audio objects of this play list
     */
    private PointedList<IAudioObject> audioObjects;
    
    private transient IState state;

    private static class PlayListAudioObjectComparator implements Comparator<PlayListAudioObject>, Serializable {
        /**
		 * 
		 */
		private static final long serialVersionUID = 2216966505910863325L;

		@Override
        public int compare(PlayListAudioObject o1, PlayListAudioObject o2) {
            return -Integer.valueOf(o1.getPosition()).compareTo(Integer.valueOf(o2.getPosition()));
        }
    }

    static class PlayListPointedList extends PointedList<IAudioObject> {
        private static final long serialVersionUID = -6966402482637754615L;

        private transient IState state;
        
        PlayListPointedList(IState state) {
            super();
            this.state = state;
        }

        PlayListPointedList(PointedList<IAudioObject> pointedList, IState state) {
            super(pointedList);
            this.state = state;
        }

        @Override
        public boolean isCyclic() {
            return state.isRepeat();
        }
        
        public void setState(IState state) {
			this.state = state;
		}
    };

    /**
     * Default constructor
     */
    protected PlayList(IState state) {
        this((List<IAudioObject>) null, state);
    }

    /**
     * Builds a new play list with the given list of audio objects
     * 
     * @param list
     */
    protected PlayList(List<? extends IAudioObject> audioObjectsList, IState state) {
        this.audioObjects = new PlayListPointedList(state);
        this.mode = PlayListMode.getPlayListMode(this, state);
        this.state = state;
        if (audioObjectsList != null) {
            add(audioObjectsList);
        }
    }

    /**
     * Private constructor, only for clone
     * 
     * @param playList
     * @param state
     */
    private PlayList(PlayList playList, IState state) {
        this.name = playList.name == null ? null : playList.name;
        this.state = state;
        this.audioObjects = new PlayListPointedList(playList.audioObjects, state);
        this.mode = PlayListMode.getPlayListMode(this, state);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayList#setState(net.sourceforge.atunes.model.IState)
	 */
    @Override
	public void setState(IState state) {
		this.state = state;
		((PlayListPointedList)this.audioObjects).setState(state);
	}
    
    //////////////////////////////////////////////////////////////// ADD OPERATIONS /////////////////////////////////////////////////////////

    /**
     * Adds an audio object at the end of play list
     * 
     * @param audioObject
     */
    protected void add(IAudioObject audioObject) {
        List<IAudioObject> audioObjectsAdded = new ArrayList<IAudioObject>();
        audioObjectsAdded.add(audioObject);
        this.add(audioObjectsAdded);
    }

    /**
     * Adds a list of audio objects at the end of play list
     * 
     * @param audioObjectsList
     */
    private void add(List<? extends IAudioObject> audioObjectsList) {
        int position = this.audioObjects.size();
        add(position, audioObjectsList);
    }

    /**
     * Adds an audio object at given index position
     * 
     * @param index
     * @param list
     */
    protected void add(int index, IAudioObject audioObject) {
        List<IAudioObject> audioObjectsAdded = new ArrayList<IAudioObject>();
        audioObjectsAdded.add(audioObject);
        add(index, audioObjectsAdded);
    }

    /**
     * Adds a list of audio objects at given index position
     * 
     * @param index
     * @param audioObjectsList
     */
    protected void add(int index, List<? extends IAudioObject> audioObjectsList) {
        this.audioObjects.addAll(index, audioObjectsList);
        notifyAudioObjectsAdded(index, audioObjectsList);
    }

    ////////////////////////////////////////////////////////////// REMOVE OPERATIONS /////////////////////////////////////////////////////////////

    /**
     * Removes given row from this play list
     * 
     * @param list
     */
    protected void remove(int index) {
        IAudioObject ao = get(index);
        if (ao != null) {
            PlayListAudioObject plao = new PlayListAudioObject();
            plao.setPosition(index);
            plao.setAudioObject(ao);
            List<PlayListAudioObject> removedAudioObjects = new ArrayList<PlayListAudioObject>();
            removedAudioObjects.add(plao);
            audioObjects.remove(index);
            notifyAudioObjectsRemoved(removedAudioObjects);
        }
    }

    /**
     * Removes given list of audio objects from this play list. All ocurrences
     * are removed
     * 
     * @param list
     */
    protected void remove(List<? extends IAudioObject> list) {
        // First get all positions of objects to remove
        List<PlayListAudioObject> playListAudioObjects = new ArrayList<PlayListAudioObject>();
        for (IAudioObject ao : list) {
            List<IAudioObject> clonedList = new ArrayList<IAudioObject>(audioObjects.getList());
            while (clonedList.indexOf(ao) != -1) {
                int index = clonedList.indexOf(ao);
                PlayListAudioObject playListAudioObject = new PlayListAudioObject();
                playListAudioObject.setPosition(index);
                playListAudioObject.setAudioObject(ao);
                playListAudioObjects.add(playListAudioObject);
                clonedList = clonedList.subList(index + 1, clonedList.size());
            }
        }

        // Sort in reverse order to remove last index first and avoid shift
        Collections.sort(playListAudioObjects, new PlayListAudioObjectComparator());

        for (PlayListAudioObject plao : playListAudioObjects) {
            this.audioObjects.remove(plao.getPosition());
        }
        notifyAudioObjectsRemoved(playListAudioObjects);
    }

    /**
     * Clears play list
     */
    protected void clear() {
        this.audioObjects.clear();
        notifyAudioObjectsRemovedAll();
    }

    /**
     * Replaces given position with given object
     * 
     * @param index
     * @param newObject
     */
    protected void replace(int index, IAudioObject newObject) {
        this.audioObjects.replace(index, newObject);
    }

    /////////////////////////////////////////////////////////////// ROW MOVE OPERATIONS //////////////////////////////////////////////////////////

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayList#moveRowTo(int, int)
	 */
    @Override
	public void moveRowTo(int sourceRow, int targetRow) {
        // Check arguments
        if (sourceRow < 0 || sourceRow >= size()) {
            throw new IllegalArgumentException(StringUtils.getString("sourceRow = ", sourceRow, " playlist size = ", size()));
        }
        if (targetRow < 0 || targetRow >= size()) {
            throw new IllegalArgumentException(StringUtils.getString("targetRow = ", targetRow, " playlist size = ", size()));
        }

        IAudioObject audioObjectToMove = get(sourceRow);

        boolean sourceRowIsPointed = audioObjects.getPointer() == sourceRow;

        // Remove from previous row
        remove(sourceRow);

        // Add at new row
        add(targetRow, audioObjectToMove);

        // Update current index if necessary
        if (sourceRowIsPointed) {
            audioObjects.setPointer(targetRow);
        }
    }

    /**
     * Sorts this play list with given comparator
     * 
     * @param c
     */
    protected void sort(Comparator<IAudioObject> c) {
        this.audioObjects.sort(c);
        notifyCurrentAudioObjectChanged(this.audioObjects.getCurrentObject());
    }

    /**
     * Shuffles this play list
     */
    protected void shuffle() {
        this.audioObjects.shuffle();
        notifyCurrentAudioObjectChanged(this.audioObjects.getCurrentObject());
    }

    //////////////////////////////////////////////////////////////// OTHER OPERATIONS /////////////////////////////////////////////////////////////

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayList#indexOf(net.sourceforge.atunes.model.IAudioObject)
	 */
    @Override
	public int indexOf(IAudioObject audioObject) {
        return this.audioObjects.indexOf(audioObject);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayList#size()
	 */
    @Override
	public int size() {
        return this.audioObjects.size();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayList#isEmpty()
	 */
    @Override
	public boolean isEmpty() {
        return this.audioObjects.isEmpty();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayList#get(int)
	 */
    @Override
	public IAudioObject get(int index) {
        if (index < 0 || index >= this.audioObjects.size()) {
            return null;
        }
        return this.audioObjects.get(index);
    }

    /**
     * Return current audio object.
     * 
     * @return the current audio object
     */
    protected IAudioObject getCurrentAudioObject() {
        return this.audioObjects.getCurrentObject();
    }

    /**
     * Sets the current audio object index
     * 
     * @param index
     */
    protected void setCurrentAudioObjectIndex(int index) {
        this.audioObjects.setPointer(index);
        notifyCurrentAudioObjectChanged(this.audioObjects.getCurrentObject());
    }

    /**
     * Returns the index of the current audio object
     * 
     * @return
     */
    protected int getCurrentAudioObjectIndex() {
        // If pointer is not null return index, otherwise return 0
        return this.audioObjects.getPointer() != null ? this.audioObjects.getPointer() : 0;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayList#contains(net.sourceforge.atunes.model.IAudioObject)
	 */
    @Override
	public boolean contains(IAudioObject audioObject) {
        return this.audioObjects.contains(audioObject);
    }

    /**
     * Gets the name of this play list
     * 
     * @return the name
     */
    protected String getName() {
        return this.name;
    }

    /**
     * Sets the name of this play list
     * 
     * @param name
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a random index
     * 
     * @return
     */
    protected int getRandomPosition() {
        return new Random(System.currentTimeMillis()).nextInt(this.audioObjects.size());
    }

    @Override
    protected PlayList clone() {
        return new PlayList(this, state);
    }

    /**
     * Returns next audio object
     * 
     * @return
     */
    protected IAudioObject moveToNextAudioObject() {
        IAudioObject nextObject = getMode().moveToNextAudioObject();
        notifyCurrentAudioObjectChanged(nextObject);
        return nextObject;
    }

    /**
     * Returns previous audio object
     * 
     * @return
     */
    protected IAudioObject moveToPreviousAudioObject() {
        IAudioObject previousObject = getMode().moveToPreviousAudioObject();
        notifyCurrentAudioObjectChanged(previousObject);
        return previousObject;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayList#getNextAudioObject(int)
	 */
    @Override
	public IAudioObject getNextAudioObject(int index) {
        return getMode().getNextAudioObject(index);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayList#getPreviousAudioObject(int)
	 */
    @Override
	public IAudioObject getPreviousAudioObject(int index) {
        return getMode().getPreviousAudioObject(index);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayList#getLength()
	 */
    @Override
	public String getLength() {
        long seconds = 0;
        for (IAudioObject song : this.audioObjects.getList()) {
            seconds += song.getDuration();
        }
        return StringUtils.fromSecondsToHoursAndDays(seconds);
    }

    /**
     * Returns audio objects of this play list
     * 
     * @return
     */
    protected List<IAudioObject> getAudioObjects() {
        return this.audioObjects.getList();
    }

    /**
     * Private method to call listeners
     * 
     * @param position
     * @param audioObjectList
     */
    private void notifyAudioObjectsAdded(int position, List<? extends IAudioObject> audioObjectList) {
        List<PlayListAudioObject> playListAudioObjects = PlayListAudioObject.getList(position, audioObjectList);

        // Notify mode too
        getMode().audioObjectsAdded(playListAudioObjects);

        PlayListEventListeners.audioObjectsAdded(playListAudioObjects);
    }

    /**
     * Private method to call listeners
     * 
     * @param audioObjectList
     */
    private void notifyAudioObjectsRemoved(List<PlayListAudioObject> audioObjectList) {   	
        // Notify mode too
        getMode().audioObjectsRemoved(audioObjectList);

    	PlayListEventListeners.audioObjectsRemoved(audioObjectList);
    }

    /**
     * Private method to call listeners
     */
    private void notifyAudioObjectsRemovedAll() {
    	
        // Notify mode too
        getMode().audioObjectsRemovedAll();

    	PlayListEventListeners.playListCleared();
    }

    /**
     * Private method to call listeners
     * @param audioObject
     */
    private void notifyCurrentAudioObjectChanged(IAudioObject audioObject) {
    	PlayListEventListeners.selectedAudioObjectHasChanged(audioObject);
    }

    /**
     * @return the mode
     */
    protected PlayListMode getMode() {
        return mode;
    }

    /**
     * @param mode
     *            the mode to set
     */
    protected void setMode(PlayListMode mode) {
        this.mode = mode;
    }

    /**
     * Returns pointed list of audio objects
     * 
     * @return
     */
    PointedList<IAudioObject> getPointedList() {
        return this.audioObjects;
    }

    void addToPlaybackHistory(IAudioObject object) {
        this.mode.addToPlaybackHistory(object);
    }

    /**
     * Used when setting content to a play list read from disk
     * 
     * @param content
     */
    protected void setContent(List<IAudioObject> content) {
        this.audioObjects.setContent(content);
        // This is an internal operation used when reading play lists from disk
        // There is no need to call listeners here. Even it will cause wrong behaviour
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayList#reset()
	 */
	@Override
	public void reset() {
		setCurrentAudioObjectIndex(0);
		getMode().reset();
	}
}
