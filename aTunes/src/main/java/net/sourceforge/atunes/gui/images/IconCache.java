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

package net.sourceforge.atunes.gui.images;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.IIconCache;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.sanselan.ImageWriteException;

/**
 * Stores icons generated in memory and disk
 * 
 * @author alex
 * 
 */
public class IconCache implements IIconCache {

	private IOSManager osManager;

	private Map<String, ImageIcon> iconsInMemory;

	private String iconsFolderName;

	/**
	 * @param iconsFolderName
	 */
	public void setIconsFolderName(String iconsFolderName) {
		this.iconsFolderName = iconsFolderName;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * Initializes cache
	 */
	public void initialize() {
		File iconsFolder = new File(StringUtils.getString(
				osManager.getUserConfigFolder(), osManager.getFileSeparator(),
				iconsFolderName, osManager.getFileSeparator()));
		if (!iconsFolder.exists()) {
			iconsFolder.mkdir();
		}
		iconsInMemory = new HashMap<String, ImageIcon>();
	}

	/**
	 * Returns icon for given color
	 * 
	 * @param icon
	 * @param color
	 * @return
	 */
	@Override
	public ImageIcon readIcon(IIconFactory icon, Color color) {
		String iconFileName = getIconFileName(icon, color);
		if (iconsInMemory.containsKey(iconFileName)) {
			return iconsInMemory.get(iconFileName);
		} else {
			File iconFile = new File(getIconFileName(icon, color));
			if (iconFile.exists()) {
				try {
					BufferedImage bi = ImageIO.read(iconFile);
					// Check if there is any error reading icon file, then
					// return null to force create icon
					if (bi != null) {
						ImageIcon imageIcon = new ImageIcon(bi);
						iconsInMemory.put(iconFileName, imageIcon);
						return imageIcon;
					}
				} catch (IOException e) {
					Logger.error(e);
				}
			}
		}
		return null;
	}

	/**
	 * Stores icon
	 * 
	 * @param icon
	 * @param color
	 * @param image
	 */
	@Override
	public void storeIcon(IIconFactory icon, Color color, ImageIcon image) {
		try {
			iconsInMemory.put(getIconFileName(icon, color), image);
			ImageUtils.writeImageToFile(image.getImage(),
					getIconFileName(icon, color));
		} catch (IOException e) {
			Logger.error(e);
		} catch (ImageWriteException e) {
			Logger.error(e);
		}
	}

	/**
	 * Name of file containing icon for given color
	 * 
	 * @param icon
	 * @param color
	 * @return
	 */
	private String getIconFileName(IIconFactory icon, Color color) {
		return StringUtils.getString(osManager.getUserConfigFolder(),
				osManager.getFileSeparator(), iconsFolderName,
				osManager.getFileSeparator(), icon.getClass().getName(), "_",
				color.toString(), ".png");
	}
}
