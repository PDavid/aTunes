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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Detects CD drives, gathers CD information and rip CD's all using cdda2wav
 * (icedax).
 */
public class Cdda2wav extends AbstractCdToWavConverter {

    private static String converterCommand = Cdda2wavConstants.CDDA2WAV_COMMAND_STRING;

    private final int devNumber;
    private final String device;
    private boolean cdLoaded;
    private boolean doNotRepeatNoCdDialog = true;
    private boolean ata = false;
    /** Use paranoia mode (better error correction but slower ripping) */
    private boolean useParanoia = false;
    private List<String> devices = new ArrayList<String>();
    /** Variable used to count number of checked drives */
    private int devCounter = 1;

    private final IOSManager osManager;

    /**
     * <p>
     * Constructor
     * </p>
     * 
     * <p>
     * Does call the doScanBus method to probe for present CD drives, then
     * assigns the first CD device in the list to the variable device.
     * </p>
     * 
     * @param osManager
     */
    public Cdda2wav(final IOSManager osManager) {
	this.osManager = osManager;
	Cdda2wavScanBus scanBus = new Cdda2wavScanBus(converterCommand,
		osManager);
	this.devices = scanBus.doScanBus();
	this.devNumber = this.devices.size();
	this.device = this.devNumber > 0 ? devices.get(0) : null;
	this.ata = scanBus.isAta();
    }

    /**
     * @param cdLoaded
     */
    void setCdLoaded(final boolean cdLoaded) {
	this.cdLoaded = cdLoaded;
    }

    /**
     * Tests if cdda2wav or icedax is present On Windows system cdda2wav is
     * assumed present.
     * 
     * @param osManager
     * @return true if either cdda2wav or icedax was found, false else.
     */
    public static boolean pTestTool(final IOSManager osManager) {

	BufferedReader stdInput = null;
	BufferedReader stdInput2 = null;
	try {
	    // If user adds cdda2wav while aTunes is running it will be detected
	    // even if icedax was used.
	    converterCommand = Cdda2wavConstants.CDDA2WAV_COMMAND_STRING;
	    Process p = new ProcessBuilder(StringUtils.getString(
		    osManager.getExternalToolsPath(), converterCommand),
		    Cdda2wavConstants.VERSION).start();
	    stdInput = new BufferedReader(new InputStreamReader(
		    p.getErrorStream()));
	    String line = null;
	    while ((line = stdInput.readLine()) != null) {
		// On some Linux distributions we have a symlink to icedax so
		// detect it.
		if (line.contains("icedax")) {
		    converterCommand = Cdda2wavConstants.ICEDAX_COMMAND_STRING;
		}
	    }
	    int code = p.waitFor();
	    if (code != 0) {
		return false;
	    }
	    return true;
	} catch (IOException e) {
	    // cdda2wav is not present. Maybe we have more luck with icedax.
	    try {
		converterCommand = Cdda2wavConstants.ICEDAX_COMMAND_STRING;
		Process icedaxCheck = new ProcessBuilder(StringUtils.getString(
			osManager.getExternalToolsPath(), converterCommand),
			Cdda2wavConstants.VERSION).start();
		stdInput2 = new BufferedReader(new InputStreamReader(
			icedaxCheck.getInputStream()));

		String line = null;
		while ((line = stdInput2.readLine()) != null) {
		    Logger.debug(line);
		}

		int code2 = icedaxCheck.waitFor();
		if (code2 != 0) {
		    return false;
		}
		return true;
	    } catch (IOException e2) {
		return false;
	    } catch (InterruptedException e3) {
		return false;
	    }
	} catch (InterruptedException e) {
	    return false;
	} finally {
	    ClosingUtils.close(stdInput);
	    ClosingUtils.close(stdInput2);
	}
    }

    /**
     * Ripps the selected tracks using cdda2wav or icedax. The audio CD must
     * have been detected previously and should not have moved (so still should
     * be in the same drive as when it was detected.
     * 
     * @param track
     *            The track number of the song to be ripped.
     * @param fileName
     *            The filename of the wav file that will be created.
     * 
     * @return Returns true if the ripping process was successfull, false
     *         otherwise.
     */
    @Override
    public boolean cdda2wav(final int track, final File fileName) {
	Logger.info(StringUtils.getString("Writing wav file for track ", track,
		" in file ", fileName.getName()));

	BufferedReader stdInput = null;
	try {
	    List<String> command = createCommand(track, fileName);

	    setProcess(new ProcessBuilder(command).start());
	    stdInput = new BufferedReader(new InputStreamReader(getProcess()
		    .getErrorStream()));
	    String s = null;
	    while ((s = stdInput.readLine()) != null) {
		if (getListener() != null && s.matches(".*%.*")) {
		    int pos = s.indexOf('%');
		    final int percent = Integer.parseInt(s.substring(pos - 3,
			    pos).trim());
		    SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
			    getListener().notifyProgress(percent);
			}
		    });
		}
	    }

	    int code = getProcess().waitFor();
	    if (code != 0) {
		Logger.error(StringUtils.getString("Process returned code ",
			code));
		return false;
	    }

	    Logger.info("Wav file ok!!");
	    return true;
	} catch (IOException e) {
	    Logger.error(StringUtils.getString(
		    "Process execution caused exception ", e));
	    return false;
	} catch (InterruptedException e) {
	    Logger.error(StringUtils.getString(
		    "Process execution caused exception ", e));
	    return false;
	} finally {
	    ClosingUtils.close(stdInput);
	}
    }

    /**
     * @param track
     * @param fileName
     * @return
     */
    private List<String> createCommand(final int track, final File fileName) {
	List<String> command = new ArrayList<String>();
	command.add(StringUtils.getString(osManager.getExternalToolsPath(),
		converterCommand));
	if (device != null) {
	    // When -scanbus dev=ATA is used we use another syntax
	    if (ata) {
		String devATA = StringUtils.getString(Cdda2wavConstants.ATA,
			":", getDriveId());
		command.add(devATA);
	    } else {
		command.add(Cdda2wavConstants.DEVICE);
		command.add(getDriveId());
	    }
	}
	command.add(Cdda2wavConstants.WAVFORMAT);
	command.add(StringUtils.getString(Cdda2wavConstants.TRACKS, track));
	command.add(Cdda2wavConstants.NO_INFO_FILE);
	if (useParanoia) {
	    Logger.info("Using paranoia mode");
	    command.add(Cdda2wavConstants.PARANOIA);
	}
	command.add(FileUtils.getPath(fileName));

	/* Check that we've got somewhere to write the track to */
	if (!fileName.getParentFile().exists()
		&& !fileName.getParentFile().mkdirs()) {
	    Logger.error(StringUtils.getString(fileName.getParentFile(),
		    " not created"));
	}

	Logger.debug((Object[]) command.toArray(new String[command.size()]));
	return command;
    }

    /**
     * Enable paranoia mode for ripping
     */
    @Override
    public boolean cdda2wav(final int track, final File fileName,
	    final boolean useParanoia) {
	this.useParanoia = useParanoia;
	return cdda2wav(track, fileName);
    }

    /**
     * <p>
     * Gathers the various information from the audio CD and tries to get more
     * info from either CDDB or CD Text if present. Also checks if no or a data
     * CD is inserted and gives a "No CD" message in this case. All found
     * devices get probed until an audio CD is found or there are no more
     * devices to get probed. Currently icedax does not support special
     * characters and returns "_" instead. Cdda2wav is read using charset, what
     * happens when other characters get into play is unknown.
     * </p>
     * 
     * <p>
     * Data tracks do get substracted from the total track number and are not
     * considered! So data track must be the last one.
     * </p>
     * 
     * @return Returns either information about the inserted CD or false if no
     *         audio CD was found This includes: -Artist and album name -Total
     *         duration -CD id number -Number of tracks -Track names and
     *         duration
     */
    @Override
    public CDInfo retrieveDiscInformation() {
	Logger.info("Getting cd information...");
	// If no devices detected do exit
	if (devNumber == 0) {
	    return null;
	}

	try {
	    // Prepare cdda2wav commands and execute
	    List<String> command = createCommandToRetrieveDiscInformation();

	    setProcess(new ProcessBuilder(command).start());
	    cdLoaded = false;

	    readCddaThread();

	    // Check if we have either a data CD or no CD inserted at all
	    // If this is the case we must check the other devices
	    if (getCDInfo().getTracks() == 0 || !cdLoaded) {
		CDInfo cdRecursive = checkAnotherDrive();
		// If the recursive function had some result, assign it to cd
		if (cdRecursive != null) {
		    setCDInfo(cdRecursive);
		}
	    }

	    // If no tracks are found, assuming no CD inserted.
	    if (!cdLoaded || getCDInfo().getTracks() == 0) {
		noCdInserted();
		return null;
	    }

	    Logger.info(StringUtils.getString("CD info: ", getCDInfo()));
	    return getCDInfo();

	} catch (IOException e) {
	    Logger.error(e);
	    return null;
	} catch (InterruptedException e) {
	    Logger.error(e);
	    return null;
	}
    }

    /**
	 * 
	 */
    private void noCdInserted() {
	// Only print no CD dialog once
	if (doNotRepeatNoCdDialog) {
	    notifyNoCd();
	}
	doNotRepeatNoCdDialog = false;
    }

    /**
     * @param cdRecursive
     * @return
     */
    private CDInfo checkAnotherDrive() {
	devCounter = devCounter + 1;
	// Go to next drive
	devices.remove(0);
	// if there is another drive, try it
	if (getDriveId() != null && devCounter <= devNumber) {
	    return retrieveDiscInformation();
	}
	return null;
    }

    /**
     * @throws InterruptedException
     */
    private void readCddaThread() throws InterruptedException {
	Thread readCdda = new ReadCddaThread(this);

	// Here we control the thread. The join() is needed in case there is no
	// cd to
	// avoid hanging. Increase join() waiting time if "no CD" errors are
	// occuring
	// on slow drives. The thread is interupted to avoid a hang when no CD
	// is present
	readCdda.start();
	readCdda.join(650);
	if (cdLoaded) {
	    // If a CD is detected, give more time
	    readCdda.join(80000);
	}

	// This produces error outputs but is necessary to avoid cdda2wav
	// blocking the drive
	getProcess().destroy();
	readCdda.interrupt();
	Logger.info("Interrupt");
	// cdMonitor.join();
    }

    /**
     * @return
     */
    private List<String> createCommandToRetrieveDiscInformation() {
	List<String> command = new ArrayList<String>();
	command.add(StringUtils.getString(osManager.getExternalToolsPath(),
		converterCommand));

	if (device != null) {
	    // If -scanbus dev=ATA method finds something, we need to use a
	    // different syntax
	    if (ata) {
		String devATA = StringUtils.getString(Cdda2wavConstants.ATA,
			":", getDriveId());
		command.add(devATA);
	    } else {
		command.add(Cdda2wavConstants.DEVICE);
		command.add(getDriveId());
	    }
	}
	command.add(Cdda2wavConstants.LIST_TRACKS);
	command.add(Cdda2wavConstants.VERBOSE);
	command.add(Cdda2wavConstants.GUI);
	command.add(Cdda2wavConstants.NO_INFO_FILE);
	command.add(Cdda2wavConstants.CDDB);

	Logger.debug((Object[]) command.toArray(new String[command.size()]));
	return command;
    }

    /**
     * Returns OS name of CD drive.
     * 
     * @return OS name of CD drive
     */
    private String getDriveId() {
	if (!devices.isEmpty()) {
	    return devices.get(0);
	}
	return null;
    }

    /**
     * Sets no CD found so a dialog gets displayed.
     */
    @Override
    public void notifyNoCd() {
	if (getNoCdListener() != null) {
	    getNoCdListener().noCd();
	}
    }

    /**
     * Destroys the ripping process.
     */
    @Override
    public void stop() {
	getProcess().destroy();
    }

}
