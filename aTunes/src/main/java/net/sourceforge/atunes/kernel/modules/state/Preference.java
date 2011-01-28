package net.sourceforge.atunes.kernel.modules.state;

import java.io.Serializable;

public class Preference implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6098500054034715366L;

	private Object value;

	public Preference() {
		
	}
	
	public Preference(Object value) {
		setValue(value);
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}	
}
