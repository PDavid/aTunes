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

package net.sourceforge.atunes.model;


/**
 * This class represents a version, i.e. "1.7.4" where 1 is major number, 7 is
 * minor number, and 4 is revision number.
 * 
 * Also contains a download URL for the version and a release date
 */
public class ApplicationVersion implements Comparable<ApplicationVersion> {

	/**
	 * Type of version: beta, release candidate or final
	 * 
	 * @author alex
	 * 
	 */
	public enum VersionType {
		/**
		 * In development
		 */
		BETA,

		/**
		 * Candidate to be released as final
		 */
		RELEASE_CANDIDATE,

		/**
		 * Development of version finished
		 */
		FINAL
	}

	/** Release date of version. */
	private String date;

	/** Major number. */
	private int majorNumber;

	/** Minor number. */
	private int minorNumber;

	/** Revision number. */
	private int revisionNumber;

	/** Final version? */
	private VersionType versionType = VersionType.FINAL;

	/** Name */
	private String name;

	/** Url where download this version. */
	private String downloadURL;

	/**
	 * Changes of version
	 */
	private String changes;

	/**
	 * URL to download version directly
	 */
	private String directDownloadURL;

	ApplicationVersion() {
		super();
	}

	/**
	 * @param date
	 * @param majorNumber
	 * @param minorNumber
	 * @param revisionNumber
	 * @param versionType
	 * @param name
	 * @param downloadURL
	 * @param directDownloadURL
	 * @param changes
	 */
	public ApplicationVersion(final String date, final int majorNumber,
			final int minorNumber, final int revisionNumber,
			final VersionType versionType, final String name,
			final String downloadURL, final String directDownloadURL,
			final String changes) {
		super();
		this.date = date;
		this.majorNumber = majorNumber;
		this.minorNumber = minorNumber;
		this.revisionNumber = revisionNumber;
		this.versionType = versionType;
		this.name = name;
		this.downloadURL = downloadURL;
		this.directDownloadURL = directDownloadURL;
		this.changes = changes;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ApplicationVersion) {
			return compareTo((ApplicationVersion) obj) == 0;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new StringBuilder().append(this.majorNumber).append(".")
				.append(this.minorNumber).append(".")
				.append(this.revisionNumber).append(".")
				.append(this.versionType).toString().hashCode();
	}

	/**
	 * @return
	 */
	public String getChanges() {
		return this.changes;
	}

	/**
	 * Gets release date.
	 * 
	 * @return the date
	 */
	public String getDate() {
		return this.date;
	}

	/**
	 * Gets download url.
	 * 
	 * @return the download url
	 */
	public String getDownloadURL() {
		return this.downloadURL;
	}

	/**
	 * @return direct download url
	 */
	public String getDirectDownloadURL() {
		return this.directDownloadURL;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets major number.
	 * 
	 * @return the major number
	 */
	public int getMajorNumber() {
		return this.majorNumber;
	}

	/**
	 * Gets minor number.
	 * 
	 * @return the minor number
	 */
	public int getMinorNumber() {
		return this.minorNumber;
	}

	/**
	 * Gets revision number.
	 * 
	 * @return the revision number
	 */
	public int getRevisionNumber() {
		return this.revisionNumber;
	}

	/**
	 * Returns full version in string format.
	 * 
	 * @return the version in string format
	 */
	@Override
	public String toString() {
		return new StringBuilder()
				.append(this.majorNumber)
				.append(".")
				.append(this.minorNumber)
				.append(".")
				.append(this.revisionNumber)
				.append(" ")
				.append(this.name)
				.append(" ")
				.append(this.versionType != VersionType.FINAL ? this.versionType
						: "").toString();
	}

	/**
	 * Returns short version in string format.
	 * 
	 * @return the short version in string format
	 */
	public String toShortString() {
		return new StringBuilder()
				.append(this.majorNumber)
				.append(".")
				.append(this.minorNumber)
				.append(".")
				.append(this.revisionNumber)
				.append(" ")
				.append(this.versionType != VersionType.FINAL ? this.versionType
						: "").toString();
	}

	/**
	 * Returns version type
	 * 
	 * @return VersionType
	 */
	public VersionType getVersionType() {
		return this.versionType;
	}

	@Override
	public int compareTo(final ApplicationVersion version) {
		if (version == null) {
			throw new IllegalArgumentException();
		}

		if (this.majorNumber > version.getMajorNumber()) {
			return 1;
		} else if (this.majorNumber < version.getMajorNumber()) {
			return -1;
		} else if (this.minorNumber > version.getMinorNumber()) {
			return 1;
		} else if (this.minorNumber < version.getMinorNumber()) {
			return -1;
		} else if (this.revisionNumber > version.getRevisionNumber()) {
			return 1;
		} else if (this.revisionNumber < version.getRevisionNumber()) {
			return -1;
		} else {
			return this.versionType.compareTo(version.getVersionType());
		}
	}

}
