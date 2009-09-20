package net.sourceforge.atunes.test.lastfm;

import java.util.List;

import junit.framework.Assert;

import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmLovedTrack;

import org.junit.Test;

public class LastFmTests {

	@Test
	public void testLovedTracks() {
		ApplicationState.getInstance().setLastFmUser("alexaranda");
		List<LastFmLovedTrack> lovedTracks = LastFmService.getInstance().getLovedTracks();
		Assert.assertFalse(lovedTracks.isEmpty());
	}
}
