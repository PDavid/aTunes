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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Serialization service based on Kryo serialization
 * 
 * @author alex
 * 
 * @param <T>
 */
public class KryoSerializerService {

	private KryoFactory kryoFactory;

	/**
	 * @param kryoFactory
	 */
	public void setKryoFactory(final KryoFactory kryoFactory) {
		this.kryoFactory = kryoFactory;
	}

	/**
	 * Reads an object from a file
	 * 
	 * @param filename
	 *            filename
	 * @param clazz
	 *            class of object
	 * @return The object read or null
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public Object readObjectFromFile(final String filename, final Class<?> clazz)
			throws IOException {
		Input input = null;
		Timer timer = new Timer();
		try {
			Logger.info("Reading serialized object: ", clazz.getName(),
					" from file: ", filename);
			timer.start();
			input = new Input(new FileInputStream(filename));
			return this.kryoFactory.getKryo().readObject(input, clazz);
		} catch (FileNotFoundException e) {
			Logger.info(e.getMessage());
		} catch (Exception e) {
			Logger.error(e);
		} finally {
			Logger.info(StringUtils.getString("Reading ", clazz.getName(),
					" done (", timer.stop(), " seconds)"));
			ClosingUtils.close(input);
		}
		return null;
	}

	/**
	 * Writes an object to a file
	 * 
	 * @param filename
	 * @param object
	 * @return if write was successful
	 */
	public boolean writeObjectToFile(final String filename, final Object object) {
		Output output = null;
		try {
			output = new Output(new FileOutputStream(filename));
			this.kryoFactory.getKryo().writeObject(output, object);
			output.flush();
			return true;
		} catch (IOException e) {
			Logger.error(e);
		} catch (ClassNotFoundException e) {
			Logger.error(e);
		} finally {
			ClosingUtils.close(output);
		}
		return false;
	}
}
