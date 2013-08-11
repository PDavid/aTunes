/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.ITaskService;
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
			IPlayList playList = RemoveDuplicatesFromPlayListAction.this.playListHandler
					.getVisiblePlayList();
			List<IAudioObject> audioObjectsToCheck = new ArrayList<IAudioObject>();
			for (int i = 0; i < playList.size(); i++) {
				audioObjectsToCheck.add(playList.get(i));
			}
			List<IAudioObject> duplicated = RemoveDuplicatesFromPlayListAction.this.beanFactory
					.getBean(IAudioObjectDuplicateFinder.class).findDuplicates(
							audioObjectsToCheck);
			List<Integer> rows = new ArrayList<Integer>();
			for (IAudioObject ao : duplicated) {
				rows.add(playList.indexOf(ao));
			}
			// List of rows must be sorted in order to remove from play list to
			// work
			Collections.sort(rows, new Comparator<Integer>() {

				@Override
				public int compare(final Integer o1, final Integer o2) {
					return o1.compareTo(o2);
				}
			});

			Logger.info(StringUtils.getString(duplicated.size(),
					" items duplicated"));
			return rows;
		}
	}

	private static final long serialVersionUID = 7784228526804232608L;

	private IPlayListHandler playListHandler;

	private IBackgroundWorkerFactory backgroundWorkerFactory;

	private IIndeterminateProgressDialog dialog;

	private IDialogFactory dialogFactory;

	private IBeanFactory beanFactory;

	private ITaskService taskService;

	/**
	 * @param taskService
	 */
	public void setTaskService(final ITaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param backgroundWorkerFactory
	 */
	public void setBackgroundWorkerFactory(
			final IBackgroundWorkerFactory backgroundWorkerFactory) {
		this.backgroundWorkerFactory = backgroundWorkerFactory;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * Default constructor
	 */
	public RemoveDuplicatesFromPlayListAction() {
		super(I18nUtils.getString("REMOVE_DUPLICATES"));
	}

	@Override
	protected void executeAction() {
		IBackgroundWorker<List<Integer>, Void> worker = this.backgroundWorkerFactory
				.getWorker();
		worker.setActionsBeforeBackgroundStarts(new Runnable() {
			@Override
			public void run() {
				RemoveDuplicatesFromPlayListAction.this.dialog = RemoveDuplicatesFromPlayListAction.this.dialogFactory
						.newDialog(IIndeterminateProgressDialog.class);
				RemoveDuplicatesFromPlayListAction.this.dialog.showDialog();
			}
		});

		worker.setBackgroundActions(new RemoveDuplicatesCallable());

		worker.setActionsWhenDone(new IBackgroundWorker.IActionsWithBackgroundResult<List<Integer>>() {
			@Override
			public void call(final List<Integer> duplicated) {
				int[] rowsArray = new int[duplicated.size()];
				int i = 0;
				for (Integer row : duplicated) {
					rowsArray[i++] = row;
				}
				RemoveDuplicatesFromPlayListAction.this.playListHandler
						.removeAudioObjects(rowsArray);
				RemoveDuplicatesFromPlayListAction.this.dialog.hideDialog();
			}
		});

		worker.execute(this.taskService);
	}

	@Override
	public boolean isEnabledForPlayListSelection(
			final List<IAudioObject> selection) {
		return true;
	}

	@Override
	public boolean isEnabledForPlayList(final IPlayList playlist) {
		return !playlist.isDynamic();
	}

}
