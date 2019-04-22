package com.revature;

import java.util.HashMap;

public class MemStore<T extends SynchronizedData<ID>, ID> implements DataStore <T, ID> {
	
	HashMap<ID, T> data;
	
	@Override
	public void open (String fileName) {		
		data = new HashMap<ID, T>();
	}

	@Override
	public void close () {
		data.clear();		
	}

	@Override
	public boolean store (T obj) {
		data.put(obj.getId(), obj);
		return true;
	}

	@Override
	public T retrieve (ID id) {	return data.get(id); }

}
