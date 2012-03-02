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
 * @class
 * This static class provides basic image support by using CSS and background 
 * images rather than &lt;img&gt; tags.
 *  
 * @author Conrad Damon
 * @author Ross Dargahi
 * 
 * @private
 */
AjxImg = function() {};

AjxImg.prototype = new Object;
AjxImg.prototype.constructor = null;

AjxImg._VIEWPORT_ID = "AjxImg_VP";

AjxImg.DISABLED = true;

AjxImg.RE_COLOR = /^(.*?),color=(.*)$/;

/**
 * This method will set the image for <i>parentEl</i>. <i>parentEl</i> should 
 * only contain this image and no other children
 *
 * @param parentEl 		the parent element for the image
 * @param imageName 		the name of the image.  The CSS class for the image will be "Img&lt;imageName&gt;".
 * @param useParenEl 	if <code>true</code> will use the parent element as the root for the image and will not create an intermediate DIV
 * @param _disabled		if <code>true</code>, will append " ZDisabledImage" to the CSS class for the image, 
 *							which will make the image partly transparent
 */
AjxImg.setImage =
function(parentEl, imageName, useParentEl, _disabled) {
	var color, m = imageName.match(AjxImg.RE_COLOR);
	if (m) {
		imageName = m && m[1];
		color = m && m[2];
	}

	var className = AjxImg.getClassForImage(imageName, _disabled);
	if (useParentEl) {
		parentEl.className = className;
		return;
	}

	var overlayName = className+"Overlay";
	var maskName = className+"Mask";
	if (color && window.AjxImgData && AjxImgData[overlayName] && AjxImgData[maskName]) {
		color = (color.match(/^\d$/) ? ZmOrganizer.COLOR_VALUES[color] : color) ||
				ZmOrganizer.COLOR_VALUES[ZmOrganizer.ORG_DEFAULT_COLOR];

		var overlay = AjxImgData[overlayName], mask = AjxImgData[maskName];
		if (AjxEnv.isIE) {
            var clip = "";
			var size = [
				"width:",overlay.w,";",
				"height:",overlay.h,";"
			].join("");
			var position = [
				"top:",mask.t,";",
				"left:",mask.l,";"
			].join("");
            if(typeof document.documentMode != 'undefined'){ //IE8 is the first one to define this. IE8 can lie when in compat mode, so we need to really know it's it.
                clip = [
                    'clip:rect(',
                    (-1*mask.t)-1,'px, ',
                    overlay.w-1,'px, ',
                    (mask.t*-1)+overlay.h-1,'px, ',
                    overlay.l,'px);'
                ].join('');
            }
			parentEl.innerHTML = [
				"<div style='position:relative;overflow:hidden;",size,"'>",
					"<div style='overflow:hidden;position:relative;",size,"'>",
						"<img src='",mask.f,"' ",
							 "style='filter:mask(color=",color,");position:absolute;",position,clip,"'>",
					"</div>",
					"<div class='",overlayName,"' style='",size,";position:absolute;top:",overlay.t,";left:",overlay.l,"'></div>",
				"</div>"
			].join("");
			return;
		}

		if (!overlay[color]) {
			var width = overlay.w, height = overlay.h;

			var canvas = document.createElement("CANVAS");
			canvas.width = width;
			canvas.height = height;

			var ctx = canvas.getContext("2d");

			ctx.save();
			ctx.clearRect(0,0,width,height);

			ctx.save();
			ctx.drawImage(document.getElementById(maskName),mask.l,mask.t);
			ctx.globalCompositeOperation = "source-out";
			ctx.fillStyle = color;
			ctx.fillRect(0,0,width,height);
			ctx.restore();

			ctx.drawImage(document.getElementById(overlayName),overlay.l,overlay.t);
			ctx.restore();

			overlay[color] = canvas.toDataURL();
		}

		parentEl.innerHTML = ["<img src='",overlay[color],"'>"].join("");
		return;
	}

	if (parentEl.firstChild == null) {
		parentEl.innerHTML = className ? "<div class='" + className + "'></div>" : "<div></div>";
		return;
	}

	parentEl.firstChild.className = className;
};

AjxImg.setDisabledImage = function(parentEl, imageName, useParentEl) {
	return AjxImg.setImage(parentEl, imageName, useParentEl, true);
};

AjxImg.getClassForImage =
function(imageName, disabled) {
	var className = "Img" + imageName;
	if (disabled) className += " ZDisabledImage";
	return className;
};

AjxImg.getImageClass =
function(parentEl) {
	return parentEl.firstChild ? parentEl.firstChild.className : parentEl.className;
};

AjxImg.getImageElement =
function(parentEl) {
	return parentEl.firstChild ? parentEl.firstChild : parentEl;
};

AjxImg.getParentElement =
function(imageEl) {
	return imageEl.parentNode;
};

/**
 * Gets the "image" as an HTML string. 
 *
 * @param imageName		the image you want to render
 * @param styleStr		optional style info (for example, "display:inline")
 * @param attrStr		optional attributes (for example, "id=X748")
 * @param wrapInTable	surround the resulting code in a table
 * @return	{string}	the image string
 */
AjxImg.getImageHtml = 
function(imageName, styleStr, attrStr, wrapInTable) {
	attrStr = attrStr || "";
	styleStr = styleStr ? (["style='", styleStr, "' "].join("")) : "";
	var pre = wrapInTable ? "<table style='display:inline' cellpadding=0 cellspacing=0 border=0><tr><td align=center valign=bottom>" : "";
	var post = wrapInTable ? "</td></tr></table>" : "";
	if (imageName) {
		return [pre, "<div class='", "Img", imageName, "' ", styleStr, " ", attrStr, "></div>", post].join("");
	}
	return [pre, "<div ", styleStr, " ", attrStr, "></div>", post].join("");
};

/**
 * Gets the "image" as an HTML string.
 *
 * @param imageName		the image you want to render
 * @param styleStr		optional style info (for example, "display:inline")
 * @param attrStr		optional attributes (for example, "id=X748")
 * @param label			the text that follows this image
 * @return	{string}	the image string
 */
AjxImg.getImageSpanHtml =
function(imageName, styleStr, attrStr, label) {
	var className = AjxImg.getClassForImage(imageName);

	var html = [];
	var i = 0;
	html[i++] = "<span style='white-space:nowrap'>";
	html[i++] = "<span class='";
	html[i++] = className;
	html[i++] = " inlineIcon'";
	html[i++] = styleStr ? ([" style='", styleStr, "' "].join("")) : "";
	html[i++] = attrStr ? ([" ", attrStr].join("")) : "";
	html[i++] = ">&nbsp;&nbsp;&nbsp;</span>";
	html[i++] = (label || "");
	html[i++] = "</span>";

	return html.join("");
};
