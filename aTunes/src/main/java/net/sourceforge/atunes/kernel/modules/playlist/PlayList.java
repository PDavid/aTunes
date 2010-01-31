/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.kernel.modules.playlist;

import java.awt.EventQueue;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.swing.SwingUtilities;

import org.commonjukebox.plugins.PluginApi;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.PointedList;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class represents a play list.
 * 
 * @author fleax
 */
public class PlayList implements Serializable, Cloneable {

    private static final long serialVersionUID = 2756513776762920794L;

    /**
     * Listeners notified when this play list changes
     */
    private transient List<PlayListChangedListener> listeners;

    /**
     * Play list mode to select audio objects
     */
    private transient PlayListMode mode;

    /**
     * Name of play list as shown on play list tabs.
     */
    private String name;
    
    /**
     * Flag indicating if this playlist contents need to be written to disk
     */
    private transient boolean dirty;

    /**
     * Pointed List of audio objects of this play list
     */
    private PointedList<AudioObject> audioObjects = new PlayListPointedList();
    
    class PlayListPointedList extends PointedList<AudioObject> {
        private static final long serialVersionUID = -6966402482637754615L;

        PlayListPointedList() {
        	super();
        }
        
        PlayListPointedList(PointedList<AudioObject> pointedList) {
        	super(pointedList);
        }
        
        @Override
        public boolean isCyclic() {
            return ApplicationState.getInstance().isRepeat();
        }
    };

    /**
     * Default constructor
     */
    protected PlayList() {
        this((List<AudioObject>) null);
    }

    /**
     * Builds a new play list with the given list of audio objects
     * 
     * @param list
     */
    protected PlayList(List<AudioObject> audioObjectsList) {
        this.mode = PlayListMode.getPlayListMode(this);
        if (audioObjectsList != null) {
            add(audioObjectsList);
        }
    }

    /**
     * Private constructor, only for clone
     * 
     * @param playList
     */
    private PlayList(PlayList playList) {
        this.name = playList.name == null ? null : new String(playList.name);
        this.audioObjects = new PlayListPointedList(playList.audioObjects);
        this.mode = PlayListMode.getPlayListMode(this);
    }

    //////////////////////////////////////////////////////////////// ADD OPERATIONS /////////////////////////////////////////////////////////

    /**
     * Adds an audio object at the end of play list
     * 
     * @param audioObject
     */
    protected void add(AudioObject audioObject) {
        List<AudioObject> audioObjectsAdded = new ArrayList<AudioObject>();
        audioObjectsAdded.add(audioObject);
        this.add(audioObjectsAdded);
    }

    /**
     * Adds a list of audio objects at the end of play list
     * 
     * @param audioObjectsList
     */
    private void add(List<? extends AudioObject> audioObjectsList) {
        int position = this.audioObjects.size();
        add(position, audioObjectsList);
    }

    /**
     * Adds an audio object at given index position
     * 
     * @param index
     * @param list
     */
    protected void add(int index, AudioObject audioObject) {
        List<AudioObject> audioObjectsAdded = new ArrayList<AudioObject>();
        audioObjectsAdded.add(audioObject);
        add(index, audioObjectsAdded);
    }

    /**
     * Adds a list of audio objects at given index position
     * 
     * @param index
     * @param audioObjectsList
     */
    protected void add(int index, List<? extends AudioObject> audioObjectsList) {
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
        AudioObject ao = get(index);
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
    protected void remove(List<AudioObject> list) {
        // First get all positions of objects to remove
        List<PlayListAudioObject> playListAudioObjects = new ArrayList<PlayListAudioObject>();
        for (AudioObject ao : list) {
            List<AudioObject> clonedList = new ArrayList<AudioObject>(audioObjects.getList());
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
        Collections.sort(playListAudioObjects, new Comparator<PlayListAudioObject>() {
            @Override
            public int compare(PlayListAudioObject o1, PlayListAudioObject o2) {
                return -Integer.valueOf(o1.getPosition()).compareTo(Integer.valueOf(o2.getPosition()));
            }
        });

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
    protected void replace(int index, AudioObject newObject) {
        this.audioObjects.replace(index, newObject);
    }

    /////////////////////////////////////////////////////////////// ROW MOVE OPERATIONS //////////////////////////////////////////////////////////

    /**
     * Moves one row in play list
     * 
     * @param sourceRow
     * @param targetRow
     */
    protected void moveRowTo(int sourceRow, int targetRow) {
        // Check arguments
        if (sourceRow < 0 || sourceRow >= size()) {
            throw new IllegalArgumentException(StringUtils.getString("sourceRow = ", sourceRow, " playlist size = ", size()));
        }
        if (targetRow < 0 || targetRow >= size()) {
            throw new IllegalArgumentException(StringUtils.getString("targetRow = ", targetRow, " playlist size = ", size()));
        }

        AudioObject audioObjectToMove = get(sourceRow);

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
    protected void sort(Comparator<AudioObject> c) {
        this.audioObjects.sort(c);
        // Mark as dirty
        setDirty(true);
    }

    /**
     * Shuffles this play list
     */
    protected void shuffle() {
        this.audioObjects.shuffle();
        updateUI();
        // Mark as dirty
        setDirty(true);
    }

    //////////////////////////////////////////////////////////////// OTHER OPERATIONS /////////////////////////////////////////////////////////////

    /**
     * Returns first index position of given audio object
     * 
     * @param audioObject
     * @return
     */
    public int indexOf(AudioObject audioObject) {
        return this.audioObjects.indexOf(audioObject);
    }

    /**
     * Returns size of this play list
     * 
     * @return
     */
    public int size() {
        return this.audioObjects.size();
    }

    /**
     * Returns <code>true</code> if this play list is empty
     * 
     * @return
     */
    public boolean isEmpty() {
        return this.audioObjects.isEmpty();
    }

    /**
     * Returns AudioObject at given index
     * 
     * @param index
     * @return
     */
    public AudioObject get(int index) {
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
    protected AudioObject getCurrentAudioObject() {
        return this.audioObjects.getCurrentObject();
    }

    /**
     * Sets the current audio object index
     * 
     * @param index
     */
    protected void setCurrentAudioObjectIndex(int index) {
        this.audioObjects.setPointer(index);
        updateUI();
    }

    protected void updateUI() {
    	if (!EventQueue.isDispatchThread()) {
    		SwingUtilities.invokeLater(new Runnable() {
    			@Override
    			public void run() {
    		        ControllerProxy.getInstance().getPlayListController().refreshPlayList();
    		        ControllerProxy.getInstance().getPlayListController().scrollPlayList(false);
    			}
    		});
    	} else {
            ControllerProxy.getInstance().getPlayListController().refreshPlayList();
            ControllerProxy.getInstance().getPlayListController().scrollPlayList(false);
    	}
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

    /**
     * Returns only AudioFile objects contained in this play list
     * 
     * @return
     */
    public List<AudioObject> getObjectsOfType(Class<? extends AudioObject> clazz) {
        List<AudioObject> result = new ArrayList<AudioObject>();
        for (AudioObject ao : this.audioObjects.getList()) {
            if (ao.getClass().equals(clazz)) {
                result.add(ao);
            }
        }
        return result;
    }

    /**
     * Returns <code>true</code> if audio object is in play list
     * 
     * @param audioObject
     * @return
     */
    public boolean contains(AudioObject audioObject) {
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
        return new PlayList(this);
    }

    /**
     * Returns next audio object
     * 
     * @return
     */
    protected AudioObject moveToNextAudioObject() {
        AudioObject nextObject = getMode().moveToNextAudioObject();
        updateUI();
        return nextObject;
    }

    /**
     * Returns previous audio object
     * 
     * @return
     */
    protected AudioObject moveToPreviousAudioObject() {
        AudioObject previousObject = getMode().moveToPreviousAudioObject();
        updateUI();
        return previousObject;
    }

    protected AudioObject getNextAudioObject(int index) {
        return getMode().getNextAudioObject(index);
    }

    protected AudioObject getPreviousAudioObject(int index) {
        return getMode().getPreviousAudioObject(index);
    }

    /**
     * Returns play list length in string format.
     * 
     * @return the length
     */
    public String getLength() {
        long seconds = 0;
        for (AudioObject song : this.audioObjects.getList()) {
            seconds += song.getDuration();
        }
        return StringUtils.fromSecondsToHoursAndDays(seconds);
    }

    /**
     * Returns audio objects of this play list
     * 
     * @return
     */
    protected List<AudioObject> getAudioObjects() {
        return this.audioObjects.getList();
    }

    /**
     * Adds a play list changed listener
     * 
     * @param listener
     */
    protected void addPlayListChangedListener(PlayListChangedListener listener) {
        getListeners().add(listener);
    }

    /**
     * Removes a play list changed listener
     * 
     * @param listener
     */
    protected void removePlayListChangedListener(PlayListChangedListener listener) {
        getListeners().remove(listener);
    }

    /**
     * Private method to call listeners
     * 
     * @param position
     * @param audioObjectList
     */
    private void notifyAudioObjectsAdded(int position, List<? extends AudioObject> audioObjectList) {
        List<PlayListAudioObject> playListAudioObjects = PlayListAudioObject.getList(position, audioObjectList);
        for (PlayListChangedListener listener : getListeners()) {
            listener.audioObjectsAdded(playListAudioObjects);
        }
        // Notify mode too
        getMode().audioObjectsAdded(playListAudioObjects);
        
        // Mark as dirty
        setDirty(true);
    }

    /**
     * Private method to call listeners
     * 
     * @param audioObjectList
     */
    private void notifyAudioObjectsRemoved(List<PlayListAudioObject> audioObjectList) {
        for (PlayListChangedListener listener : getListeners()) {
            listener.audioObjectsRemoved(audioObjectList);
        }
        // Notify mode too
        getMode().audioObjectsRemoved(audioObjectList);
        
        // Mark as dirty
        setDirty(true);
    }

    /**
     * Private method to call listeners
     */
    private void notifyAudioObjectsRemovedAll() {
        for (PlayListChangedListener listener : getListeners()) {
            listener.audioObjectsRemovedAll();
        }
        // Notify mode too
        getMode().audioObjectsRemovedAll();

        // Mark as dirty
        setDirty(true);
    }

    private List<PlayListChangedListener> getListeners() {
        if (listeners == null) {
            listeners = new ArrayList<PlayListChangedListener>();
        }
        return listeners;
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
    PointedList<AudioObject> getPointedList() {
        return this.audioObjects;
    }

    void addToPlaybackHistory(AudioObject object) {
        this.mode.addToPlaybackHistory(object);
    }

	/**
	 * @return the dirty
	 */
	protected boolean isDirty() {
		return dirty;
	}

	/**
	 * @param dirty the dirty to set
	 */
	protected void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	/**
	 * Used when setting content to a play list read from disk
	 * @param content
	 */
	protected void setContent(List<AudioObject> content) {
		this.audioObjects.setContent(content);
		notifyAudioObjectsAdded(0, content);
        // As this method is used when reading from disk playlist can't be dirty
        setDirty(false);
	}
}
