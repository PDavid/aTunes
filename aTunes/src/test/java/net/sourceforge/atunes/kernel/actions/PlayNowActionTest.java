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

package net.sourceforge.atunes.kernel.actions;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import junit.framework.Assert;
import net.sourceforge.atunes.model.IAudioObject;

import org.junit.Test;

public class PlayNowActionTest {

	@Test
	public void testEnabled() {
		PlayNowAction sut = new PlayNowAction();
		Assert.assertFalse(sut.isEnabledForNavigationTableSelection(null));
		Assert.assertFalse(sut.isEnabledForNavigationTableSelection(new ArrayList<IAudioObject>()));
		Assert.assertTrue(sut.isEnabledForNavigationTableSelection(Collections.singletonList(mock(IAudioObject.class))));
		Assert.assertFalse(sut.isEnabledForNavigationTableSelection(Arrays.asList(mock(IAudioObject.class), mock(IAudioObject.class))));
	}
}
