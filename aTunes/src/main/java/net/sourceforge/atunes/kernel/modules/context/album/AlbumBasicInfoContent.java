package net.sourceforge.atunes.kernel.modules.context.album;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.ContextPanelContent;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

import org.jdesktop.swingx.border.DropShadowBorder;

/**
 * Basic information about an album
 * @author alex
 *
 */
public class AlbumBasicInfoContent extends ContextPanelContent {

	private static final long serialVersionUID = -5538266144953409867L;
	
	private JLabel albumCoverLabel;
	private UrlLabel albumLabel;
	private UrlLabel artistLabel;
	private UrlLabel yearLabel;

	public AlbumBasicInfoContent() {
		super(new AlbumInfoDataSource());
	}
	
	@Override
	protected String getContentName() {
		return LanguageTool.getString("INFO");
	}

	@Override
	protected Map<String, ?> getDataSourceParameters(AudioObject audioObject) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(AlbumInfoDataSource.INPUT_AUDIO_OBJECT, audioObject);
		// Want image too 
		parameters.put(AlbumInfoDataSource.INPUT_BOOLEAN_IMAGE, true);
		return parameters;
	}

	@Override
	protected void updateContentWithDataSourceResult(Map<String, ?> result) {
		AudioObject audioObject = (AudioObject) result.get(AlbumInfoDataSource.OUTPUT_AUDIO_OBJECT);
		if (result.containsKey(AlbumInfoDataSource.OUTPUT_ALBUM)) {
			AlbumInfo album = (AlbumInfo) result.get(AlbumInfoDataSource.OUTPUT_ALBUM);
	        artistLabel.setText(album != null ? album.getArtist() : audioObject.getArtist(), album != null ? album.getArtistUrl() : null);
	        artistLabel.setEnabled(album != null && album.getArtistUrl() != null);
	        albumLabel.setText(album != null ? album.getTitle() : LanguageTool.getString("UNKNOWN_ALBUM"), album != null ? album.getUrl() : null);
	        albumLabel.setEnabled(album != null && album.getUrl() != null);
	        // TODO: wikipedia is opened in English
	        yearLabel.setText(album != null ? album.getYear() : "", album != null && album.getYear() != null ? StringUtils.getString("http://en.wikipedia.org/wiki/", album.getYear()) : null);
	        
	        Image image = (Image) result.get(AlbumInfoDataSource.OUTPUT_IMAGE);
	        if (image != null) {
	            ImageIcon imageIcon = ImageUtils.resize(new ImageIcon(image), Constants.ALBUM_IMAGE_SIZE, Constants.ALBUM_IMAGE_SIZE);
	            albumCoverLabel.setIcon(imageIcon);
	            albumCoverLabel.setBorder(new DropShadowBorder());
	        }
		}
	}
	
	@Override
	protected void clearContextPanelContent() {
		super.clearContextPanelContent();
		albumCoverLabel.setIcon(null);
        albumCoverLabel.setBorder(null);
		albumLabel.setText(null);
		artistLabel.setText(null);
		yearLabel.setText(null);
	}
	
	@Override
	protected Component getComponent() {
		// Create components
        albumCoverLabel = new JLabel();
        albumLabel = new UrlLabel();
        albumLabel.setHorizontalAlignment(SwingConstants.CENTER);
        albumLabel.setFont(Fonts.CONTEXT_INFORMATION_BIG_FONT);
        artistLabel = new UrlLabel();
        artistLabel.setHorizontalAlignment(SwingConstants.CENTER);
        yearLabel = new UrlLabel();
        yearLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Add components
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(15, 0, 0, 0);
        panel.add(albumCoverLabel, c);
        c.gridy = 1;
        c.insets = new Insets(5, 5, 0, 5);
        panel.add(albumLabel, c);
        c.gridy = 2;
        panel.add(artistLabel, c);
        c.gridy = 3;
        panel.add(yearLabel, c);
        
        return panel;
	}

}
