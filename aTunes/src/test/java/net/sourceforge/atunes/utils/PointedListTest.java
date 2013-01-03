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

package net.sourceforge.atunes.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class PointedListTest {

    private static final String ELEM_0 = "Element 0";
    private static final String ELEM_1 = "Element 1";
    private static final String ELEM_2 = "Element 2";
    private static final String ELEM_3 = "Element 3";
    private static final String ELEM_4 = "Element 4";
    private static final String ELEM_5 = "Element 5";
    private static final String ELEM_6 = "Element 6";

    @Test
    public void test() {

        PointedList<String> list = new PointedList<String>();

        // Tests for empty list
        Assert.assertEquals((Integer) 0, list.getPointer());
        Assert.assertNull(list.getCurrentObject());
        Assert.assertNull(list.getNextObject(1));
        Assert.assertNull(list.getPreviousObject(1));
        Assert.assertTrue(list.isEmpty());
        list.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        list.shuffle();

        list.add(ELEM_1);
        list.add(ELEM_2);
        list.add(ELEM_3);
        list.add(ELEM_4);
        list.add(ELEM_5);
        list.add(ELEM_6);

        // Size is correct
        Assert.assertEquals(6, list.size());

        // Get test
        Assert.assertEquals(ELEM_1, list.get(0));
        Assert.assertNull(list.get(-1));
        Assert.assertNull(list.get(10));

        // Initially current object is first
        Assert.assertEquals(ELEM_1, list.getCurrentObject());
        Assert.assertEquals((Integer) 0, list.getPointer());

        // Test next objects
        Assert.assertEquals(ELEM_2, list.getNextObject(1));
        Assert.assertEquals(ELEM_3, list.getNextObject(2));

        // Test previous objects (cyclic list)
        Assert.assertEquals(ELEM_6, list.getPreviousObject(1));
        Assert.assertEquals(ELEM_5, list.getPreviousObject(2));

        // Move to next object
        Assert.assertEquals(ELEM_2, list.moveToNextObject());
        Assert.assertEquals(ELEM_3, list.moveToNextObject());

        // Move to previous object
        Assert.assertEquals(ELEM_2, list.moveToPreviousObject());
        Assert.assertEquals(ELEM_1, list.moveToPreviousObject());

        // Remove test
        list.remove(-1);
        list.remove(0);
        Assert.assertFalse(list.contains(ELEM_1));
        Assert.assertEquals(5, list.size());
        Assert.assertEquals(ELEM_2, list.getCurrentObject());
        Assert.assertEquals(ELEM_6, list.getPreviousObject(1));
        list.setPointer(1);
        list.remove(0);
        Assert.assertEquals(ELEM_3, list.getCurrentObject());

        // Remove test, set pointer to last element and remove
        list.setPointer(list.size() - 1);
        String previous = list.getPreviousObject(1);
        list.remove(list.size() - 1);
        Assert.assertEquals(previous, list.getCurrentObject());

        // Remove test, set pointer to the only element and remove
        PointedList<String> list4 = new PointedList<String>();
        list4.add(ELEM_0);
        list4.remove(0);
        Assert.assertNull(list4.getPointer());
        Assert.assertNull(list4.getCurrentObject());

        // Shuffle test
        String current = list.getCurrentObject();
        list.shuffle();
        Assert.assertEquals(current, list.getCurrentObject());

        // Sort test
        current = list.getCurrentObject();
        list.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                // sort in reverse order
                return -o1.compareTo(o2);
            }
        });
        Assert.assertEquals(current, list.getCurrentObject());

        // Create new pointed list non cyclic
        PointedList<String> list2 = new PointedList<String>(list) {
            /**
             * 
             */
            private static final long serialVersionUID = -5129167489922007594L;

            @Override
            public boolean isCyclic() {
                return false;
            }
        };
        Assert.assertEquals(list.getPointer(), list2.getPointer());
        Assert.assertEquals(list.getList(), list2.getList());

        // Previous to first is null as is not cyclic
        list2.setPointer(0);
        Assert.assertNull(list2.getPreviousObject(1));

        // Next to last is null as is not cyclic
        list2.setPointer(list2.size() - 1);
        Assert.assertNull(list2.getNextObject(1));

        // Add element at
        list.setPointer(0);
        current = list.getCurrentObject();
        list.add(0, ELEM_0);
        Assert.assertEquals(current, list.getCurrentObject());

        // Add all at
        List<String> l = new ArrayList<String>();
        l.add(ELEM_0);
        l.add(ELEM_1);
        list.addAll(0, l);
        Assert.assertEquals(current, list.getCurrentObject());

        // Replace
        PointedList<String> list3 = new PointedList<String>(list2);
        current = list3.getCurrentObject();
        list3.replace(list3.getPointer(), ELEM_0);
        Assert.assertEquals(ELEM_0, list3.getCurrentObject());

        // Clear
        list3.clear();
        Assert.assertTrue(list3.isEmpty());

        // Get next and previous when pointer is null
        String first = list3.get(0);
        String last = list3.get(list3.size() - 1);
        list3.setPointer(null);
        Assert.assertEquals(first, list3.getNextObject(1));
        list3.setPointer(null);
        Assert.assertEquals(last, list3.getPreviousObject(1));

    }

}
