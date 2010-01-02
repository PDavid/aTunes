package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.gui.model.NavigationTableColumnModel;
import net.sourceforge.atunes.kernel.modules.columns.ColumnBean;
import net.sourceforge.atunes.kernel.modules.columns.ColumnSet;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;

/**
 * Custom column set to be used by navigation views that use custom columns
 * @author fleax
 *
 */
public abstract class CustomNavigatorColumnSet extends ColumnSet {
	
	/**
	 * Name of column set. Used to store and retrieve settings 
	 */
	private String columnSetName; 
	
	/**
	 * Default constructor
	 */
	public CustomNavigatorColumnSet(String columnSetName) {
		super();
		this.columnSetName = columnSetName;
	}
	
	@Override
	protected void setColumnsConfiguration(Map<String, ColumnBean> columnsConfiguration) {
		Map<String, Map<String, ColumnBean>> configuration = ApplicationState.getInstance().getCustomNavigatorColumns();
		if (configuration == null) {
			configuration = new HashMap<String, Map<String,ColumnBean>>();
			ApplicationState.getInstance().setCustomNavigatorColumns(configuration);
		}
		configuration.put(this.columnSetName, columnsConfiguration);		
	}
	
	@Override
	protected Map<String, ColumnBean> getColumnsConfiguration() {
		Map<String, Map<String, ColumnBean>> configuration = ApplicationState.getInstance().getCustomNavigatorColumns();
		if (configuration != null) {
			return configuration.get(this.columnSetName);
		}
		return null;
	}
	
	@Override
	protected void refreshColumns() {
		((NavigationTableColumnModel) GuiHandler.getInstance().getNavigationTablePanel().getNavigationTable().getColumnModel()).arrangeColumns(false);
	}

}
