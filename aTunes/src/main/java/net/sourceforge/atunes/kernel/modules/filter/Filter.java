package net.sourceforge.atunes.kernel.modules.filter;

public abstract class Filter {
	
	/**
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * @return the description
	 */
	public abstract String getDescription();
	
	/**
	 * Called to apply a filter. If argument is null filter should be removed
	 * @param filter
	 */
	public abstract void applyFilter(String filter);
	

}
