/*
 * aTunes 3.1.0
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

package net.sourceforge.atunes.kernel.modules.repository;

import net.sourceforge.atunes.model.IHandlerBackgroundInitializationTask;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.model.ISearchableObject;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.IStateRepository;

/**
 * Loads repository
 * @author alex
 *
 */
public final class ReadRepositoryInitializationTask implements IHandlerBackgroundInitializationTask {
	
	private RepositoryReader repositoryReader;
	
	private IStateHandler stateHandler;
	
	private IStateRepository stateRepository;
	
	private ISearchHandler searchHandler;
	
    private RepositoryAutoRefresher repositoryRefresher;
    
	private ISearchableObject repositorySearchableObject;
	
	/**
	 * @param repositorySearchableObject
	 */
	public void setRepositorySearchableObject(ISearchableObject repositorySearchableObject) {
		this.repositorySearchableObject = repositorySearchableObject;
	}
    
    /**
     * @param repositoryRefresher
     */
    public void setRepositoryRefresher(RepositoryAutoRefresher repositoryRefresher) {
		this.repositoryRefresher = repositoryRefresher;
	}
	
	/**
	 * @param searchHandler
	 */
	public void setSearchHandler(ISearchHandler searchHandler) {
		this.searchHandler = searchHandler;
	}
	
	/**
	 * @param stateRepository
	 */
	public void setStateRepository(IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}
	
	/**
	 * @param repositoryReader
	 */
	public void setRepositoryReader(RepositoryReader repositoryReader) {
		this.repositoryReader = repositoryReader;
	}
	
	/**
	 * @param stateHandler
	 */
	public void setStateHandler(IStateHandler stateHandler) {
		this.stateHandler = stateHandler;
	}
	
	@Override
	public Runnable getInitializationTask() {
		return new Runnable() {
			@Override
			public void run() {
			    // This is the first access to repository, so execute the command defined by user
			    new LoadRepositoryCommandExecutor().execute(stateRepository.getCommandBeforeAccessRepository());
			    repositoryReader.setRepositoryRetrievedFromCache(stateHandler.retrieveRepositoryCache());
			}
		};
	}
	
	@Override
	public Runnable getInitializationCompletedTask() {
    	return new Runnable() {
    		@Override
    		public void run() {
    	    	repositoryReader.testRepositoryRetrievedFromCache();
    	    	repositoryReader.applyRepositoryFromCache();
    	        searchHandler.registerSearchableObject(repositorySearchableObject);
    	        repositoryRefresher.start();
    		}
    	};
	}	
}