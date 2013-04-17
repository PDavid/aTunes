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

package net.sourceforge.atunes.kernel.modules.podcast;

/**
 * Contains information about progress of podcast feed entry download
 * 
 * @author alex
 * 
 */
class DownloadPodcastFeedEntryIntermediateResult {

	private int percentage;

	private long byteProgress;

	private long totalBytes;

	private boolean failed;

	/**
	 * @param percentage
	 * @param byteProgress
	 * @param totalBytes
	 * @param failed
	 */
	public DownloadPodcastFeedEntryIntermediateResult(int percentage,
			long byteProgress, long totalBytes, boolean failed) {
		this.percentage = percentage;
		this.byteProgress = byteProgress;
		this.totalBytes = totalBytes;
		this.failed = failed;
	}

	/**
	 * @return the percentage
	 */
	public int getPercentage() {
		return percentage;
	}

	/**
	 * @param percentage
	 *            the percentage to set
	 */
	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	/**
	 * @return the byteProgress
	 */
	public long getByteProgress() {
		return byteProgress;
	}

	/**
	 * @param byteProgress
	 *            the byteProgress to set
	 */
	public void setByteProgress(long byteProgress) {
		this.byteProgress = byteProgress;
	}

	/**
	 * @return the totalBytes
	 */
	public long getTotalBytes() {
		return totalBytes;
	}

	/**
	 * @param totalBytes
	 *            the totalBytes to set
	 */
	public void setTotalBytes(long totalBytes) {
		this.totalBytes = totalBytes;
	}

	/**
	 * @return the failed
	 */
	public boolean isFailed() {
		return failed;
	}

	/**
	 * @param failed
	 *            the failed to set
	 */
	public void setFailed(boolean failed) {
		this.failed = failed;
	}
}
