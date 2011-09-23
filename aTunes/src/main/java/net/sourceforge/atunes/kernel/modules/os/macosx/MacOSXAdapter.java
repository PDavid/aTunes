/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.os.macosx;

import java.awt.PopupMenu;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.PlayAction;
import net.sourceforge.atunes.kernel.actions.PlayNextAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.PlayPreviousAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.StopCurrentAudioObjectAction;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

// TODO: Refactor
public class MacOSXAdapter implements InvocationHandler {

	private static final class AppReOpenedListener implements InvocationHandler {
		
		private Object targetObject;
		private Method targetMethod;

		public AppReOpenedListener(Object target, Method reOpenedListener) {
	        this.targetObject = target;
	        this.targetMethod = reOpenedListener;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			targetMethod.invoke(targetObject, (Object[]) null);
			return null;
		}
	}
	
    private static final class FileHandlerOSXAdapter extends MacOSXAdapter {
		private FileHandlerOSXAdapter(String proxySignature, Object target,
				Method handler) {
			super(proxySignature, target, handler);
		}

		// Override OSXAdapter.callTarget to send information on the
		// file to be opened
		@Override
		public boolean callTarget(Object appleEvent) {
		    if (appleEvent != null) {
		    	String filename = null;
		        try {
		            Method getFilenameMethod = appleEvent.getClass().getDeclaredMethod("getFilename", (Class[]) null);
		            filename = (String) getFilenameMethod.invoke(appleEvent, (Object[]) null);
		            this.getTargetMethod().invoke(this.getTargetObject(), new Object[] { filename });
		        } catch (SecurityException e) {
		        	logCallTargetException(filename, e);
				} catch (NoSuchMethodException e) {
		        	logCallTargetException(filename, e);
				} catch (IllegalArgumentException e) {
		        	logCallTargetException(filename, e);
				} catch (IllegalAccessException e) {
		        	logCallTargetException(filename, e);
				} catch (InvocationTargetException e) {
		        	logCallTargetException(filename, e);
				}
		    }
		    return true;
		}
	}

	private Object targetObject;
    private Method targetMethod;
    private String proxySignature;

    private static Object macOSXApplication;
    
    // Pass this method an Object and Method equipped to perform application shutdown logic
    // The method passed should return a boolean stating whether or not the quit should occur
    public static void setQuitHandler(Object target, Method quitHandler) {
        setHandler(new MacOSXAdapter("handleQuit", target, quitHandler));
    }

    // Pass this method an Object and Method equipped to display application info
    // They will be called when the About menu item is selected from the application menu
    public static void setAboutHandler(Object target, Method aboutHandler) {
        boolean enableAboutMenu = (target != null && aboutHandler != null);
        if (enableAboutMenu) {
            setHandler(new MacOSXAdapter("handleAbout", target, aboutHandler));
        }
        // If we're setting a handler, enable the About menu item by calling
        // com.apple.eawt.Application reflectively
        try {
            Method enableAboutMethod = macOSXApplication.getClass().getDeclaredMethod("setEnabledAboutMenu", new Class[] { boolean.class });
            enableAboutMethod.invoke(macOSXApplication, new Object[] { Boolean.valueOf(enableAboutMenu) });
        } catch (SecurityException e) {
        	logAboutMenuException(e);
		} catch (NoSuchMethodException e) {
        	logAboutMenuException(e);
		} catch (IllegalArgumentException e) {
        	logAboutMenuException(e);
		} catch (IllegalAccessException e) {
        	logAboutMenuException(e);
		} catch (InvocationTargetException e) {
        	logAboutMenuException(e);
		}
    }
    
    public static void setAppReOpenedListener(Object target, Method reOpenedListener) {
        setHandler(new AppReOpenedListener(target, reOpenedListener));
    }

    
    private static void logAboutMenuException(Exception e) {
        Logger.error("OSXAdapter could not access the About Menu");
        Logger.error(e);
    }

    // Pass this method an Object and a Method equipped to display application options
    // They will be called when the Preferences menu item is selected from the application menu
    public static void setPreferencesHandler(Object target, Method prefsHandler) {
        boolean enablePrefsMenu = (target != null && prefsHandler != null);
        if (enablePrefsMenu) {
            setHandler(new MacOSXAdapter("handlePreferences", target, prefsHandler));
        }
        // If we're setting a handler, enable the Preferences menu item by calling
        // com.apple.eawt.Application reflectively
        try {
            Method enablePrefsMethod = macOSXApplication.getClass().getDeclaredMethod("setEnabledPreferencesMenu", new Class[] { boolean.class });
            enablePrefsMethod.invoke(macOSXApplication, new Object[] { Boolean.valueOf(enablePrefsMenu) });
        } catch (SecurityException e) {
        	logPreferencesMenuException(e);
        } catch (NoSuchMethodException e) {
        	logPreferencesMenuException(e);
		} catch (IllegalArgumentException e) {
        	logPreferencesMenuException(e);
		} catch (IllegalAccessException e) {
        	logPreferencesMenuException(e);
		} catch (InvocationTargetException e) {
        	logPreferencesMenuException(e);
		}
    }
    
    private static void logPreferencesMenuException(Exception e) {
        Logger.error("OSXAdapter could not access the Preferences Menu");
        Logger.error(e);
    }

    // Pass this method an Object and a Method equipped to handle document events from the Finder
    // Documents are registered with the Finder via the CFBundleDocumentTypes dictionary in the 
    // application bundle's Info.plist
    public static void setFileHandler(Object target, Method fileHandler) {
        setHandler(new FileHandlerOSXAdapter("handleOpenFile", target, fileHandler));
    }
    
    private static void logCallTargetException(String fileName, Exception e) {
        Logger.error(StringUtils.getString("OSXAdapter could not handle file: ", fileName));
        Logger.error(e);
    }
    
    // setHandler creates a Proxy object from the passed OSXAdapter and adds it as an ApplicationListener
    public static void setHandler(InvocationHandler adapter) {
        try {
            Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
            if (macOSXApplication == null) {
                macOSXApplication = applicationClass.getConstructor((Class[]) null).newInstance((Object[]) null);
            }
            Class<?> applicationListenerClass = Class.forName("com.apple.eawt.ApplicationListener");
            Method addListenerMethod = applicationClass.getDeclaredMethod("addApplicationListener", new Class[] { applicationListenerClass });
            // Create a proxy object around this handler that can be reflectively added as an Apple ApplicationListener
            Object osxAdapterProxy = Proxy.newProxyInstance(MacOSXAdapter.class.getClassLoader(), new Class[] { applicationListenerClass }, adapter);
            addListenerMethod.invoke(macOSXApplication, new Object[] { osxAdapterProxy });
        } catch (ClassNotFoundException cnfe) {
            Logger.error(StringUtils.getString("This version of Mac OS X does not support the Apple EAWT.  ApplicationEvent handling has been disabled (", cnfe, ")"));
        } catch (IllegalArgumentException e) {
        	logHandlerException(e);
		} catch (SecurityException e) {
        	logHandlerException(e);
		} catch (InstantiationException e) {
        	logHandlerException(e);
		} catch (IllegalAccessException e) {
        	logHandlerException(e);
		} catch (InvocationTargetException e) {
        	logHandlerException(e);
		} catch (NoSuchMethodException e) {
        	logHandlerException(e);
		}
    }
    
    public static void setListener(Object target, Method method) {
    	setAppReOpenedListener(new AppReOpenedListener(target, method));
    }
    
    /**
     * creates a Proxy object from the passed OSXAdapter and adds it as an AppReOpenedListener
     * @param adapter
     */
    private static void setAppReOpenedListener(AppReOpenedListener adapter) {
        try {
            Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
            if (macOSXApplication == null) {
                macOSXApplication = applicationClass.getConstructor((Class[]) null).newInstance((Object[]) null);
            }
            Class<?> appEventListenerClass = Class.forName("com.apple.eawt.AppEventListener");
            Method addListenerMethod = applicationClass.getDeclaredMethod("addAppEventListener", new Class[] { appEventListenerClass });
            
            Class<?> appReOpenedListenerClass = Class.forName("com.apple.eawt.AppReOpenedListener");
            
            
            // Create a proxy object around this handler that can be reflectively added as an Apple AppReOpenedListener
            Object osxAdapterProxy = Proxy.newProxyInstance(AppReOpenedListener.class.getClassLoader(), new Class[] { appReOpenedListenerClass }, adapter);
            addListenerMethod.invoke(macOSXApplication, new Object[] { osxAdapterProxy });
        } catch (ClassNotFoundException cnfe) {
            Logger.error(StringUtils.getString("This version of Mac OS X does not support the Apple EAWT.  ApplicationEvent handling has been disabled (", cnfe, ")"));
        } catch (IllegalArgumentException e) {
        	logHandlerException(e);
		} catch (SecurityException e) {
        	logHandlerException(e);
		} catch (InstantiationException e) {
        	logHandlerException(e);
		} catch (IllegalAccessException e) {
        	logHandlerException(e);
		} catch (InvocationTargetException e) {
        	logHandlerException(e);
		} catch (NoSuchMethodException e) {
        	logHandlerException(e);
		}
    }

    
    private static void logHandlerException(Exception e) {
    	Logger.error("Mac OS X Adapter could not talk to EAWT:");
    	Logger.error(e);
    }

    // Each OSXAdapter has the name of the EAWT method it intends to listen for (handleAbout, for example),
    // the Object that will ultimately perform the task, and the Method to be called on that Object
    protected MacOSXAdapter(String proxySignature, Object target, Method handler) {
        this.proxySignature = proxySignature;
        this.targetObject = target;
        this.targetMethod = handler;
    }

    // Override this method to perform any operations on the event 
    // that comes with the various callbacks
    // See setFileHandler above for an example
    public boolean callTarget(Object appleEvent) throws InvocationTargetException, IllegalAccessException {
        Object result = targetMethod.invoke(targetObject, (Object[]) null);
        if (result == null) {
            return true;
        }
        return Boolean.valueOf(result.toString()).booleanValue();
    }

    // InvocationHandler implementation
    // This is the entry point for our proxy object; it is called every time an ApplicationListener method is invoked
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        if (isCorrectMethod(method, args)) {
            boolean handled = callTarget(args[0]);
            setApplicationEventHandled(args[0], handled);
        }
        // All of the ApplicationListener methods are void; return null regardless of what happens
        return null;
    }

    // Compare the method that was called to the intended method when the OSXAdapter instance was created
    // (e.g. handleAbout, handleQuit, handleOpenFile, etc.)
    protected boolean isCorrectMethod(Method method, Object[] args) {
        return (targetMethod != null && proxySignature.equals(method.getName()) && args.length == 1);
    }

    // It is important to mark the ApplicationEvent as handled and cancel the default behavior
    // This method checks for a boolean result from the proxy method and sets the event accordingly
    protected void setApplicationEventHandled(Object event, boolean handled) {
        if (event != null) {
            try {
                Method setHandledMethod = event.getClass().getDeclaredMethod("setHandled", new Class[] { boolean.class });
                // If the target method returns a boolean, use that as a hint
                setHandledMethod.invoke(event, new Object[] { Boolean.valueOf(handled) });
            } catch (SecurityException e) {
            	logApplicationEventException(event, e);
			} catch (NoSuchMethodException e) {
            	logApplicationEventException(event, e);
			} catch (IllegalArgumentException e) {
            	logApplicationEventException(event, e);
			} catch (IllegalAccessException e) {
            	logApplicationEventException(event, e);
			} catch (InvocationTargetException e) {
            	logApplicationEventException(event, e);
			}
        }
    }

    private static void logApplicationEventException(Object event, Exception e) {
        Logger.error(StringUtils.getString("OSXAdapter was unable to handle an ApplicationEvent: ", event));
        Logger.error(e);
    }

    /**
     * @return the targetObject
     */
    protected Object getTargetObject() {
        return targetObject;
    }

    /**
     * @return the targetMethod
     */
    protected Method getTargetMethod() {
        return targetMethod;
    }    
    
    /**
     * Adds dock icon menu
     */
    protected static void addDockIconMenu() {
		try {
			Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
	    	if (macOSXApplication == null) {
	    		macOSXApplication = applicationClass.getConstructor((Class[]) null).newInstance((Object[]) null);
	    	}
	    	Method setDockMenu = applicationClass.getDeclaredMethod("setDockMenu", new Class[] { PopupMenu.class });
	    	
	    	setDockMenu.invoke(macOSXApplication, new Object[] { getDockMenu() });
	    	
		} catch (ClassNotFoundException e) {
        	logHandlerException(e);
		} catch (IllegalArgumentException e) {
        	logHandlerException(e);
		} catch (SecurityException e) {
        	logHandlerException(e);
		} catch (InstantiationException e) {
        	logHandlerException(e);
		} catch (IllegalAccessException e) {
        	logHandlerException(e);
		} catch (InvocationTargetException e) {
        	logHandlerException(e);
		} catch (NoSuchMethodException e) {
        	logHandlerException(e);
		}
    }
    
    /**
     * Returns menu for dock icon
     * @return
     */
    private static PopupMenu getDockMenu() {
    	PopupMenu menu = new PopupMenu();
    	menu.add(Actions.getMenuItemForAction(PlayAction.class));
    	menu.add(Actions.getMenuItemForAction(StopCurrentAudioObjectAction.class));
    	menu.add(Actions.getMenuItemForAction(PlayPreviousAudioObjectAction.class));
    	menu.add(Actions.getMenuItemForAction(PlayNextAudioObjectAction.class));
    	return menu;
    }
}
