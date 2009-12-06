package net.sourceforge.atunes.kernel.modules.columns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

import org.commonjukebox.plugins.Plugin;
import org.commonjukebox.plugins.PluginInfo;
import org.commonjukebox.plugins.PluginListener;
import org.commonjukebox.plugins.PluginSystemException;

/**
 * Class to handle column sets and plugins
 * @author fleax
 *
 */
public class ColumnSets implements PluginListener {
	
	/**
	 * Column sets
	 */
	private static List<ColumnSet> columnSets;
	
	/**
	 * Singleton instance
	 */
	private static ColumnSets instance;
	
	public static ColumnSets getInstance() {
		if (instance == null) {
			instance = new ColumnSets();
		}
		return instance;
	}
	
	/**
	 * Column sets
	 * @return
	 */
	private static List<ColumnSet> getColumnSets() {
		if (columnSets == null) {
			columnSets = new ArrayList<ColumnSet>();
		}
		return columnSets;
	}
	
	/**
	 * Register a new column set
	 * @param columnSet
	 */
	protected static void registerColumnSet(ColumnSet columnSet) {
		getColumnSets().add(columnSet);
	}
	
	/**
	 * Store columns settings
	 */
	public static void storeColumnSettings() {
		for (ColumnSet columnSet : getColumnSets()) {
			columnSet.storeCurrentColumnSettings();
		}
	}
	
	@Override
	public void pluginActivated(PluginInfo plugin) {
		try {
			for (ColumnSet columnSet : getColumnSets()) {
				columnSet.addNewColumn((Column) plugin.getInstance());
			}
		} catch (PluginSystemException e) {
			new Logger().error(LogCategories.COLUMNS, e);
		}
	}

	@Override
	public void pluginDeactivated(PluginInfo plugin, Collection<Plugin> createdInstances) {
		// Take class of column (just the first)
		Class<?> columnClass = null;
		for (Plugin instancedColumn : createdInstances) {
			columnClass = instancedColumn.getClass();
			break;
		}
		
		for (ColumnSet columnSet : getColumnSets()) {
			columnSet.removeColumn(columnClass);
		}
	}
}
