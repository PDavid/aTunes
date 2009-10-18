package net.sourceforge.atunes.kernel.modules.context.artist;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.controls.ScrollableFlowPanel;
import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.ContextPanelContent;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.DesktopUtils;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

import org.jdesktop.swingx.border.DropShadowBorder;

public class ArtistAlbumsFlowContent extends ContextPanelContent {

    private ScrollableFlowPanel coversPanel;

    public ArtistAlbumsFlowContent() {
        super(new ArtistInfoDataSource());
    }

    @Override
    protected Component getComponent() {
        coversPanel = new ScrollableFlowPanel();
        coversPanel.setOpaque(false);
        coversPanel.setLayout(new FlowLayout());
        return coversPanel;
    }

    @Override
    protected String getContentName() {
        return I18nUtils.getString("ALBUMS");
    }

    @Override
    protected Map<String, ?> getDataSourceParameters(AudioObject audioObject) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(ArtistInfoDataSource.INPUT_AUDIO_OBJECT, audioObject);
        parameters.put(ArtistInfoDataSource.INPUT_ALBUMS, true);
        return parameters;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void updateContentWithDataSourceResult(Map<String, ?> result) {
        if (result != null && result.containsKey(ArtistInfoDataSource.OUTPUT_ALBUMS)) {
            List<AlbumInfo> albums = (List<AlbumInfo>) result.get(ArtistInfoDataSource.OUTPUT_ALBUMS);
            for (AlbumInfo album : albums) {
                coversPanel.add(getLabelForAlbum(album));
            }
            coversPanel.revalidate();
            coversPanel.repaint();
            coversPanel.validate();
        }
    }

    protected void clearContextPanelContent() {
        super.clearContextPanelContent();
        coversPanel.removeAll();
    };

    /**
     * Gets the Label for album.
     * 
     * @param album
     *            the album
     * @return the label for album
     */
    JLabel getLabelForAlbum(final AlbumInfo album) {
        final JLabel coverLabel = new JLabel(album.getCover());
        coverLabel.setToolTipText(album.getTitle());
        if (album.getCover() == null) {
            coverLabel.setPreferredSize(new Dimension(Constants.CONTEXT_IMAGE_WIDTH, Constants.CONTEXT_IMAGE_HEIGHT));
            coverLabel.setBorder(BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
        } else {
            coverLabel.setBorder(new DropShadowBorder());
        }

        coverLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                coverLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                coverLabel.setCursor(Cursor.getDefaultCursor());
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                DesktopUtils.openURL(album.getUrl());
            }
        });

        return coverLabel;
    }

}
