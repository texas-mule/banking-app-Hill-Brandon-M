package com.revature;

/**
 * An abstract class for data that is kept persistent.
 * 
 *
 * @param <ID> The type of data used by this class as a unique identifier.
 */
public abstract class SynchronizedData<ID> implements Accessible {

	protected static DataStore source = null;

	
	// ID allocation
	protected ID id;
	
	/**
	 * Generates a new  ID.
	 * @return the new  ID.
	 */
	protected abstract ID generateId ();
	public abstract ID getId();
	
	public SynchronizedData () {
		super();
		this.id = generateId();
	}
}
