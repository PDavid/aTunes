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

import org.joda.time.base.BaseDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Kryo serializer for joda time
 * 
 * @author alex
 * 
 */
public class DateSerializer extends Serializer<BaseDateTime> {

	private final DateTimeFormatter fmt = DateTimeFormat
			.forPattern("yyyyMMddHHmm");

	private final DateTimeFormatter secondaryFmt = DateTimeFormat
			.forPattern("yyyyMMdd");

	@Override
	public BaseDateTime read(final Kryo kryo, final Input input,
			final Class<BaseDateTime> dateClass) {
		String dateAndTime = input.readString();
		try {
			return this.fmt.parseDateTime(dateAndTime);
		} catch (IllegalArgumentException e) {
			// Try a second read for backward compatibility
			return this.secondaryFmt.parseDateTime(dateAndTime);
		}
	}

	@Override
	public void write(final Kryo kryo, final Output output,
			final BaseDateTime date) {
		output.writeString(this.fmt.print(date));
	}
}