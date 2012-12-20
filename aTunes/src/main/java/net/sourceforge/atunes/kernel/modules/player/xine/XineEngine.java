/*
 * aTunes 3.1.0
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

package net.sourceforge.atunes.kernel.modules.player.xine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import net.sourceforge.atunes.kernel.modules.player.AbstractPlayerEngine;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IEqualizer;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IUIHandler;
import net.sourceforge.atunes.model.PlayerEngineCapability;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.libxinejna.Xine;
import org.libxinejna.XineController;
import org.libxinejna.XineEventListener;

/**
 * Engine for Xine player
 * 
 * @author <a href="mailto:Helbrass@gmail.com">Aekold Helbrass</a>
 */
public class XineEngine extends AbstractPlayerEngine {

	private XineController xineController;
	private Timer durationUpdater;
	private final Object xineLock = new Object();

	private IEqualizer equalizer;

	private IUIHandler uiHandler;

	/**
	 * @param uiHandler
	 */
	public void setUiHandler(final IUIHandler uiHandler) {
		this.uiHandler = uiHandler;
	}

	/**
	 * @param equalizer
	 */
	public void setEqualizer(final IEqualizer equalizer) {
		this.equalizer = equalizer;
	}

	@Override
	public boolean isEngineAvailable() {
		try {
			String v = Xine.getLibXineVersion();
			info(StringUtils.getString("Engine version: ", v));
			if (v != null && v.trim().length() > 0) {
				return true;
			}
		} catch (Exception e) {
			Logger.info("Xine not available: ", e.getMessage());
		} catch (UnsatisfiedLinkError e) {
			Logger.info("Xine not available: ", e.getMessage());
		}
		return false;
	}

	@Override
	public void pausePlayback() {
		if (this.xineController != null) {
			this.xineController.pause();
		}
	}

	@Override
	public void resumePlayback() {
		if (this.xineController != null) {
			this.xineController.resume();
		}
	}

	@Override
	public void startPlayback(final IAudioObject audioObjectToPlay,
			final IAudioObject audioObject) {

		if (audioObjectToPlay instanceof IPodcastFeedEntry
				|| audioObjectToPlay instanceof IRadio) {
			showMessage(I18nUtils.getString("XINE_RADIOS_NOT_SUPPORTED"));
			stopCurrentAudioObject(false);
		} else if (audioObjectToPlay.getUrl() == null) {
			handlePlayerEngineError(new FileNotFoundException(
					audioObjectToPlay.getTitleOrFileName()));
		} else {

			boolean valid = true;

			if (this.xineController == null) {
				info("Creating xineController for playback");
				this.xineController = Xine.getXine().createController();
				this.xineController
						.registerXineEventListener(new XineListener());
			} else {
				info("Stopping before new playback");
				internalStop();
			}
			info("Opening stream and setting xine params");

			this.xineController.open(audioObjectToPlay.getUrl());

			this.xineController.setVolume(getStatePlayer().getVolume());

			// Apply equalizer
			applyEqualization(this.equalizer.isEnabled(),
					this.equalizer.getEqualizerValues());

			String errorMessage = null;
			int streamLength = this.xineController.getStreamLength();
			// Check if stream length is 0 which may indicate a wrong audio
			// object, and then skip
			if (streamLength <= 0) {
				valid = false;
				errorMessage = StringUtils.getString(
						I18nUtils.getString("ERROR"), ": ",
						audioObjectToPlay.getUrl());
			}
			setCurrentAudioObjectLength(streamLength);

			if (valid) {
				startPlayback(0);
				info("Starting duration thread");
				this.durationUpdater = new Timer(250,
						new DurationUpdaterActionListener(audioObjectToPlay));
				this.durationUpdater.start();
			} else {
				currentAudioObjectFinishedWithError(new Exception(errorMessage));
			}
		}
	}

	protected void setTime(final int time) {
		super.setCurrentAudioObjectPlayedTime(time);
	}

	@Override
	public void stopPlayback(final boolean userStopped,
			final boolean useFadeAway) {
		if (this.xineController == null) {
			return;
		}
		stopDurationUpdater();
		if (useFadeAway) {
			info("Fading away...");
			new FadeAwayThread("FadeAway").start();
		} else {
			info("Stopping without fade");
			internalStop();
		}
	}

	@Override
	public void seekTo(final long milliseconds, final int perCent) {
		if (this.xineController != null) {
			startPlayback(milliseconds);
		}
	}

	@Override
	public void finishPlayer() {
		info("Finishing");
		try {
			if (this.xineController != null) {
				synchronized (this.xineLock) {
					stopDurationUpdater();
					this.xineController.dispose();
					this.xineController = null;
				}
			}
			Xine.getXine().dispose();
		} catch (UnsatisfiedLinkError le) {
			info("Error catched: " + le);
		} catch (Exception e) {
			info("Error catched: " + e);
		}
	}

	@Override
	public boolean isEnginePlaying() {
		if (this.xineController != null) {
			synchronized (this.xineLock) {
				return this.xineController.isPlaying();
			}
		}
		return false;
	}

	@Override
	public void applyMuteState(final boolean mute) {
		if (mute) {
			setVolume(0);
		} else {
			setVolume(getStatePlayer().getVolume());
		}
	}

	@Override
	public void setVolume(final int volume) {
		if (this.xineController != null) {
			this.xineController.setVolume(volume);
		}
	}

	protected void internalStop() {
		info("Internal stop");
		if (this.xineController != null) {
			stopDurationUpdater();
			if (this.xineController.isPlaying()
					|| this.xineController.isPaused()) {
				synchronized (this.xineLock) {
					this.xineController.stop();
				}
			}
		}
	}

	private void startPlayback(final long milliseconds) {
		info("Starting playback");
		try {
			this.xineController.start(0, (int) milliseconds);
		} catch (Exception e) {
			info("Xine encountered an error: " + e);
			currentAudioObjectFinishedWithError(new Exception(
					StringUtils.getString("Xine encountered an error: ",
							e.getMessage()), e));
		}
	}

	private void stopDurationUpdater() {
		if (this.durationUpdater != null) {
			this.durationUpdater.stop();
			this.durationUpdater = null;
		}
	}

	protected void info(final String info) {
		Logger.info("Xine: ", info);
	}

	protected void error(final Exception o) {
		Logger.error(o);
	}

	private final class FadeAwayThread extends Thread {
		private FadeAwayThread(final String name) {
			super(name);
		}

		@Override
		public void run() {
			// xineController.getVolume() always returns 0 ??
			// Using volume value from app instead of xine
			int vol = XineEngine.this.getStatePlayer().getVolume();
			int i = 0;
			while (i < 50 && vol > 0) {
				vol = vol - 2;
				vol = vol < 0 ? 0 : vol;
				XineEngine.this.xineController.setVolume(vol);
				try {
					Thread.sleep(25);
				} catch (InterruptedException e) {
					Logger.error(e);
				}
				i++;
			}

			internalStop();
			// Volume restored after stop
			// If we don't do this, volume is 0 in next audio object
			XineEngine.this.xineController.setVolume(XineEngine.this
					.getStatePlayer().getVolume());
		}
	}

	private final class DurationUpdaterActionListener implements ActionListener {
		private final IAudioObject audioObjectToPlay;
		private int prevPosition;

		private DurationUpdaterActionListener(
				final IAudioObject audioObjectToPlay) {
			this.audioObjectToPlay = audioObjectToPlay;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (isEnginePlaying()) {
				int s = -1;
				synchronized (XineEngine.this.xineLock) {
					s = XineEngine.this.xineController.getStreamPos();
				}
				// TODO perform better checks. May be >= is better here, but
				// there is some bug with seek
				if (s >= 0) {
					this.prevPosition = s;
					setTime(this.prevPosition);
					// if ((audioObjectToPlay instanceof PodcastFeedEntry ||
					// audioObjectToPlay instanceof Radio) && s < 1000) {
					if (s < 1000) {
						XineEngine.this.uiHandler
								.updateTitleBar(this.audioObjectToPlay);
					}
				}
			}
		}
	}

	protected class XineListener implements XineEventListener {

		@Override
		public void handleXineEvent(final int eventID, final byte[] data) {
			if (eventID == 1) {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							currentAudioObjectFinished();
						}
					});
				} catch (InterruptedException e) {
					error(e);
				} catch (InvocationTargetException e) {
					error(e);
				}
			}
		}
	}

	@Override
	public boolean supportsCapability(final PlayerEngineCapability capability) {
		return EnumSet.of(PlayerEngineCapability.EQUALIZER,
				PlayerEngineCapability.EQUALIZER_CHANGE,
				PlayerEngineCapability.STREAMING).contains(capability);
	}

	@Override
	public void applyEqualization(final boolean enabled, final float[] values) {
		if (this.xineController != null && values != null && enabled) {
			float[] transformedValues = transformEqualizerValues(values);
			for (int i = 0, p = 18; i < transformedValues.length; i++, p++) {
				this.xineController.setParam(p, (int) transformedValues[i]);
			}
		}
	}

	@Override
	public float[] transformEqualizerValues(final float[] eq) {
		if (eq == null) {
			return new float[0];
		}
		float[] result = new float[eq.length];
		float one = 100f / 12f;
		for (int i = 0; i < eq.length; i++) {
			if (eq[i] == 0) {
				result[i] = 0;
			} else {
				result[i] = (int) (one * eq[i]);
			}
		}
		return result;
	}

	@Override
	public String getEngineName() {
		return "Xine";
	}

	@Override
	public void destroyPlayer() {
		if (this.xineController != null) {
			synchronized (this.xineLock) {
				stopDurationUpdater();
				this.xineController.dispose();
				this.xineController = null;
			}
		}
		Xine.getXine().dispose();
	}

	@Override
	public void applyNormalization() {
		// TODO normalization must be applied here
	}

	@Override
	public void initializePlayerEngine() {
	}
}
