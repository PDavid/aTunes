package net.sourceforge.atunes.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RepositoryStructure<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3802494024307120854L;

	private Map<String, T> structure;
	
	RepositoryStructure() {
		this.structure = new HashMap<String, T>();
	}
	
	int count() {
		return this.structure.size();
	}
	
	Collection<T> getAll() {
		return this.structure.values();
	}
	
	T get(String key) {
		return this.structure.get(key);
	}
	
	void put(String key, T value) {
		this.structure.put(key, value);
	}
	
	void remove(String key) {
		this.structure.remove(key);
	}
	
	Map<String, T> getStructure() {
		return this.structure;
	}
}
