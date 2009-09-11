package net.sourceforge.atunes.kernel.modules.context.similar;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.substance.SubstanceContextImageJTable;
import net.sourceforge.atunes.kernel.modules.context.ArtistInfo;
import net.sourceforge.atunes.kernel.modules.context.ContextPanelContent;
import net.sourceforge.atunes.kernel.modules.context.SimilarArtistsInfo;
import net.sourceforge.atunes.kernel.modules.desktop.DesktopHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;

public class SimilarArtistsContent extends ContextPanelContent {
	
	private static final long serialVersionUID = 5041098100868186051L;
	private SubstanceContextImageJTable similarArtistsTable;

	public SimilarArtistsContent() {
		super(new SimilarArtistsDataSource());
	}
	
	@Override
	protected String getContentName() {
		return LanguageTool.getString("SIMILAR");
	}
	
	@Override
	protected Map<String, ?> getDataSourceParameters(AudioObject audioObject) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(SimilarArtistsDataSource.INPUT_AUDIO_OBJECT, audioObject);
		return parameters;
	}
	
	@Override
	protected void updateContentWithDataSourceResult(Map<String, ?> result) {
		if (result.containsKey(SimilarArtistsDataSource.OUTPUT_ARTISTS)) {
			similarArtistsTable.setModel(new SimilarArtistsTableModel(((SimilarArtistsInfo)result.get(SimilarArtistsDataSource.OUTPUT_ARTISTS)).getArtists()));
		}
	}
	
	@Override
	protected void clearContextPanelContent() {
		// TODO Auto-generated method stub
		super.clearContextPanelContent();
		similarArtistsTable.setModel(new SimilarArtistsTableModel(null));
	}
	
	@Override
	protected Component getComponent() {
		// Create components
        similarArtistsTable = new SubstanceContextImageJTable();
        similarArtistsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        similarArtistsTable.setShowGrid(false);
        similarArtistsTable.setDefaultRenderer(ArtistInfo.class, new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 0L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
                Color backgroundColor = super.getTableCellRendererComponent(table, value, arg2, arg3, arg4, arg5).getBackground();
                return getPanelForTableRenderer(((ArtistInfo) value).getImage(), StringUtils.getString("<html><br>", ((ArtistInfo) value).getName(), "<br>", ((ArtistInfo) value)
                        .getMatch(), "%<br>", ((ArtistInfo) value).isAvailable() ? LanguageTool.getString("AVAILABLE_IN_REPOSITORY") : "", "</html>"), backgroundColor, Constants.CONTEXT_IMAGE_WIDTH, Constants.CONTEXT_IMAGE_HEIGHT);
            }
        });
        similarArtistsTable.setColumnSelectionAllowed(false);
        similarArtistsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        similarArtistsTable.getTableHeader().setReorderingAllowed(false);

        similarArtistsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedArtist = similarArtistsTable.getSelectedRow();
                    if (selectedArtist != -1) {
                        ArtistInfo artist = ((SimilarArtistsTableModel) similarArtistsTable.getModel()).getArtist(selectedArtist);
                        DesktopHandler.getInstance().openURL(artist.getUrl());
                    }
                }
            }
        });
        
        return similarArtistsTable;
	}

}
