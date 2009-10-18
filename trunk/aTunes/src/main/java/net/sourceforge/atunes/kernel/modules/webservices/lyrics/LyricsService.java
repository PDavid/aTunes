/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateChangeListener;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines.LyricsEngine;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines.LyricsEngineInfo;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

public class LyricsService implements ApplicationStateChangeListener {

    private static final List<LyricsEngineInfo> DEFAULT_LYRICS_ENGINES;
    static {
        List<LyricsEngineInfo> list = new ArrayList<LyricsEngineInfo>();
        list.add(new LyricsEngineInfo("LyricWiki", "net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines.LyricWikiEngine", true));
        list.add(new LyricsEngineInfo("Lyricsfly", "net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines.LyricsflyEngine", true));
        list.add(new LyricsEngineInfo("LyricsDirectory", "net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines.LyricsDirectoryEngine", false));
        list.add(new LyricsEngineInfo("LyrcEngine", "net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines.LyrcEngine", false));
        list.add(new LyricsEngineInfo("Winampcn", "net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines.WinampcnEngine", false));
        DEFAULT_LYRICS_ENGINES = Collections.unmodifiableList(list);
    }

    private Logger logger;

    /** Contains a list of LyricsEngine to get lyrics. */
    private List<LyricsEngine> lyricsEngines;

    /** Cache */
    private LyricsCache lyricsCache = new LyricsCache();

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
        this.lyricsEngines = loadEngines(ApplicationState.getInstance().getProxy());
    }

    /**
     * Updates service after a configuration change
     */
    public void updateService() {
        // Force create service again
        instance = null;
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
        getLogger().debug(LogCategories.SERVICE, artist, song);

        // Try to get from cache
        Lyrics lyric = lyricsCache.retrieveLyric(artist, song);
        if (lyric == null) {
            // If any engine is loaded
            if (lyricsEngines != null) {
                // Ask for lyrics until a lyric is found in some engine
                int i = 0;
                while (i < lyricsEngines.size() && (lyric == null || lyric.getLyrics().trim().isEmpty())) {
                    lyric = lyricsEngines.get(i++).getLyricsFor(artist, song);
                }
            }
            if (lyric != null) {
                lyric.setLyrics(lyric.getLyrics().replaceAll("'", "\'"));
                lyric.setLyrics(lyric.getLyrics().replaceAll("\n\n", "\n"));
            }
            lyricsCache.storeLyric(artist, song, lyric);
        }
        // Return lyric
        return lyric;
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
        for (LyricsEngine lyricsEngine : lyricsEngines) {
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
    private void setLyricsEngines(List<LyricsEngine> lyricsEngines) {
        this.lyricsEngines = lyricsEngines;
    }

    /**
     * Loads lyrics engines
     * 
     * @return the lyrics engines
     */
    private List<LyricsEngine> loadEngines(ProxyBean proxy) {
        List<LyricsEngineInfo> lyricsEnginesInfo = ApplicationState.getInstance().getLyricsEnginesInfo();

        Proxy p = null;
        try {
            if (proxy != null) {
                p = Proxy.getProxy(proxy);
            }
        } catch (Exception e) {
            getLogger().error(LogCategories.HANDLER, e);
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
            loadDefault = true;
        }

        if (loadDefault) {
            lyricsEnginesInfo = new ArrayList<LyricsEngineInfo>(DEFAULT_LYRICS_ENGINES);
        } else {
            lyricsEnginesInfo = new ArrayList<LyricsEngineInfo>(lyricsEnginesInfo);
            for (LyricsEngineInfo defaultLyricsEngine : DEFAULT_LYRICS_ENGINES) {
                if (!lyricsEnginesInfo.contains(defaultLyricsEngine)) {
                    lyricsEnginesInfo.add(defaultLyricsEngine);
                }
            }
        }
        ApplicationState.getInstance().setLyricsEnginesInfo(lyricsEnginesInfo);

        List<LyricsEngine> result = new ArrayList<LyricsEngine>();
        // Get engines
        for (LyricsEngineInfo lyricsEngineInfo : lyricsEnginesInfo) {
            if (lyricsEngineInfo.isEnabled()) {
                try {
                    Class<?> clazz = Class.forName(lyricsEngineInfo.getClazz());
                    Constructor<?> constructor = clazz.getConstructor(Proxy.class);
                    result.add((LyricsEngine) constructor.newInstance(p));
                } catch (ClassNotFoundException e) {
                    getLogger().error(LogCategories.HANDLER, e);
                } catch (InstantiationException e) {
                    getLogger().error(LogCategories.HANDLER, e);
                } catch (IllegalAccessException e) {
                    getLogger().error(LogCategories.HANDLER, e);
                } catch (SecurityException e) {
                    getLogger().error(LogCategories.HANDLER, e);
                } catch (NoSuchMethodException e) {
                    getLogger().error(LogCategories.HANDLER, e);
                } catch (IllegalArgumentException e) {
                    getLogger().error(LogCategories.HANDLER, e);
                } catch (InvocationTargetException e) {
                    getLogger().error(LogCategories.HANDLER, e);
                }
            }
        }
        return result;
    }

    /**
     * Sets the lyric engines for the lyrics service
     * 
     * @param lyricsEnginesInfo
     *            the lyrics engines info
     */
    private void setLyricsEngines(ProxyBean p, List<LyricsEngineInfo> lyricsEnginesInfo) {
        List<LyricsEngine> result = new ArrayList<LyricsEngine>();

        // Get engines
        for (LyricsEngineInfo lyricsEngineInfo : lyricsEnginesInfo) {
            if (lyricsEngineInfo.isEnabled()) {
                try {
                    Class<?> clazz = Class.forName(lyricsEngineInfo.getClazz());
                    Constructor<?> constructor = clazz.getConstructor(Proxy.class);
                    result.add((LyricsEngine) constructor.newInstance(p));
                } catch (ClassNotFoundException e) {
                    getLogger().error(LogCategories.HANDLER, e);
                } catch (InstantiationException e) {
                    getLogger().error(LogCategories.HANDLER, e);
                } catch (IllegalAccessException e) {
                    getLogger().error(LogCategories.HANDLER, e);
                } catch (SecurityException e) {
                    getLogger().error(LogCategories.HANDLER, e);
                } catch (NoSuchMethodException e) {
                    getLogger().error(LogCategories.HANDLER, e);
                } catch (IllegalArgumentException e) {
                    getLogger().error(LogCategories.HANDLER, e);
                } catch (InvocationTargetException e) {
                    getLogger().error(LogCategories.HANDLER, e);
                }
            }
        }
        setLyricsEngines(result);
    }

    /**
     * Getter for logger
     * 
     * @return
     */
    private Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
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
