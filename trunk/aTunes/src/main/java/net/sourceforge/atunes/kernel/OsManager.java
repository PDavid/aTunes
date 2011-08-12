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

package net.sourceforge.atunes.kernel;

import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.frame.Frame;
import net.sourceforge.atunes.gui.lookandfeel.AbstractLookAndFeel;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.AbstractCdToWavConverter;
import net.sourceforge.atunes.kernel.modules.hotkeys.AbstractHotkeys;
import net.sourceforge.atunes.kernel.modules.os.LinuxOperatingSystem;
import net.sourceforge.atunes.kernel.modules.os.OperatingSystemAdapter;
import net.sourceforge.atunes.kernel.modules.os.SolarisOperatingSystem;
import net.sourceforge.atunes.kernel.modules.os.WindowsOperatingSystem;
import net.sourceforge.atunes.kernel.modules.os.macosx.MacOSXOperatingSystem;
import net.sourceforge.atunes.kernel.modules.player.AbstractPlayerEngine;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FileUtils;

public class OsManager {

	/**
	 * Current OS
	 */
	public static OperatingSystem osType;
	
	private static OperatingSystemAdapter adapter;
	
    /** Path to config folder as passed as argument to app. */
    private static String customConfigFolder = null;
    
    /** Path to repository config folder as passed as argument to app. */
    private static String customRepositoryConfigFolder = null;
	
    static {
    	 osType = detectOperatingSystem();
    	 if (osType.isLinux()) {
    		 adapter = new LinuxOperatingSystem(osType);
    	 } else if (osType.isMacOsX()) {
    		 adapter = new MacOSXOperatingSystem(osType);
    	 } else if (osType.isSolaris()) {
    		 adapter = new SolarisOperatingSystem(osType);
    	 } else {
    		 adapter = new WindowsOperatingSystem(osType);
    	 }
    }
    
    /**
     * Private constructor 
     */
    private OsManager() {
    	
    }
    
	/**
     * Gets folder where state is stored. If not exists, it's created
     * 
     * @param useWorkDir
     *            If the current working directory should be used
     * 
     * @return The folder where the state is stored
     */
	public static String getUserConfigFolder(boolean useWorkDir) {
		if (useWorkDir) {
			return "./debug";
		}
		
	    if (customConfigFolder != null) {
            return customConfigFolder;
        }
		
        String appDataFolder = adapter.getAppDataFolder();

        File userConfigFolder = new File(appDataFolder);
        String newAppDataFolder = userConfigFolder.getAbsolutePath();
        
        /* Move config folder from old one if necessary */
        if (!userConfigFolder.exists()) {
            /* Try to read old config folder. If found, move contents to new location */
            String userHomePath = adapter.getUserHome();
            String oldAppDataFolder = null;
            if (userHomePath != null) {
                File oldConfigFolder = new File(StringUtils.getString(userHomePath, "/.aTunes"));
                if (oldConfigFolder.exists() && oldConfigFolder.isDirectory()) {
                	oldAppDataFolder = oldConfigFolder.getAbsolutePath();
                }            
            }

            if (oldAppDataFolder != null && !oldAppDataFolder.equals(newAppDataFolder)) {
            	System.out.println(StringUtils.getString("Moving configuration from ", oldAppDataFolder, " to ", newAppDataFolder));
            	try {
            		FileUtils.moveDirectory(new File(oldAppDataFolder), new File(newAppDataFolder));
            		System.out.println("Configuration moved");
            	} catch (IOException e) {
            		System.out.println(StringUtils.getString("Error moving configuration: ", e.getMessage()));
            		e.printStackTrace();
            	}
            }
        }
        
        if (!userConfigFolder.exists() && !userConfigFolder.mkdir()) {
        	newAppDataFolder = ".";
        }
        
        return newAppDataFolder;
	}
	
    /**
     * Returns file from the user config folder.
     * 
     * @param name
     *            The name of the file (Example: aTunes.log or folder/file.abc)
     * @param useWorkDir
     *            If the current working directory should be used
     * 
     * @return The file from the user config folder
     */
    public static File getFileFromUserConfigFolder(String name, boolean useWorkDir) {
        String userConfigFolder = getUserConfigFolder(useWorkDir);
        if (userConfigFolder.equals(".")) {
            return new File(name);
        }
        return new File(StringUtils.getString(userConfigFolder, "/", name));
    }

    /**
     * Sets the custom config folder.
     * 
     * @param customConfigFolder
     *            the customConfigFolder to set
     */
    public static void setCustomConfigFolder(String folder) {
        customConfigFolder = folder;
    }

    /**
     * Return path to temporal folder, which is inside user's configuration folder.
     * 
     * @return the temporal folder
     */
    public static String getTempFolder() {
        String userConfigFolder = getUserConfigFolder(Kernel.isDebug());
        String tempFolder = StringUtils.getString(userConfigFolder, adapter.getFileSeparator(), Constants.TEMP_DIR);
        File tempFile = new File(tempFolder);
        if (!tempFile.exists() && !tempFile.mkdir()) {
            return userConfigFolder;
        }
        return tempFolder;
    }



    /**
     * Detect OS.
     * 
     * @return The detected OS
     */
    private static OperatingSystem detectOperatingSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            return OperatingSystem.WINDOWS;
        } else if (osName.contains("mac os x")) {
            return OperatingSystem.MACOSX;
        } else if (osName.contains("sunos")) {
            return OperatingSystem.SOLARIS;
        }
        return OperatingSystem.LINUX;
    }
    
    /**
     * Returns the current working directory
     * 
     * @return
     */
    public static String getWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    /**
     * @return the customRepositoryConfigFolder
     */
    public static String getCustomRepositoryConfigFolder() {
        return customRepositoryConfigFolder;
    }

	/**
	 * Sets custom repository folder
	 * @param folder
	 */
	public static void setCustomRepositoryConfigFolder(String folder) {
		customRepositoryConfigFolder = folder;
	}
	
    /**
     * Returns a string with command to launch application This method is
     * used when restarting app
     * 
     * @return
     */
    public static String getLaunchCommand() {
    	return adapter.getLaunchCommand();
    }
    
    /**
     * Setup specific properties for frame
     * @param frame
     */
    public static void setupFrame(Frame frame) {
    	adapter.setUpFrame(frame);
    }

	/**
	 * Returns if shadow borders are supported
	 * @return
	 */
	public static boolean areShadowBordersForToolTipsSupported() {
		return adapter.areShadowBordersForToolTipsSupported();
	}

	/**
	 * Returns user home
	 * @return
	 */
	public static String getUserHome() {
		return adapter.getUserHome();
	}

	/**
	 * Returns path file separator
	 * @return
	 */
	public static String getFileSeparator() {
		return adapter.getFileSeparator();
	}
	
	/**
	 * Returns if OS uses short path names
	 * @return
	 */
	public static boolean usesShortPathNames() {
		return adapter.usesShortPathNames();
	}

	/**
	 * Sets window in full screen
	 * @param window
	 * @param fullscreen
	 */
	public static void setFullScreen(Window window, boolean fullscreen) {
		adapter.setFullScreen(window, fullscreen);
	}

	/**
	 * Return OS-dependent converter
	 * @return
	 */
	public static AbstractCdToWavConverter getCdToWavConverter() {
		return adapter.getCdToWavConverter();
	}

	/**
	 * Test OS-dependent converter
	 * @return
	 */
	public static boolean testCdToWavConverter() {
		return adapter.testCdToWavConverter();
	}

	/**
	 * Returns OS hotkey listener
	 * @return
	 */
	public static Class<? extends AbstractHotkeys> getHotkeysListener() {
		return adapter.getHotkeysListener();
	}

	public static Object getLineTerminator() {
		return adapter.getSystemLineTerminator();
	}

    /**
     * Returns <code>true</code> if the current operating system (actually
     * the VM) is 64 bit.
     * 
     * @return If the current operating system is 64 bit
     */
	public static boolean is64Bit() {
		return adapter.is64Bit();
	}
	
	/**
	 * Returns if player engine is supported for current OS
	 * @param engine
	 * @return
	 */
	public static boolean isPlayerEngineSupported(AbstractPlayerEngine engine) {
		return adapter.isPlayerEngineSupported(engine); 
	}

	/**
	 * Returns command used (if any) to execute player engine
	 * @param engine
	 * @return
	 */
	public static String getPlayerEngineCommand(AbstractPlayerEngine engine) {
		return adapter.getPlayerEngineCommand(engine);
	}

	/**
	 * Returns specific player engine parameters
	 * @param engine
	 * @return
	 */
	public static Collection<String> getPlayerEngineParameters(AbstractPlayerEngine engine) {
		return adapter.getPlayerEngineParameters(engine);
	}

	/**
	 * Returns path where external tools are (cdda2wav, mencoder, etc.)
	 * Leave "" when tools are in path
	 * @return
	 */
	public static Object getExternalToolsPath() {
		return adapter.getExternalToolsPath();
	}

	/**
	 * Returns supported look and feels
	 * @return
	 */
	public static Map<String, Class<? extends AbstractLookAndFeel>> getLookAndFeels() {
		return adapter.getSupportedLookAndFeels();
	}

	/**
	 * Returns default look and feel class
	 * @return
	 */
	public static Class<? extends AbstractLookAndFeel> getDefaultLookAndFeel() {
		return adapter.getDefaultLookAndFeel();
	}

	/**
	 * Manages when no player engine is available
	 */
	public static void manageNoPlayerEngine() {
		adapter.manageNoPlayerEngine();
	}
	
	/**
	 * Called when player engine is found (after searching or entering manually)
	 */
	public static void playerEngineFound() {
		adapter.playerEngineFound();
	}
	
	/**
	 * Returns os property
	 * @param key
	 * @return
	 */
	public static String getOSProperty(String key) {
		return adapter.getOsProperties().getProperty(key);
	}
	
	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public static void setOSProperty(String key, String value) {
		Properties p = adapter.getOsProperties();
		p.setProperty(key, value);
		adapter.setOsProperties(p);
	}

	/**
	 * Returns if OS supports tray icons
	 * @return
	 */
	public static boolean areTrayIconsSupported() {
		return adapter.areTrayIconsSupported();
	}
	
	/**
	 * Returns if some menu entries (preferences, about) are delegated to OS
	 * @return
	 */
	public static boolean areMenuEntriesDelegated() {
		return adapter.areMenuEntriesDelegated();
	}
	
	/**
	 * Returns if closing main window will terminate application
	 * 
	 * If not, OS will have to provide some method to make window visible again
	 * @return
	 */
	public static boolean isClosingMainWindowClosesApplication() {
		return adapter.isClosingMainWindowClosesApplication();
	}
}
