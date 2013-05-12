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

package net.sourceforge.atunes.kernel.modules.player.mplayer;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;

import net.sourceforge.atunes.kernel.modules.player.AbstractPlayerEngine;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.PlayerEngineCapability;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.Logger;

import org.apache.commons.io.IOUtils;

/**
 * Engine for MPlayer
 */
public class MPlayerEngine extends AbstractPlayerEngine {

	private MPlayerProcessBuilder processBuilder;

	private MPlayerProcess process;
	private MPlayerCommandWriter commandWriter = new MPlayerCommandWriter(null);
	private AbstractMPlayerOutputReader mPlayerOutputReader;
	/** The current fade away process running */
	private FadeAwayRunnable currentFadeAwayRunnable = null;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
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
		this.commandWriter.sendPauseCommand();
	}

	@Override
	public void resumePlayback() {
		this.commandWriter.sendResumeCommand();
		/*
		 * Mplayer volume problem workaround If player was paused, set volume
		 * again as it could be changed when paused
		 */
		if (!isMuteEnabled()) {
			this.commandWriter.sendVolumeCommand(getStatePlayer().getVolume());
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
			if (isFadeAwayInProgress()) {
				this.currentFadeAwayRunnable.finish();
			}

			// Send stop command in order to try to avoid two mplayer
			// instaces are running at the same time
			this.commandWriter.sendStopCommand();

			// Start the play process
			this.process = this.processBuilder.getProcess(audioObjectToPlay);

			if (this.process != null) {
				this.commandWriter = this.process
						.newCommandWriter(getOsManager());
				// Output reader needs original audio object, specially when
				// cacheFilesBeforePlaying is true, as
				// statistics must be applied over original audio object, not
				// the cached one
				this.mPlayerOutputReader = this.beanFactory.getBean(
						MPlayerOutputReaderFactory.class).newInstance(
						this.process, audioObject);
				this.mPlayerOutputReader.setAudioObject(audioObjectToPlay);
				this.mPlayerOutputReader.setWorkaroundApplied(getStatePlayer()
						.isCacheFilesBeforePlaying());
				this.mPlayerOutputReader.start();
				this.commandWriter.sendGetDurationCommand();
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
			if (isFadeAwayInProgress()) {
				return;
			}
			this.currentFadeAwayRunnable = new FadeAwayRunnable(this.process,
					getStatePlayer().getVolume(), this, getOsManager());
			Thread t = new Thread(this.currentFadeAwayRunnable);
			// Start fade away process
			t.start();
		} else {
			this.commandWriter.sendStopCommand();
			// If there is a fade away process stop immediately
			if (isFadeAwayInProgress()) {
				this.currentFadeAwayRunnable.finish();
			} else {
				// This is already called from fade away runnable when finishing
				this.process = null;
				this.mPlayerOutputReader = null;
				this.commandWriter.finishProcess();
			}
		}
	}

	private boolean isFadeAwayInProgress() {
		return this.currentFadeAwayRunnable != null;
	}

	/**
	 * Called when finished fade away
	 */
	protected void finishedFadeAway() {
		this.commandWriter.sendStopCommand();
		this.process = null;
		this.mPlayerOutputReader = null;
		this.commandWriter.finishProcess();
		// No fade away process working
		this.currentFadeAwayRunnable = null;
	}

	protected void setTime(final int time) {
		super.setCurrentAudioObjectPlayedTime(time, isFadeAwayInProgress());
	}

	@Override
	public void seekTo(final int percentage) {
		if (this.mPlayerOutputReader != null) {
			this.mPlayerOutputReader.seekStarted();
			this.commandWriter.sendSeekCommandPerCent(percentage);
		}
	}

	@Override
	public void finishPlayer() {
		stopCurrentAudioObject(false);
		Logger.info("Stopping player");
	}

	@Override
	public boolean isEnginePlaying() {
		return this.process != null && !isEnginePaused();
	}

	@Override
	public void applyMuteState(final boolean mute) {
		this.commandWriter.sendMuteCommand();

		// volume must be applied again because of the volume bug
		setVolume(getStatePlayer().getVolume());

		// MPlayer bug: paused, demute, muted -> starts playing
		if (isEnginePaused() && !mute) {
			this.commandWriter.sendPauseCommand();
			Logger.debug("MPlayer bug (paused, demute, muted -> starts playing) workaround applied");
		}
	}

	@Override
	public void setVolume(final int volume) {
		// MPlayer bug: paused, volume change -> starts playing
		// If is paused, volume will be sent to mplayer when user resumes
		// playback
		if (!isEnginePaused() && !isMuteEnabled()) {
			this.commandWriter.sendVolumeCommand(volume);
		}
	}

	/**
	 * Gets the command writer.
	 * 
	 * @return the command writer
	 */
	MPlayerCommandWriter getCommandWriter() {
		return this.commandWriter;
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
		this.commandWriter.sendStopCommand();
	}

	@Override
	public void initializePlayerEngine() {
	}

	/**
	 * MPlayer has problems with filenames too long and filenames with
	 * non-english chars. To avoid both problems when this problem is detected a
	 * workaround is applied to activate cache of files and restart playback
	 */
	void applyMPlayerFilenamesWorkaround(final IAudioObject audioObject) {
		Logger.info("Applying mplayer workaround for filenames");
		// Force a stop to finish all player engine processes
		stopCurrentAudioObject(false);
		getStatePlayer().setCacheFilesBeforePlaying(true);
		// Play again
		playAudioObject(audioObject);
	}
}
