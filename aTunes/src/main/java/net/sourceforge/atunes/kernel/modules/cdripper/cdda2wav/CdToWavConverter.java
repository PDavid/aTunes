/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav;

import java.io.File;

import net.sourceforge.atunes.kernel.modules.cdripper.ProgressListener;
import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.model.CDInfo;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.SystemProperties.OperatingSystem;

/**
 * Abstract class for Cdda2wav and Cdparanoia
 */
public abstract class CdToWavConverter {

    private ProgressListener listener;
    private NoCdListener noCdListener;
    private Process process;
    // Cd must be initialized here, otherwise it wont work
    private CDInfo cdInfo = new CDInfo();

    /*
     * Public methods
     */

    /**
     * creates a new CdToWavConverter-object. The implementation depends on the
     * users OS.
     */
    public static CdToWavConverter createNewConverterForOS() {
        if (SystemProperties.OS == OperatingSystem.MACOSX) {
            return new Cdparanoia();
        } else if (SystemProperties.OS == OperatingSystem.WINDOWS) {
            return new Cdda2wav();
        } else if (Cdda2wav.pTestTool()) {
            return new Cdda2wav();
        } else {
            return new Cdparanoia();
        }
    }

    /**
     * Sets no CD found so a dialog gets displayed.
     */
    public void notifyNoCd() {
        if (noCdListener != null) {
            noCdListener.noCd();
        }
    }

    /**
     * Destroys the ripping process.
     */
    public void stop() {
        process.destroy();
    }

    /**
     * Tests if cdda2wav or icedax is present On Windows system cdda2wav is
     * assumed present.
     * 
     * @return true if either cdda2wav or icedax was found, false else.
     */
    public static boolean testTool() {
        if (SystemProperties.OS == OperatingSystem.WINDOWS) {
            // Cdda2wav should be present by default, but allow user to switch it to icedax if he wants
            return Cdda2wav.pTestTool();
        }
        if (SystemProperties.OS == OperatingSystem.MACOSX) {
            return Cdparanoia.pTestTool();
        } else if (Cdda2wav.pTestTool()) {
            return true;
        }
        return Cdparanoia.pTestTool();
    }

    /*
     * Abstract methods
     */

    public abstract boolean cdda2wav(int track, File file);

    public abstract boolean cdda2wav(int track, File file, boolean useParanoia);

    public abstract CDInfo retrieveDiscInformation();

    /*
     * Getters and Setters
     */

    /**
     * Sets the listener.
     * 
     * @param listener
     *            the new listener
     */
    public void setListener(ProgressListener listener) {
        this.listener = listener;
    }

    /**
     * Sets the no cd listener.
     * 
     * @param noCdListener
     *            the new no cd listener
     */
    public void setNoCdListener(NoCdListener noCdListener) {
        this.noCdListener = noCdListener;
    }

    /**
     * @return the process
     */
    protected Process getProcess() {
        return process;
    }

    /**
     * @param process
     *            the process to set
     */
    protected void setProcess(Process process) {
        this.process = process;
    }

    /**
     * @return the listener
     */
    protected ProgressListener getListener() {
        return listener;
    }

    /**
     * @return the cd
     */
    protected CDInfo getCdInfo() {
        return cdInfo;
    }

    /**
     * @param cdInfo
     *            the cdInfo to set
     */
    protected void setCdInfo(CDInfo cdInfo) {
        this.cdInfo = cdInfo;
    }

    /**
     * @return the noCdListener
     */
    protected NoCdListener getNoCdListener() {
        return noCdListener;
    }

}
