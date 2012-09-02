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

package net.sourceforge.atunes.kernel;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ICommandHandler;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IKernel;
import net.sourceforge.atunes.model.ILocaleBeanFactory;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.ITemporalDiskStorage;
import net.sourceforge.atunes.model.IUIHandler;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.Timer;

/**
 * The Kernel is the class responsible of create and interconnect all modules of
 * aTunes.
 */
public final class Kernel implements IKernel {

    /** Timer used to measure start time */
    private Timer timer;
    
    private IStateUI stateUI;
    
    private ILocaleBeanFactory localeBeanFactory;
    
    private IStateCore stateCore;
    
    private IBeanFactory beanFactory;
    
    /**
     * @param beanFactory
     */
    public void setBeanFactory(IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
    
    /**
     * @param stateCore
     */
    public void setStateCore(IStateCore stateCore) {
		this.stateCore = stateCore;
	}
    
    /**
     * @param stateUI
     */
    public void setStateUI(IStateUI stateUI) {
		this.stateUI = stateUI;
	}
    
    /**
     * @param localeBeanFactory
     */
    public void setLocaleBeanFactory(ILocaleBeanFactory localeBeanFactory) {
		this.localeBeanFactory = localeBeanFactory;
	}
    
    @Override
	public void start() {
        Logger.debug("Starting Kernel");
        
        timer = new Timer();
        timer.start();

        new LanguageSelector().setLanguage(stateCore, localeBeanFactory);
        
        initializeUI();
        beanFactory.getBean(HandlerInitializer.class).initializeHandlers();
        createUI();
    }
    
    /**
     * Initializes UI
     */
    private void initializeUI() {
        Logger.debug("Initializing UI");
        try {
        	// Call invokeAndWait to wait until splash screen is visible
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    beanFactory.getBean(ILookAndFeelManager.class).setLookAndFeel(stateUI.getLookAndFeel(), stateCore, stateUI, beanFactory.getBean(IOSManager.class));

                    beanFactory.getBean(HandlerInitializer.class).setFrameForHandlers(beanFactory.getBean(IFrame.class));
                }
            });
        } catch (InvocationTargetException e) {
            Logger.error(e);
            Logger.error(e.getCause());
        } catch (InterruptedException e) {
            Logger.error(e);
            Logger.error(e.getCause());
		}
    }
    
    /**
     * Creates UI
     */
    private void createUI() {
        Logger.debug("Creating UI");
        try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					// Start component creation
					startCreation();

			    	callActionsAfterStart();
			    	Logger.info(StringUtils.getString("Application started (", StringUtils.toString(timer.stop(), 3), " seconds)"));
			    	timer = null;
				}
			});
		} catch (InterruptedException e) {
			Logger.error(e);
		} catch (InvocationTargetException e) {
			Logger.error(e);
		}
    }

    @Override
	public void finish() {
        Timer finishTimer = new Timer();
        try {
            finishTimer.start();
            Logger.info(StringUtils.getString("Closing ", Constants.APP_NAME, " ", Constants.VERSION.toString()));
            beanFactory.getBean(ITemporalDiskStorage.class).removeAll();
            
            beanFactory.getBean(ApplicationLifeCycleListeners.class).applicationFinish();
            
            beanFactory.getBean("taskService", ITaskService.class).shutdownService();
            
        } finally {
            Logger.info(StringUtils.getString("Application finished (", StringUtils.toString(finishTimer.stop(), 3), " seconds)"));
            Logger.info("Goodbye!!");
            // Exit normally
            System.exit(0);
        }
    }

    /**
     * Call actions after start.
     */
    void callActionsAfterStart() {
    	beanFactory.getBean(ApplicationLifeCycleListeners.class).applicationStarted();
    	beanFactory.getBean(ApplicationLifeCycleListeners.class).allHandlersInitialized();
    	beanFactory.getBean("taskService", ITaskService.class).submitOnce("Deferred handler initialization", 3, new Runnable() {
    		@Override
    		public void run() {
    			beanFactory.getBean(ApplicationLifeCycleListeners.class).deferredInitialization();
    		}
    	});
    }

    /**
     * Creates all objects of aTunes: visual objects, controllers, and handlers.
     */
    private void startCreation() {
        Logger.debug("Starting components");
        beanFactory.getBean(IUIHandler.class).startVisualization();
    }

    @Override
	public void restart() {
        try {
            // Store all configuration and finish all active modules
        	beanFactory.getBean(ApplicationLifeCycleListeners.class).applicationFinish();

        	IOSManager osManager = beanFactory.getBean(IOSManager.class);
        	
            // Build a process builder with OS-specific command and saved arguments
        	String parameters = osManager.getLaunchParameters();
            ProcessBuilder pb = null;
            if (parameters != null && !parameters.trim().isEmpty()) {
            	pb = new ProcessBuilder(osManager.getLaunchCommand(), parameters, beanFactory.getBean(IApplicationArguments.class).getSavedArguments(beanFactory.getBean(ICommandHandler.class)));
            } else {
            	pb = new ProcessBuilder(osManager.getLaunchCommand(), beanFactory.getBean(IApplicationArguments.class).getSavedArguments(beanFactory.getBean(ICommandHandler.class)));
            }

            // Start new application instance
            pb.start();

        } catch (IOException e) {
            Logger.error(e);
        } finally {
            // Exit normally
            System.exit(0);
        }
    }
}
