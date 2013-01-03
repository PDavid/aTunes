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

import java.util.Map;

public class AbstractStateUIMock implements IStateUI {

	@Override
	public FontSettings getFontSettings() {
		return null;
	}

	@Override
	public Class<? extends IFrame> getFrameClass() {
		return null;
	}

	@Override
	public IFrameState getFrameState(final Class<? extends IFrame> frame) {
		return null;
	}

	@Override
	public String getFullScreenBackground() {
		return null;
	}

	@Override
	public LookAndFeelBean getLookAndFeel() {
		return null;
	}

	@Override
	public int getOsdDuration() {
		return 0;
	}

	@Override
	public int getOsdHorizontalAlignment() {
		return 0;
	}

	@Override
	public int getOsdVerticalAlignment() {
		return 0;
	}

	@Override
	public int getOsdWidth() {
		return 0;
	}

	@Override
	public Map<String, ColumnBean> getSearchResultsColumns() {
		return null;
	}

	@Override
	public IColorBean getTrayPlayerIconsColor() {
		return null;
	}

	@Override
	public boolean isShowAdvancedPlayerControls() {
		return false;
	}

	@Override
	public boolean isShowOSD() {
		return false;
	}

	@Override
	public boolean isShowPlayerControlsOnTop() {
		return false;
	}

	@Override
	public boolean isShowStatusBar() {
		return false;
	}

	@Override
	public boolean isShowSystemTray() {
		return false;
	}

	@Override
	public boolean isShowTrayPlayer() {
		return false;
	}

	@Override
	public void setFontSettings(final FontSettings fontSettings) {
	}

	@Override
	public void setFrameClass(final Class<? extends IFrame> frameClass) {
	}

	@Override
	public void setFrameState(final Class<? extends IFrame> frame,
			final IFrameState fs) {
	}

	@Override
	public void setFullScreenBackground(final String fullScreenBackground) {
	}

	@Override
	public void setLookAndFeel(final LookAndFeelBean lookAndFeel) {
	}

	@Override
	public void setOsdDuration(final int osdDuration) {
	}

	@Override
	public void setOsdHorizontalAlignment(final int osdHorizontalAlignment) {
	}

	@Override
	public void setOsdVerticalAlignment(final int osdVerticalAlignment) {
	}

	@Override
	public void setOsdWidth(final int osdWidth) {
	}

	@Override
	public void setSearchResultsColumns(
			final Map<String, ColumnBean> searchResultsColumns) {
	}

	@Override
	public void setShowAdvancedPlayerControls(final boolean show) {
	}

	@Override
	public void setShowOSD(final boolean showOSD) {
	}

	@Override
	public void setShowPlayerControlsOnTop(final boolean onTop) {
	}

	@Override
	public void setShowStatusBar(final boolean showStatusBar) {
	}

	@Override
	public void setShowSystemTray(final boolean showSystemTray) {
	}

	@Override
	public void setShowTrayPlayer(final boolean showTrayPlayer) {
	}

	@Override
	public void setTrayPlayerIconsColor(final IColorBean color) {
	}

	@Override
	public IFrameSize getFrameSize() {
		return null;
	}

	@Override
	public void setFrameSize(final IFrameSize frameSize) {
	}

	@Override
	public IFramePosition getFramePosition() {
		return null;
	}

	@Override
	public void setFramePosition(final IFramePosition framePosition) {
	}

	@Override
	public Map<String, String> describeState() {
		return null;
	}
}
