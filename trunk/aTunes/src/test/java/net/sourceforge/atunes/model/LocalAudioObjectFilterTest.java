package net.sourceforge.atunes.model;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class LocalAudioObjectFilterTest {

	@Test
	public void testEmpty() {
		LocalAudioObjectFilter sut = new LocalAudioObjectFilter();
		Assert.assertTrue(sut.getLocalAudioObjects(null).isEmpty());
		Assert.assertTrue(sut.getLocalAudioObjects(new ArrayList<IAudioObject>()).isEmpty());
	}

	@Test
	public void testNotEmpty() {
		LocalAudioObjectFilter sut = new LocalAudioObjectFilter();
		List<IAudioObject> list = new ArrayList<IAudioObject>();
		ILocalAudioObject lao1 = mock(ILocalAudioObject.class);
		ILocalAudioObject lao2 = mock(ILocalAudioObject.class);
		list.add(lao1);
		list.add(mock(IAudioObject.class));
		list.add(mock(IRadio.class));
		list.add(mock(IPodcastFeedEntry.class));
		list.add(lao2);
		Assert.assertEquals(2, sut.getLocalAudioObjects(list).size());
		Assert.assertEquals(lao1, sut.getLocalAudioObjects(list).get(0));
		Assert.assertEquals(lao2, sut.getLocalAudioObjects(list).get(1));
	}
}
