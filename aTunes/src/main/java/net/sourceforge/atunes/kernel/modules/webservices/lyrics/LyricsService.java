/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.webservices.lyrics;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.proxy.ExtendedProxy;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateChangeListener;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines.AbstractLyricsEngine;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines.LyricsEngineInfo;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.StringUtils;

public final class LyricsService implements ApplicationStateChangeListener {

    private static final List<LyricsEngineInfo> DEFAULT_LYRICS_ENGINES;
    static {
        List<LyricsEngineInfo> list = new ArrayList<LyricsEngineInfo>();
        list.add(new LyricsEngineInfo("LyrDB", "net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines.LyrDBEngine", true));
        list.add(new LyricsEngineInfo("LyricWiki", "net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines.LyricWikiEngine", true));
        list.add(new LyricsEngineInfo("LyricsDirectory", "net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines.LyricsDirectoryEngine", false));
        list.add(new LyricsEngineInfo("LyrcEngine", "net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines.LyrcEngine", false));
        list.add(new LyricsEngineInfo("Winampcn", "net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines.WinampcnEngine", false));
        DEFAULT_LYRICS_ENGINES = Collections.unmodifiableList(list);
    }

    /** Cache */
    private static LyricsCache lyricsCache = new LyricsCache();

    /** Contains a list of LyricsEngine to get lyrics. */
    private List<AbstractLyricsEngine> lyricsEngines;

    /**
     * Singleton instance
     */
    private static LyricsService instance;

    /**
     * Getter for singleton instance
     * 
     * @return
     */
    public static LyricsService getInstance() {
        if (instance == null) {
            instance = new LyricsService();
        }
        return instance;
    }

    /**
     * Private constructor for singleton instance
     * 
     * @param lyricsEngines
     * @param lyricsCache
     */
    private LyricsService() {
    	updateService();
    }

    /**
     * Updates service after a configuration change
     */
    public void updateService() {
        this.lyricsEngines = loadEngines(ApplicationState.getInstance().getProxy());
    }

    /**
     * Public method to retrieve lyrics for a song.
     * 
     * @param artist
     *            the artist
     * @param song
     *            the song
     * 
     * @return the lyrics
     */
    public Lyrics getLyrics(String artist, String song) {
        // Try to get from cache
        Lyrics lyric = lyricsCache.retrieveLyric(artist, song);
        
        // Discard stored lyrics containing HTML
        if (lyric != null && lyric.getLyrics().contains("<") && lyric.getLyrics().contains(">")) {
        	Logger.debug("Discarding lyrics. Seems to contain some HTML code: ");
        	Logger.debug(lyric.getLyrics());
        	lyric = null;
        }
        
        if (lyric == null) {
            // If any engine is loaded
            if (lyricsEngines != null) {
                // Ask for lyrics until a lyric is found in some engine
                int i = 0;
                while (i < lyricsEngines.size() && (lyric == null || lyric.getLyrics().trim().isEmpty())) {
                    lyric = lyricsEngines.get(i).getLyricsFor(artist, song);
                    if (lyric == null) {
                    	Logger.info(StringUtils.getString("Lyrics for: ", artist, "/", song, " not found with engine: ", lyricsEngines.get(i).getLyricsProviderName()));
                    } else {
                    	Logger.debug("Engine: ", lyricsEngines.get(i).getLyricsProviderName(), " returned lyrics for: ", artist, "/", song, ": ", lyric.getLyrics());
                    }
                    
                    i++;
                }
            }
            
            fixLyrics(lyric);
            
            lyricsCache.storeLyric(artist, song, lyric);
        }
        // Return lyric
        return lyric;
    }
    
    /**
     * Applies several common string manipulation to improve lyrics
     * @param lyrics
     */
    private void fixLyrics(Lyrics lyrics) {
        if (lyrics != null) {
        	String lyricsString = lyrics.getLyrics()
            						.replaceAll("'", "\'")
            						.replaceAll("\n\n", "\n") // Remove duplicate \n            	
            						.replaceAll("<.*>", "")   // Remove HTML
            						.trim();
            lyrics.setLyrics(lyricsString);
        }
    }

    /**
     * Returns a map with lyric provider names and urls for adding new lyrics
     * for the specified title and artist
     * 
     * @param artist
     *            the artist
     * @param title
     *            the title
     * @return a map with lyric provider names and urls for adding new lyrics
     */
    public Map<String, String> getUrlsForAddingNewLyrics(String artist, String title) {
        Map<String, String> result = new HashMap<String, String>();
        for (AbstractLyricsEngine lyricsEngine : lyricsEngines) {
            String url = lyricsEngine.getUrlForAddingNewLyrics(artist, title);
            if (url != null && !url.trim().equals("")) {
                result.put(lyricsEngine.getLyricsProviderName(), url);
            }
        }
        return result;
    }

    /**
     * @param lyricsEngines
     *            the lyricsEngines to set
     */
    private void setLyricsEngines(List<AbstractLyricsEngine> lyricsEngines) {
        this.lyricsEngines = lyricsEngines;
    }

    /**
     * Loads lyrics engines
     * 
     * @return the lyrics engines
     */
    private List<AbstractLyricsEngine> loadEngines(ProxyBean proxy) {
        List<LyricsEngineInfo> lyricsEnginesInfo = ApplicationState.getInstance().getLyricsEnginesInfo();
        boolean enginesModified = false;

        ExtendedProxy p = null;
        try {
            if (proxy != null) {
                p = ExtendedProxy.getProxy(proxy);
            }
        } catch (Exception e) {
            Logger.error(e);
        }

        boolean loadDefault = false;
        if (lyricsEnginesInfo != null) {
            for (LyricsEngineInfo lyricsEngineInfo : lyricsEnginesInfo) {
                if (lyricsEngineInfo == null) {
                    loadDefault = true;
                    break;
                }
            }
        } else {
        	enginesModified = true;
            loadDefault = true;
        }

        if (loadDefault) {
            lyricsEnginesInfo = new ArrayList<LyricsEngineInfo>(DEFAULT_LYRICS_ENGINES);
        } else {
            lyricsEnginesInfo = new ArrayList<LyricsEngineInfo>(lyricsEnginesInfo);
            for (LyricsEngineInfo defaultLyricsEngine : DEFAULT_LYRICS_ENGINES) {
                if (!lyricsEnginesInfo.contains(defaultLyricsEngine)) {
                	enginesModified = true;
                    lyricsEnginesInfo.add(defaultLyricsEngine);
                }
            }
        }
        List<AbstractLyricsEngine> result = new ArrayList<AbstractLyricsEngine>();
        // Get engines
        // If some engine can't be loaded will be removed from settings
        List<LyricsEngineInfo> enginesToUnload = new ArrayList<LyricsEngineInfo>();
        for (LyricsEngineInfo lyricsEngineInfo : lyricsEnginesInfo) {
            if (lyricsEngineInfo.isEnabled()) {
                try {
                    Class<?> clazz = Class.forName(lyricsEngineInfo.getClazz());
                    Constructor<?> constructor = clazz.getConstructor(ExtendedProxy.class);
                    result.add((AbstractLyricsEngine) constructor.newInstance(p));
                } catch (ClassNotFoundException e) {
                	enginesToUnload.add(lyricsEngineInfo);
                	logLyricEngineLoadError(lyricsEngineInfo.getClazz(), e);
                } catch (InstantiationException e) {
                	enginesToUnload.add(lyricsEngineInfo);
                	logLyricEngineLoadError(lyricsEngineInfo.getClazz(), e);
                } catch (IllegalAccessException e) {
                	enginesToUnload.add(lyricsEngineInfo);
                	logLyricEngineLoadError(lyricsEngineInfo.getClazz(), e);
                } catch (SecurityException e) {
                	enginesToUnload.add(lyricsEngineInfo);
                	logLyricEngineLoadError(lyricsEngineInfo.getClazz(), e);
                } catch (NoSuchMethodException e) {
                	enginesToUnload.add(lyricsEngineInfo);
                	logLyricEngineLoadError(lyricsEngineInfo.getClazz(), e);
                } catch (IllegalArgumentException e) {
                	enginesToUnload.add(lyricsEngineInfo);
                	logLyricEngineLoadError(lyricsEngineInfo.getClazz(), e);
                } catch (InvocationTargetException e) {
                	enginesToUnload.add(lyricsEngineInfo);
                	logLyricEngineLoadError(lyricsEngineInfo.getClazz(), e);
                }
            }
        }

        for (LyricsEngineInfo engineToUnload : enginesToUnload) {
        	enginesModified = true;
        	lyricsEnginesInfo.remove(engineToUnload);
        }
        
        if (enginesModified) {
        	ApplicationState.getInstance().setLyricsEnginesInfo(lyricsEnginesInfo);
        }
        
        return result;
    }
    
    /**
     * Logs an error loading a lyric engine
     * @param lyricEngineClass
     * @param e
     */
    private void logLyricEngineLoadError(String lyricEngineClass, Exception e) {
    	Logger.error(StringUtils.getString("Error loading lyrics engine: ", lyricEngineClass));
    	Logger.error(StringUtils.getString("Error was: ", e.getClass().getCanonicalName(), " (", e.getMessage(), ")"));
    }
 
    /**
     * Sets the lyric engines for the lyrics service
     * 
     * @param lyricsEnginesInfo
     *            the lyrics engines info
     */
    private void setLyricsEngines(ProxyBean p, List<LyricsEngineInfo> lyricsEnginesInfo) {
        List<AbstractLyricsEngine> result = new ArrayList<AbstractLyricsEngine>();

        // Get engines
        for (LyricsEngineInfo lyricsEngineInfo : lyricsEnginesInfo) {
            if (lyricsEngineInfo.isEnabled()) {
                try {
                    Class<?> clazz = Class.forName(lyricsEngineInfo.getClazz());
                    Constructor<?> constructor = clazz.getConstructor(ExtendedProxy.class);
                    result.add((AbstractLyricsEngine) constructor.newInstance(p));
                } catch (ClassNotFoundException e) {
                    Logger.error(e);
                } catch (InstantiationException e) {
                    Logger.error(e);
                } catch (IllegalAccessException e) {
                    Logger.error(e);
                } catch (SecurityException e) {
                    Logger.error(e);
                } catch (NoSuchMethodException e) {
                    Logger.error(e);
                } catch (IllegalArgumentException e) {
                    Logger.error(e);
                } catch (InvocationTargetException e) {
                    Logger.error(e);
                }
            }
        }
        setLyricsEngines(result);
    }

    /**
     * Delegate method to clear cache
     * 
     * @return
     */
    public boolean clearCache() {
        return lyricsCache.clearCache();
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
        setLyricsEngines(newState.getProxy(), newState.getLyricsEnginesInfo());
    }

    /**
     * Finishes service
     */
    public void finishService() {
        lyricsCache.shutdown();
    }

}
