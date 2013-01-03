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

import javax.swing.AbstractAction;

import net.sourceforge.atunes.model.AbstractStatePlayerMock;
import net.sourceforge.atunes.model.IStatePlayer;

import org.junit.Assert;
import org.junit.Test;

public class RepeatModeActionTest {

	@Test
	public void test() {
		RepeatModeAction sut = new RepeatModeAction();
		IStatePlayer state = new AbstractStatePlayerMock() {
			private boolean repeat;

			public void setRepeat(boolean repeat) { 
				this.repeat = repeat;
			};
			
			@Override
			public boolean isRepeat() {
				return this.repeat;
			}
		};
		sut.setStatePlayer(state);

		state.setRepeat(false);
		sut.putValue(AbstractAction.SELECTED_KEY, true);
		sut.executeAction();
		Assert.assertTrue(state.isRepeat());
		sut.putValue(AbstractAction.SELECTED_KEY, false);
		sut.executeAction();
		Assert.assertFalse(state.isRepeat());
	}

}
