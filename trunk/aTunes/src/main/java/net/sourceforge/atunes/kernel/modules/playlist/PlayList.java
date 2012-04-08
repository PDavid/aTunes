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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.PlayListEventListeners;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListAudioObject;
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

	/**
	 * @param state
	 */
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
    protected final void add(IAudioObject audioObject) {
        add(Collections.singletonList(audioObject));
    }

    /**
     * Adds a list of audio objects at the end of play list
     * 
     * @param audioObjectsList
     */
    private void add(List<? extends IAudioObject> audioObjectsList) {
        add(this.audioObjects.size(), audioObjectsList);
    }

    /**
     * Adds an audio object at given index position
     * 
     * @param index
     * @param list
     */
    @Override
    public final void add(int index, IAudioObject audioObject) {
        add(index, Collections.singletonList(audioObject));
    }

    /**
     * Adds a list of audio objects at given index position
     * 
     * @param index
     * @param audioObjectsList
     */
    @Override
	public final void add(int index, List<? extends IAudioObject> audioObjectsList) {
        this.audioObjects.addAll(index, audioObjectsList);
        notifyAudioObjectsAdded(index, audioObjectsList);
    }

    ////////////////////////////////////////////////////////////// REMOVE OPERATIONS /////////////////////////////////////////////////////////////

    /**
     * Removes given row from this play list
     * 
     * @param list
     */
    @Override
    public void remove(int index) {
        IAudioObject ao = get(index);
        if (ao != null) {
            PlayListAudioObject plao = new PlayListAudioObject();
            plao.setPosition(index);
            plao.setAudioObject(ao);
            List<IPlayListAudioObject> removedAudioObjects = new ArrayList<IPlayListAudioObject>();
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
    @Override
    public void remove(List<? extends IAudioObject> list) {
        // First get all positions of objects to remove
        List<IPlayListAudioObject> playListAudioObjects = new ArrayList<IPlayListAudioObject>();
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

        for (IPlayListAudioObject plao : playListAudioObjects) {
            this.audioObjects.remove(plao.getPosition());
        }
        notifyAudioObjectsRemoved(playListAudioObjects);
    }

    /**
     * Clears play list
     */
    @Override
    public void clear() {
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
    @Override
    public void sort(Comparator<IAudioObject> c) {
        this.audioObjects.sort(c);
        notifyCurrentAudioObjectChanged(this.audioObjects.getCurrentObject());
    }

    /**
     * Shuffles this play list
     */
    @Override
	public void shuffle() {
        this.audioObjects.shuffle();
        notifyCurrentAudioObjectChanged(this.audioObjects.getCurrentObject());
    }

    //////////////////////////////////////////////////////////////// OTHER OPERATIONS /////////////////////////////////////////////////////////////

    @Override
	public int indexOf(IAudioObject audioObject) {
        return this.audioObjects.indexOf(audioObject);
    }

    @Override
	public int size() {
        return this.audioObjects.size();
    }

    @Override
	public boolean isEmpty() {
        return this.audioObjects.isEmpty();
    }

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
    @Override
    public IAudioObject getCurrentAudioObject() {
        return this.audioObjects.getCurrentObject();
    }

    /**
     * Sets the current audio object index
     * 
     * @param index
     */
    @Override
    public void setCurrentAudioObjectIndex(int index) {
        this.audioObjects.setPointer(index);
        notifyCurrentAudioObjectChanged(this.audioObjects.getCurrentObject());
    }

    /**
     * Returns the index of the current audio object
     * 
     * @return
     */
    @Override
    public int getCurrentAudioObjectIndex() {
        // If pointer is not null return index, otherwise return 0
        return this.audioObjects.getPointer() != null ? this.audioObjects.getPointer() : 0;
    }

    @Override
	public boolean contains(IAudioObject audioObject) {
        return this.audioObjects.contains(audioObject);
    }

    /**
     * Gets the name of this play list
     * 
     * @return the name
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of this play list
     * 
     * @param name
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a random index
     * 
     * @return
     */
    @Override
    public int getRandomPosition() {
        return new Random(System.currentTimeMillis()).nextInt(this.audioObjects.size());
    }

    @Override
    public PlayList clone() {
        return new PlayList(this, state);
    }

    /**
     * Returns next audio object
     * 
     * @return
     */
    @Override
    public IAudioObject moveToNextAudioObject() {
        IAudioObject nextObject = getMode().moveToNextAudioObject();
        notifyCurrentAudioObjectChanged(nextObject);
        return nextObject;
    }

    /**
     * Returns previous audio object
     * 
     * @return
     */
    @Override
    public IAudioObject moveToPreviousAudioObject() {
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
        List<IPlayListAudioObject> playListAudioObjects = PlayListAudioObject.getList(position, audioObjectList);

        // Notify mode too
        getMode().audioObjectsAdded(playListAudioObjects);

        Context.getBean(PlayListEventListeners.class).audioObjectsAdded(playListAudioObjects);
    }

    /**
     * Private method to call listeners
     * 
     * @param audioObjectList
     */
    private void notifyAudioObjectsRemoved(List<IPlayListAudioObject> audioObjectList) {   	
        // Notify mode too
        getMode().audioObjectsRemoved(audioObjectList);

        Context.getBean(PlayListEventListeners.class).audioObjectsRemoved(audioObjectList);
    }

    /**
     * Private method to call listeners
     */
    private void notifyAudioObjectsRemovedAll() {
    	
        // Notify mode too
        getMode().audioObjectsRemovedAll();

        Context.getBean(PlayListEventListeners.class).playListCleared();
    }

    /**
     * Private method to call listeners
     * @param audioObject
     */
    private void notifyCurrentAudioObjectChanged(IAudioObject audioObject) {
    	Context.getBean(PlayListEventListeners.class).selectedAudioObjectHasChanged(audioObject);
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

    @Override 
    public void addToPlaybackHistory(IAudioObject object) {
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
	
	@Override
	public List<IAudioObject> getAudioObjectsList() {
		return new ArrayList<IAudioObject>(audioObjects.getList());
	}
}
