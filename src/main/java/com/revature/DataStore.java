package com.revature;

/**
 * An interface for using objects to store persistent data.
 * @author Brandon Hill
 *
 * @param <T> 	-	The type of object retrieved from this DataStore
 * @param <ID> 	-	The type used to make identifiers for this 
 * 					object in the DataStore
 */
public interface DataStore <T extends SynchronizedData<ID>, ID> {
	
	public abstract void open (String fileName);
	public abstract void close ();
	
	public abstract boolean store (T obj);	
	public abstract T retrieve (ID id);
}
