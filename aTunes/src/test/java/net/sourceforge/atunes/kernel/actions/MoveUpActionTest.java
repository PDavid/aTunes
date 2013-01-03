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
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collections;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;

import org.junit.Assert;
import org.junit.Test;

public class MoveUpActionTest {

	@Test
	public void test() {
		MoveUpAction sut = new MoveUpAction();
		IPlayListHandler playListHandler = mock(IPlayListHandler.class);
		sut.setPlayListHandler(playListHandler);
		
		sut.executeAction();
		
		verify(playListHandler).moveUp();
	}
	
	@Test
	public void testEnabled() {
		MoveUpAction sut = new MoveUpAction();
		Assert.assertTrue(sut.isEnabledForPlayListSelection(Collections.singletonList(mock(IAudioObject.class))));
	}

	@Test
	public void testDisabled() {
		MoveUpAction sut = new MoveUpAction();
		Assert.assertFalse(sut.isEnabledForPlayListSelection(new ArrayList<IAudioObject>()));
		Assert.assertFalse(sut.isEnabledForPlayListSelection(null));
	}

}
