package net.sourceforge.atunes.model;

import java.io.Serializable;

/**
 * A representation of a search node to be serialized
 * 
 * @author alex
 * 
 */
public interface ISearchNodeRepresentation extends Serializable {

	/**
	 * @param beanFactory
	 * @return search node created from representation
	 */
	ISearchNode createSearchQuery(IBeanFactory beanFactory);

}
