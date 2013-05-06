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

package net.sourceforge.atunes.kernel.modules.os;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.WString;

/**
 * @author alex
 * 
 */
public interface Kernel32 extends Library {

	/*
	 * http://msdn2.microsoft.com/en-us/library/aa364989(VS.85).aspx
	 */
	/**
	 * <p>
	 * Unicode (wchar_t*) version of GetShortPathName()
	 * </p>
	 * <code>
	 * DWORD WINAPI GetShortPathNameW( __in LPCTSTR lpszLongPath,
	 * __out LPTSTR lpdzShortPath,
	 * __in DWORD cchBuffer );
	 * </code>.
	 * 
	 * @param inPath
	 *            the in path
	 * @param outPathBuffer
	 *            the out path buffer
	 * @param outPathBufferSize
	 *            the out path buffer size
	 * 
	 * @return the int
	 */
	public int GetShortPathNameW(WString inPath, Memory outPathBuffer,
			int outPathBufferSize);

}