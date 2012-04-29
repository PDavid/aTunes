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

import java.io.FileNotFoundException;
import java.io.IOException;
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
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.Logger;

public class MPlayerProcessBuilder {

	private IState state;
	
	private IStatePlayer statePlayer;
	
	private IOSManager osManager;
	
	private IPodcastFeedHandler podcastFeedHandler;
	
	private MPlayerEngine engine;
	
	private INetworkHandler networkHandler;
	
	private IEqualizer equalizer;
	
	/**
	 * @param statePlayer
	 */
	public void setStatePlayer(IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}
	
	/**
	 * @param equalizer
	 */
	public void setEqualizer(IEqualizer equalizer) {
		this.equalizer = equalizer;
	}
	
	/**
	 * @param networkHandler
	 */
	public void setNetworkHandler(INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}
	
	/**
	 * @param podcastFeedHandler
	 */
	public void setPodcastFeedHandler(IPodcastFeedHandler podcastFeedHandler) {
		this.podcastFeedHandler = podcastFeedHandler;
	}
	
	/**
	 * @param state
	 */
	public void setState(IState state) {
		this.state = state;
	}
	
	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
	
	/**
	 * @param engine
	 */
	public void setEngine(MPlayerEngine engine) {
		this.engine = engine;
	}
	
	/**
	 * Returns if audio object is remote or not
	 * @param audioObject
	 * @return
	 */
	private boolean isRemoteAudio(IAudioObject audioObject) {
        return !(audioObject instanceof ILocalAudioObject || 
        		(audioObject instanceof IPodcastFeedEntry && this.state.isUseDownloadedPodcastFeedEntries() && ((IPodcastFeedEntry) audioObject).isDownloaded()));
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
    public Process getProcess(IAudioObject audioObject) throws IOException {
        boolean isRemoteAudio = isRemoteAudio(audioObject);

        String url = getUrlToPlay(audioObject, isRemoteAudio);
        
        if (url == null) {
        	engine.handlePlayerEngineError(new FileNotFoundException(audioObject.getTitleOrFileName()));
        	return null;
        } else {
            List<String> command = prepareCommand(audioObject, isRemoteAudio, url);
        	Logger.debug((Object[]) command.toArray(new String[command.size()]));
        	return new ProcessBuilder().command(command).start();
        }
    }

	/**
	 * @param audioObject
	 * @param isRemoteAudio
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private List<String> prepareCommand(IAudioObject audioObject, boolean isRemoteAudio, String url) throws IOException {
		List<String> command = new ArrayList<String>();
		prepareBasicCommand(audioObject, command, isRemoteAudio);

		if (needsShortPathName(audioObject, isRemoteAudio)) {
			String shortPath = FileNameUtils.getShortPathNameW(url, osManager);
			command.add(shortPath != null && !shortPath.isEmpty() ? shortPath : url);
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
		return command;
	}

	/**
	 * Returns URL to play audio object
	 * @param audioObject
	 * @param isRemoteAudio
	 * @return
	 */
	private String getUrlToPlay(IAudioObject audioObject, boolean isRemoteAudio) {
		String url;
        if (audioObject instanceof IPodcastFeedEntry && !isRemoteAudio) {
            url = podcastFeedHandler.getDownloadPath((IPodcastFeedEntry) audioObject);
        } else {
            url = audioObject.getUrl();
        }
		return url;
	}
    
    /**
     * Returns true if audio object needs short path names
     * @param audioObject
     * @param isRemoteAudio
     * @return
     */
    private boolean needsShortPathName(IAudioObject audioObject, boolean isRemoteAudio) {
    	// First check state and OS
    	if (!statePlayer.isUseShortPathNames() || !osManager.usesShortPathNames()) {
    		return false;
    	}
    	
    	// local audio objects and downloaded podcasts
        return audioObject instanceof ILocalAudioObject || (audioObject instanceof IPodcastFeedEntry && !isRemoteAudio);
    }

	/**
	 * @param audioObject
	 * @param command
	 * @param isRemoteAudio
	 * @throws IOException 
	 */
	private void prepareBasicCommand(IAudioObject audioObject, List<String> command, boolean isRemoteAudio) throws IOException {
		command.add(osManager.getPlayerEngineCommand(engine));
        command.addAll(osManager.getPlayerEngineParameters(engine));
        command.add(MPlayerConstants.QUIET);
        command.add(MPlayerConstants.SLAVE);

        // PREFER_IPV4 for radios and podcast entries
        if (isRemoteAudio) {
            command.add(MPlayerConstants.PREFER_IPV4);
        }
        
        // If a radio has a playlist url add playlist command
        if (audioObject instanceof IRadio && ((IRadio) audioObject).hasPlaylistUrl(networkHandler, state.getProxy())) {
            command.add(MPlayerConstants.PLAYLIST);
        }
	}

	/**
	 * @param command
	 * @param url
	 */
	private void prepareProxy(List<String> command, String url) {
		// proxy
		StringBuilder proxy = new StringBuilder();
		IProxyBean proxyBean = state.getProxy();
		if (proxyBean != null && proxyBean.getType().equals(IProxyBean.HTTP_PROXY)) {
			//String user = proxyBean.getUser();
			//String password = proxyBean.getPassword();
			String proxyUrl = proxyBean.getUrl();
			int port = proxyBean.getPort();

			proxy.append("http_proxy://");
			//proxy.append(!user.isEmpty() ? user : "");
			//proxy.append(!user.isEmpty() && !password.isEmpty() ? ":" : "");
			//proxy.append(!user.isEmpty() && !password.isEmpty() ? password : "");
			//proxy.append(!user.isEmpty() ? "@" : "");
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
	 * @param command
	 * @param isRemoteAudio
	 */
	private void prepareCache(List<String> command, boolean isRemoteAudio) {
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
	private void prepareNormalization(List<String> command) {
		if (statePlayer.isUseNormalisation()) {
			command.add(MPlayerConstants.AUDIO_FILTER);
			command.add(MPlayerConstants.VOLUME_NORM);
		}
	}

	/**
	 * @param audioObject
	 * @param command
	 */
	private void prepareEqualizer(IAudioObject audioObject, List<String> command) {
		// Build equalizer command. Mplayer uses 10 bands
		float[] equalizerValues = equalizer.getEqualizerValues();
		if (audioObject instanceof ILocalAudioObject && equalizerValues != null && equalizerValues.length != 0) {
			command.add(MPlayerConstants.AUDIO_FILTER);
			command.add(prepareEqualizerString(equalizerValues));
		}
	}

	/**
	 * @param equalizer
	 * @return
	 */
	private String prepareEqualizerString(float[] equalizer) {
		StringBuilder eqString = new StringBuilder(MPlayerConstants.EQUALIZER);
		for (int i = 0; i <= 9; i++) {
			eqString.append(equalizer[i]);
			if (i < 9) {
				eqString.append(":");
			}
		}
		return eqString.toString();
	}
}
