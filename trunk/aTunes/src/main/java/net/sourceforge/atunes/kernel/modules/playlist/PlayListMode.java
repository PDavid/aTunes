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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IState;

final class PlayListMode {

    /**
     * List with play list positions in random order. This list is updated when
     * there is a change in play list, even if shuffle is disabled
     */
    private ShufflePointedList shufflePlayList;

    /**
     * Play back history
     */
    private PlaybackHistory playbackHistory;

    /**
     * Play list bound to this mode
     */
    private PlayList playList;
    
    private IState state;

    protected static PlayListMode getPlayListMode(PlayList playList, IState state) {
        return new PlayListMode(playList, state);
    }

    private PlayListMode(PlayList playList, IState state) {
        // Initialize shuffle list
        this.shufflePlayList = new ShufflePointedList(state);
        this.state = state;
        this.playbackHistory = new PlaybackHistory();
        this.playList = playList;
        if (playList != null && playList.getAudioObjects() != null && !playList.getAudioObjects().isEmpty()) {
            audioObjectsAdded(PlayListAudioObject.getList(0, playList.getAudioObjects()));
            // Set shuffle pointer
            int currentIndex = playList.getCurrentAudioObjectIndex();
            int indexInShuffle = shufflePlayList.indexOf(currentIndex);
            if (indexInShuffle != -1) {
                Collections.swap(shufflePlayList.getList(), 0, indexInShuffle);
            }
        }
    }

    IAudioObject moveToPreviousAudioObject() {
        if (this.playList.isEmpty()) {
            return null;
        }

        IAudioObject previosulyPlayedObject = playbackHistory.moveToPreviousInHistory();
        if (previosulyPlayedObject != null) {
            int index = this.playList.indexOf(previosulyPlayedObject);
            // Update pointed object
            this.playList.getPointedList().setPointer(index);
            return previosulyPlayedObject;
        }

        // No previous object
        if (isShuffle()) {
            Integer previousIndex = shufflePlayList.moveToPreviousObject();
            if (previousIndex == null) {
                return null;
            }
            // Update current index and return object
            this.playList.setCurrentAudioObjectIndex(previousIndex);
            return this.playList.get(previousIndex);
        }

        IAudioObject previousAudioObject = playList.getPointedList().moveToPreviousObject();
        return previousAudioObject;
    }

    IAudioObject moveToNextAudioObject() {
        if (this.playList.isEmpty()) {
            return null;
        }

        IAudioObject nextPreviouslyPlayedObject = playbackHistory.moveToNextInHistory();
        if (nextPreviouslyPlayedObject != null) {
            int index = this.playList.indexOf(nextPreviouslyPlayedObject);
            // Update pointed object
            this.playList.getPointedList().setPointer(index);
            return nextPreviouslyPlayedObject;
        }

        // No next previously object
        if (isShuffle()) {
            Integer nextIndex = shufflePlayList.moveToNextObject();
            if (nextIndex == null) {
                return null;
            }
            // Update current index of play list and return object
            this.playList.setCurrentAudioObjectIndex(nextIndex);
            return this.playList.get(nextIndex);
        }

        IAudioObject nextAudioObject = playList.getPointedList().moveToNextObject();
        return nextAudioObject;
    }

    IAudioObject getPreviousAudioObject(int index) {
        if (this.playList.isEmpty()) {
            return null;
        }

        IAudioObject previosulyPlayedObject = playbackHistory.getPreviousInHistory(index);
        if (previosulyPlayedObject != null) {
            return previosulyPlayedObject;
        }

        // No previous object
        if (isShuffle()) {
            Integer previousIndex = shufflePlayList.getPreviousObject(index);
            if (previousIndex == null) {
                return null;
            }
            return this.playList.get(previousIndex);
        }

        return playList.getPointedList().getPreviousObject(index);
    }

    IAudioObject getNextAudioObject(int index) {
        if (this.playList.isEmpty()) {
            return null;
        }

        IAudioObject nextPreviouslyPlayedObject = playbackHistory.getNextInHistory(index);
        if (nextPreviouslyPlayedObject != null) {
            return nextPreviouslyPlayedObject;
        }

        // No next previously object
        if (isShuffle()) {
            Integer nextIndex = shufflePlayList.getNextObject(index);
            if (nextIndex == null) {
                return null;
            }
            return this.playList.get(nextIndex);
        }

        return playList.getPointedList().getNextObject(index);
    }

    void audioObjectsAdded(List<PlayListAudioObject> audioObjectsAdded) {
        if (audioObjectsAdded == null || audioObjectsAdded.isEmpty()) {
            return;
        }

        // Update shuffle play list 
        this.shufflePlayList.add(audioObjectsAdded);
    }

    void audioObjectsRemoved(List<PlayListAudioObject> audioObjectsRemoved) {
        if (audioObjectsRemoved == null || audioObjectsRemoved.isEmpty()) {
            return;
        }

        // Update shuffle list
        for (PlayListAudioObject plao : audioObjectsRemoved) {
            int indexToRemove = shufflePlayList.indexOf(plao.getPosition());
            shufflePlayList.remove(indexToRemove);
        }

        // Update history
        // Only remove from history audio objects removed from play list
        // If an audio object is duplicated in play list and is in history, if one of its occurrences is removed, history is not updated
        List<IAudioObject> audioObjectsToRemoveFromHistory = new ArrayList<IAudioObject>();
        for (PlayListAudioObject plao : audioObjectsRemoved) {
            if (!playList.contains(plao.getAudioObject())) {
                // If audio object is no more present in play list then remove from history
                audioObjectsToRemoveFromHistory.add(plao.getAudioObject());
            }
        }
        playbackHistory.remove(audioObjectsToRemoveFromHistory);
    }

    void audioObjectsRemovedAll() {
        shufflePlayList.clear();
        playbackHistory.clear();
    }

    private boolean isShuffle() {
        return state.isShuffle();
    }

    void addToPlaybackHistory(IAudioObject object) {
        this.playbackHistory.addToHistory(object);
    }    
    
    void reset() {
    	shufflePlayList.setPointer(0);
    }
}
