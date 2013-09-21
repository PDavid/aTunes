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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IEqualizer;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IPodcastFeedHandler;
import net.sourceforge.atunes.model.IProxyBean;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.model.IStatePodcast;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Builds mplayer process
 * 
 * @author alex
 * 
 */
public class MPlayerProcessBuilder {

	private static final String[] PLAYLISTS = { "m3u", "pls", "asx", "wax",
			"b4s", "kpl", "wvx", "ram", "rm", "smil" };

	private IStatePlayer statePlayer;

	private IOSManager osManager;

	private IPodcastFeedHandler podcastFeedHandler;

	private MPlayerEngine engine;

	private INetworkHandler networkHandler;

	private IEqualizer equalizer;

	private IStateCore stateCore;

	private IStatePodcast statePodcast;

	/**
	 * @param statePodcast
	 */
	public void setStatePodcast(final IStatePodcast statePodcast) {
		this.statePodcast = statePodcast;
	}

	/**
	 * @param stateCore
	 */
	public void setStateCore(final IStateCore stateCore) {
		this.stateCore = stateCore;
	}

	/**
	 * @param statePlayer
	 */
	public void setStatePlayer(final IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}

	/**
	 * @param equalizer
	 */
	public void setEqualizer(final IEqualizer equalizer) {
		this.equalizer = equalizer;
	}

	/**
	 * @param networkHandler
	 */
	public void setNetworkHandler(final INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}

	/**
	 * @param podcastFeedHandler
	 */
	public void setPodcastFeedHandler(
			final IPodcastFeedHandler podcastFeedHandler) {
		this.podcastFeedHandler = podcastFeedHandler;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param engine
	 */
	public void setEngine(final MPlayerEngine engine) {
		this.engine = engine;
	}

	/**
	 * Returns if audio object is remote or not
	 * 
	 * @param audioObject
	 * @return
	 */
	private boolean isRemoteAudio(final IAudioObject audioObject) {
		return !(audioObject instanceof ILocalAudioObject || (audioObject instanceof IPodcastFeedEntry
				&& this.statePodcast.isUseDownloadedPodcastFeedEntries() && ((IPodcastFeedEntry) audioObject)
					.isDownloaded()));
	}

	/**
	 * Returns a mplayer process to play an audiofile.
	 * 
	 * @param audioObject
	 *            audio object which should be played
	 * 
	 * @return mplayer process
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public MPlayerProcess getProcess(final IAudioObject audioObject)
			throws IOException {
		boolean isRemoteAudio = isRemoteAudio(audioObject);

		String url = getUrlToPlay(audioObject, isRemoteAudio);

		if (url == null) {
			this.engine.handlePlayerEngineError(new FileNotFoundException(
					audioObject.getTitleOrFileName()));
			return null;
		} else {
			List<String> command = prepareCommand(audioObject, isRemoteAudio,
					url);
			Logger.debug((Object[]) command.toArray(new String[command.size()]));
			return new MPlayerProcess(command);
		}
	}

	/**
	 * @param audioObject
	 * @param isRemoteAudio
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private List<String> prepareCommand(final IAudioObject audioObject,
			final boolean isRemoteAudio, final String url) throws IOException {
		List<String> command = new ArrayList<String>();
		prepareBasicCommand(audioObject, command, isRemoteAudio);

		if (needsShortPathName(audioObject, isRemoteAudio)) {
			String shortPath = this.osManager.getFileNormalized(url);
			command.add(shortPath != null && !shortPath.isEmpty() ? shortPath
					: url);
		} else {
			if (url.startsWith("http")) {
				prepareProxy(command, url);
			} else {
				command.add(url);
			}
		}

		prepareCache(command, isRemoteAudio);
		prepareNormalization(command);
		prepareEqualizer(audioObject, command);
		prepareVolume(command);
		return command;
	}

	/**
	 * Returns URL to play audio object
	 * 
	 * @param audioObject
	 * @param isRemoteAudio
	 * @return
	 */
	private String getUrlToPlay(final IAudioObject audioObject,
			final boolean isRemoteAudio) {
		String url = null;
		if (audioObject != null) {
			if (audioObject instanceof IPodcastFeedEntry && !isRemoteAudio) {
				url = this.podcastFeedHandler
						.getDownloadPath((IPodcastFeedEntry) audioObject);
			} else {
				url = audioObject.getUrl();
				if (isRemoteAudio) {
					// Normalize URLs adding http if necessary
					if (url != null && !url.toLowerCase().startsWith("http")) {
						url = StringUtils.getString("http://", url);
					}
				}
			}
		}
		return url;
	}

	/**
	 * Returns true if audio object needs short path names
	 * 
	 * @param audioObject
	 * @param isRemoteAudio
	 * @return
	 */
	private boolean needsShortPathName(final IAudioObject audioObject,
			final boolean isRemoteAudio) {
		// First check state and OS
		if (!this.statePlayer.isUseShortPathNames()
				|| !this.osManager.usesShortPathNames()) {
			return false;
		}

		// local audio objects and downloaded podcasts
		return audioObject instanceof ILocalAudioObject
				|| (audioObject instanceof IPodcastFeedEntry && !isRemoteAudio);
	}

	/**
	 * @param audioObject
	 * @param command
	 * @param isRemoteAudio
	 * @throws IOException
	 */
	private void prepareBasicCommand(final IAudioObject audioObject,
			final List<String> command, final boolean isRemoteAudio)
			throws IOException {
		command.add(this.osManager.getPlayerEngineCommand(this.engine));
		command.addAll(this.osManager.getPlayerEngineParameters(this.engine));
		command.add(MPlayerConstants.QUIET);
		command.add(MPlayerConstants.SLAVE);

		// Disable video output
		command.add(MPlayerConstants.NOVIDEO1);
		command.add(MPlayerConstants.NOVIDEO2);

		// PREFER_IPV4 for radios and podcast entries
		if (isRemoteAudio) {
			command.add(MPlayerConstants.PREFER_IPV4);
		}

		// If a radio has a playlist url add playlist command
		if (audioObject instanceof IRadio
				&& hasPlaylistUrl((IRadio) audioObject, this.networkHandler)) {
			command.add(MPlayerConstants.PLAYLIST);
		}
	}

	private boolean hasPlaylistUrl(final IRadio radio,
			final INetworkHandler networkHandler) {
		// First check based on URL end (extension)
		for (String pl : PLAYLISTS) {
			if (radio.getUrl().trim().toLowerCase().endsWith(pl)) {
				return true;
			}
		}

		// WORKAROUND: If URL has no extension, then try to get from content,
		// read a number of bytes, as URL can be audio stream
		try {
			String radioContent = networkHandler.readURL(
					networkHandler.getConnection(radio.getUrl()), 1000);
			if (!StringUtils.isEmpty(radioContent)) {
				for (String pl : PLAYLISTS) {
					if (radioContent.trim().toLowerCase().contains(pl)) {
						return true;
					}
				}
			}

		} catch (UnknownHostException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

		return false;
	}

	/**
	 * @param command
	 * @param url
	 */
	private void prepareProxy(final List<String> command, final String url) {
		// proxy
		StringBuilder proxy = new StringBuilder();
		IProxyBean proxyBean = this.stateCore.getProxy();
		if (proxyBean != null
				&& proxyBean.getType().equals(IProxyBean.HTTP_PROXY)) {
			// String user = proxyBean.getUser();
			// String password = proxyBean.getPassword();
			String proxyUrl = proxyBean.getUrl();
			int port = proxyBean.getPort();

			proxy.append("http_proxy://");
			// proxy.append(!user.isEmpty() ? user : "");
			// proxy.append(!user.isEmpty() && !password.isEmpty() ? ":" : "");
			// proxy.append(!user.isEmpty() && !password.isEmpty() ? password :
			// "");
			// proxy.append(!user.isEmpty() ? "@" : "");
			proxy.append(proxyUrl);
			proxy.append(port != 0 ? ":" : "");
			proxy.append(port != 0 ? port : "");
			proxy.append("/");
		}
		proxy.append(url);

		command.add(proxy.toString());
	}

	/**
	 * Cache for radios and podcast entries
	 * 
	 * @param command
	 * @param isRemoteAudio
	 */
	private void prepareCache(final List<String> command,
			final boolean isRemoteAudio) {
		if (isRemoteAudio) {
			command.add(MPlayerConstants.CACHE);
			command.add(MPlayerConstants.CACHE_SIZE);
			command.add(MPlayerConstants.CACHE_MIN);
			command.add(MPlayerConstants.CACHE_FILL_SIZE_IN_PERCENT);
		}
	}

	/**
	 * @param command
	 */
	private void prepareNormalization(final List<String> command) {
		if (this.statePlayer.isUseNormalisation()) {
			command.add(MPlayerConstants.AUDIO_FILTER);
			command.add(MPlayerConstants.VOLUME_NORM);
		}
	}

	/**
	 * @param audioObject
	 * @param command
	 */
	private void prepareEqualizer(final IAudioObject audioObject,
			final List<String> command) {
		// Build equalizer command. Mplayer uses 10 bands
		boolean enabled = this.equalizer.isEnabled();
		if (enabled) {
			Logger.debug("Equalizer enabled");
			float[] equalizerValues = this.equalizer.getEqualizerValues();
			if (audioObject instanceof ILocalAudioObject
					&& equalizerValues != null && equalizerValues.length != 0) {
				command.add(MPlayerConstants.AUDIO_FILTER);
				command.add(prepareEqualizerString(equalizerValues));
			}
		}
	}

	/**
	 * @param equalizer
	 * @return
	 */
	private String prepareEqualizerString(final float[] equalizer) {
		StringBuilder eqString = new StringBuilder(MPlayerConstants.EQUALIZER);
		for (int i = 0; i <= 9; i++) {
			eqString.append(equalizer[i]);
			if (i < 9) {
				eqString.append(":");
			}
		}
		return eqString.toString();
	}

	/**
	 * @param command
	 */
	private void prepareVolume(final List<String> command) {
		command.add(MPlayerConstants.VOLUME);
		command.add(Integer.toString(this.statePlayer.isMuteEnabled() ? 0
				: this.statePlayer.getVolume()));
	}
}
