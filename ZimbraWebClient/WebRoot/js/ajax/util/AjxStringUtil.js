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
 * Default constructor does nothing (static class).
 * @constructor
 * @class
 * This class provides static methods to perform miscellaneous string-related utility functions.
 *
 * @author Ross Dargahi
 * @author Roland Schemers
 * @author Conrad Damon
 */
AjxStringUtil = function() {
}

AjxStringUtil.TRIM_RE = /^\s+|\s+$/g;
AjxStringUtil.COMPRESS_RE = /\s+/g;
AjxStringUtil.ELLIPSIS = " ... ";

AjxStringUtil.makeString =
function(val) {
	return val ? String(val) : "";
};

AjxStringUtil.capitalize = function(str) {
	return str.charAt(0).toUpperCase() + str.substr(1).toLowerCase();
};

/**
 * Converts the given text to mixed-case. The input text is one or more words
 * separated by spaces. The output is a single word in mixed (or camel) case.
 * 
 * @param {string}	text		text to convert
 * @param {string|RegEx}	sep		text separator (defaults to any space)
 * @param {boolean}	camel		if <code>true</code>, first character of result is lower-case
 * @return	{string}	the resulting string
 */
AjxStringUtil.toMixed =
function(text, sep, camel) {
	if (!text || (typeof text != "string")) { return ""; }
	sep = sep || /\s+/;
	var wds = text.split(sep);
	var newText = [];
	newText.push(camel ? wds[0].toLowerCase() : wds[0].substring(0, 1).toUpperCase() + wds[0].substring(1).toLowerCase());
	for (var i = 1; i < wds.length; i++) {
		newText.push(wds[i].substring(0, 1).toUpperCase() + wds[i].substring(1).toLowerCase());
	}
	return newText.join("");
};

/**
 * Removes white space from the beginning and end of a string, optionally compressing internal white space. By default, white
 * space is defined as a sequence of  Unicode whitespace characters (\s in regexes). Optionally, the user can define what
 * white space is by passing it as an argument.
 *
 * <p>TODO: add left/right options</p>
 *
 * @param {string}	str      	the string to trim
 * @param {boolean}	compress 	whether to compress internal white space to one space
 * @param {string}	space    	a string that represents a user definition of white space
 * @return	{string}	a trimmed string
 */
AjxStringUtil.trim =
function(str, compress, space) {

	if (!str) {return "";}

	var trim_re = AjxStringUtil.TRIM_RE;

	var compress_re = AjxStringUtil.COMPRESS_RE;
	if (space) {
		trim_re = new RegExp("^" + space + "+|" + space + "+$", "g");
		compress_re = new RegExp(space + "+", "g");
	} else {
		space = " ";
	}
	str = str.replace(trim_re, '');
	if (compress) {
		str = str.replace(compress_re, space);
	}

	return str;
};

/**
 * Returns the string repeated the given number of times.
 *
 * @param {string}	str		a string
 * @param {number}	num		number of times to repeat the string
 * @return	{string}	the string
 */
AjxStringUtil.repeat =
function(str, num) {
	var text = "";
	for (var i = 0; i < num; i++) {
		text += str;
	}
	return text;
};

/**
 * Gets the units from size string.
 * 
 * @param	{string}	sizeString	the size string
 * @return	{string}	the units
 */
AjxStringUtil.getUnitsFromSizeString =
function(sizeString) {
	var units = "px";
	if (typeof(sizeString) == "string") {
		var digitString = Number(parseInt(sizeString,10)).toString();
		if (sizeString.length > digitString.length) {
			units = sizeString.substr(digitString.length, (sizeString.length-digitString.length));
			if (!(units=="em" || units=="ex" || units=="px" || units=="in" || units=="cm" == units=="mm" || units=="pt" || units=="pc" || units=="%")) {
				units = "px";
			}
		}
	}
	return units;
};

/**
* Splits a string, ignoring delimiters that are in quotes or parentheses. Comma
* is the default split character, but the user can pass in a string of multiple
* delimiters. It can handle nested parentheses, but not nested quotes.
*
* <p>TODO: handle escaped quotes</p>
*
* @param {string} str	the string to split
* @param {string}	[dels]	an optional string of delimiter characters
* @return	{array}	an array of strings
*/
AjxStringUtil.split =
function(str, dels) {

	if (!str) {return [];}
	var i = 0;
	dels = dels ? dels : ',';
	var isDel = new Object();
	if (typeof dels == 'string') {
		isDel[dels] = 1;
	} else {
		for (i = 0; i < dels.length; i++) {
			isDel[dels[i]] = 1;
		}
	}

	var q = false;
	var p = 0;
	var start = 0;
	var chunk;
	var chunks = [];
	var j = 0;
	for (i = 0; i < str.length; i++) {
		var c = str.charAt(i);
		if (c == '"') {
			q = !q;
		} else if (c == '(') {
			p++;
		} else if (c == ')') {
			p--;
		} else if (isDel[c]) {
			if (!q && !p) {
				chunk = str.substring(start, i);
				chunks[j++] = chunk;
				start = i + 1;
			}
		}
	}
	chunk = str.substring(start, str.length);
	chunks[j++] = chunk;

	return chunks;
};

/**
 * Wraps text to the given length and optionally quotes it. The level of quoting in the
 * source text is preserved based on the prefixes. Special lines such as email headers
 * always start a new line.
 *
 * @param {hash}	params	a hash of parameters
 * @param {string}      params.text 				the text to be wrapped
 * @param {number}      [params.len=80]				the desired line length of the wrapped text, defaults to 80
 * @param {string}      [params.pre]				an optional string to prepend to each line (useful for quoting)
 * @param {string}      [params.before]				text to prepend to final result
 * @param {string}      [params.after]				text to append to final result
 * @param {boolean}		[params.preserveReturns]	if true, don't combine small lines
 *
 * @return	{string}	the wrapped/quoted text
 */
AjxStringUtil.wordWrap =
function(params) {

	if (!(params && params.text)) { return ""; }

	var text = params.text;
	var before = params.before || "", after = params.after || "";

	// For HTML, just surround the content with the before and after, which is
	// typically a block-level element that puts a border on the left
	if (params.htmlMode) {
		return [before, text, after].join("");
	}

	var len = params.len || 80;
	var pre = params.pre || "";
	var eol = "\n";

	text = AjxStringUtil.trim(text);
	text = text.replace(/\n\r/g, eol);
	var lines = text.split(eol);
	var words = [];

	// Divides lines into words. Each word is part of a hash that also has
	// the word's prefix, whether it's a paragraph break, and whether it's
	// special (cannot be wrapped into a previous line)
	for (var l = 0, llen = lines.length; l < llen; l++) {
		var line = AjxStringUtil.trim(lines[l]);
		// get this line's prefix
		var m = line.match(/^([\s>\|]+)/);
		var prefix = m ? m[1] : "";
		if (prefix) {
			line = line.substr(prefix.length);
		}
		line = line.replace(/\s+/g, " ");	// compress all space into single spaces
		var wds = line.split(" ");
		if (wds && wds[0] && wds[0].length) {
			var isSpecial = AjxStringUtil.MSG_SEP_RE.test(line) || AjxStringUtil.COLON_RE.test(line) ||
							AjxStringUtil.HDR_RE.test(line) || AjxStringUtil.SIG_RE.test(line);
			for (var w = 0, wlen = wds.length; w < wlen; w++) {
				var lastWord = params.preserveReturns && (w == wlen - 1);
				words.push({w:wds[w], p:prefix, special:(isSpecial && w == 0), lastWord:lastWord});
			}
		} else {
			words.push({para:true, p:prefix});	// paragraph marker
		}
	}

	// Take the array of words and put them back together. We break for a new line
	// when we hit the max line length, change prefixes, or hit a special word.
	var max = params.len || 72;
	var addPrefix = params.pre || "";
	var apl = addPrefix.length;
	var result = "", curLen = 0, wds = [], curP = null;
	for (var i = 0, len = words.length; i < len; i++) {
		var word = words[i];
		var w = word.w, p = word.p;
		var sp = wds.length ? 1 : 0;
		var pl = (curP === null) ? 0 : curP.length;
		if (word.para) {
			// paragraph break - output what we have, add a blank line
			if (wds.length) {
				result += addPrefix + (curP || "") + wds.join(" ") + eol;
			}
			result += addPrefix + p + eol;
			wds = [];
			curLen = 0;
			curP = null;
		} else if ((apl + pl + curLen + sp + w.length <= max) && (p == curP || curP === null) && !word.special) {
			// still room left on the current line, add the word
			wds.push(w);
			curLen += w.length + sp;
			curP = p;
			if (word.lastWord && words[i + 1]) {
				words[i + 1].special = true;
			}
		} else {
			// output what we have and start a new line
			if (wds.length) {
				result += addPrefix + (curP || "") + wds.join(" ") + eol;
			}
			wds = [w];
			curLen = w.length;
			curP = p;
			if (word.lastWord && words[i + 1]) {
				words[i + 1].special = true;
			}
		}
	}

	// handle last line
	if (wds.length) {
		result += addPrefix + curP + wds.join(" ") + eol;
	}

	return [before, result, after].join("");
};

/**
 * Quotes text with the given quote character. For HTML, surrounds the text with the
 * given strings. Does no wrapping.
 *
 * @param {hash}	params	a hash of parameters
 * @param {string}      params.text 				the text to be wrapped
 * @param {string}      [params.pre]				prefix for quoting
 * @param {string}      [params.before]				text to prepend to final result
 * @param {string}      [params.after]				text to append to final result
 *
 * @return	{string}	the quoted text
 */
AjxStringUtil.quoteText =
function(params) {

	if (!(params && params.text)) { return ""; }

	var text = params.text;
	var before = params.before || "", after = params.after || "";

	// For HTML, just surround the content with the before and after, which is
	// typically a block-level element that puts a border on the left
	if (params.htmlMode || !params.pre) {
		return [before, text, after].join("");
	}

	var len = params.len || 80;
	var pre = params.pre || "";
	var eol = "\n";

	text = AjxStringUtil.trim(text);
	text = text.replace(/\n\r/g, eol);
	var lines = text.split(eol);
	var result = [];

	for (var l = 0, llen = lines.length; l < llen; l++) {
		var line = AjxStringUtil.trim(lines[l]);
		result.push(pre + line + eol);
	}

	return before + result.join("") + after;
};

/**
 * Returns true if the character for the given key is considered printable.
 *
 * @param keycode	a numeric keycode (not a character code)
 * @return	{boolean}	<code>true</code> if the character for the given key is considered printable
 */
AjxStringUtil.IS_PRINT_CODE = {};
var print_codes = [32,48,49,50,51,52,53,54,55,56,57,59,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,
                   81,82,83,84,85,86,87,88,89,90,96,97,98,99,100,101,102,103,104,105,106,107,109,110,111,186,
                   187,188,189,190,191,192,219,220,221,222];
var l = print_codes.length;
for (var i = 0; i < l; i++) {
	AjxStringUtil.IS_PRINT_CODE[print_codes[i]] = true;
}

AjxStringUtil.isPrintKey =
function(keycode) {
	return AjxStringUtil.IS_PRINT_CODE[keycode];
};

AjxStringUtil.SHIFT_CHAR = { 48:')', 49:'!', 50:'@', 51:'#', 52:'$', 53:'%', 54:'^', 55:'&', 56:'*', 57:'(',
							59:':', 186:':', 187:'+', 188:'<', 189:'_', 190:'>', 191:'?', 192:'~',
							219:'{', 220:'|', 221:'}', 222:'"' };

/**
* Returns the character for the given key, taking the shift key into consideration.
*
* @param {number}	keycode	a numeric keycode (not a character code)
* @param {boolean}	shifted		whether the shift key is down
* @return	{char}	a character
*/
AjxStringUtil.shiftChar =
function(keycode, shifted) {
	return shifted ? AjxStringUtil.SHIFT_CHAR[keycode] || String.fromCharCode(keycode) : String.fromCharCode(keycode);
};

/**
 * Does a diff between two strings, returning the index of the first differing character.
 *
 * @param {string}	str1	a string
 * @param {string}	str2	another string
 * @return	{number}	the index at which they first differ
 */
AjxStringUtil.diffPoint =
function(str1, str2) {
	if (!(str1 && str2)) {
		return 0;
	}
	var len = Math.min(str1.length, str2.length);
	var i = 0;
	while (i < len && (str1.charAt(i) == str2.charAt(i))) {
		i++;
	}
	return i;
};

/*
* DEPRECATED
*
* Replaces variables in a string with values from a list. The variables are
* denoted by a '$' followed by a number, starting from 0. For example, a string
* of "Hello $0, meet $1" with a list of ["Harry", "Sally"] would result in the
* string "Hello Harry, meet Sally".
*
* @param str		the string to resolve
* @param values	 	an array of values to interpolate
* @returns			a string with the variables replaced
* 
* @deprecated
*/
AjxStringUtil.resolve =
function(str, values) {
	DBG.println(AjxDebug.DBG1, "Call to deprecated function AjxStringUtil.resolve");
	return AjxMessageFormat.format(str, values);
};

/**
 * Encodes a complete URL. Leaves delimiters alone.
 *
 * @param {string}	str	the string to encode
 * @return	{string}	the encoded string
 */
AjxStringUtil.urlEncode =
function(str) {
	if (!str) return "";
	var func = window.encodeURL || window.encodeURI;
	return func(str);
};

/**
 * Encodes a string as if it were a <em>part</em> of a URL. The
 * difference between this function and {@link AjxStringUtil.urlEncode}
 * is that this will also encode the following delimiters:
 *
 * <pre>
 *  			: / ? & =
 * </pre>
 * 
 * @param	{string}	str		the string to encode
 * @return	{string}	the resulting string
 */
AjxStringUtil.urlComponentEncode =
function(str) {
	if (!str) return "";
	var func = window.encodeURLComponent || window.encodeURIComponent;
	return func(str);
};

/**
 * Decodes a complete URL.
 *
 * @param {string}	str	the string to decode
 * @return	{string}	the decoded string
 */
AjxStringUtil.urlDecode =
function(str) {
	if (!str) return "";
	var func = window.decodeURL || window.decodeURI;
	return func(str);
};

/**
 * Decodes a string as if it were a <em>part</em> of a URL. Falls back
 * to unescape() if necessary.
 * 
 * @param	{string}	str		the string to decode
 * @return	{string}	the decoded string
 */
AjxStringUtil.urlComponentDecode =
function(str) {
	if (!str) return "";
	var func = window.decodeURLComponent || window.decodeURIComponent;
	var result;
	try {
		result = func(str);
	} catch(e) {
		result = unescape(str);
	}

	return result || str;
};

AjxStringUtil.ENCODE_MAP = { '>' : '&gt;', '<' : '&lt;', '&' : '&amp;' };

/**
 * HTML-encodes a string.
 *
 * @param {string}	str	the string to encode
 * @param	{boolean}	includeSpaces		if <code>true</code>, to include encoding spaces
 * @return	{string}	the encoded string
 */
AjxStringUtil.htmlEncode =
function(str, includeSpaces) {
	if (!str) {return "";}

	if (!AjxEnv.isSafari || AjxEnv.isSafariNightly) {
		if (includeSpaces) {
			return str.replace(/[<>&]/g, function(htmlChar) { return AjxStringUtil.ENCODE_MAP[htmlChar]; }).replace(/  /g, ' &nbsp;');
		} else {
			return str.replace(/[<>&]/g, function(htmlChar) { return AjxStringUtil.ENCODE_MAP[htmlChar]; });
		}
	} else {
		if (includeSpaces) {
			return str.replace(/[&]/g, '&amp;').replace(/  /g, ' &nbsp;').replace(/[<]/g, '&lt;').replace(/[>]/g, '&gt;');
		} else {
			return str.replace(/[&]/g, '&amp;').replace(/[<]/g, '&lt;').replace(/[>]/g, '&gt;');
		}
	}
};

/**
 * Decodes the string.
 * 
 * @param	{string}	str		the string to decode
 * @param	{boolean}	decodeSpaces	if <code>true</code>, decode spaces
 * @return	{string}	the string
 */
AjxStringUtil.htmlDecode =
function(str, decodeSpaces) {
	 
	 if(decodeSpaces)
	 	str = str.replace(/&nbsp;/g," ");
	 	
     return str.replace(/&amp;/g, "&").replace(/&lt;/g, "<").replace(/&gt;/g, ">");
};

/**
 * Removes HTML tags from the given string.
 * 
 * @param {string}	str			text from which to strip tags
 * @param {boolean}	removeContent	if <code>true</code>, also remove content within tags
 * @return	{string}	the resulting HTML string
 */
AjxStringUtil.stripTags =
function(str, removeContent) {
	if (!str) { return ""; }
	if (removeContent) {
		str = str.replace(/(<(\w+)[^>]*>).*(<\/\2[^>]*>)/, "$1$3");
	}
	return str.replace(/<\/?[^>]+>/gi, '');
};

/**
 * Converts the string to HTML.
 * 
 * @param	{string}	str		the string
 * @return	{string}	the resulting string
 */
AjxStringUtil.convertToHtml =
function(str) {
	if (!str) {return "";}
	str = str
		.replace(/&/mg, "&amp;")
		.replace(/  /mg, " &nbsp;")
		.replace(/^ /mg, "&nbsp;")
		.replace(/\t/mg, "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
		.replace(/</mg, "&lt;")
		.replace(/>/mg, "&gt;")
		.replace(/\r?\n/mg, "<br>");
	return str;
};

AjxStringUtil.SPACE_ENCODE_MAP = { ' ' : '&nbsp;', '>' : '&gt;', '<' : '&lt;', '&' : '&amp;' , '\n': '<br>'};

/**
 * HTML-encodes a string.
 *
 * @param {string}	str	the string to encode
 * 
 * @private
 */
AjxStringUtil.htmlEncodeSpace =
function(str) {
	if (!str) { return ""; }
	return str.replace(/[&]/g, '&amp;').replace(/ /g, '&nbsp;').replace(/[<]/g, '&lt;').replace(/[>]/g, '&gt;');
};

// this function makes sure a leading space is preservered, takes care of tabs,
// then finally takes replaces newlines with <br>'s
AjxStringUtil.nl2br =
function(str) {
	if (!str) return "";
	return str.replace(/^ /mg, "&nbsp;").
		// replace(/\t/g, "<pre style='display:inline;'>\t</pre>").
		// replace(/\t/mg, "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").
		replace(/\t/mg, "<span style='white-space:pre'>\t</span>").
		replace(/\n/g, "<br>");
};

AjxStringUtil.xmlEncode =
function(str) {
	if (str) {
		// bug fix #8779 - safari barfs if "str" is not a String type
		str = "" + str;
		return str.replace(/&/g,"&amp;").replace(/</g,"&lt;");
	}
	return "";
};

AjxStringUtil.xmlDecode =
function(str) {
	return str ? str.replace(/&amp;/g,"&").replace(/&lt;/g,"<") : "";
};

AjxStringUtil.xmlAttrEncode =
function(str) {
	return str ? str.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/\x22/g, '&quot;').replace(/\x27/g,"&apos;") : "";
};

AjxStringUtil.xmlAttrDecode =
function(str) {
	return str ? str.replace(/&amp;/g,"&").replace(/&lt;/g,"<").replace(/&quot;/g, '"').replace(/&apos;/g,"'") : "";
};

AjxStringUtil.__RE_META = { " ":" ", "\n":"\\n", "\r":"\\r", "\t":"\\t" };
AjxStringUtil.__reMetaEscape = function($0, $1) {
	return AjxStringUtil.__RE_META[$1] || "\\"+$1;
};
AjxStringUtil.regExEscape =
function(str) {
	return str.replace(/(\W)/g, AjxStringUtil.__reMetaEscape);
};

AjxStringUtil._calcDIV = null; // used by 'clip()' and 'wrap()' functions

AjxStringUtil.calcDIV =
function() {
	if (AjxStringUtil._calcDIV == null) {
		AjxStringUtil._calcDIV = document.createElement("div");
		AjxStringUtil._calcDIV.style.zIndex = 0;
		AjxStringUtil._calcDIV.style.position = DwtControl.ABSOLUTE_STYLE;
		AjxStringUtil._calcDIV.style.visibility = "hidden";
		document.body.appendChild(AjxStringUtil._calcDIV);
	}
	return AjxStringUtil._calcDIV;
};

/**
 * Clips a string at "pixelWidth" using using "className" on hidden 'AjxStringUtil._calcDIV'.
 * Returns "origString" with "..." appended if clipped.
 *
 * NOTE: The same CSS style ("className") must be assigned to both the intended
 * display area and the hidden 'AjxStringUtil._calcDIV'.  "className" is
 * optional; if supplied, it will be assigned to 'AjxStringUtil._calcDIV' to
 * handle different CSS styles ("className"s) on same page.
 *
 * NOTE2: MSIE Benchmark - clipping an average of 17 characters each over 190
 * iterations averaged 27ms each (5.1 seconds total for 190)
 * 
 * @private
 */
AjxStringUtil.clip =
function(origString, pixelWidth, className) {
	var calcDIV = AjxStringUtil.calcDIV();
	if (arguments.length == 3) calcDIV.className = className;
	//calcDIV.innerHTML = "<div>" + origString + "</div>"; // prevents screen flash in IE?
	calcDIV.innerHTML = origString;
	if (calcDIV.offsetWidth <= pixelWidth) return origString;

	for (var i=origString.length-1; i>0; i--) {
		var newString = origString.substr(0,i);
		calcDIV.innerHTML = newString + AjxStringUtil.ELLIPSIS;
		if (calcDIV.offsetWidth <= pixelWidth) return newString + AjxStringUtil.ELLIPSIS;
	}
	return origString;
};

AjxStringUtil.clipByLength =
function(str,clipLen) {
	var len = str.length;
	return (len <= clipLen)
		?  str
		: [str.substr(0,clipLen/2), '...', str.substring(len - ((clipLen/2) - 3),len)].join("");
};

/**
 * Forces a string to wrap at "pixelWidth" using "className" on hidden 'AjxStringUtil._calcDIV'.
 * Returns "origString" with "&lt;br&gt;" tags inserted to force wrapping.
 * Breaks string on embedded space characters, EOL ("/n") and "&lt;br&gt;" tags when possible.
 *
 * @returns		"origString" with "&lt;br&gt;" tags inserted to force wrapping.
 * 
 * @private
 */
AjxStringUtil.wrap =
function(origString, pixelWidth, className) {
	var calcDIV = AjxStringUtil.calcDIV();
	if (arguments.length == 3) calcDIV.className = className;

	var newString = "";
	var newLine = "";
	var textRows = origString.split("/n");
	for (var trCount = 0; trCount < textRows.length; trCount++) {
		if (trCount != 0) {
			newString += newLine + "<br>";
			newLine = "";
		}
		htmlRows = textRows[trCount].split("<br>");
		for (var hrCount=0; hrCount<htmlRows.length; hrCount++) {
			if (hrCount != 0) {
				newString += newLine + "<br>";
				newLine = "";
			}
			words = htmlRows[hrCount].split(" ");
			var wCount=0;
			while (wCount<words.length) {
				calcDIV.innerHTML = newLine + " " + words[wCount];
				var newLinePixels = calcDIV.offsetWidth;
				if (newLinePixels > pixelWidth) {
					// whole "words[wCount]" won't fit on current "newLine" - insert line break, avoid incrementing "wCount"
					calcDIV.innerHTML = words[wCount];
					newLinePixels = newLinePixels - calcDIV.offsetWidth;
					if ( (newLinePixels >= pixelWidth) || (calcDIV.offsetWidth <= pixelWidth) ) {
						// either a) excess caused by <space> character or b) will fit completely on next line
						// so just break without incrementing "wCount" and append next time
						newString += newLine + "<br>";
						newLine = "";
					}
					else { // must break "words[wCount]"
						var keepLooping = true;
						var atPos = 0;
						while (keepLooping) {
							atPos++;
							calcDIV.innerHTML = newLine + " " + words[wCount].substring(0,atPos);
							keepLooping = (calcDIV.offsetWidth <= pixelWidth);
						}
						atPos--;
						newString += newLine + words[wCount].substring(0,atPos) + "<br>";
						words[wCount] = words[wCount].substr(atPos);
						newLine = "";
					}
				} else { // doesn't exceed pixelWidth, append to "newLine" and increment "wCount"
					newLine += " " + words[wCount];
					wCount++;
				}
			}
		}
	}
	newString += newLine;
	return newString;
};

// Regexes for finding non-quoted content
AjxStringUtil.MSG_SEP_RE = new RegExp("^\\s*--+\\s*(" + AjxMsg.origMsg + "|" + AjxMsg.forwardedMessage + ")\\s*--+", "i");
AjxStringUtil.SIG_RE = /^(- ?-+)|(__+)\r?$/;
AjxStringUtil.COLON_RE = /\S+:$/;
AjxStringUtil.PREFIX_RE = /^\s*(&gt;|>|\|)/;
AjxStringUtil.BRACKET_RE = /^\s*\[.+\]\s*$/;
AjxStringUtil.LINE_RE = /^\s*_{30,}\s*$/;
AjxStringUtil.BLANK_RE = /^\s*$/;
AjxStringUtil.SPLIT_RE = /\r\n|\r|\n/;
AjxStringUtil.HDR_RE = /^\s*\w+:/;
AjxStringUtil.HTML_BLANK_RE = /^\s*(<br\s*\/?>)*\s*$/i;
AjxStringUtil.HTML_BR_RE = /<br\s*\/?>/gi;
AjxStringUtil.HTML_BODY_RE = /<body(\s|>)/i;
AjxStringUtil.HTML_QUOTE_PRE_RE = /^\s*<blockquote/i;
AjxStringUtil.HTML_QUOTE_POST_RE = /^\s*<\/blockquote>/i;
AjxStringUtil.HTML_QUOTE_COLOR = "rgb(16, 16, 255)";

/**
 * Returns a list of chunks of top-level content in a message body. Top-level
 * content is what was actually typed by the sender. We attempt to exclude quoted
 * content and signatures.
 *
 * The following lines/blocks (and variants) and any text after them are ignored:
 *
 * 		----- Original Message -----
 *
 * 		----- Forwarded Message -----
 *
 *		--
 *		some signature text
 *
 *		______________________________		|
 *											| Outlook 2003 does this
 *		From:								|
 *
 * Lines that begin with a prefix character ("&gt;" or "|") are ignored. The
 * following lines/blocks are ignored if they precede a line that begins with a
 * prefix character:
 *
 * 		Fred Flintstone <fred@bedrock.org> wrote:
 *
 * 		Fred Flintstone <fred@bedrock.org> wrote:
 * 		[snipped]
 *
 * Since quoted text may be interleaved with original text, we may return several
 * chunks of original text. That is so they may be separated when they are quoted.
 *
 * @param {string}	text		a message body
 * 
 * @private
 */
AjxStringUtil.getTopLevel =
function(text, eol, htmlMode) {
	var isHtml = /<br|<div/i.test(text);
	var split = isHtml ? AjxStringUtil.HTML_BR_RE : AjxStringUtil.SPLIT_RE;
	var eol = isHtml ? '<br>' : '\n';
	text = AjxStringUtil._trimBlankLines(text, split, eol, isHtml);
	var lines = text.split(split);
	var len = lines.length;
	var i = 0, start = 0;
	var chunks = [];
	var skipping = false;

	while (i < len) {
		var wasSkipping = skipping;
		var skip = AjxStringUtil._linesToSkip(lines, i, htmlMode);
		skipping = (skip > 0);
		if (wasSkipping && !skipping) {
			start = i;
		} else if (!wasSkipping && skipping && i > start) {
			var chunk = AjxStringUtil._trimBlankLines(lines.slice(start, i).join(eol), split, eol, htmlMode);
			if (chunk && chunk.length) {
				chunks.push(chunk);
			}
		}
		i += skipping ? skip : 1;
	}

	if (!skipping && i > start) {
		var chunk = AjxStringUtil._trimBlankLines(lines.slice(start, i).join(eol), split, eol, htmlMode);
		if (chunk && chunk.length) {
			chunks.push(chunk);
		}
	}

	return chunks;
};

/**
 * Starting at a given line, returns the number of lines that should be skipped because
 * they are quoted (or signature) content.
 * 
 * @param {array}	lines		lines of text
 * @param {number}	i			index of current line
 * @param {boolean}	htmlMode	if <code>true</code>, text is HTML
 * 
 * @private
 */
AjxStringUtil._linesToSkip =
function(lines, i, htmlMode) {
	var len = lines.length;
	var skip = 0;
	var start = i;
	var line = htmlMode ? AjxStringUtil.stripTags(lines[i]) : lines[i];
	
	if (AjxStringUtil.MSG_SEP_RE.test(line)) {
		skip = len - i;
	} else if (AjxStringUtil.PREFIX_RE.test(line)) {
		while (i < lines.length && (AjxStringUtil.PREFIX_RE.test(line) || AjxStringUtil.BLANK_RE.test(line))) {
			i++;
			line = htmlMode ? AjxStringUtil.stripTags(lines[i]) : lines[i];
		}
		skip = i - start;
	} else if (AjxStringUtil.HTML_QUOTE_PRE_RE.test(line) && line.indexOf(AjxStringUtil.HTML_QUOTE_COLOR) != -1) {
		while (i < lines.length && (!AjxStringUtil.HTML_QUOTE_POST_RE.test(line))) {
			i++;
			line = lines[i];
		}
		if (i < lines.length) {
			lines[i] = lines[i].replace(AjxStringUtil.HTML_QUOTE_POST_RE, "");
		}
		skip = i - start;
	} else if (AjxStringUtil.COLON_RE.test(line)) {
		var idx = AjxStringUtil._nextNonBlankLineIndex(lines, i + 1, htmlMode);
		var line1 = (idx == -1) ? null : htmlMode ? AjxStringUtil.stripTags(lines[idx]) : lines[idx];
		if (line1 && AjxStringUtil.PREFIX_RE.test(line1)) {
			skip = idx - i;
		} else {
			if (idx != -1) {
				idx = AjxStringUtil._nextNonBlankLineIndex(lines, idx + 1, htmlMode);
			}
			var line2 = (idx == -1) ? null : htmlMode ? AjxStringUtil.stripTags(lines[idx]) : lines[idx];
			if (line2 && AjxStringUtil.BRACKET_RE.test(line1) && AjxStringUtil.PREFIX_RE.test(line2)) {
				skip = idx - i;
			}
		}
	} else if (AjxStringUtil.LINE_RE.test(line)) {
		var idx = AjxStringUtil._nextNonBlankLineIndex(lines, i + 1, htmlMode);
		var line1 = (idx == -1) ? null : htmlMode ? AjxStringUtil.stripTags(lines[idx]) : lines[idx];
		if (line1 && AjxStringUtil.HDR_RE.test(line1)) {
			skip = len - i;
		}
	} else if (AjxStringUtil.SIG_RE.test(line)) {
		skip = len - i;
	}
	return skip;
};

/**
 * Returns the index of the next non-blank line
 * 
 * @param {array}	lines		lines of text
 * @param {number}	i			index of current line
 * @param {boolean}	htmlMode	if <code>true</code>, text is HTML
 * 
 * @private
 */
AjxStringUtil._nextNonBlankLineIndex =
function(lines, i, htmlMode) {
	while (i < lines.length && AjxStringUtil.BLANK_RE.test(htmlMode ? AjxStringUtil.stripTags(lines[i]) : lines[i])) {
		i++;
	}
	return ((i < lines.length) ? i : -1);
};

/**
 * Removes blank lines from the beginning and end of text
 * 
 * @param {string}	text		a message body
 * @param {string|RegExp}	split		used to divide text into lines
 * @param {string}	eol		the eol sequence
 * @param {boolean}	htmlMode	if <code>true</code>, text is HTML
 * 
 * @private
 */
AjxStringUtil._trimBlankLines =
function(text, split, eol, htmlMode) {
	var lines = text.split(split);
	var len = lines.length;
	var regEx = htmlMode ? AjxStringUtil.HTML_BLANK_RE : AjxStringUtil.BLANK_RE;
	var i = 0;
	while (i < len && regEx.test(lines[i])) {
		i++;
	}
	var j = len;
	while (j > 0 && regEx.test(lines[j - 1])) {
		j--;
	}
	if (i != 0 || j != len) {
		text = lines.slice(i, j).join(eol) + eol;
	}
	
	// if the HTML text has a <body> tag, remove leading/trailing returns
	if (htmlMode && AjxStringUtil.HTML_BODY_RE.test(text)) {
		text = text.replace(/<body\s*[^>]*>(<br\s*\/?>)+/i, "<body>");
		text = text.replace(/(<br\s*\/?>)+<\/body>/i, "</body>");
	}

	return text;
};

// Converts a HTML document represented by a DOM tree to text
// XXX: There has got to be a better way of doing this!
AjxStringUtil._NO_LIST = 0;
AjxStringUtil._ORDERED_LIST = 1;
AjxStringUtil._UNORDERED_LIST = 2;
AjxStringUtil._INDENT = "    ";
AjxStringUtil._NON_WHITESPACE = /\S+/;
AjxStringUtil._LF = /\n/;

AjxStringUtil.convertHtml2Text =
function(domRoot) {
	if (!domRoot) { return null; }

	if (typeof domRoot == "string") {
		var domNode = document.createElement("SPAN");
		domNode.innerHTML = domRoot;
		domRoot = domNode;
	}
	var text = [];
	var idx = 0;
	var ctxt = {};
	this._traverse(domRoot, text, idx, AjxStringUtil._NO_LIST, 0, 0, ctxt);
	return text.join("");
};

AjxStringUtil._traverse =
function(el, text, idx, listType, listLevel, bulletNum, ctxt) {
	var nodeName = el.nodeName.toLowerCase();

	if (nodeName == "#text") {
		if (el.nodeValue.search(AjxStringUtil._NON_WHITESPACE) != -1) {
			if (ctxt.lastNode == "ol" || ctxt.lastNode == "ul")
				text[idx++] = "\n";
			if (ctxt.isPreformatted)
				text[idx++] = AjxStringUtil.trim(el.nodeValue) + " ";
			else
				text[idx++] = AjxStringUtil.trim(el.nodeValue.replace(AjxStringUtil._LF, " "), true) + " ";
		}
	} else if (nodeName == "p") {
		text[idx++] = "\n\n";
	} else if (listType == AjxStringUtil._NO_LIST && (nodeName == "br" || nodeName == "hr")) {
		text[idx++] = "\n";
	} else if (nodeName == "ol" || nodeName == "ul") {
		text[idx++] = "\n";
		if (el.parentNode.nodeName.toLowerCase() != "li" && ctxt.lastNode != "br"
			&& ctxt.lastNode != "hr")
			text[idx++] = "\n";
		listType = (nodeName == "ol") ? AjxStringUtil._ORDERED_LIST : AjxStringUtil._UNORDERED_LIST;
		listLevel++;
		bulletNum = 0;
	} else if (nodeName == "li") {
		for (var i = 0; i < listLevel; i++)
			text[idx++] = AjxStringUtil._INDENT;
		if (listType == AjxStringUtil._ORDERED_LIST)
			text[idx++] = bulletNum + ". ";
		else
			text[idx++] = "\u2022 "; // TODO LmMsg.bullet
	} else if (nodeName == "img") {
		if (el.alt && el.alt != "")
			text[idx++] = el.alt;
	} else if (nodeName == "tr" && el.parentNode.firstChild != el) {
		text[idx++] = "\n";
	} else if (nodeName == "td" && el.parentNode.firstChild != el) {
		text[idx++] = "\t";
	} else if (nodeName == "div") {
		text[idx++] = "\n";
	} else if (nodeName == "blockquote") {
		text[idx++] = "\n\n";
	} else if (nodeName == "pre") {
		ctxt.isPreformatted = true;
	} else if (nodeName == "#comment" ||
			   nodeName == "script" ||
			   nodeName == "select" ||
			   nodeName == "style") {
		return idx;
	}

	var childNodes = el.childNodes;
	var len = childNodes.length;
	for (var i = 0; i < len; i++) {
		var tmp = childNodes[i];
		if (tmp.nodeType == 1 && tmp.tagName.toLowerCase() == "li")
			bulletNum++;
		idx = this._traverse(tmp, text, idx, listType, listLevel, bulletNum, ctxt);
	}

	if (nodeName == "h1" || nodeName == "h2" || nodeName == "h3" || nodeName == "h4"
		|| nodeName == "h5" || nodeName == "h6") {
			text[idx++] = "\n";
			ctxt.list = false;
	} else if (nodeName == "pre") {
		ctxt.isPreformatted = false;
	} else if (nodeName == "li") {
		if (!ctxt.list)
			text[idx++] = "\n";
		ctxt.list = false;
	} else if (nodeName == "ol" || nodeName == "ul") {
		ctxt.list = true;
	} else if (nodeName != "#text") {
		ctxt.list = false;
	}

	ctxt.lastNode = nodeName;
	return idx;
};

/**
 * Sets the given name/value pairs into the given query string. Args that appear
 * in both will get the new value. The order of args in the returned query string
 * is indeterminate.
 *
 * @param args		[hash]		name/value pairs to add to query string
 * @param qsReset	[boolean]	if true, start with empty query string
 * 
 * @private
 */
AjxStringUtil.queryStringSet =
function(args, qsReset) {
	var qs = qsReset ? "" : location.search;
	if (qs.indexOf("?") == 0) {
		qs = qs.substr(1);
	}
	var qsArgs = qs.split("&");
	var newArgs = {};
	for (var i = 0; i < qsArgs.length; i++) {
		var f = qsArgs[i].split("=");
		newArgs[f[0]] = f[1];
	}
	for (var name in args) {
		newArgs[name] = args[name];
	}
	var pairs = [];
	var i = 0;
	for (var name in newArgs) {
		if (name) {
			pairs[i++] = [name, newArgs[name]].join("=");
		}
	}

	return "?" + pairs.join("&");
};

/**
 * Removes the given arg from the query string.
 *
 * @param {String}	qs	a query string
 * @param {String}	name	the arg name
 * 
 * @return	{String}	the resulting query string
 */
AjxStringUtil.queryStringRemove =
function(qs, name) {
	qs = qs ? qs : "";
	if (qs.indexOf("?") == 0) {
		qs = qs.substr(1);
	}
	var pairs = qs.split("&");
	var pairs1 = [];
	for (var i = 0; i < pairs.length; i++) {
		if (pairs[i].indexOf(name) != 0) {
			pairs1.push(pairs[i]);
		}
	}

	return "?" + pairs1.join("&");
};

/**
 * Returns the given object/primitive as a string.
 *
 * @param {primitive|Object}	o		an object or primitive
 * @return	{String}	the string
 */
AjxStringUtil.getAsString =
function(o) {
	return !o ? "" : (typeof(o) == 'object') ? o.toString() : o;
};

AjxStringUtil.isWhitespace = 
function(str) {
	return (str.charCodeAt(0) <= 32);
};

AjxStringUtil.isDigit = 
function(str) {
	var charCode = str.charCodeAt(0);
	return (charCode >= 48 && charCode <= 57);
};

AjxStringUtil.compareRight = 
function(a,b) {
	var bias = 0;
	var idxa = 0;
	var idxb = 0;
	var ca;
	var cb;

	for (; (idxa < a.length || idxb < b.length); idxa++, idxb++) {
		ca = a.charAt(idxa);
		cb = b.charAt(idxb);

		if (!AjxStringUtil.isDigit(ca) &&
			!AjxStringUtil.isDigit(cb))
		{
			return bias;
		}
		else if (!AjxStringUtil.isDigit(ca))
		{
			return -1;
		}
		else if (!AjxStringUtil.isDigit(cb))
		{
			return +1;
		}
		else if (ca < cb)
		{
			if (bias == 0) bias = -1;
		}
		else if (ca > cb)
		{
			if (bias == 0) bias = +1;
		}
	}
};

AjxStringUtil.natCompare = 
function(a, b) {
	var idxa = 0, idxb = 0;
	var nza = 0, nzb = 0;
	var ca, cb;

	while (idxa < a.length || idxb < b.length)
	{
		// number of zeroes leading the last number compared
		nza = nzb = 0;

		ca = a.charAt(idxa);
		cb = b.charAt(idxb);

		// ignore overleading spaces/zeros and move the index accordingly
		while (AjxStringUtil.isWhitespace(ca) || ca =='0') {
			nza = (ca == '0') ? (nza+1) : 0;
			ca = a.charAt(++idxa);
		}
		while (AjxStringUtil.isWhitespace(cb) || cb == '0') {
			nzb = (cb == '0') ? (nzb+1) : 0;
			cb = b.charAt(++idxb);
		}

		// current index points to digit in both str
		if (AjxStringUtil.isDigit(ca) && AjxStringUtil.isDigit(cb)) {
			var result = AjxStringUtil.compareRight(a.substring(idxa), b.substring(idxb));
			if (result && result!=0) {
				return result;
			}
		}

		if (ca == 0 && cb == 0) {
			return nza - nzb;
		}

		if (ca < cb) {
			return -1;
		} else if (ca > cb) {
			return +1;
		}

		++idxa; ++idxb;
	}
};

AjxStringUtil.clipFile =
function(fileName, limit) {
	var index = fileName.lastIndexOf('.');
	var len = index ? (index + 1) : fileName.length;

	if (len <= limit) {
		return fileName;
	} else {
		var fName = fileName.substr(0, index);
		var ext = fileName.substr(index+1, fileName.length-1);

		return [
			fName.substr(0, limit/2),
			'...',
			fName.substring(len - ((limit/2) - 3), len),
			'.', (ext ? ext : '')  // file extension
		].join("")
	}
};


AjxStringUtil.URL_PARSE_RE = new RegExp("^(?:([^:/?#.]+):)?(?://)?(([^:/?#]*)(?::(\\d*))?)?((/(?:[^?#](?![^?#/]*\\.[^?#/.]+(?:[\\?#]|$)))*/?)?([^?#/]*))?(?:\\?([^#]*))?(?:#(.*))?");

AjxStringUtil.parseURL = 
function(sourceUri) {

	var names = ["source","protocol","authority","domain","port","path","directoryPath","fileName","query","anchor"];
	var parts = AjxStringUtil.URL_PARSE_RE.exec(sourceUri);
	var uri = {};

	for (var i = 0; i < names.length; i++) {
		uri[names[i]] = (parts[i] ? parts[i] : "");
	}

	if (uri.directoryPath.length > 0) {
		uri.directoryPath = uri.directoryPath.replace(/\/?$/, "/");
	}

	return uri;
};

/**
 * Parse the query string (part after the "?") and return it as a hash of key/value pairs.
 * 
 * @param	{String}	sourceUri		the source query string
 * @return	{Hash}	a hash of query string params
 */
AjxStringUtil.parseQueryString =
function(sourceUri) {

	var location = sourceUri || ("" + window.location);
	var idx = location.indexOf("?");
	if (idx == -1) { return null; }
	var qs = location.substring(idx + 1).replace(/#.*$/, '');
	var list = qs.split("&");
	var params = {};
	for (var i = 0; i < list.length; i++) {
		var pair = list[i].split("=");
		params[pair[0]] = pair[1];
	}
	return params;
};

AjxStringUtil._SPECIAL_CHARS = /["\\\x00-\x1f]/g;
AjxStringUtil._CHARS = {
	'\b': '\\b',
	'\t': '\\t',
	'\n': '\\n',
	'\f': '\\f',
	'\r': '\\r',
	'"' : '\\"',
	'\\': '\\\\'
};

/**
 * Converts a JS object to a string representation. Adapted from YAHOO.lang.JSON.stringify() in YUI 2.5.0, with the
 * following differences:
 * 		- does not support whitelist or depth limit
 * 		- no special conversion for Date objects
 * 
 * @param o		[object]	object to convert to string
 * 
 * @private
 */
AjxStringUtil.objToString =
function(o) {
	var t = typeof o,
	i,len,j, // array iteration
	k,v,     // object iteration
	vt,      // typeof v during iteration
	a,       // composition array for performance over string concat
	pstack = []; // Processing stack used for cyclical ref detection

	// escape encode special characters
	var _char = function (c) {
		if (!AjxStringUtil._CHARS[c]) {
			var a = c.charCodeAt();
			AjxStringUtil._CHARS[c] = '\\u00' + Math.floor(a / 16).toString(16) + (a % 16).toString(16);
		}
		return AjxStringUtil._CHARS[c];
	};

	var _string = function (s) {
		return '"' + s.replace(AjxStringUtil._SPECIAL_CHARS, _char) + '"';
	}
	
	// String
	if (t === 'string') {
		return _string(o);
	}

	// native boolean and Boolean instance
	if (t === 'boolean' || o instanceof Boolean) {
		return String(o);
	}

	// native number and Number instance
	if (t === 'number' || o instanceof Number) {
		return isFinite(o) ? String(o) : 'null';
	}

	// Array
	//Special check ( t==='object' && o.length && typeof o.push === "function") becoz when objects are passed from child window to parent window they loose their types.
	//Parent window considers every object/custome object/array as 'object' type.
    if (AjxUtil.isArray(o) || (t === 'object' &&  o.length) ) {
		// Check for cyclical references
		for (i = pstack.length - 1; i >= 0; --i) {
			if (pstack[i] === o) {
				return 'null';
			}
		}
	
		// Add the array to the processing stack
		pstack[pstack.length] = o;
	
		a = [];
		for (i = o.length - 1; i >= 0; --i) {
			a[i] = AjxStringUtil.objToString(o[i]);
		}
	
		// remove the array from the stack
		pstack.pop();
	
		return '[' + a.join(',') + ']';
	}

	// Object
	if (t === 'object' && o) {
		// Check for cyclical references
		for (i = pstack.length - 1; i >= 0; --i) {
			if (pstack[i] === o) {
				return 'null';
			}
		}

		// Add the object to the  processing stack
		pstack[pstack.length] = o;

		a = [];
		j = 0;
		for (k in o) {
			if (typeof k === 'string' && o.hasOwnProperty(k)) {
				v = o[k];
				vt = typeof v;
				if (vt !== 'undefined' && vt !== 'function') {
					a[j++] = _string(k) + ':' + AjxStringUtil.objToString(v);
				}
			}
		}

		// Remove the object from processing stack
		pstack.pop();

		return '{' + a.join(',') + '}';
	}

	return 'null';
};

AjxStringUtil.prettyPrint =
function(obj, recurse, showFuncs, omit) {

	AjxStringUtil._visited = new AjxVector();
	var text = AjxStringUtil._prettyPrint(obj, recurse, showFuncs, omit);
	AjxStringUtil._visited = null;

	return text;
};

AjxStringUtil._visited = null;

AjxStringUtil._prettyPrint =
function(obj, recurse, showFuncs, omit) {

	var indentLevel = 0;
	var showBraces = false;
	var stopRecursion = false;
	if (arguments.length > 4) {
		indentLevel = arguments[4];
		showBraces = arguments[5];
		stopRecursion = arguments[6];
	}

	if (AjxUtil.isObject(obj)) {
		var objStr = obj.toString();
		if (omit && omit[objStr]) {
			return "[" + objStr + "]";
		}
		if (AjxStringUtil._visited.contains(obj)) {
			return "[visited object]";
		} else {
			AjxStringUtil._visited.add(obj);
		}
	}

	var indent = AjxStringUtil.repeat(" ", indentLevel);
	var text = "";

	if (obj === undefined) {
		text += "[undefined]";
	} else if (obj === null) {
		text += "[null]";
	} else if (AjxUtil.isBoolean(obj)) {
		text += obj ? "true" : "false";
	} else if (AjxUtil.isString(obj)) {
		text += '"' + AjxStringUtil._escapeForHTML(obj) + '"';
	} else if (AjxUtil.isNumber(obj)) {
		text += obj;
	} else if (AjxUtil.isObject(obj)) {
		var isArray = AjxUtil.isArray(obj);
		if (stopRecursion) {
			text += isArray ? "[Array]" : obj.toString();
		} else {
			stopRecursion = !recurse;
			var keys = new Array();
			for (var i in obj) {
				keys.push(i);
			}

			if (isArray) {
				keys.sort(function(a,b) {return a - b;});
			} else {
				keys.sort();
			}

			if (showBraces) {
				text += isArray ? "[" : "{";
			}
			var len = keys.length;
			for (var i = 0; i < len; i++) {
				var key = keys[i];
				var nextObj = obj[key];
				var value = null;
				// For dumping events, and dom elements, though I may not want to
				// traverse the node, I do want to know what the attribute is.
				if (nextObj == window || nextObj == document || (!AjxEnv.isIE && nextObj instanceof Node)){
					value = nextObj.toString();
				}
				if ((typeof(nextObj) == "function")){
					if (showFuncs) {
						value = "[function]";
					} else {
						continue;
					}
				}

				if (i > 0) {
					text += ",";
				}
				text += "\n" + indent;
				if (value != null) {
					text += key + ": " + value;
				} else {
					text += key + ": " + this._prettyPrint(nextObj, recurse, showFuncs, omit, indentLevel + 2, true, stopRecursion);
				}
			}
			if (i > 0) {
				text += "\n" + AjxStringUtil.repeat(" ", indentLevel - 1);
			}
			if (showBraces) {
				text += isArray ? "]" : "}";
			}
		}
	}
	return text;
};

AjxStringUtil._escapeForHTML =
function(str){

	if (typeof(str) != 'string') { return str; }

	var s = str;
	s = s.replace(/\&/g, '&amp;');
	s = s.replace(/\</g, '&lt;');
	s = s.replace(/\>/g, '&gt;');
	s = s.replace(/\"/g, '&quot;');
	s = s.replace(/\xA0/g, '&nbsp;');

	return s;
};
