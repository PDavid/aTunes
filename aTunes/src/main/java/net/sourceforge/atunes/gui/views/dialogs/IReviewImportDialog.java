package net.sourceforge.atunes.gui.views.dialogs;

import java.io.File;
import java.util.List;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITagAttributesReviewed;

/**
 * A dialog to review tags of a set of files before importing them
 * @author alex
 *
 */
public interface IReviewImportDialog {

	/**
	 * @return the dialogCancelled
	 */
	public boolean isDialogCancelled();

	/**
	 * Shows dialog with given data
	 * 
	 * @param folders
	 * @param files
	 */
	public void showDialog(List<File> folders,
			List<ILocalAudioObject> filesToLoad);

	/**
	 * Returns result of reviewing tags
	 * 
	 * @return
	 */
	public ITagAttributesReviewed getResult();

}