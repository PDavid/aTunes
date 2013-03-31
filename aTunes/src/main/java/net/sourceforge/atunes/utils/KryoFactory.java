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

import java.io.File;
import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

import com.esotericsoftware.kryo.Kryo;

/**
 * A factory of Kryo objects
 * 
 * @author alex
 * 
 */
public class KryoFactory {

	private List<String> classes;

	/**
	 * @param classes
	 */
	public void setClasses(final List<String> classes) {
		this.classes = classes;
	}

	/**
	 * Returns a kryo instance for serialization
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 */
	public Kryo getKryo() throws ClassNotFoundException {
		// Kryo is not thread safe so return a new instance in each call
		Kryo kryo = new Kryo();
		kryo.register(DateMidnight.class, new DateSerializer());
		kryo.register(DateTime.class, new DateSerializer());
		kryo.register(File.class, new FileSerializer());
		kryo.register(String.class, new StringInternSerializer());

		for (String clazz : this.classes) {
			kryo.register(Class.forName(clazz));
		}

		return kryo;
	}
}
