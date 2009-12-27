package net.sourceforge.atunes.kernel.controllers.filter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.views.panels.ToolBarFilterPanel;
import net.sourceforge.atunes.kernel.controllers.model.SimpleController;
import net.sourceforge.atunes.kernel.modules.filter.Filter;
import net.sourceforge.atunes.kernel.modules.filter.FilterHandler;

public class ToolBarFilterController extends SimpleController<ToolBarFilterPanel> {

	/**
	 * Group of controls (filters)
	 */
	private ButtonGroup group;
	
	/**
	 * Filters and UI controls
	 */
	private Map<String, JRadioButtonMenuItem> filters;
	
	public ToolBarFilterController(ToolBarFilterPanel panel) {
		super(panel);
		addBindings();
		group = new ButtonGroup();
		filters = new HashMap<String, JRadioButtonMenuItem>();
	}
	
	@Override
	protected void addBindings() {
        // Add listeners
        getComponentControlled().getFilterTextField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(final KeyEvent e) {
                super.keyTyped(e);
                // Search as user type
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                       	applyFilter(getFilter());
                    }
                });
            }
        });
	}

	@Override
	protected void addStateBindings() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void notifyReload() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Adds a new filter to controls
	 * @param name
	 * @param filterListener
	 */
	public void addFilter(final Filter filter) {
		JRadioButtonMenuItem radioButton = new JRadioButtonMenuItem(filter.getDescription());
		radioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Remove previous filter
				applyFilter(null);
				
				// Set selected filter and apply
				FilterHandler.getInstance().setSelectedFilter(filter.getName());
				applyFilter(getFilter());
			}
		});
		filters.put(filter.getName(), radioButton);
		group.add(radioButton);
		getComponentControlled().getFilterButton().add(radioButton);
	}
	
	/**
	 * Removes a filter
	 * @param name
	 */
	public void removeFilter(String name) {
		JRadioButtonMenuItem radioButton = filters.get(name);
		filters.remove(name);
		group.remove(radioButton);
		getComponentControlled().getFilterButton().remove(radioButton);
	}
	
	/**
	 * Sets filter selected
	 * @param filterName
	 */
	public void setSelectedFilter(String filterName) {
		this.filters.get(filterName).setSelected(true);
	}
	
	/**
	 * Applies filter by calling FilterHandler
	 * @param filter
	 */
	private void applyFilter(String filter) {
		FilterHandler.getInstance().applyFilter(filter);
		
	}
	
	/**
	 * Returns filter
	 * @return
	 */
	public String getFilter() {
		String filter = getComponentControlled().getFilterTextField().getText();
		return filter.trim().equals("") ? null : filter;
	}
	
	/**
	 * Sets filter enabled
	 * @param name
	 * @param enabled
	 */
	public void setFilterEnabled(String name, boolean enabled) {
		JRadioButtonMenuItem filter = this.filters.get(name);
		// Filter can be null if filters have not been added yet
		if (filter != null) {
			filter.setEnabled(enabled);
		}
	}

}
