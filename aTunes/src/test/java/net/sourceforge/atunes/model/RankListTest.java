/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.model;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class RankListTest {

	private class DummyObject {
		private final String s;

		public DummyObject(final String s) {
			this.s = s;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((s == null) ? 0 : s.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			DummyObject other = (DummyObject) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (s == null) {
				if (other.s != null) {
					return false;
				}
			} else if (!s.equals(other.s)) {
				return false;
			}
			return true;
		}

		private RankListTest getOuterType() {
			return RankListTest.this;
		}

	}

	private RankList<DummyObject> testedObject;
	private DummyObject o1;
	private DummyObject o2;
	private DummyObject o3;

	@Before
	public void init() {
		testedObject = new RankList<DummyObject>();
		testedObject.addItem(null);
		o1 = new DummyObject("a");
		testedObject.addItem(o1);
		testedObject.addItem(o1);
		testedObject.addItem(o1);
		o2 = new DummyObject("b");
		testedObject.addItem(o2);
		testedObject.addItem(o2);
		o3 = new DummyObject("c");
		testedObject.addItem(o3);
	}

	@Test
	public void testAddItem() {
		Assert.assertTrue(testedObject.getCount(o1) == 3);
	}

	@Test
	public void testGetNFirstElements() {
		List<DummyObject> nFirstElements = testedObject.getNFirstElements(2);
		Assert.assertEquals(Arrays.asList(o1, o2), nFirstElements);
	}

	@Test
	public void testGetNFirstElementCounts() {
		List<Integer> nFirstElementCounts = testedObject.getNFirstElementCounts(2);
		List<Integer> nFirstElementCounts2 = testedObject.getNFirstElementCounts(Integer.MAX_VALUE);
		Assert.assertEquals(Arrays.asList(3, 2), nFirstElementCounts);
		Assert.assertEquals(Arrays.asList(3, 2, 1), nFirstElementCounts2);
	}

	@Test
	public void testGetOrder() {
		List<DummyObject> order1 = testedObject.getOrder();
		Assert.assertEquals(Arrays.asList(o1, o2, o3), order1);
		List<DummyObject> order2 = testedObject.getOrder();
		Assert.assertNotSame(order1, order2);
	}

	@Test
	public void testReplaceItem() {
		DummyObject o4 = new DummyObject("d");
		testedObject.replaceItem(o2, o4);
		List<DummyObject> order = testedObject.getOrder();
		Assert.assertEquals(Arrays.asList(o1, o4, o3), order);
		Assert.assertTrue(testedObject.getCount(o4) == 2);
		testedObject.replaceItem(o4, o2);
	}

	@Test
	public void testSize() {
		Assert.assertTrue(testedObject.size() == 3);
	}

	@Test
	public void testClear() {
		testedObject.clear();
		Assert.assertTrue(testedObject.size() == 0);
		Assert.assertTrue(testedObject.getOrder().isEmpty());
	}
}
