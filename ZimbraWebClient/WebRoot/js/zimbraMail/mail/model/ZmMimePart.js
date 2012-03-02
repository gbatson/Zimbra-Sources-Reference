/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011 VMware, Inc.
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
 * Creates a mime part.
 * @class
 * This class represents a mime part.
 * 
 * @extends		ZmModel
 */
ZmMimePart = function() {
	
	ZmModel.call(this, ZmEvent.S_ATT);
	
	this.children = new AjxVector();
	this.node = new Object();
};

ZmMimePart.prototype = new ZmModel;
ZmMimePart.prototype.constructor = ZmMimePart;

ZmMimePart.prototype.toString = 
function() {
	return "ZmMimePart";
};

ZmMimePart.createFromDom =
function(node, args) {
	var mimePart = new ZmMimePart();
	mimePart._loadFromDom(node, args.attachments, args.bodyParts, args.parentNode);
	return mimePart;
};

/**
 * Gets the content.
 * 
 * @return		{String}	content.
 */
ZmMimePart.prototype.getContent = 
function() {
	return this.node.content;
};

/**
 * Gets the content by type.
 * 
 * @param	{String}	contentType		the content type
 * @return	{String}	the content
 * 
 */
ZmMimePart.prototype.getContentForType = 
function(contentType) {
	var topChildren = this.children.getArray();

	if (topChildren.length) {
		for (var i = 0; i < topChildren.length; i++) {
			if (topChildren[i].getContentType() == contentType)
				return topChildren[i].getContent();
		}
	} else {
		if (this.getContentType() == contentType)
			return this.getContent();
	}
	return null;
};

/**
 * Sets the content.
 * 
 * @param	{String}	content		the content
 */
ZmMimePart.prototype.setContent = 
function(content) {
	this.node.content = content;
};

/**
 * Gets the content disposition.
 * 
 * @return	{String}	the content disposition
 */
ZmMimePart.prototype.getContentDisposition =
function() {
	return this.node.cd;
};

/**
 * Gets the content type.
 * 
 * @return	{String}	the content type
 */
ZmMimePart.prototype.getContentType =
function() {
	return this.node.ct;
};

/**
 * Sets the content type.
 * 
 * @param	{String}	ct		the content type
 */
ZmMimePart.prototype.setContentType =
function(ct) {
	this.node.ct = ct;
};

/**
 * Sets the is body flag.
 * 
 * @param	{Boolean}	isBody		if <code>true</code>, this part is the body
 */
ZmMimePart.prototype.setIsBody = 
function(isBody) {
	this.node.body = isBody;
};

/**
 * Gets the filename.
 * 
 * @return	{String}	the filename
 */
ZmMimePart.prototype.getFilename =
function() {
	return this.node.filename;
};

ZmMimePart.prototype.isIgnoredPart =
function(parentNode) {
	// bug fix #5889 - if parent node was multipart/appledouble,
	// ignore all application/applefile attachments - YUCK
	if (parentNode && parentNode.ct == ZmMimeTable.MULTI_APPLE_DBL &&
		this.node.ct == ZmMimeTable.APP_APPLE_DOUBLE)
	{
		return true;
	}

	// bug fix #7271 - dont show renderable body parts as attachments anymore
	if (this.node.body && AjxUtil.isSpecified(this.node.content) && 
		(this.node.ct == ZmMimeTable.TEXT_HTML || this.node.ct == ZmMimeTable.TEXT_PLAIN))
	{
		return true;
	}

	if (this.node.ct == ZmMimeTable.MULTI_DIGEST) {
		return true;
	}

	return false;
};

ZmMimePart.prototype._loadFromDom =
function(partNode, attachments, bodyParts, parentNode) {
	for (var i = 0; i < partNode.length; i++) {
		this.node = partNode[i];

		if (this.node.content)
			this._loaded = true;

        var isAtt = false;
		if (this.node.cd == "attachment" || 
			this.node.ct == ZmMimeTable.MSG_RFC822 ||
            this.node.ct == ZmMimeTable.TEXT_CAL ||            
			this.node.filename != null || 
			this.node.ci != null ||
			this.node.cl != null)
		{
			if (!this.isIgnoredPart(parentNode)) {
				attachments.push(this.node);
                isAtt = true;
			}
		}

        if(this.node.body){
            var hasContent = AjxUtil.isSpecified(this.node.content);
            if((ZmMimeTable.isRenderableImage(this.node.ct) || hasContent)) {
                bodyParts.push(this.node);
                if(isAtt){
                    //To avoid duplication, Remove attachment that was just added as bodypart.
                    attachments.pop();
                }
            }else if(!isAtt && this.node.size != 0 && !this.isIgnoredPart(parentNode)){
                attachments.push(this.node);
                isAtt = true;
            }
        }

		// bug fix #4616 - dont add attachments part of a rfc822 msg part
		if (this.node.mp && this.node.ct != ZmMimeTable.MSG_RFC822) {
			var params = {attachments: attachments, bodyParts: bodyParts, parentNode: this.node};
			this.children.add(ZmMimePart.createFromDom(this.node.mp, params));
		}
	}
};
