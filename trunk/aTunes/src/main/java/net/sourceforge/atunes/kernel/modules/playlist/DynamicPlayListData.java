package net.sourceforge.atunes.kernel.modules.playlist;

import java.io.Serializable;

import net.sourceforge.atunes.model.ISearchNodeRepresentation;

/**
 * Date to write / read to a play list file
 * 
 * @author alex
 * 
 */
public class DynamicPlayListData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3506979542915411533L;

	ISearchNodeRepresentation query;

	/**
	 * @param query
	 */
	public void setQuery(final ISearchNodeRepresentation query) {
		this.query = query;
	}

	/**
	 * @return
	 */
	public ISearchNodeRepresentation getQuery() {
		return this.query;
	}

}
