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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Executes scan bus to find CD devices
 * 
 * @author alex
 * 
 */
public class Cdda2wavScanBus {

    private static final String PROCESS_EXECUTION_CAUSED_EXCEPTION = "Process execution caused exception ";

    private final String converterCommand;

    private final IOSManager osManager;

    private Process process;

    private boolean ata = false;

    /**
     * @param converterCommand
     * @param osManager
     */
    Cdda2wavScanBus(final String converterCommand, final IOSManager osManager) {
	this.converterCommand = converterCommand;
	this.osManager = osManager;
    }

    /**
     * Tests for present CD drives. Various methods are tried to improve
     * detection rate.
     * 
     * @return Returns an List of the found devices, either in the cdda2wav
     *         format (for example 1,0,0) or how the OS calls it (for example
     *         /dev/hdc). Please keep in mind that for now when using the
     *         dev=ATA method, cdda2wav/icedax will need to use another command
     *         than if the other methods are used. For now, this indicated by
     *         the boolean variable ata which however can not accessed from the
     *         outside for now. True if dev=ATA is used.
     */
    List<String> doScanBus() {
	Logger.info(StringUtils.getString("Scanning bus using ",
		converterCommand));

	List<String> devices = tryScanWithIcedax();
	if (devices == null || !devices.isEmpty()) {
	    return devices;
	}

	devices = tryScanWithCdda2wav();
	if (devices == null || !devices.isEmpty()) {
	    return devices;
	}

	devices = tryScanWithATA();

	return devices;
    }

    /**
     * List of devices found with icedax or null if error happens
     * 
     * @return
     */
    private List<String> tryScanWithIcedax() {
	List<String> devices = new ArrayList<String>();

	// When icedax is used, try the --devices method, otherwise leave it as
	// the maintainer
	// of cdda2wav does not seem to like this method.
	if (converterCommand.equals(Cdda2wavConstants.ICEDAX_COMMAND_STRING)) {
	    BufferedReader stdInput = null;
	    try {
		List<String> command = new ArrayList<String>();
		command.add(StringUtils.getString(
			osManager.getExternalToolsPath(), converterCommand));
		command.add(Cdda2wavConstants.SCANDEVICES);
		command.add(Cdda2wavConstants.NO_INFO_FILE);

		Logger.debug((Object[]) command.toArray(new String[command
			.size()]));

		setProcess(new ProcessBuilder(command).start());
		stdInput = new BufferedReader(new InputStreamReader(
			getProcess().getInputStream()));
		readIcedaxDevices(devices, stdInput);
		int code = getProcess().waitFor();
		if (code != 0) {
		    Logger.error(StringUtils.getString(
			    "Process returned code ", code));
		    // Do not return null. Application hangs otherwise
		    // return null;
		}

		Logger.info(StringUtils.getString("Found ", devices.size(),
			" devices with --device method"));
	    } catch (IOException e) {
		Logger.error(StringUtils.getString(
			PROCESS_EXECUTION_CAUSED_EXCEPTION, e));
		return null;
	    } catch (InterruptedException e) {
		Logger.error(StringUtils.getString(
			PROCESS_EXECUTION_CAUSED_EXCEPTION, e));
		return null;
	    } finally {
		ClosingUtils.close(stdInput);
	    }
	}
	return devices;
    }

    /**
     * @param devices
     * @param stdInput
     * @throws IOException
     */
    private void readIcedaxDevices(final List<String> devices,
	    final BufferedReader stdInput) throws IOException {
	// read the output from the command
	String s = null;
	// Search for the dev='...' lines to know what the OS calls the CD-Rom
	// devices
	while ((s = stdInput.readLine()) != null) {
	    if (s.contains("dev=") && !s.contains("dev=help")) {
		String line = s.trim();
		java.util.StringTokenizer getDeviceOSName = new java.util.StringTokenizer(
			line, "'");
		// The first part is not interesting, we look for the second
		// token, thus the next line
		getDeviceOSName.nextToken();
		String id = getDeviceOSName.nextToken();
		id = id.replace("\t", "");
		// Write found device to list
		devices.add(id);
	    }
	}
    }

    /**
     * List of devices found with cdda2wav command or null if error happens
     * 
     * @return
     */
    private List<String> tryScanWithCdda2wav() {
	List<String> devices = new ArrayList<String>();

	BufferedReader stdInput = null;
	try {
	    List<String> command = new ArrayList<String>();
	    command.add(StringUtils.getString(osManager.getExternalToolsPath(),
		    converterCommand));
	    command.add(Cdda2wavConstants.SCAN_BUS);
	    command.add(Cdda2wavConstants.NO_INFO_FILE);

	    Logger.debug((Object[]) command.toArray(new String[command.size()]));

	    setProcess(new ProcessBuilder(command).start());
	    // Icedax and cdda2wav seem to behave differently
	    if ((osManager.isLinux() && !converterCommand
		    .equals(Cdda2wavConstants.ICEDAX_COMMAND_STRING))
		    || osManager.isWindows()) {
		stdInput = new BufferedReader(new InputStreamReader(
			getProcess().getErrorStream()));
	    } else {
		stdInput = new BufferedReader(new InputStreamReader(
			getProcess().getInputStream()));
	    }

	    // read the output from the command
	    readCdda2wavDevices(devices, stdInput);

	    int code = getProcess().waitFor();
	    if (code != 0) {
		Logger.error(StringUtils.getString("Process returned code ",
			code));
		// Do not return null. Application hangs otherwise
		// return null;
	    }
	    Logger.info(StringUtils.getString("Found ", devices.size(),
		    " devices with scanbus method"));

	} catch (IOException e) {
	    Logger.error(StringUtils.getString(
		    "Process execution caused exception: ", e.getMessage()));
	    Logger.error(e);
	} catch (InterruptedException e) {
	    Logger.error(StringUtils.getString(
		    "Process execution caused exception: ", e.getMessage()));
	    Logger.error(e);
	} finally {
	    ClosingUtils.close(stdInput);
	}
	return devices;
    }

    /**
     * @param devices
     * @param stdInput
     * @throws IOException
     */
    private void readCdda2wavDevices(final List<String> devices,
	    final BufferedReader stdInput) throws IOException {
	String s = null;
	while ((s = stdInput.readLine()) != null) {
	    if (s.contains("CD-ROM")) {
		String line = s.trim();
		String id = null;
		// Icedax and cdda2wav seem to behave differently
		if ((osManager.isLinux() && !converterCommand
			.equals(Cdda2wavConstants.ICEDAX_COMMAND_STRING))
			|| osManager.isWindows()) {
		    // Workaround: Neither of this solutions work on all
		    // machines, but first one usually throws an exception
		    // so we know we should try the second one.
		    try {
			id = line.substring(0, line.indexOf('\t'));
			id = id.replace("\t", "");
		    } catch (Exception e) {
			id = line.substring(0, line.indexOf(' '));
			id = id.replace(" ", "");
		    }
		} else {
		    id = line.substring(0, line.indexOf('\t'));
		    id = id.replace("\t", "");
		}

		if (osManager.isSolaris()) {
		    /* we need to munge the BTL notation into cXtYdZ */
		    String devPath = null;
		    Scanner munge = new Scanner(id).useDelimiter(",");
		    devPath = "/dev/rdsk/c" + munge.nextInt();
		    devPath = devPath + "t" + munge.nextInt();
		    devPath = devPath + "d" + munge.nextInt();
		    devPath = devPath + "s2";
		    Logger.info("device found is " + devPath);
		    devices.add(devPath);
		} else {
		    devices.add(id);
		}
	    }
	}
    }

    /**
     * @return
     */
    private List<String> tryScanWithATA() {
	List<String> devices = new ArrayList<String>();
	// On some Linux machines -scanbus does not give any CDROM, so lets try
	// it with -scanbus dev=ATA:
	BufferedReader stdInput = null;
	try {
	    List<String> command = new ArrayList<String>();
	    command.add(StringUtils.getString(osManager.getExternalToolsPath(),
		    converterCommand));
	    command.add(Cdda2wavConstants.SCAN_BUS);
	    command.add(Cdda2wavConstants.ATA);
	    command.add(Cdda2wavConstants.NO_INFO_FILE);

	    Logger.debug((Object[]) command.toArray(new String[command.size()]));

	    setProcess(new ProcessBuilder(command).start());
	    // Icedax and cdda2wav seem to behave differently
	    if ((osManager.isLinux() && !converterCommand
		    .equals(Cdda2wavConstants.ICEDAX_COMMAND_STRING))
		    || osManager.isWindows()) {
		stdInput = new BufferedReader(new InputStreamReader(
			getProcess().getErrorStream()));
	    } else {
		stdInput = new BufferedReader(new InputStreamReader(
			getProcess().getInputStream()));
	    }

	    readATAScanOutput(devices, stdInput);

	    int code = getProcess().waitFor();
	    if (code != 0) {
		Logger.error(StringUtils.getString("Process returned code ",
			code));
		// Do not return null. Application hangs otherwise
		// return null;
	    }
	    Logger.info(StringUtils.getString("Found ", devices.size(),
		    " devices with '-scanbus dev=ATA' method"));
	} catch (IOException e) {
	    Logger.error(StringUtils.getString(
		    PROCESS_EXECUTION_CAUSED_EXCEPTION, e));
	    return null;
	} catch (InterruptedException e) {
	    Logger.error(StringUtils.getString(
		    PROCESS_EXECUTION_CAUSED_EXCEPTION, e));
	    return null;
	} finally {
	    ClosingUtils.close(stdInput);
	}
	return devices;
    }

    /**
     * @param devices
     * @param stdInput
     * @throws IOException
     */
    private void readATAScanOutput(final List<String> devices,
	    final BufferedReader stdInput) throws IOException {
	// Read the output from the command
	String s = null;
	while ((s = stdInput.readLine()) != null) {
	    if (s.contains("CD-ROM")) {
		String line = s.trim();
		String id = null;
		// Icedax and cdda2wav seem to behave differently
		if (osManager.isLinux()
			&& !converterCommand
				.equals(Cdda2wavConstants.ICEDAX_COMMAND_STRING)) {
		    // Workaround: Neither of this solutions work on all
		    // machines, but first one usually throws an exception
		    // so we know we should try the second one.
		    try {
			id = line.substring(0, line.indexOf('\t'));
			id = id.replace("\t", "");
		    } catch (Exception e) {
			id = line.substring(0, line.indexOf(' '));
			id = id.replace(" ", "");
		    }
		} else {
		    id = line.substring(0, line.indexOf('\t'));
		    id = id.replace("\t", "");
		}
		devices.add(id);
		ata = true;
	    }
	}
    }

    /**
     * @param process
     */
    private void setProcess(final Process process) {
	this.process = process;
    }

    /**
     * @return
     */
    private Process getProcess() {
	return process;
    }

    /**
     * @return if scan bus succeeded using ATA bus
     */
    public boolean isAta() {
	return ata;
    }
}
