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

package net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.cdripper.cdda2wav.model.CDInfo;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Detects CD drives, gathers CD information and rip CD's all using cdda2wav
 * (icedax).
 */
public class Cdda2wav extends AbstractCdToWavConverter {

    private final class ReadCddaThread extends Thread {
		@Override
		public void run() {
		    BufferedReader stdInput = null;
		    try {
		        stdInput = new BufferedReader(new InputStreamReader(getProcess().getErrorStream(), "ISO8859_1"));
		        Logger.info("Trying to read cdda2wav stream");

		        String s = null;
		        int tracks = 0;
		        String totalDuration = null;
		        String id = null;
		        String album = null;
		        String artist = null;
		        String title = null;
		        List<String> durations = new ArrayList<String>();
		        List<String> titles = new ArrayList<String>();
		        List<String> artists = new ArrayList<String>();
		        List<String> composers = new ArrayList<String>();

		        boolean cddbError = false;

		        //	read the output from the command
		        while ((s = stdInput.readLine()) != null) {
		            Logger.info(StringUtils.getString("While loop: ", s));

		            // Used to detect if a CD is present. Don't know if this gets returned on
		            // all drive, so may not work as expected. But if it does, this means a CD
		            // is present and we don't have to wait until the disk info is read out. This 
		            // means we can give the "no CD" error much faster!
		            if (s.contains("bytes buffer memory requested")) {
		                cdLoaded = true;
		            }

		            // Sometimes cdda2wav gives an error message 
		            // when a data CD is inserted
		            if (s.contains("This disk has no audio tracks")) {
		                cdLoaded = false;
		            }

		            if (s.matches("Tracks:.*")) {
		                cdLoaded = true;
		                tracks = Integer.parseInt(s.substring(s.indexOf(':') + 1, s.indexOf(' ')));
		                totalDuration = s.substring(s.indexOf(' ') + 1);
		            } else if (s.matches("CDDB discid.*")) {
		                cdLoaded = true;
		                id = s.substring(s.indexOf('0'));
		            }

		            // We need to check if there was an connection error to avoid an exception
		            // In this case aTunes will behave as previously (no Artist/Album info).
		            if (s.matches(".cddb connect failed.*")) {
		                cddbError = true;
		            }

		            // Get album info (only if connection to cddb could be established)
		            if (s.matches("Album title:.*") && !cddbError) {
		                cdLoaded = true;
		                String line = s.trim();

		                // Avoid '' sequences
		                line = line.replaceAll("''", "' '");

		                StringTokenizer albumInfoTokenizer = new java.util.StringTokenizer(line, "'");
		                // The first part is not interesting, we look for the second token, thus the next line
		                if (albumInfoTokenizer.hasMoreTokens()) {
		                    albumInfoTokenizer.nextToken();
		                }
		                if (albumInfoTokenizer.hasMoreTokens()) {
		                    album = albumInfoTokenizer.nextToken();
		                }
		                String token = null;
		                if (albumInfoTokenizer.hasMoreElements()) {
		                    token = albumInfoTokenizer.nextToken();
		                }
		                // Album names can contain "'" so check if there is something left
		                StringBuilder sb = new StringBuilder(album);
		                while (albumInfoTokenizer.hasMoreElements() && token != null && !token.matches(" from ")) {
		                    sb.append(token);
		                    token = albumInfoTokenizer.nextToken();
		                }
		                album = sb.toString();
		                if (albumInfoTokenizer.hasMoreTokens()) {
		                    artist = albumInfoTokenizer.nextToken();
		                }
		                // Artist names can contain "'" so check if there is something left
		                sb = new StringBuilder(artist);
		                while (albumInfoTokenizer.hasMoreTokens()) {
		                    token = albumInfoTokenizer.nextToken();
		                    sb.append(token);
		                }
		                artist = sb.toString();
		            }

		            // Get track info (track number, title name) - Data tracks get ignored.
		            else if (s.matches("T..:.*") && !s.matches("......................data.*") && !cddbError) {
		                cdLoaded = true;
		                String duration = s.substring(12, 18).trim();
		                durations.add(duration);
		                // If connection to cddb could be established do
		                if (!cddbError) {
		                    String line = s.trim();

		                    // Avoid '' sequences
		                    line = line.replaceAll("''", "' '");

		                    StringTokenizer titleInfoTokenizer = new StringTokenizer(line, "'");
		                    // The first part is not interesting, we look for the second token, thus the next line
		                    if (titleInfoTokenizer.hasMoreElements()) {
		                        titleInfoTokenizer.nextToken();
		                    }
		                    if (titleInfoTokenizer.hasMoreElements()) {
		                        title = titleInfoTokenizer.nextToken();
		                    }
		                    String token = null;
		                    if (titleInfoTokenizer.hasMoreTokens()) {
		                        token = titleInfoTokenizer.nextToken();
		                    }
		                    // Album names can contain "'" so check if there is something left. Also, add "\" for Windows
		                    StringBuilder sb = new StringBuilder(title);
		                    while (titleInfoTokenizer.hasMoreTokens() && token != null && !token.matches(" from ")) {
		                        sb.append(token);
		                        token = titleInfoTokenizer.nextToken();
		                    }
		                    title = sb.toString();

		                    title = title != null ? title.trim() : null;

		                    title = title != null && !title.equals("") ? title.replace("\\", "\'") : null;

		                    if (title != null) {
		                        titles.add(title);
		                        //TODO add Song artist
		                        artists.add("");
		                        composers.add("");
		                    }
		                }
		            }

		            // If there is a data track do remove one track.
		            if (s.matches("......................data.*")) {
		                tracks = tracks - 1;
		            }
		        }

		        // Write data to variable cd
		        if (!cddbError) {
		            artist = artist != null ? artist.replace("\\", "\'") : null;
		            album = album != null ? album.replace("\\", "\'") : null;
		        }

		        artist = artist != null ? artist.trim() : null;
		        album = album != null ? album.trim() : null;

		        getCdInfo().setTracks(tracks);
		        getCdInfo().setDurations(durations);
		        getCdInfo().setDuration(totalDuration);
		        getCdInfo().setId(id);
		        if (album != null && !album.equals("")) {
		            getCdInfo().setAlbum(album);
		        }

		        if (artist != null && !artist.equals("")) {
		            getCdInfo().setArtist(artist);
		        }

		        getCdInfo().setTitles(titles);
		        getCdInfo().setArtists(artists);
		        getCdInfo().setComposers(composers);

		    } catch (IOException e) {
		        Logger.error(e);
		    } finally {
		        ClosingUtils.close(stdInput);
		    }
		}
	}

	// Define cdda2wav command strings
    private static final String CDDA2WAV_COMMAND_STRING = "cdda2wav";
    private static final String ICEDAX_COMMAND_STRING = "icedax";
    // private static final String CDDA2WAV_COMMAND_STRING_SOLARIS = "/usr/bin/cdda2wav.bin";
    // private static final String ICEDAX_COMMAND_STRING_SOLARIS = "";
    private static final String SCAN_BUS = "-scanbus";
    private static final String SCANDEVICES = "--devices";
    private static final String ATA = "dev=ATA";
    private static final String DEVICE = "-D";
    private static final String LIST_TRACKS = "-J";
    private static final String GUI = "-g";
    private static final String NO_INFO_FILE = "-H";
    private static final String CDDB = "--cddb=1";
    private static final String PARANOIA = "-paranoia";
    private static final String TRACKS = "-t";
    private static final String VERBOSE = "-verbose-level=summary,toc,sectors,titles";
    private static final String VERSION = "--version";
    private static final String WAVFORMAT = "-output-format=wav";

    private static String converterCommand = CDDA2WAV_COMMAND_STRING;

    private int devNumber;
    private CDInfo cdRecursive;
    private String device;
    private boolean cdLoaded;
    private boolean doNotRepeatNoCdDialog = true;
    private boolean ata = false;
    /** Use paranoia mode (better error correction but slower ripping) */
    private boolean useParanoia = false;
    private List<String> devices = new ArrayList<String>();
    /** Variable used to count number of checked drives */
    private int devCounter = 1;
    
    private IOSManager osManager;

    /**
     * <p>
     * Constructor
     * </p>
     * 
     * <p>
     * Does call the doScanBus method to probe for present CD drives, then
     * assigns the first CD device in the list to the variable device.
     * </p>
     */
    public Cdda2wav(IOSManager osManager) {
    	this.osManager = osManager;
        device = doScanBus().get(0);
    }

    /**
     * Tests if cdda2wav or icedax is present On Windows system cdda2wav is
     * assumed present.
     * 
     * @param osManager
     * @return true if either cdda2wav or icedax was found, false else.
     */
    public static boolean pTestTool(IOSManager osManager) {

        BufferedReader stdInput = null;
        BufferedReader stdInput2 = null;
        try {
            // If user adds cdda2wav while aTunes is running it will be detected even if icedax was used. 
            converterCommand = CDDA2WAV_COMMAND_STRING;
            Process p = new ProcessBuilder(StringUtils.getString(osManager.getExternalToolsPath(), converterCommand), VERSION).start();
            stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line = null;
            while ((line = stdInput.readLine()) != null) {
                // On some Linux distributions we have a symlink to icedax so detect it.
                if (line.contains("icedax")) {
                    converterCommand = ICEDAX_COMMAND_STRING;
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
                converterCommand = ICEDAX_COMMAND_STRING;
                Process icedaxCheck = new ProcessBuilder(StringUtils.getString(osManager.getExternalToolsPath(), converterCommand), VERSION).start();
                stdInput2 = new BufferedReader(new InputStreamReader(icedaxCheck.getInputStream()));

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
    public boolean cdda2wav(int track, File fileName) {
        Logger.info(StringUtils.getString("Writing wav file for track ", track, " in file ", fileName.getName()));

        BufferedReader stdInput = null;
        try {
            List<String> command = new ArrayList<String>();
            command.add(StringUtils.getString(osManager.getExternalToolsPath(), converterCommand));
            if (device != null) {
                // When -scanbus dev=ATA is used we use another syntax
                if (ata) {
                    String devATA = StringUtils.getString(ATA, ":", getDriveId());
                    command.add(devATA);
                } else {
                    command.add(DEVICE);
                    command.add(getDriveId());
                }
            }
            command.add(WAVFORMAT);
            command.add(StringUtils.getString(TRACKS, track));
            command.add(NO_INFO_FILE);
            if (useParanoia) {
                Logger.info("Using paranoia mode");
                command.add(PARANOIA);
            }
            command.add(fileName.getAbsolutePath());

            /* Check that we've got somewhere to write the track to */
            if (!fileName.getParentFile().mkdirs()) {
            	Logger.error(StringUtils.getString(fileName.getParentFile(), " not created"));
            }

            Logger.debug((Object[]) command.toArray(new String[command.size()]));

            setProcess(new ProcessBuilder(command).start());
            stdInput = new BufferedReader(new InputStreamReader(getProcess().getErrorStream()));
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                if (getListener() != null && s.matches(".*%.*")) {
                    int pos = s.indexOf('%');
                    final int percent = Integer.parseInt(s.substring(pos - 3, pos).trim());
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
                Logger.error(StringUtils.getString("Process returned code ", code));
                return false;
            }

            Logger.info("Wav file ok!!");
            return true;
        } catch (IOException e) {
            Logger.error(StringUtils.getString("Process execution caused exception ", e));
            return false;
        } catch (InterruptedException e) {
            Logger.error(StringUtils.getString("Process execution caused exception ", e));
            return false;
        } finally {
            ClosingUtils.close(stdInput);
        }
    }

    /**
     * Enable paranoia mode for ripping
     */
    @Override
    public boolean cdda2wav(int track, File fileName, boolean useParanoia) {
        this.useParanoia = useParanoia;
        return cdda2wav(track, fileName);
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
    private List<String> doScanBus() {
        Logger.info(StringUtils.getString("Scanning bus using ", converterCommand));

        // When icedax is used, try the --devices method, otherwise leave it as the maintainer 
        // of cdda2wav does not seem to like this method.
        if (converterCommand.equals(ICEDAX_COMMAND_STRING)) {
            BufferedReader stdInput = null;
            try {
                List<String> command = new ArrayList<String>();
                command.add(StringUtils.getString(osManager.getExternalToolsPath(), converterCommand));
                command.add(SCANDEVICES);
                command.add(NO_INFO_FILE);

                Logger.debug((Object[]) command.toArray(new String[command.size()]));

                setProcess(new ProcessBuilder(command).start());
                stdInput = new BufferedReader(new InputStreamReader(getProcess().getInputStream()));

                // read the output from the command
                String s = null;
                // Search for the dev='...' lines to know what the OS calls the CD-Rom devices
                while ((s = stdInput.readLine()) != null) {
                    if (s.contains("dev=") && !s.contains("dev=help")) {
                        String line = s.trim();
                        java.util.StringTokenizer getDeviceOSName = new java.util.StringTokenizer(line, "'");
                        // The first part is not interesting, we look for the second token, thus the next line
                        getDeviceOSName.nextToken();
                        String id = getDeviceOSName.nextToken();
                        id = id.replace("\t", "");
                        // Write found device to list
                        devNumber = devNumber + 1;
                        devices.add(id);
                    }
                }

                int code = getProcess().waitFor();
                if (code != 0) {
                    Logger.error(StringUtils.getString("Process returned code ", code));
                    // Do not return null. Application hangs otherwise
                    // return null;
                }

                Logger.info(StringUtils.getString("Found ", devices.size(), " devices with --device method"));
            } catch (IOException e) {
                Logger.error(StringUtils.getString("Process execution caused exception ", e));
                return null;
            } catch (InterruptedException e) {
                Logger.error(StringUtils.getString("Process execution caused exception ", e));
                return null;
            } finally {
                ClosingUtils.close(stdInput);
            }
        }
        // Regular cdda2wav command
        if (devices.size() == 0) {
            BufferedReader stdInput = null;
            try {
                List<String> command = new ArrayList<String>();
                command.add(StringUtils.getString(osManager.getExternalToolsPath(), converterCommand));
                command.add(SCAN_BUS);
                command.add(NO_INFO_FILE);

                Logger.debug((Object[]) command.toArray(new String[command.size()]));

                setProcess(new ProcessBuilder(command).start());
                // Icedax and cdda2wav seem to behave differently
                if ((osManager.isLinux() && !converterCommand.equals(ICEDAX_COMMAND_STRING)) || osManager.isWindows()) {
                    stdInput = new BufferedReader(new InputStreamReader(getProcess().getErrorStream()));
                } else {
                    stdInput = new BufferedReader(new InputStreamReader(getProcess().getInputStream()));
                }

                // read the output from the command
                String s = null;
                devNumber = 0;
                while ((s = stdInput.readLine()) != null) {
                    if (s.contains("CD-ROM")) {
                        String line = s.trim();
                        String id = null;
                        // Icedax and cdda2wav seem to behave differently
                        if ((osManager.isLinux() && !converterCommand.equals(ICEDAX_COMMAND_STRING)) || osManager.isWindows()) {
                            // Workaround: Neither of this solutions work on all machines, but first one usually throws an exception
                            // so we know we should try the second one.
                            try {
                                id = line.substring(0, line.indexOf("\t"));
                                id = id.replace("\t", "");
                            } catch (Exception e) {
                                id = line.substring(0, line.indexOf(' '));
                                id = id.replace(" ", "");
                            }
                        } else {
                            id = line.substring(0, line.indexOf("\t"));
                            id = id.replace("\t", "");
                        }
                        devNumber = devNumber + 1;

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

                int code = getProcess().waitFor();
                if (code != 0) {
                    Logger.error(StringUtils.getString("Process returned code ", code));
                    // Do not return null. Application hangs otherwise
                    // return null;
                }
                Logger.info(StringUtils.getString("Found ", devices.size(), " devices with scanbus method"));

            } catch (IOException e) {
                Logger.error(StringUtils.getString("Process execution caused exception: ", e.getMessage()));
                Logger.error(e);
            } catch (InterruptedException e) {
                Logger.error(StringUtils.getString("Process execution caused exception: ", e.getMessage()));
                Logger.error(e);
			} finally {
                ClosingUtils.close(stdInput);
            }
        }
        // On some Linux machines -scanbus does not give any CDROM, so lets try it with -scanbus dev=ATA:
        if (devices.size() == 0) {
            BufferedReader stdInput = null;
            try {
                List<String> command = new ArrayList<String>();
                command.add(StringUtils.getString(osManager.getExternalToolsPath(), converterCommand));
                command.add(SCAN_BUS);
                command.add(ATA);
                command.add(NO_INFO_FILE);

                Logger.debug((Object[]) command.toArray(new String[command.size()]));

                setProcess(new ProcessBuilder(command).start());
                // Icedax and cdda2wav seem to behave differently
                if ((osManager.isLinux() && !converterCommand.equals(ICEDAX_COMMAND_STRING)) || osManager.isWindows()) {
                    stdInput = new BufferedReader(new InputStreamReader(getProcess().getErrorStream()));
                } else {
                    stdInput = new BufferedReader(new InputStreamReader(getProcess().getInputStream()));
                }

                // Read the output from the command
                String s = null;
                devNumber = 0;
                while ((s = stdInput.readLine()) != null) {
                    if (s.contains("CD-ROM")) {
                        String line = s.trim();
                        String id = null;
                        // Icedax and cdda2wav seem to behave differently
                        if (osManager.isLinux() && !converterCommand.equals(ICEDAX_COMMAND_STRING)) {
                            // Workaround: Neither of this solutions work on all machines, but first one usually throws an exception
                            // so we know we should try the second one.
                            try {
                                id = line.substring(0, line.indexOf("\t"));
                                id = id.replace("\t", "");
                            } catch (Exception e) {
                                id = line.substring(0, line.indexOf(' '));
                                id = id.replace(" ", "");
                            }
                        } else {
                            id = line.substring(0, line.indexOf("\t"));
                            id = id.replace("\t", "");
                        }
                        devNumber = devNumber + 1;
                        devices.add(id);
                        ata = true;
                    }
                }

                int code = getProcess().waitFor();
                if (code != 0) {
                    Logger.error(StringUtils.getString("Process returned code ", code));
                    // Do not return null. Application hangs otherwise
                    // return null;
                }
                Logger.info(StringUtils.getString("Found ", devices.size(), " devices with '-scanbus dev=ATA' method"));
                // This is needed to avoid hanging. devNumber is checked further and exits if it equals to zero.
                // Please always check if devNumber is null and exit/give notification if this is the case.
                if (devices.size() == 0) {
                    devices.add(null);
                }
                return devices;
            } catch (IOException e) {
                Logger.error(StringUtils.getString("Process execution caused exception ", e));
                return null;
            } catch (InterruptedException e) {
                Logger.error(StringUtils.getString("Process execution caused exception ", e));
                return null;
            } finally {
                ClosingUtils.close(stdInput);
            }
        }

        return devices;
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
        cdRecursive = null;
        // If no devices detected do exit
        if (devNumber == 0) {
            return null;
        }

        try {
            // Prepare cdda2wav commands and execute
            List<String> command = new ArrayList<String>();
            command.add(StringUtils.getString(osManager.getExternalToolsPath(), converterCommand));

            if (device != null) {
                // If -scanbus dev=ATA method finds something, we need to use a different syntax
                if (ata) {
                    String devATA = StringUtils.getString(ATA, ":", getDriveId());
                    command.add(devATA);
                } else {
                    command.add(DEVICE);
                    command.add(getDriveId());
                }
            }
            command.add(LIST_TRACKS);
            command.add(VERBOSE);
            command.add(GUI);
            command.add(NO_INFO_FILE);
            command.add(CDDB);

            Logger.debug((Object[]) command.toArray(new String[command.size()]));

            setProcess(new ProcessBuilder(command).start());
            cdLoaded = false;

            Thread readCdda = new ReadCddaThread();

            // Here we control the thread. The join() is needed in case there is no cd to
            // avoid hanging. Increase join() waiting time if "no CD" errors are occuring 
            // on slow drives. The thread is interupted to avoid a hang when no CD is present
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

            // Check if we have either a data CD or no CD inserted at all
            // If this is the case we must check the other devices
            try {
                if (getCdInfo().getTracks() == 0 || cdLoaded == false) {
                    devCounter = devCounter + 1;
                    // Go to next drive
                    devices.remove(0);
                    //if there is another drive, try it
                    if (getDriveId() != null && devCounter <= devNumber) {
                        cdRecursive = retrieveDiscInformation();
                    }
                }
            } catch (Exception e) {
                Logger.error(e);
            }

            // If the recursive function had some result, assign it to cd
            if (cdRecursive != null) {
                setCdInfo(cdRecursive);
            }
            // If no tracks are found, assuming no CD inserted.
            if (cdLoaded == false || getCdInfo().getTracks() == 0) {
                // Only print no CD dialog once
                if (doNotRepeatNoCdDialog == true) {
                    notifyNoCd();
                }
                doNotRepeatNoCdDialog = false;
                return null;
            }

            //  The following is commented because it always returns an error under Windows.

            //	int code = getProcess().waitFor();

            //	if (code != 0) {
            //		Logger.error("Process returned code " + code);
            //		return null;
            //	}
            Logger.info(StringUtils.getString("CD info: ", getCdInfo()));
            return getCdInfo();

        } catch (IOException e) {
            Logger.error(e);
            return null;
        } catch (InterruptedException e) {
            Logger.error(e);
            return null;
        }
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
