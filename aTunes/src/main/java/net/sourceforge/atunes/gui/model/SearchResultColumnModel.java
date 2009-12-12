package net.sourceforge.atunes.gui.model;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.kernel.modules.columns.SearchResultsColumnSet;

public class SearchResultColumnModel extends CommonColumnModel {
	
    private static final long serialVersionUID = -2211160302611944001L;

    /**
     * Instantiates a new play list column model.
     * 
     * @param playList
     *            the play list
     */
    public SearchResultColumnModel(JTable table) {
        super(table, SearchResultsColumnSet.getInstance());
        enableColumnChange(true);
    }

    @Override
    protected void reapplyFilter() {
    	// Nothing to do    	
    }    
    
    @Override
    public void addColumn(TableColumn aColumn) {
        super.addColumn(aColumn);
        updateColumnSettings(aColumn);
    }


}
