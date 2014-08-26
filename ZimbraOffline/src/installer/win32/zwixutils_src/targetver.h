/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010, 2013, 2014 Zimbra, Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK *****
 */
#pragma once

// The following macros define the minimum required platform.  The minimum required platform
// is the earliest version of Windows, Internet Explorer etc. that has the necessary features to run 
// your application.  The macros work by enabling all features available on platform versions up to and 
// including the version specified.

// Modify the following defines if you have to target a platform prior to the ones specified below.
// Refer to MSDN for the latest info on corresponding values for different platforms.
#ifndef WINVER                  // Specifies that the minimum required platform is Windows 2000.
#define WINVER 0x0500           // Change this to the appropriate value to target other versions of Windows.
#endif

#ifndef _WIN32_WINNT            // Specifies that the minimum required platform is Windows 2000.
#define _WIN32_WINNT 0x0500     // Change this to the appropriate value to target other versions of Windows.
#endif

#ifndef _WIN32_IE               // Specifies that the minimum required platform is Internet Explorer 5.0.
#define _WIN32_IE 0x0500        // Change this to the appropriate value to target other versions of IE.
#endif

#ifndef _WIN32_MSI              // Specifies that the minimum required MSI version is MSI 3.1
#define _WIN32_MSI 310          // Change this to the appropriate value to target other versions of MSI.
#endif
