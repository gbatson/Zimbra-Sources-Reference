/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/**
 * 
 * @private
 */
AjxEnv = function() {
};

AjxEnv._inited = false;
AjxEnv.DEFAULT_LOCALE = window.navigator.userLanguage || window.navigator.language || window.navigator.systemLanguage;

AjxEnv.reset =
function() {
	AjxEnv.geckoDate = 0;
	AjxEnv.mozVersion = -1;
	AjxEnv.webKitVersion = -1;
	AjxEnv.isMac = false;
	AjxEnv.isWindows = false;
	AjxEnv.isLinux = false;
	AjxEnv.isNav  = false;
	AjxEnv.isIE = false;
	AjxEnv.isNav4 = false;
	AjxEnv.trueNs = true;
	AjxEnv.isNav6 = false;
	AjxEnv.isNav6up = false;
	AjxEnv.isNav7 = false;
	AjxEnv.isIE3 = false;
	AjxEnv.isIE4 = false;
	AjxEnv.isIE4up = false;
	AjxEnv.isIE5 = false;
	AjxEnv.isIE5_5 = false;
	AjxEnv.isIE5up = false;
	AjxEnv.isIE5_5up = false;
	AjxEnv.isIE6  = false;
	AjxEnv.isIE6up = false;
	AjxEnv.isIE7  = false;
	AjxEnv.isIE7up = false;
	AjxEnv.isIE8  = false;
	AjxEnv.isIE8up = false;
	AjxEnv.isNormalResolution = false;
	AjxEnv.ieScaleFactor = 1;
	AjxEnv.isFirefox = false;
	AjxEnv.isFirefox1up = false;
	AjxEnv.isFirefox1_5up = false;
	AjxEnv.isFirefox3up = false;
	AjxEnv.isFirefox3_6up = false;
	AjxEnv.isMozilla = false;
	AjxEnv.isMozilla1_4up = false;
	AjxEnv.isSafari = false;
	AjxEnv.isSafari2 = false;
	AjxEnv.isSafari3 = false;
    AjxEnv.isSafari4 = false;
	AjxEnv.isSafari3up = false;
	AjxEnv.isSafari4up = false;
	AjxEnv.isCamino = false;
	AjxEnv.isChrome = false;
	AjxEnv.isGeckoBased = false;
	AjxEnv.isWebKitBased = false;
	AjxEnv.isOpera = false;
	AjxEnv.useTransparentPNGs = false;
	AjxEnv.isDesktop = false;
	AjxEnv.isDesktop2up = false;

	// screen resolution - ADD MORE RESOLUTION CHECKS AS NEEDED HERE:
	AjxEnv.is800x600orLower = screen && (screen.width <= 800 && screen.height <= 600);
    AjxEnv.is1024x768orLower = screen && (screen.width <= 1024 && screen.height <= 768);
};

AjxEnv.parseUA = 
function() {
	AjxEnv.reset();

	var agt = navigator.userAgent.toLowerCase();
	var agtArr = agt.split(" ");
	var isSpoofer = false;
	var isWebTv = false;
	var isHotJava = false;
	var beginsWithMozilla = false;
	var isCompatible = false;

	if (agtArr != null) {
		var browserVersion;
		var index = -1;

		if ((index = agtArr[0].search(/^\s*mozilla\//))!= -1) {
			beginsWithMozilla = true;
			AjxEnv.browserVersion = parseFloat(agtArr[0].substring(index + 8));
			AjxEnv.isNav = true;
		}

		var token;
		for (var i = 0; i < agtArr.length; ++i) {
			token = agtArr[i];
			if (token.indexOf('compatible') != -1 ) {
				isCompatible = true;
				AjxEnv.isNav = false;
			} else if ((token.indexOf('opera')) != -1) {
				AjxEnv.isOpera = true;
				AjxEnv.isNav = false;
				browserVersion = parseFloat(agtArr[i+1]);
			} else if ((token.indexOf('spoofer')) != -1) {
				isSpoofer = true;
				AjxEnv.isNav = false;
			} else if ((token.indexOf('webtv')) != -1) {
				isWebTv = true;
				AjxEnv.isNav = false;
			} else if ((token.indexOf('hotjava')) != -1) {
				isHotJava = true;
				AjxEnv.isNav = false;
			} else if ((index = token.indexOf('msie')) != -1) {
				AjxEnv.isIE = true;
				browserVersion = parseFloat(agtArr[i+1]);
			} else if ((index = token.indexOf('gecko/')) != -1) {
				AjxEnv.isGeckoBased = true;
				AjxEnv.geckoDate = parseFloat(token.substr(index + 6));
			} else if ((index = token.indexOf('applewebkit/')) != -1) {
				AjxEnv.isWebKitBased = true;
				AjxEnv.webKitVersion = parseFloat(token.substr(index + 12));
			} else if ((index = token.indexOf('rv:')) != -1) {
				AjxEnv.mozVersion = parseFloat(token.substr(index + 3));
				browserVersion = AjxEnv.mozVersion;
			} else if ((index = token.indexOf('firefox/')) != -1) {
				AjxEnv.isFirefox = true;
				browserVersion = parseFloat(token.substr(index + 8));
			} else if ((index = token.indexOf('prism')) != -1) {
				AjxEnv.isPrism = true;
			} else if ((index = token.indexOf('camino/')) != -1) {
				AjxEnv.isCamino = true;
				browserVersion = parseFloat(token.substr(index + 7));
			} else if ((index = token.indexOf('netscape6/')) != -1) {
				AjxEnv.trueNs = true;
				browserVersion = parseFloat(token.substr(index + 10));
			} else if ((index = token.indexOf('netscape/')) != -1) {
				AjxEnv.trueNs = true;
				browserVersion = parseFloat(token.substr(index + 9));
			} else if ((index = token.indexOf('safari/')) != -1) {
				AjxEnv.isSafari = true;
			} else if ((index = token.indexOf('chrome/')) != -1) {
				AjxEnv.isChrome = true;
				browserVersion = parseFloat(token.substr(index + 7));
			} else if (index = token.indexOf('version/') != -1) {
				// this is how safari sets browser version
				browserVersion = parseFloat(token.substr(index + 7));
			} else if (token.indexOf('windows') != -1) {
				AjxEnv.isWindows = true;
			} else if ((token.indexOf('macintosh') != -1) ||
					   (token.indexOf('mac_') != -1)) {
				AjxEnv.isMac = true;
			} else if (token.indexOf('linux') != -1) {
				AjxEnv.isLinux = true;
			} else if ((index = token.indexOf('zdesktop/')) != -1) {
				AjxEnv.isDesktop = true;
				browserVersion = parseFloat(token.substr(index + 9));
			}
		}
		// Note: Opera and WebTV spoof Navigator. We do strict client detection.
		AjxEnv.isNav 			= (beginsWithMozilla && !isSpoofer && !isCompatible && !AjxEnv.isOpera && !isWebTv && !isHotJava && !AjxEnv.isSafari);
		AjxEnv.isIE				= (AjxEnv.isIE && !AjxEnv.isOpera);
		AjxEnv.isNav4			= (AjxEnv.isNav && (browserVersion == 4) && (!AjxEnv.isIE));
		AjxEnv.isNav6			= (AjxEnv.isNav && AjxEnv.trueNs && (browserVersion >= 6.0 && browserVersion < 7.0));
		AjxEnv.isNav6up 		= (AjxEnv.isNav && AjxEnv.trueNs && (browserVersion >= 6.0));
		AjxEnv.isNav7 			= (AjxEnv.isNav && AjxEnv.trueNs && (browserVersion >= 7.0 && browserVersion < 8.0));
		AjxEnv.isIE3 			= (AjxEnv.isIE && browserVersion <  4.0);
		AjxEnv.isIE4			= (AjxEnv.isIE && browserVersion >= 4.0 && browserVersion < 5.0);
		AjxEnv.isIE4up			= (AjxEnv.isIE && browserVersion >= 4.0);
		AjxEnv.isIE5			= (AjxEnv.isIE && browserVersion >= 5.0 && browserVersion < 6.0);
		AjxEnv.isIE5_5			= (AjxEnv.isIE && browserVersion == 5.5);
		AjxEnv.isIE5up			= (AjxEnv.isIE && browserVersion >= 5.0);
		AjxEnv.isIE5_5up		= (AjxEnv.isIE && browserVersion >= 5.5);
		AjxEnv.isIE6			= (AjxEnv.isIE && browserVersion >= 6.0 && browserVersion < 7.0);
		AjxEnv.isIE6up			= (AjxEnv.isIE && browserVersion >= 6.0);
		AjxEnv.isIE7			= (AjxEnv.isIE && browserVersion >= 7.0 && browserVersion < 8.0);
		AjxEnv.isIE7up			= (AjxEnv.isIE && browserVersion >= 7.0);
		AjxEnv.isIE8			= (AjxEnv.isIE && browserVersion >= 8.0 && browserVersion < 9.0);
		AjxEnv.isIE8up			= (AjxEnv.isIE && browserVersion >= 8.0);
		AjxEnv.isMozilla		= ((AjxEnv.isNav && AjxEnv.mozVersion && AjxEnv.isGeckoBased && (AjxEnv.geckoDate != 0)));
		AjxEnv.isMozilla1_4up	= (AjxEnv.isMozilla && (AjxEnv.mozVersion >= 1.4));
		AjxEnv.isFirefox 		= ((AjxEnv.isMozilla && AjxEnv.isFirefox));
		AjxEnv.isFirefox1up		= (AjxEnv.isFirefox && browserVersion >= 1.0);
		AjxEnv.isFirefox1_5up	= (AjxEnv.isFirefox && browserVersion >= 1.5);
		AjxEnv.isFirefox2_0up	= (AjxEnv.isFirefox && browserVersion >= 2.0);
		AjxEnv.isFirefox3up		= (AjxEnv.isFirefox && browserVersion >= 3.0);
		AjxEnv.isFirefox3_6up	= (AjxEnv.isFirefox && browserVersion >= 3.6);
		AjxEnv.isSafari2		= (AjxEnv.isSafari && browserVersion >= 2.0 && browserVersion < 3.0);
		AjxEnv.isSafari3		= (AjxEnv.isSafari && browserVersion >= 3.0 && browserVersion < 4.0) || AjxEnv.isChrome;
        AjxEnv.isSafari4        = (AjxEnv.isSafari && browserVersion >= 4.0);
		AjxEnv.isSafari3up		= (AjxEnv.isSafari && browserVersion >= 3.0) || AjxEnv.isChrome;
		AjxEnv.isSafari4up		= (AjxEnv.isSafari && browserVersion >= 4.0) || AjxEnv.isChrome;
		AjxEnv.isDesktop2up		= (AjxEnv.isDesktop && browserVersion >= 2.0);

		AjxEnv.browser = "[unknown]";
		if (AjxEnv.isOpera) 				{	AjxEnv.browser = "OPERA";	}
		else if (AjxEnv.isSafari3up)		{	AjxEnv.browser = "SAF3";	}
		else if (AjxEnv.isSafari)			{	AjxEnv.browser = "SAF";		}
		else if (AjxEnv.isCamino)			{	AjxEnv.browser = "CAM";		}
		else if (isWebTv)					{	AjxEnv.browser = "WEBTV";	}
		else if (isHotJava)					{	AjxEnv.browser = "HOTJAVA";	}
		else if (AjxEnv.isFirefox3up)		{	AjxEnv.browser = "FF3.0";	}
		else if (AjxEnv.isFirefox2_0up)		{	AjxEnv.browser = "FF2.0";	}
		else if (AjxEnv.isFirefox1_5up)		{	AjxEnv.browser = "FF1.5";	}
		else if (AjxEnv.isFirefox1up)		{	AjxEnv.browser = "FF1.0";	}
		else if (AjxEnv.isFirefox)			{	AjxEnv.browser = "FF";		}
		else if (AjxEnv.isPrism)			{	AjxEnv.browser = "PRISM";	}
		else if (AjxEnv.isNav7)				{	AjxEnv.browser = "NAV7";	}
		else if (AjxEnv.isNav6)				{	AjxEnv.browser = "NAV6";	}
		else if (AjxEnv.isNav4)				{	AjxEnv.browser = "NAV4";	}
		else if (AjxEnv.isIE8)				{	AjxEnv.browser = "IE8";		}
		else if (AjxEnv.isIE7)				{	AjxEnv.browser = "IE7";		}
		else if (AjxEnv.isIE6)				{	AjxEnv.browser = "IE6";		}
		else if (AjxEnv.isIE5)				{	AjxEnv.browser = "IE5";		}
		else if (AjxEnv.isIE4)				{	AjxEnv.browser = "IE4";		}
		else if (AjxEnv.isIE3)				{	AjxEnv.browser = "IE";		}
		else if (AjxEnv.isDesktop)			{	AjxEnv.browser = "ZDESKTOP";}

		AjxEnv.platform = "[unknown]";
		if (AjxEnv.isWindows)				{	AjxEnv.platform = "Win";	}
		else if (AjxEnv.isMac)				{	AjxEnv.platform = "Mac";	}
		else if (AjxEnv.isLinux)			{	AjxEnv.platform = "Linux";	}
	}

	// setup some global setting we can check for high resolution
	if (AjxEnv.isIE) {
		AjxEnv.isNormalResolution = true;
		AjxEnv.ieScaleFactor = screen.deviceXDPI / screen.logicalXDPI;
		if (AjxEnv.ieScaleFactor > 1) {
			AjxEnv.isNormalResolution = false;
		}
	}

	// show transparent PNGs on platforms that support them well (eg: all but IE and Linux)
	// MOW: having trouble getting safari to render transparency for shadows, skipping there, too
	AjxEnv.useTransparentPNGs = !AjxEnv.isIE && !AjxEnv.isLinux && !AjxEnv.isSafari;
	AjxEnv._inited = !AjxEnv.isIE;

	// test for safari nightly
	if (AjxEnv.isSafari) {
		var webkit = AjxEnv.getWebkitVersion();
		AjxEnv.isSafariNightly = (webkit && webkit['is_nightly']);
		// if not safari v3 or the nightly, assume we're dealing with v2  :/
		AjxEnv.isSafari2 = !AjxEnv.isSafari3 && !AjxEnv.isSafariNightly;
	}
};

// code provided by webkit authors to determine if nightly browser
AjxEnv.getWebkitVersion =
function() {
	var webkit_version;
	var regex = new RegExp("\\(.*\\) AppleWebKit/(.*) \\((.*)");
	var matches = regex.exec(navigator.userAgent);
	if (matches) {
		var version = matches[1];
		var bits = version.split(".");
		var is_nightly = (version[version.length - 1] == "+");
		var minor = is_nightly ? "+" : parseInt(bits[1]);
		// If minor is Not a Number (NaN) return an empty string
		if (isNaN(minor)) minor = "";

		webkit_version = { major:parseInt(bits[0]), minor:minor, is_nightly:is_nightly};
	}
	return {major: webkit_version['major'], minor: webkit_version['minor'], is_nightly: webkit_version['is_nightly']};
};


AjxEnv.parseUA();
