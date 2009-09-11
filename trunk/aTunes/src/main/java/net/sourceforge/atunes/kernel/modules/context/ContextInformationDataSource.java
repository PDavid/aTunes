package net.sourceforge.atunes.kernel.modules.context;

import java.util.Map;

/**
 * This interface must be implemented by classes responsible of retrieve data used by a context panel
 * @author alex
 *
 */
public interface ContextInformationDataSource {

	/**
	 * This method returns a map of objects containing information retrieved from data source given some parameters
	 * @param parameters
	 * @return Map of objects containing information
	 */
	public Map<String, ?> getData(Map<String, ?> parameters);
}
