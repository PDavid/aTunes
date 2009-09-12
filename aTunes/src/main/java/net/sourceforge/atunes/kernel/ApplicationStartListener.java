package net.sourceforge.atunes.kernel;

/**
 * Interface for classes that must execute code after application start (usually
 * handlers)
 * 
 * @author fleax
 * 
 */
public interface ApplicationStartListener {

	/**
	 * Called after application start
	 */
	public void applicationStarted();
}
