package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.ViewMode;

class RepositoryAudioObjectsHelper {

    /**
     * Returns objects from node with view and filter or all objects if selection is root and no filter
     * @param allObjects
     * @param node
     * @param viewMode
     * @param treeFilter
     * @return
     */
    List<? extends IAudioObject> getAudioObjectForTreeNode(Collection<ILocalAudioObject> allObjects, DefaultMutableTreeNode node, ViewMode viewMode, String treeFilter) {
        List<ILocalAudioObject> songs = new ArrayList<ILocalAudioObject>();
        if (node.isRoot()) {
            if (treeFilter == null) {
                songs.addAll(allObjects);
            } else {
                for (int i = 0; i < node.getChildCount(); i++) {
                    @SuppressWarnings("unchecked")
					ITreeObject<ILocalAudioObject> obj = (ITreeObject<ILocalAudioObject>) ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject();
                    songs.addAll(obj.getAudioObjects());
                }
            }
        } else {
            @SuppressWarnings("unchecked")
			ITreeObject<ILocalAudioObject> obj = (ITreeObject<ILocalAudioObject>) node.getUserObject();
            songs = obj.getAudioObjects();
        }
        return songs;
    }

}
