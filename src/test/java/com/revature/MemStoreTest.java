package com.revature;

import static org.junit.Assert.*;

import org.junit.*;


public class MemStoreTest {
	
	MemStore<MemStoreTestObject, Integer> m;
	
	@Before
	public void setup () {
		m = new MemStore<MemStoreTestObject, Integer>();
		m.open(null);
		m.data.put(1, new MemStoreTestObject(1, 1));
		m.data.put(2, new MemStoreTestObject(2, 2));
		m.data.put(3, new MemStoreTestObject(3, 3));
	}

	@Test
	public void testStore () {

		m.store(new MemStoreTestObject(4, 4));
		
		assertTrue(m.data.get(4).id == 4);
	}

	@Test
	public void testRetrieve () { assertTrue(m.retrieve(3).value == 3); }
	
	@Test
	public void testClose () {
		m.close();
		assertTrue(m.data.size() == 0);
	}

	private static class MemStoreTestObject extends SynchronizedData<Integer> {
		Integer value;
		
		public MemStoreTestObject (Integer id, Integer value) {
			this.id = id;
			this.value = value;
		}

		@Override
		public boolean readableBy (User user) {	return false; }

		@Override
		public boolean writableBy (User user) {	return false; }

		@Override
		protected Integer generateId () { return null; }

		@Override
		public Integer getId () { return this.id; }
		
	}
}