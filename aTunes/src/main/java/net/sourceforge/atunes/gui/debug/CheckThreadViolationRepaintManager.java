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

package net.sourceforge.atunes.gui.debug;

import java.awt.Toolkit;
import java.lang.ref.WeakReference;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.utils.Logger;

/**
 * <p>
 * This class is used to detect Event Dispatch Thread rule violations<br>
 * See <a
 * href="http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html">How
 * to Use Threads</a> for more info
 * </p>
 * <p/>
 * <p>
 * This is a modification of original idea of Scott Delap<br>
 * Initial version of ThreadCheckingRepaintManager can be found here<br>
 * <a href="http://www.clientjava.com/blog/2004/08/20/1093059428000.html">Easily
 * Find Swing Threading Mistakes</a>
 * </p>
 * 
 * @author Scott Delap
 * @author Alexander Potochkin
 * 
 *         https://swinghelper.dev.java.net/
 */
public final class CheckThreadViolationRepaintManager extends RepaintManager {

    // it is recommended to pass the complete check  
    /** The complete check. */
    private boolean completeCheck = true;

    /** The last component. */
    private WeakReference<JComponent> lastComponent;

    /**
     * Instantiates a new check thread violation repaint manager.
     */
    public CheckThreadViolationRepaintManager() {
        this(true);
    }

    /**
     * Instantiates a new check thread violation repaint manager.
     * 
     * @param completeCheck
     *            the complete check
     */
    public CheckThreadViolationRepaintManager(boolean completeCheck) {
        this.completeCheck = completeCheck;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.RepaintManager#addDirtyRegion(javax.swing.JComponent,
     * int, int, int, int)
     */
    @Override
    public void addDirtyRegion(JComponent component, int x, int y, int w, int h) {
        checkThreadViolations(component);
        super.addDirtyRegion(component, x, y, w, h);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.RepaintManager#addInvalidComponent(javax.swing.JComponent)
     */
    @Override
    public synchronized void addInvalidComponent(JComponent component) {
        checkThreadViolations(component);
        super.addInvalidComponent(component);
    }

    /**
     * Check thread violations.
     * 
     * @param c
     *            the c
     */
    private void checkThreadViolations(JComponent c) {
        if (!SwingUtilities.isEventDispatchThread() && (completeCheck || c.isShowing())) {
            checkPossibleViolation(c);
        }
    }

	/**
	 * @param c
	 */
	private void checkPossibleViolation(JComponent c) {
		boolean repaint = false;
		boolean fromSwing = false;
		boolean imageUpdate = false;
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (StackTraceElement st : stackTrace) {
		    if (repaint && st.getClassName().startsWith("javax.swing.")) {
		        fromSwing = true;
		    }
		    if (repaint && "imageUpdate".equals(st.getMethodName())) {
		        imageUpdate = true;
		    }
		    if ("repaint".equals(st.getMethodName())) {
		        repaint = true;
		        fromSwing = false;
		    }
		}
		if (!checkConditions(c, repaint, fromSwing, imageUpdate)) {
			lastComponent = new WeakReference<JComponent>(c);
			logViolation(c, stackTrace);
		}
	}

	/**
	 * @param c
	 * @param stackTrace
	 */
	private void logViolation(JComponent c, StackTraceElement[] stackTrace) {
		Logger.error("EDT violation detected");
		Logger.error(c);
		for (StackTraceElement st : stackTrace) {
			Logger.error("\tat ", st.toString());
		}
		Toolkit.getDefaultToolkit().beep();
	}

	private boolean checkConditions(JComponent c, boolean repaint, boolean fromSwing, boolean imageUpdate) {
        if (imageUpdate) {
            //assuming it is java.awt.image.ImageObserver.imageUpdate(...) 
            //image was asynchronously updated, that's ok 
            return true;
        }
        if (repaint && !fromSwing) {
            //no problems here, since repaint() is thread safe
            return true;
        }
        //ignore the last processed component
        if (lastComponent != null && c == lastComponent.get()) {
            return true;
        }
		return false;
	}
}
