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

package net.sourceforge.atunes.kernel.modules.os;

import java.awt.Window;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.kernel.OperatingSystemDetector;
import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayerEngine;
import net.sourceforge.atunes.model.IPlayerTrayIconsHandler;
import net.sourceforge.atunes.model.ITrayIcon;
import net.sourceforge.atunes.model.OperatingSystem;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class OsManager implements IOSManager, ApplicationContextAware {

	/**
	 * Current OS
	 */
	private OperatingSystem osType;
	
	private OperatingSystemAdapter adapter;

	private IApplicationArguments applicationArguments;
	
	private ApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.context = applicationContext;
	}
	
    /**
     * @param applicationArguments
     */
    public void setApplicationArguments(IApplicationArguments applicationArguments) {
    	this.applicationArguments = applicationArguments;
	}
	
    /**
     *  Initializes os manager 
     */
    public void initialize() {
    	osType = OperatingSystemDetector.getOperatingSystem();
    	if (osType.isLinux()) {
    		adapter = context.getBean(LinuxOperatingSystem.class);
    	} else if (osType.isMacOsX()) {
    		adapter = context.getBean(MacOSXOperatingSystem.class);
    	} else if (osType.isSolaris()) {
    		adapter = context.getBean(SolarisOperatingSystem.class);
    	} else {
    		adapter = context.getBean(WindowsOperatingSystem.class);
    	}    	
		adapter.setSystemType(osType);
    }
    
	@Override
	public final String getUserConfigFolder() {
		// Get path depending on parameters
		String userConfigFolder = getConfigFolder();

		// Test if it's valid
		if (!isValidConfigFolder(userConfigFolder)) {
			// As workaround if can't get a valid folder, use temporal folder, for example: /tmp/atunes
			userConfigFolder = StringUtils.getString(System.getProperty("java.io.tmpdir"), getFileSeparator(), "atunes");
			Logger.error("Using ", userConfigFolder, " as config folder");
		}
		
		return userConfigFolder;
	}
	
	/**
	 * Test if path is valid
	 * @param path
	 * @return
	 */
	private boolean isValidConfigFolder(String path) {
		File folder = new File(path);
		
		// If folder does not exist, try to create
		if (!folder.exists() && !folder.mkdirs()) {
			Logger.error("Can't create folder ", path);
			return false;
		}
		
		// Folder exists, check if can write
		if (!folder.canWrite()) {
			Logger.error("Can't write folder ", path);
			return false;
		}
		
		// Also check if it's a directory
		if (!folder.isDirectory()) {
			Logger.error(path, " is not a directory");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Returns folder path where application stores its configuration
	 * @param useWorkDir
	 * @return
	 */
	private String getConfigFolder() {
		if (applicationArguments.isDebug()) {
			return "./debug";
		} else if (applicationArguments.getUserConfigFolder() != null) {
            return applicationArguments.getUserConfigFolder();
        } else {
        	return adapter.getAppDataFolder();
        }
	}
		
    @Override
	public File getFileFromUserConfigFolder(String name) {
        String userConfigFolder = getUserConfigFolder();
        if (userConfigFolder.equals(".")) {
            return new File(name);
        }
        return new File(StringUtils.getString(userConfigFolder, "/", name));
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#getTempFolder()
	 */
    @Override
	public String getTempFolder() {
        String userConfigFolder = getUserConfigFolder();
        String tempFolder = StringUtils.getString(userConfigFolder, adapter.getFileSeparator(), Constants.TEMP_DIR);
        File tempFile = new File(tempFolder);
        if (!tempFile.exists() && !tempFile.mkdir()) {
            return userConfigFolder;
        }
        return tempFolder;
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#getWorkingDirectory()
	 */
    @Override
	public String getWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#getCustomRepositoryConfigFolder()
	 */
    @Override
	public String getCustomRepositoryConfigFolder() {
        return applicationArguments.getRepositoryConfigFolder();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#getLaunchCommand()
	 */
    @Override
	public String getLaunchCommand() {
    	return adapter.getLaunchCommand();
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#getLaunchParameters()
	 */
    @Override
	public String getLaunchParameters() {
    	return adapter.getLaunchParameters();
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#setupFrame(net.sourceforge.atunes.model.IFrame)
	 */
    @Override
	public void setupFrame(IFrame frame) {
    	adapter.setupFrame(frame);
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#areShadowBordersForToolTipsSupported()
	 */
	@Override
	public boolean areShadowBordersForToolTipsSupported() {
		return adapter.areShadowBordersForToolTipsSupported();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#getUserHome()
	 */
	@Override
	public String getUserHome() {
		return adapter.getUserHome();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#getFileSeparator()
	 */
	@Override
	public String getFileSeparator() {
		return adapter.getFileSeparator();
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#usesShortPathNames()
	 */
	@Override
	public boolean usesShortPathNames() {
		return adapter.usesShortPathNames();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#setFullScreen(java.awt.Window, boolean, net.sourceforge.atunes.model.IFrame)
	 */
	@Override
	public void setFullScreen(Window window, boolean fullscreen, IFrame frame) {
		adapter.setFullScreen(window, fullscreen, frame);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#getLineTerminator()
	 */
	@Override
	public String getLineTerminator() {
		return adapter.getSystemLineTerminator();
	}

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#is64Bit()
	 */
	@Override
	public boolean is64Bit() {
		return adapter.is64Bit();
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#isPlayerEngineSupported(net.sourceforge.atunes.kernel.modules.player.AbstractPlayerEngine)
	 */
	@Override
	public boolean isPlayerEngineSupported(IPlayerEngine engine) {
		return adapter.isPlayerEngineSupported(engine); 
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#getPlayerEngineCommand(net.sourceforge.atunes.kernel.modules.player.AbstractPlayerEngine)
	 */
	@Override
	public String getPlayerEngineCommand(IPlayerEngine engine) {
		return adapter.getPlayerEngineCommand(engine);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#getPlayerEngineParameters(net.sourceforge.atunes.kernel.modules.player.AbstractPlayerEngine)
	 */
	@Override
	public Collection<String> getPlayerEngineParameters(IPlayerEngine engine) {
		return adapter.getPlayerEngineParameters(engine);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#getExternalToolsPath()
	 */
	@Override
	public Object getExternalToolsPath() {
		return adapter.getExternalToolsPath();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#getLookAndFeels()
	 */
	@Override
	public Map<String, Class<? extends ILookAndFeel>> getLookAndFeels() {
		return adapter.getSupportedLookAndFeels();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#getDefaultLookAndFeel()
	 */
	@Override
	public Class<? extends ILookAndFeel> getDefaultLookAndFeel() {
		return adapter.getDefaultLookAndFeel();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#manageNoPlayerEngine()
	 */
	@Override
	public void manageNoPlayerEngine(final IFrame frame) {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				adapter.manageNoPlayerEngine(frame);
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#playerEngineFound()
	 */
	@Override
	public void playerEngineFound() {
		adapter.playerEngineFound();
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#getOSProperty(java.lang.String)
	 */
	@Override
	public String getOSProperty(String key) {
		return adapter.getOsProperties().getProperty(key);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#setOSProperty(java.lang.String, java.lang.String)
	 */
	@Override
	public void setOSProperty(String key, String value) {
		Properties p = adapter.getOsProperties();
		p.setProperty(key, value);
		adapter.setOsProperties(p);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#areTrayIconsSupported()
	 */
	@Override
	public boolean areTrayIconsSupported() {
		return adapter.areTrayIconsSupported();
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#areMenuEntriesDelegated()
	 */
	@Override
	public boolean areMenuEntriesDelegated() {
		return adapter.areMenuEntriesDelegated();
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#isClosingMainWindowClosesApplication()
	 */
	@Override
	public boolean isClosingMainWindowClosesApplication() {
		return adapter.isClosingMainWindowClosesApplication();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#isRipSupported()
	 */
	@Override
	public boolean isRipSupported() {
		return adapter.isRipSupported();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#isWindowsVista()
	 */
	@Override
	public boolean isWindowsVista() {
		return osType.isWindowsVista();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#isWindows7()
	 */
	@Override
	public boolean isWindows7() {
		return osType.isWindows7();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#isOldWindows()
	 */
	@Override
	public boolean isOldWindows() {
		return osType.isOldWindows();
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#isLinux()
	 */
	@Override
	public boolean isLinux() {
		return osType.isLinux();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#isMacOsX()
	 */
	@Override
	public boolean isMacOsX() {
		return osType.isMacOsX();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#isSolaris()
	 */
	@Override
	public boolean isSolaris() {
		return osType.isSolaris();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.IOSManager#isWindows()
	 */
	@Override
	public boolean isWindows() {
		return osType.isWindows();
	}
	
	@Override
	public boolean isMultipleInstancesSupported() {
		return adapter.isMultipleInstancesSupported();
	}
	
	@Override
	public ITrayIcon getTrayIcon() {
		return adapter.getTrayIcon();
	}
	
	@Override
	public IPlayerTrayIconsHandler getPlayerTrayIcons() {
		return adapter.getPlayerTrayIcons();
	}
	
	@Override
	public boolean areTrayIconsColorsSupported() {
		return adapter.areTrayIconsColorsSupported();
	}
}
