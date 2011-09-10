package net.sourceforge.atunes.model;

import java.util.Map;


public interface ITreeGeneratorFactory {

	/**
	 * Initializes factory
	 * @param generators
	 */
	public void setGenerators(Map<ViewMode, ITreeGenerator> generators);

	/**
	 * Returns generator for given mode
	 * @param viewMode
	 * @return
	 */
	public ITreeGenerator getTreeGenerator(ViewMode viewMode);

}