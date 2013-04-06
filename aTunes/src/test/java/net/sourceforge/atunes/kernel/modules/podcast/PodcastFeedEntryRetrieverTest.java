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

package net.sourceforge.atunes.kernel.modules.podcast;

import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;
import net.sourceforge.atunes.kernel.modules.network.NetworkHandler;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IStateCore;

import org.junit.Before;
import org.junit.Test;

public class PodcastFeedEntryRetrieverTest {

	private IPodcastFeed testedObject;

	@Before
	public void init() {
		this.testedObject = new PodcastFeed("",
				PodcastFeedEntryRetrieverTest.class.getResource(
						"/podcast/feed.rss").toString());
		this.testedObject.setRetrieveNameFromFeed(true);
	}

	@Test
	public void testRetrievePodcastFeedEntries() {
		NetworkHandler networkHandler = new NetworkHandler();
		networkHandler.setStateCore(mock(IStateCore.class));

		PodcastFeedEntryRetriever podcastFeedEntryRetriever = new PodcastFeedEntryRetriever();
		podcastFeedEntryRetriever.setNetworkHandler(networkHandler);
		podcastFeedEntryRetriever.setPodcastFeeds(Arrays
				.asList(this.testedObject));
		List<IPodcastFeed> podcastFeedsWithNewEntries = podcastFeedEntryRetriever
				.retrievePodcastFeedEntries(true);

		Assert.assertEquals("RadioTux GNU/Linux", this.testedObject.getName());
		Assert.assertEquals(1, podcastFeedsWithNewEntries.size());
		// TODO more checks
	}

}
