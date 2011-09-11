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

package net.sourceforge.atunes.kernel.modules.gui;

import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IFrameFactory;
import net.sourceforge.atunes.model.IState;

public class FrameFactory implements IFrameFactory {

	private IState state;
	
	private String defaultFrameClass;
	
	@Override
	public void setState(IState state) {
		this.state = state;
	}
	
	@Override
	public void setDefaultFrameClass(String defaultFrameClass) {
		this.defaultFrameClass = defaultFrameClass;
	}
	
    @Override
    public IFrame create() {
    	IFrame frame = null;
        Class<? extends IFrame> clazz = state.getFrameClass();
        if (clazz != null) {
            try {
                frame = clazz.newInstance();
            } catch (InstantiationException e) {
                Logger.error(e);
            } catch (IllegalAccessException e) {
                Logger.error(e);
            }
        }
        
        if (frame == null) {
        	frame = constructDefaultFrame();
        	if (frame != null) {
                state.setFrameClass(frame.getClass());
        	}
        }
        
        if (frame == null) {
        	throw new IllegalArgumentException("Could not create main frame");
        }
        
        return frame;
    }
    
    /**
     * Creates default frame
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    private IFrame constructDefaultFrame() {
        IFrame frame = null;
		try {
			frame = (IFrame) Class.forName(defaultFrameClass).newInstance();
		} catch (InstantiationException e) {
            Logger.error(e);
		} catch (IllegalAccessException e) {
            Logger.error(e);
		} catch (ClassNotFoundException e) {
            Logger.error(e);
		}
        return frame;
    }


}
