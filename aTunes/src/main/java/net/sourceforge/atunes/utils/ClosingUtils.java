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

package net.sourceforge.atunes.utils;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.ZipFile;

/**
 * Utility methods for closing streams, sockets, ...
 */

public final class ClosingUtils {

    private ClosingUtils() {
    }

    /**
     * Closes a closable object (e.g. InputStreams and OutputStreams)
     * <p>
     * Note: This method does not throw an IOException
     * </p>
     * 
     * @param closable
     *            The closable object that should be closed (<code>null</code>
     *            is permitted)
     */
    public static void close(Closeable closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (IOException e) {
                Logger.error(e);
            }
        }
    }

    /**
     * Closes a zip file
     * <p>
     * Note: This method does not throw an IOException
     * </p>
     * 
     * @param zipFile
     *            The zip file that should be closed (<code>null</code> is
     *            permitted)
     */
    public static void close(ZipFile zipFile) {
        if (zipFile != null) {
            try {
                zipFile.close();
            } catch (IOException e) {
                Logger.error(e);
            }
        }
    }

    /**
     * Closes a server socket
     * <p>
     * Note: This method does not throw an IOException
     * </p>
     * 
     * @param socket
     *            The server socket that should be closed (<code>null</code> is
     *            permitted)
     */
    public static void close(ServerSocket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                Logger.error(e);
            }
        }
    }

    /**
     * Closes a socket
     * <p>
     * Note: This method does not throw an IOException
     * </p>
     * 
     * @param socket
     *            The socket that should be closed (<code>null</code> is
     *            permitted)
     */
    public static void close(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                Logger.error(e);
            }
        }
    }

    /**
     * Closes a XMLDecoder
     * 
     * @param decoder
     *            The XMLDecoder that should be closed (<code>null</code> is
     *            permitted)
     */
    public static void close(XMLDecoder decoder) {
        if (decoder != null) {
            decoder.close();
        }
    }

    /**
     * Closes a XMLEncoder
     * 
     * @param encoder
     *            The XMLEncoder that should be closed (<code>null</code> is
     *            permitted)
     */
    public static void close(XMLEncoder encoder) {
        if (encoder != null) {
            encoder.close();
        }
    }

}
