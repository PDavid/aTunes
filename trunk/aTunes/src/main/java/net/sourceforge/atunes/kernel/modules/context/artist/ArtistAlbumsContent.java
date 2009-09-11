package net.sourceforge.atunes.kernel.modules.context.artist;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.substance.SubstanceContextImageJTable;
import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.ContextPanelContent;
import net.sourceforge.atunes.kernel.modules.desktop.DesktopHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;

/**
 * Albums of an artist
 * @author alex
 *
 */
public class ArtistAlbumsContent extends ContextPanelContent {

	private static final long serialVersionUID = -5538266144953409867L;
	private SubstanceContextImageJTable albumsTable;
	
	public ArtistAlbumsContent() {
		super(new ArtistInfoDataSource());
	}
	
	@Override
	protected String getContentName() {
		return LanguageTool.getString("ALBUMS");
	}

	@Override
	protected Map<String, ?> getDataSourceParameters(AudioObject audioObject) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(ArtistInfoDataSource.INPUT_AUDIO_OBJECT, audioObject);
		parameters.put(ArtistInfoDataSource.INPUT_ALBUMS, true);
		return parameters;
	}

	@Override
	protected void updateContentWithDataSourceResult(Map<String, ?> result) {
		if (result.containsKey(ArtistInfoDataSource.OUTPUT_ALBUMS)) {
	        albumsTable.setModel(new ContextAlbumsTableModel((List<AlbumInfo>) result.get(ArtistInfoDataSource.OUTPUT_ALBUMS)));
		}
	}
	
	@Override
	protected void clearContextPanelContent() {
		super.clearContextPanelContent();
		albumsTable.setModel(new ContextAlbumsTableModel(null));
	}
	
	@Override
	protected Component getComponent() {
		// Create components
        albumsTable = new SubstanceContextImageJTable();
        albumsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        albumsTable.setShowGrid(false);
        albumsTable.getTableHeader().setReorderingAllowed(false);
        albumsTable.getTableHeader().setResizingAllowed(false);
        albumsTable.setDefaultRenderer(AlbumInfo.class, new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 620892562731682118L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
                Color backgroundColor = super.getTableCellRendererComponent(table, value, arg2, arg3, arg4, arg5).getBackground();
                return getPanelForTableRenderer(((AlbumInfo) value).getCover(), StringUtils.getString("<html>", ((AlbumInfo) value).getTitle(), "</html>"), backgroundColor,
                        Constants.CONTEXT_IMAGE_WIDTH, Constants.CONTEXT_IMAGE_HEIGHT);
            }
        });

        albumsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedAlbum = albumsTable.getSelectedRow();
                    if (selectedAlbum != -1) {
                        AlbumInfo album = ((ContextAlbumsTableModel) albumsTable.getModel()).getAlbum(selectedAlbum);
                        DesktopHandler.getInstance().openURL(album.getUrl());
                    }
                }
            }
        });
        
        return albumsTable;
	}
	
}
