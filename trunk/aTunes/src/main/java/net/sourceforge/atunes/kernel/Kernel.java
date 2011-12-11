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

import net.sourceforge.atunes.ApplicationArguments;
import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.ICommandHandler;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IKernel;
import net.sourceforge.atunes.model.ILocaleBeanFactory;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.ITemporalDiskStorage;
import net.sourceforge.atunes.model.IUIHandler;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.Timer;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * The Kernel is the class responsible of create and interconnect all modules of
 * aTunes.
 */
public final class Kernel implements IKernel, ApplicationContextAware {

    /** Timer used to measure start time */
    private Timer timer;
    
    private IState state;
    
    private ILocaleBeanFactory localeBeanFactory;
    
    private ApplicationContext context;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    	this.context = applicationContext;
    }
    
    /**
     * @param localeBeanFactory
     */
    public void setLocaleBeanFactory(ILocaleBeanFactory localeBeanFactory) {
		this.localeBeanFactory = localeBeanFactory;
	}
    
    /**
     * Sets state
     * @param state
     */
    public void setState(IState state) {
		this.state = state;
	}
    
    @Override
	public void start() {
        Logger.debug("Starting Kernel");
        
        timer = new Timer();
        timer.start();

        new LanguageSelector().setLanguage(state, localeBeanFactory);
        
        initializeUI();
        context.getBean(HandlerInitializer.class).initializeHandlers(state);
        createUI();

        // Call user interaction
        context.getBean(ApplicationLifeCycleListeners.class).doUserInteraction(
        		context.getBean(ApplicationLifeCycleListeners.class).getUserInteractionRequests());
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
                    context.getBean(ILookAndFeelManager.class).setLookAndFeel(context.getBean(ApplicationArguments.class), state.getLookAndFeel(), state, context.getBean(IOSManager.class));

                    context.getBean(HandlerInitializer.class).setFrameForHandlers(context.getBean(IFrame.class));
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
        Timer timer = new Timer();
        try {
            timer.start();
            Logger.info(StringUtils.getString("Closing ", Constants.APP_NAME, " ", Constants.VERSION.toString()));
            context.getBean(ITemporalDiskStorage.class).removeAll();
            
            context.getBean(ApplicationLifeCycleListeners.class).applicationFinish();
            
            context.getBean(ITaskService.class).shutdownService();
            
        } finally {
            Logger.info(StringUtils.getString("Application finished (", StringUtils.toString(timer.stop(), 3), " seconds)"));
            Logger.info("Goodbye!!");
            // Exit normally
            System.exit(0);
        }
    }

    /**
     * Call actions after start.
     */
    void callActionsAfterStart() {
    	context.getBean(ApplicationLifeCycleListeners.class).applicationStarted();
    	context.getBean(ApplicationLifeCycleListeners.class).allHandlersInitialized();
    }

    /**
     * Creates all objects of aTunes: visual objects, controllers, and handlers.
     */
    private void startCreation() {
        Logger.debug("Starting components");
        context.getBean(IUIHandler.class).startVisualization();
    }

    @Override
	public void restart() {
        try {
            // Store all configuration and finish all active modules
        	context.getBean(ApplicationLifeCycleListeners.class).applicationFinish();

        	IOSManager osManager = context.getBean(IOSManager.class);
        	
            // Build a process builder with OS-specific command and saved arguments
        	String parameters = osManager.getLaunchParameters();
            ProcessBuilder pb = null;
            if (parameters != null && !parameters.trim().isEmpty()) {
            	pb = new ProcessBuilder(osManager.getLaunchCommand(), parameters, context.getBean(ApplicationArguments.class).getSavedArguments(context.getBean(ICommandHandler.class)));
            } else {
            	pb = new ProcessBuilder(osManager.getLaunchCommand(), context.getBean(ApplicationArguments.class).getSavedArguments(context.getBean(ICommandHandler.class)));

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
