package net.sourceforge.atunes.plugins.context;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.roarsoftware.lastfm.Event;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.DesktopUtils;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.NetworkUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

public class LastFmEventContent extends AbstractContextPanelContent {

    private JTable eventsTable;

    protected LastFmEventContent() {
        super(new LastFmEventDataSource());
    }

    @Override
    protected Component getComponent() {
        eventsTable = new JTable();
        eventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventsTable.setShowGrid(false);
        eventsTable.setRowHeight(50);
        eventsTable.setDefaultRenderer(Event.class, new SubstanceDefaultTableCellRenderer() {

            private static final long serialVersionUID = -6660398915330438358L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
                return getPanelForEvent((Event) value);
            }
        });

        final JPopupMenu menu = new JPopupMenu();
        JMenuItem item = new JMenuItem("Show in Google Maps");
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Event event = (Event) ((LastFmEventTableModel) eventsTable.getModel()).getValueAt(eventsTable.getSelectedRow(), 0);
                if (event.getVenue() != null) {
                    String url = StringUtils.getString("http://maps.google.com/maps?q=", event.getVenue().getLatitude(), ",+", event.getVenue().getLongitude(), "+(", NetworkUtils
                            .encodeString(event.getVenue().getName()), ")");
                    DesktopUtils.openURL(url);
                }
            }
        });
        menu.add(item);

        eventsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    menu.show(eventsTable, e.getX(), e.getY());
                }
            }
        });

        return eventsTable;
    }

    @Override
    protected String getContentName() {
        return "Events";
    }

    @Override
    protected Map<String, ?> getDataSourceParameters(AudioObject audioObject) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(LastFmEventDataSource.INPUT_ARTIST, audioObject.getArtist());
        return parameters;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void updateContentWithDataSourceResult(Map<String, ?> result) {
        Collection<Event> events = (Collection<Event>) result.get(LastFmEventDataSource.OUTPUT_EVENTS);
        final List<Event> eventList = new ArrayList<Event>(events);
        eventsTable.setModel(new LastFmEventTableModel(eventList));
    }

    private JPanel getPanelForEvent(Event event) {
        JPanel panel = new JPanel(new GridLayout(4, 1));
        JLabel line1 = new JLabel(event.getTitle());
        JLabel line2 = new JLabel(event.getStartDate().toString());
        JLabel line3 = new JLabel(event.getVenue() != null ? event.getVenue().getName() : "");
        JLabel line4 = new JLabel(event.getVenue() != null ? StringUtils.getString(event.getVenue().getCity(), ", ", event.getVenue().getCountry()) : "");
        panel.add(line1);
        panel.add(line2);
        panel.add(line3);
        panel.add(line4);
        GuiUtils.applyComponentOrientation(panel);
        return panel;
    }

}
