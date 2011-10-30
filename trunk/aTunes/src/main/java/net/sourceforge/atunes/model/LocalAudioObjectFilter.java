package net.sourceforge.atunes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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


}
