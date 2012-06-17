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

package net.sourceforge.atunes.kernel.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectDuplicateFinder;
import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class clears current play list
 * 
 * @author fleax
 */
public class RemoveDuplicatesFromPlayListAction extends CustomAbstractAction {

    private final class RemoveDuplicatesCallable implements
			Callable<List<Integer>> {
		@Override
		public List<Integer> call() {
			IPlayList playList = playListHandler.getCurrentPlayList(true);
			List<IAudioObject> audioObjectsToCheck = new ArrayList<IAudioObject>();
			for (int i = 0; i < playList.size(); i++) {
				audioObjectsToCheck.add(playList.get(i));
			}
			List<IAudioObject> duplicated = audioObjectDuplicateFinder.findDuplicates(audioObjectsToCheck);
			List<Integer> rows = new ArrayList<Integer>();
			for (IAudioObject ao : duplicated) {
				rows.add(playList.indexOf(ao));
			}
			// List of rows must be sorted in order to remove from play list to work
			Collections.sort(rows, new Comparator<Integer>() {
				
				@Override
				public int compare(Integer o1, Integer o2) {
					return o1.compareTo(o2);
				}
			});
			
			Logger.info(StringUtils.getString(duplicated.size(), " items duplicated"));
			return rows;
		}
	}

	private static final long serialVersionUID = 7784228526804232608L;

    private IPlayListHandler playListHandler;
    
    private IBackgroundWorkerFactory backgroundWorkerFactory;
    
	private IIndeterminateProgressDialog dialog;

	private IAudioObjectDuplicateFinder audioObjectDuplicateFinder;
	
	private IDialogFactory dialogFactory;
	
	/**
	 * @param dialog
	 */
	public void setDialog(IIndeterminateProgressDialog dialog) {
		this.dialog = dialog;
	}
	
	/**
	 * @param audioObjectDuplicateFinder
	 */
	public void setAudioObjectDuplicateFinder(IAudioObjectDuplicateFinder audioObjectDuplicateFinder) {
		this.audioObjectDuplicateFinder = audioObjectDuplicateFinder;
	}
	
    /**
     * @param backgroundWorkerFactory
     */
    public void setBackgroundWorkerFactory(IBackgroundWorkerFactory backgroundWorkerFactory) {
		this.backgroundWorkerFactory = backgroundWorkerFactory;
	}
    
    /**
     * @param playListHandler
     */
    public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
    
    public RemoveDuplicatesFromPlayListAction() {
        super(I18nUtils.getString("REMOVE_DUPLICATES"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("REMOVE_DUPLICATES"));
    }

    @Override
    protected void executeAction() {
        IBackgroundWorker<List<Integer>> worker = backgroundWorkerFactory.getWorker();
        worker.setActionsAfterBackgroundStarted(new Runnable() {
        	@Override
        	public void run() {
        		dialog = dialogFactory.newIndeterminateProgressDialog();
        		dialog.showDialog();
        	}
        });
        
        worker.setBackgroundActions(new RemoveDuplicatesCallable());
        
        worker.setActionsWhenDone(new IBackgroundWorker.IActionsWithBackgroundResult<List<Integer>>() {
        	@Override
        	public void call(List<Integer> duplicated) {
		    	int[] rowsArray = new int[duplicated.size()];
		    	int i = 0;
		    	for (Integer row : duplicated) {
		    		rowsArray[i++] = row;
		    	}
		    	playListHandler.removeAudioObjects(rowsArray);
        		dialog.hideDialog();
        	}
		});
        
        worker.execute();
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<IAudioObject> selection) {
        return true;
    }
}
