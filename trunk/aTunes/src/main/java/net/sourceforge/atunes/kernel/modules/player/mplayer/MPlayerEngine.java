/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.player.mplayer;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.player.AbstractPlayerEngine;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IStateRadio;
import net.sourceforge.atunes.model.PlayerEngineCapability;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.Logger;

import org.apache.commons.io.IOUtils;

/**
 * Engine for MPlayer
 */
public class MPlayerEngine extends AbstractPlayerEngine {

    private MPlayerProcessBuilder processBuilder;

    private Process process;
    private MPlayerCommandWriter commandWriter;
    private AbstractMPlayerOutputReader mPlayerOutputReader;
    private MPlayerErrorReader mPlayerErrorReader;
    private MPlayerPositionThread mPlayerPositionThread;
    /** The current fade away process running */
    private FadeAwayRunnable currentFadeAwayRunnable = null;

    private ILocalAudioObjectValidator localAudioObjectValidator;

    private IStateRadio stateRadio;

    /**
     * @param stateRadio
     */
    public void setStateRadio(final IStateRadio stateRadio) {
	this.stateRadio = stateRadio;
    }

    /**
     * @param localAudioObjectValidator
     */
    public void setLocalAudioObjectValidator(
	    final ILocalAudioObjectValidator localAudioObjectValidator) {
	this.localAudioObjectValidator = localAudioObjectValidator;
    }

    @Override
    public void setOsManager(final IOSManager osManager) {
	super.setOsManager(osManager);
	commandWriter = MPlayerCommandWriter.newCommandWriter(null, osManager);
    }

    /**
     * @param processBuilder
     */
    public void setProcessBuilder(final MPlayerProcessBuilder processBuilder) {
	this.processBuilder = processBuilder;
    }

    @Override
    public boolean isEngineAvailable() {
	InputStream in = null;
	try {
	    // Processes in Windows need to read input stream, if not process is
	    // blocked
	    // so read input stream
	    String command = getOsManager().getPlayerEngineCommand(this);
	    if (command != null) {
		Process testEngineProcess = new ProcessBuilder(command).start();
		in = testEngineProcess.getInputStream();
		IOUtils.readLines(in);
		int rc = testEngineProcess.waitFor();
		return rc == 0;
	    }
	} catch (IOException e) {
	    Logger.error(e);
	} catch (InterruptedException e) {
	    Logger.error(e);
	} finally {
	    ClosingUtils.close(in);
	}
	return false;
    }

    @Override
    public void pausePlayback() {
	commandWriter.sendPauseCommand();
    }

    @Override
    public void resumePlayback() {
	commandWriter.sendResumeCommand();
	/*
	 * Mplayer volume problem workaround If player was paused, set volume
	 * again as it could be changed when paused
	 */
	if (!isMuteEnabled()) {
	    commandWriter.sendVolumeCommand(getStatePlayer().getVolume());
	}
	/*
	 * End Mplayer volume problem workaround
	 */
    }

    @Override
    public void startPlayback(final IAudioObject audioObjectToPlay,
	    final IAudioObject audioObject) {
	try {
	    // If there is a fade away working, stop it inmediately
	    if (currentFadeAwayRunnable != null) {
		currentFadeAwayRunnable.finish();
	    }

	    // Send stop command in order to try to avoid two mplayer
	    // instaces are running at the same time
	    commandWriter.sendStopCommand();

	    // Start the play process
	    process = processBuilder.getProcess(audioObjectToPlay);

	    if (process != null) {
		commandWriter = MPlayerCommandWriter.newCommandWriter(process,
			getOsManager());
		// Output reader needs original audio object, specially when
		// cacheFilesBeforePlaying is true, as
		// statistics must be applied over original audio object, not
		// the cached one
		mPlayerOutputReader = AbstractMPlayerOutputReader.newInstance(
			this, process, audioObject, stateRadio, getFrame(),
			getPlayListHandler(), localAudioObjectValidator);
		mPlayerErrorReader = new MPlayerErrorReader(this, process,
			mPlayerOutputReader, audioObjectToPlay);
		mPlayerOutputReader.start();
		mPlayerErrorReader.start();
		mPlayerPositionThread = new MPlayerPositionThread(this);
		mPlayerPositionThread.start();
		commandWriter.sendGetDurationCommand();
	    }

	} catch (Exception e) {
	    stopCurrentAudioObject(false);
	    handlePlayerEngineError(e);
	}
    }

    @Override
    public void stopPlayback(final boolean userStopped,
	    final boolean useFadeAway) {
	if (!isEnginePlaying()) {
	    return;
	}

	if (useFadeAway && !isEnginePaused()) {
	    // If there is a fade away process working don't create
	    // a new process
	    if (currentFadeAwayRunnable != null) {
		return;
	    }
	    mPlayerErrorReader.interrupt();
	    currentFadeAwayRunnable = new FadeAwayRunnable(process,
		    getStatePlayer().getVolume(), this, getOsManager());
	    Thread t = new Thread(currentFadeAwayRunnable);
	    // Start fade away process
	    t.start();
	} else {
	    commandWriter.sendStopCommand();
	    // If there is a fade away process stop inmediately
	    if (currentFadeAwayRunnable != null) {
		currentFadeAwayRunnable.finish();
	    } else {
		// This is already called from fade away runnable when finishing
		process = null;
		mPlayerErrorReader = null;
		mPlayerOutputReader = null;
		commandWriter.finishProcess();
		mPlayerPositionThread.interrupt();
		mPlayerPositionThread = null;
	    }
	    setCurrentAudioObjectPlayedTime(0);
	}
    }

    /**
     * Called when finished fade away
     */
    protected void finishedFadeAway() {
	// NOTE: interrupting output reader means closing standard input
	// of mplayer process, so process is finished
	mPlayerOutputReader.interrupt();
	process = null;
	mPlayerErrorReader = null;
	mPlayerOutputReader = null;
	mPlayerPositionThread.interrupt();
	mPlayerPositionThread = null;
	commandWriter.finishProcess();
	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		setTime(0);
	    }
	});
	// No fade away process working
	currentFadeAwayRunnable = null;
    }

    protected void setTime(final int time) {
	super.setCurrentAudioObjectPlayedTime(time);
    }

    @Override
    public void seekTo(final long position, final int perCent) {
	commandWriter.sendSeekCommandPerCent(perCent);
    }

    @Override
    public void finishPlayer() {
	stopCurrentAudioObject(false);
	Logger.info("Stopping player");
    }

    @Override
    public boolean isEnginePlaying() {
	return process != null && !isEnginePaused();
    }

    @Override
    public void applyMuteState(final boolean mute) {
	commandWriter.sendMuteCommand();

	// volume must be applied again because of the volume bug
	setVolume(getStatePlayer().getVolume());

	// MPlayer bug: paused, demute, muted -> starts playing
	if (isEnginePaused() && !mute) {
	    commandWriter.sendPauseCommand();
	    Logger.debug("MPlayer bug (paused, demute, muted -> starts playing) workaround applied");
	}
    }

    @Override
    public void setVolume(final int volume) {
	// MPlayer bug: paused, volume change -> starts playing
	// If is paused, volume will be sent to mplayer when user resumes
	// playback
	if (!isEnginePaused() && !isMuteEnabled()) {
	    commandWriter.sendVolumeCommand(volume);
	}
    }

    /**
     * Gets the command writer.
     * 
     * @return the command writer
     */
    MPlayerCommandWriter getCommandWriter() {
	return commandWriter;
    }

    @Override
    public boolean supportsCapability(final PlayerEngineCapability capability) {
	return EnumSet.of(PlayerEngineCapability.EQUALIZER,
		PlayerEngineCapability.EQUALIZER_CHANGE,
		PlayerEngineCapability.STREAMING, PlayerEngineCapability.PROXY,
		PlayerEngineCapability.NORMALIZATION).contains(capability);
    }

    @Override
    public void applyEqualization(final boolean enabled, final float[] values) {
	// Mplayer does not support equalizer change
	// workaround:
	// we can stop/restart the current playing song to
	// its last position when users applied the EQ
	// test to avoid non desired startup of player
	if (isEnginePlaying()) {
	    restartPlayback();
	}
    }

    @Override
    public void applyNormalization() {
	// same comment as above, but for normalization mode
	if (isEnginePlaying()) {
	    restartPlayback();
	}
    }

    @Override
    public float[] transformEqualizerValues(final float[] values) {
	return values;
    }

    protected void setCurrentLength(final long currentDuration) {
	super.setCurrentAudioObjectLength(currentDuration);
    }

    /**
     * Checks if playback is paused.
     * 
     * @return true, if is paused
     */
    protected boolean isPlaybackPaused() {
	return isEnginePaused();
    }

    protected void notifyRadioOrPodcastFeedEntry() {
	super.notifyRadioOrPodcastFeedEntryStarted();
    }

    protected boolean isMute() {
	return super.isMuteEnabled();
    }

    @Override
    public String getEngineName() {
	return "MPlayer";
    }

    @Override
    public void destroyPlayer() {
	commandWriter.sendStopCommand();
    }

    @Override
    public void initializePlayerEngine() {
    }
}
