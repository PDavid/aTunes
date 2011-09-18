package net.sourceforge.atunes.model;

import java.io.File;

import javax.swing.table.TableCellEditor;

import net.sourceforge.atunes.kernel.modules.tags.AbstractTag;

/**
 * Keeps information about a set of changes to be made on tags when importing a
 * set of files
 * @author alex
 *
 */
public interface ITagAttributesReviewed {

	/**
	 * Returns the number of tags attributes used
	 * 
	 * @return
	 */
	public int getTagAttributesCount();

	/**
	 * Returns the name of the tag attribute at given index
	 * 
	 * @param index
	 * @return
	 */
	public String getTagAttributeName(int index);

	/**
	 * Returns the value of the tag attribute at given index
	 * 
	 * @param index
	 * @param audioFile
	 * @return
	 */
	public String getValueForTagAttribute(int index, ILocalAudioObject audioFile);

	/**
	 * Returns the value of the tag attribute at given index for the given
	 * folder if it has been changed or null
	 * 
	 * @param index
	 * @param folder
	 * @return
	 */
	public String getChangeForAttributeAndFolder(int index, File folder);

	/**
	 * Stores a tag attribute change at given index and folder
	 * 
	 * @param index
	 * @param folder
	 * @param value
	 */
	public void setTagAttributeForFolder(int index, File folder, String value);

	/**
	 * Returns a tag for given LocalAudioObject with tag attributes changed according
	 * to information stored in this object
	 * 
	 * @param file
	 * @return
	 */
	public AbstractTag getTagForAudioFile(ILocalAudioObject file);

	public TableCellEditor getCellEditorForTagAttribute(int index);

	/**
	 * Returns index of given tag attribute
	 * 
	 * @param tagAttributeName
	 * @return
	 */
	public int getTagAttributeIndex(String tagAttributeName);

}