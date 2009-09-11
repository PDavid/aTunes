package net.sourceforge.atunes.kernel.modules.context.audioobject;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.context.ContextPanelContent;
import net.sourceforge.atunes.kernel.modules.desktop.DesktopHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.Lyrics;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.LyricsService;
import net.sourceforge.atunes.misc.ClipboardFacade;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * Lyrics information
 * @author alex
 *
 */
public class LyricsContent extends ContextPanelContent {
	
	private static final long serialVersionUID = 962229017133714396L;
	
	private JTextArea lyricsContainer;

	private JMenu addLyrics;
	
	private JMenuItem copyLyrics;
	
	private JMenuItem openLyrics;
	
	private String lyricsSourceUrl;
	
	private AudioObject audioObject;
	
	public LyricsContent() {
		super(new LyricsDataSource());
	}
	
	@Override
	protected Map<String, ?> getDataSourceParameters(AudioObject audioObject) {
		Map<String, AudioObject> parameters = new HashMap<String, AudioObject>();
		parameters.put(LyricsDataSource.INPUT_AUDIO_OBJECT, audioObject);
		this.audioObject = audioObject;
		return parameters;
	}
	
	@Override
	protected void updateContentWithDataSourceResult(Map<String, ?> result) {
		if (result.containsKey(LyricsDataSource.OUTPUT_LYRIC)) {
			Lyrics lyrics = (Lyrics)result.get(LyricsDataSource.OUTPUT_LYRIC);
			lyricsContainer.setLineWrap(false);
			lyricsContainer.setText(lyrics.getLyrics());
			lyricsContainer.setCaretPosition(0);
			
			boolean lyricsNotEmpty = lyrics != null && !lyrics.getLyrics().trim().isEmpty();
			copyLyrics.setEnabled(lyricsNotEmpty);
	        addLyrics.setEnabled(!lyricsNotEmpty);
	        lyricsSourceUrl = lyrics != null ? lyrics.getUrl() : null;
	        openLyrics.setEnabled(true);
	        if (!lyricsNotEmpty) {
	            addLyrics.removeAll();
	            for (final Entry<String, String> entry : LyricsService.getInstance().getUrlsForAddingNewLyrics(audioObject.getArtist(), audioObject.getTitle()).entrySet()) {
	                JMenuItem mi = new JMenuItem(entry.getKey());
	                mi.addActionListener(new ActionListener() {
	                    @Override
	                    public void actionPerformed(ActionEvent e) {
	                        DesktopHandler.getInstance().openURL(entry.getValue());
	                    }
	                });
	                addLyrics.add(mi);
	            }
	            addLyrics.setEnabled(addLyrics.getMenuComponentCount() > 0);
	        }

		}
	}
	
	@Override
	protected void clearContextPanelContent() {
		super.clearContextPanelContent();
		lyricsContainer.setText(null);
		copyLyrics.setEnabled(false);
		addLyrics.setEnabled(false);
		addLyrics.removeAll();
		openLyrics.setEnabled(false);
		lyricsSourceUrl = null;
		audioObject = null;
	}
	
	@Override
	protected String getContentName() {
		return LanguageTool.getString("LYRICS");
	}
	
	@Override
	protected Component getComponent() {
        lyricsContainer = new JTextArea();
        lyricsContainer.setBorder(null);
        lyricsContainer.setEditable(false);
        lyricsContainer.setWrapStyleWord(true);
        lyricsContainer.setOpaque(false);
        return lyricsContainer;
	}
	
	@Override
	protected boolean isScrollNeeded() {
		return true;
	}
	
	@Override
	protected List<Component> getOptions() {
		List<Component> options = new ArrayList<Component>();
		copyLyrics = new JMenuItem(new AbstractAction(LanguageTool.getString("COPY_TO_CLIPBOARD")) {
			
			private static final long serialVersionUID = -851267486478098295L;

			@Override
			public void actionPerformed(ActionEvent e) {
		        String sLyric = lyricsContainer.getText();
		        if (sLyric == null) {
		            sLyric = "";
		        }
		        ClipboardFacade.copyToClipboard(sLyric);
			}
		});		
		options.add(copyLyrics);
		
        addLyrics = new JMenu(LanguageTool.getString("ADD_LYRICS"));
        options.add(addLyrics);
        
        openLyrics = new JMenuItem(new AbstractAction(LanguageTool.getString("OPEN_LYRICS_SOURCE")) {
			
			private static final long serialVersionUID = 9043861642969889713L;

			@Override
			public void actionPerformed(ActionEvent e) {
		        if (lyricsSourceUrl != null && !lyricsSourceUrl.trim().isEmpty()) {
		            DesktopHandler.getInstance().openURL(lyricsSourceUrl);
		        } else {
		            if (audioObject instanceof AudioFile) {
		                ControllerProxy.getInstance().getEditTagDialogController().editFiles(Arrays.asList((AudioFile) audioObject));
		            }
		        }
			}
		});
        
        options.add(openLyrics);
        
		return options;
	}

}
