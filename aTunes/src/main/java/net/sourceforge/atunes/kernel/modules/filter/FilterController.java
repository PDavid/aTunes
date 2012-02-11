/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.kernel.modules.filter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.views.panels.FilterPanel;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.IFilter;
import net.sourceforge.atunes.model.IFilterHandler;
import net.sourceforge.atunes.model.IFilterPanel;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public class FilterController extends AbstractSimpleController<FilterPanel> {

	private FilterTextFieldDocumentListener listener;
	
	boolean filterApplied = false;
	
    private IFilterHandler filterHandler;
    
    /**
     * Filter to apply when user types in filter panel managed by this controller
     */
    private IFilter filter;

    /**
     * @param panel
     * @param state
     */
    public FilterController(IFilterPanel panel, IState state) {
        super((FilterPanel)panel.getSwingComponent(), state);
    }
    
    /**
     * Initializes controller
     */
    public void initialize() {
        this.listener = new FilterTextFieldDocumentListener(this);
        addBindings();
    }
    
    /**
     * Set filter to apply
     * @param filter
     */
    public void setFilter(IFilter filter) {
		this.filter = filter;
	}
    
    /**
     * @param filterHandler
     */
    public void setFilterHandler(IFilterHandler filterHandler) {
		this.filterHandler = filterHandler;
	}

    @Override
	public void addBindings() {
    	getComponentControlled().getFilterTextField().addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				if (!filterApplied) {
					getComponentControlled().getFilterTextField().getDocument().removeDocumentListener(listener);
					getComponentControlled().getFilterTextField().setText(StringUtils.getString(I18nUtils.getString("FILTER"), "..."));
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if (!filterApplied) {
					getComponentControlled().getFilterTextField().setText("");
					getComponentControlled().getFilterTextField().getDocument().addDocumentListener(listener);
				}
			}
		});
    	
        getComponentControlled().getFilterTextField().addKeyListener(new KeyAdapter() {
        	@Override
        	public void keyTyped(KeyEvent e) {
        		super.keyTyped(e);
        		if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
        			getComponentControlled().getFilterTextField().setText("");
        			// Next is a trick to remove focus from filter text field
        			getComponentControlled().getClearButton().requestFocus();
        		}
        	}
		});        
        
        getComponentControlled().getClearButton().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getComponentControlled().getFilterTextField().setText("");
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (!filterApplied) {
							getComponentControlled().getFilterTextField().getDocument().removeDocumentListener(listener);
							getComponentControlled().getFilterTextField().setText(StringUtils.getString(I18nUtils.getString("FILTER"), "..."));
						}
					}
				});
			}
		});
    }
    
    /**
     * Applies filter by calling FilterHandler
     * 
     * @param filterText
     */
    void applyFilter(String filterText) {
    	filterApplied = filterText != null && !filterText.equals("");
        filterHandler.applyFilter(filter, filterText);
    }

    /**
     * Returns filter
     * 
     * @return
     */
    String getFilter() {
        String filter = getComponentControlled().getFilterTextField().getText();
        return filter.trim().equals("") ? null : filter;
    }
}
