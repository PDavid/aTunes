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

package net.sourceforge.atunes.kernel.modules.context.audioobject;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextPane;
import javax.swing.text.StyleConstants;

import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILyrics;
import net.sourceforge.atunes.model.ILyricsService;
import net.sourceforge.atunes.model.ITagHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.ClipboardFacade;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Lyrics information
 * 
 * @author alex
 * 
 */
public class LyricsContent extends
		AbstractContextPanelContent<LyricsDataSource> {

	private JTextPane lyricsContainer;

	private final JMenu addLyrics;

	private final JMenuItem copyLyrics;

	private final JMenuItem openLyrics;

	private String lyricsSourceUrl;

	private IAudioObject audioObject;

	private ILyricsService lyricsService;

	private ClipboardFacade clipboard;

	private IUnknownObjectChecker unknownObjectChecker;

	private ITagHandler tagHandler;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param controlsBuilder
	 */
	@Override
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param tagHandler
	 */
	public void setTagHandler(final ITagHandler tagHandler) {
		this.tagHandler = tagHandler;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * Constructor
	 */
	public LyricsContent() {
		this.copyLyrics = new JMenuItem(new AbstractAction(
				I18nUtils.getString("COPY_TO_CLIPBOARD")) {

			private static final long serialVersionUID = -851267486478098295L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				String sLyric = LyricsContent.this.lyricsContainer.getText();
				if (sLyric == null) {
					sLyric = "";
				}
				LyricsContent.this.clipboard.copyToClipboard(sLyric);
			}
		});
		this.addLyrics = new JMenu(I18nUtils.getString("ADD_LYRICS"));
		this.openLyrics = new JMenuItem(new AbstractAction(
				I18nUtils.getString("OPEN_LYRICS_SOURCE")) {

			private static final long serialVersionUID = 9043861642969889713L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (LyricsContent.this.lyricsSourceUrl != null
						&& !LyricsContent.this.lyricsSourceUrl.trim().isEmpty()) {
					getDesktop().openURL(LyricsContent.this.lyricsSourceUrl);
				} else {
					if (LyricsContent.this.audioObject instanceof ILocalAudioObject) {
						LyricsContent.this.tagHandler.editFiles(Arrays
								.asList((ILocalAudioObject) LyricsContent.this.audioObject));
					}
				}
			}
		});
	}

	@Override
	public void updateContentFromDataSource(final LyricsDataSource source) {
		this.audioObject = source.getAudioObject();
		ILyrics lyrics = source.getLyrics();
		this.lyricsContainer
				.setText(lyrics != null ? lyrics.getLyrics() : null);
		this.lyricsContainer.setCaretPosition(0);

		boolean lyricsNotEmpty = lyrics != null
				&& !lyrics.getLyrics().trim().isEmpty();
		this.copyLyrics.setEnabled(lyricsNotEmpty);
		this.addLyrics.setEnabled(!lyricsNotEmpty);
		this.lyricsSourceUrl = lyrics != null ? lyrics.getUrl() : null;
		this.openLyrics.setEnabled(lyricsNotEmpty);
		if (!lyricsNotEmpty) {
			this.addLyrics.removeAll();
			if (this.audioObject != null) {
				// Can be null due to race condition when clearing context panel
				for (final Entry<String, String> entry : getUrlsForAddingNewLyrics()) {
					JMenuItem mi = new JMenuItem(entry.getKey());
					mi.addActionListener(new OpenUrlActionListener(entry,
							getDesktop()));
					this.addLyrics.add(mi);
				}
			}
			this.addLyrics
					.setEnabled(this.addLyrics.getMenuComponentCount() > 0);
		}
	}

	/**
	 * @return
	 */
	private Set<Entry<String, String>> getUrlsForAddingNewLyrics() {
		return this.lyricsService.getUrlsForAddingNewLyrics(
				this.audioObject.getArtist(this.unknownObjectChecker),
				this.audioObject.getTitle()).entrySet();
	}

	@Override
	public void clearContextPanelContent() {
		super.clearContextPanelContent();
		this.lyricsContainer.setText(null);
		this.copyLyrics.setEnabled(false);
		this.addLyrics.setEnabled(false);
		this.addLyrics.removeAll();
		this.openLyrics.setEnabled(false);
		this.lyricsSourceUrl = null;
		this.audioObject = null;
	}

	@Override
	public String getContentName() {
		return I18nUtils.getString("LYRICS");
	}

	@Override
	public Component getComponent() {
		this.lyricsContainer = this.controlsBuilder
				.createTextPane(StyleConstants.ALIGN_CENTER);
		this.lyricsContainer.setBorder(null);
		this.lyricsContainer.setEditable(false);
		this.lyricsContainer.setOpaque(false);
		return this.lyricsContainer;
	}

	@Override
	public List<Component> getOptions() {
		List<Component> options = new ArrayList<Component>();
		options.add(this.copyLyrics);
		options.add(this.addLyrics);
		options.add(this.openLyrics);
		return options;
	}

	/**
	 * @param lyricsService
	 */
	public void setLyricsService(final ILyricsService lyricsService) {
		this.lyricsService = lyricsService;
	}

	/**
	 * @param clipboard
	 */
	public void setClipboard(final ClipboardFacade clipboard) {
		this.clipboard = clipboard;
	}
}
