package net.sourceforge.atunes.test.playlist;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.AudioObject;

import org.junit.Before;
import org.junit.Test;

public class PlayListHandlerTest {

    private PlayListHandler playlistHandler;
    private AudioFile audioFile01 = new AudioFile("audioFile01");
    private AudioFile audioFile02 = new AudioFile("audioFile02");
    private AudioFile audioFile03 = new AudioFile("audioFile03");

    @Before
    public void init() {
        GuiHandler.getInstance().getFrame();
        
        playlistHandler = PlayListHandler.getInstance();

        List<AudioObject> audioFiles = new ArrayList<AudioObject>();
        audioFiles.add(audioFile01);
        audioFiles.add(audioFile02);
        audioFiles.add(audioFile03);
        
        playlistHandler.newPlayList(audioFiles);
        

    }

    @Test
    public void testGetAudioObjectAtIndex() {
        Assert.assertEquals(audioFile01, playlistHandler.getAudioObjectAtIndex(0));
        Assert.assertEquals(audioFile02, playlistHandler.getAudioObjectAtIndex(1));
        Assert.assertEquals(audioFile03, playlistHandler.getAudioObjectAtIndex(2));
        Assert.assertNotNull(playlistHandler.getAudioObjectAtIndex(3));
        Assert.assertNotNull(playlistHandler.getAudioObjectAtIndex(-1));
    }

}