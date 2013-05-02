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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ITreeGenerator;
import net.sourceforge.atunes.model.ITreeGeneratorFactory;
import net.sourceforge.atunes.model.ViewMode;

/**
 * Returns tree generators
 * 
 * @author alex
 * 
 */
public class TreeGeneratorFactory implements ITreeGeneratorFactory {

	private IBeanFactory beanFactory;

	private final Map<ViewMode, Class<? extends ITreeGenerator>> generators;

	/**
	 * Default constructor
	 */
	public TreeGeneratorFactory() {
		this.generators = new HashMap<ViewMode, Class<? extends ITreeGenerator>>();
		this.generators.put(ViewMode.ARTIST, ArtistTreeGenerator.class);
		this.generators.put(ViewMode.ALBUM, AlbumTreeGenerator.class);
		this.generators.put(ViewMode.GENRE, GenreTreeGenerator.class);
		this.generators.put(ViewMode.YEAR, YearTreeGenerator.class);
		this.generators.put(ViewMode.FOLDER, FolderTreeGenerator.class);
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public ITreeGenerator getTreeGenerator(final ViewMode viewMode) {
		return this.beanFactory.getBean(this.generators.get(viewMode));
	}
}
