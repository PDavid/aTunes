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

package net.sourceforge.atunes.kernel.modules.updates;

import net.sourceforge.atunes.model.ApplicationVersion;
import net.sourceforge.atunes.model.ApplicationVersion.VersionType;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Reads version data from XML
 * 
 * @author alex
 * 
 */
public class VersionXmlParser {

	private IOSManager osManager;

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * Parses XML of version
	 * 
	 * @param xml
	 * @return
	 */
	ApplicationVersion getApplicationVersionFromXml(final Document xml) {
		Element element = (Element) xml.getElementsByTagName("latest").item(0);
		String date = XMLUtils.getChildElementContent(element, "date");
		int major = Integer.parseInt(XMLUtils.getChildElementContent(element,
				"majorNumber"));
		int minor = Integer.parseInt(XMLUtils.getChildElementContent(element,
				"minorNumber"));
		int revision = Integer.parseInt(XMLUtils.getChildElementContent(
				element, "revisionNumber"));
		String url = element.getAttribute("url");

		String directDownloadURL = applyVersion(
				XMLUtils.getChildElementContent(element, getElementNameForOS()),
				major, minor, revision);

		String changes = XMLUtils.getChildElementContent(element, "changes");

		return new ApplicationVersion(date, major, minor, revision,
				VersionType.FINAL, "", url, directDownloadURL, changes);
	}

	private String applyVersion(final String url, final int major,
			final int minor, final int revision) {
		return url.replace("%VERSION%",
				StringUtils.getString(major, '.', minor, '.', revision));
	}

	/**
	 * 
	 */
	private String getElementNameForOS() {
		String elementName = null;
		if (this.osManager.isWindows()) {
			elementName = "windows";
		} else if (this.osManager.isLinux()) {
			elementName = "linux";
		} else if (this.osManager.isMacOsX()) {
			elementName = "mac";
		} else if (this.osManager.isSolaris()) {
			elementName = "solaris";
		} else {
			elementName = "default";
		}
		return elementName;
	}
}
