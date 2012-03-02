<%--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
--%><%@ tag body-content="empty" dynamic-attributes="dynattrs" import="java.io.*,java.util.*"
%><%@ attribute name="value" rtexprvalue="true" required="true"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="zm" uri="com.zimbra.zm"
%><%@ taglib prefix="fmt" uri="com.zimbra.i18n" %><%!
	static final String A_IMAGES = "com.zimbra.htmlclient:images";
	static final String V_NO_SKIN = "<noskin>";
	static final Map<String,Map<String,String>> SKIN_IMAGES = Collections.synchronizedMap(new HashMap<String,Map<String,String>>());

	static File getImageSrc(File appdir, String src, Locale locale) {
		File file = new File(appdir, src);
		String filename = file.getName();
		File dir = file.getParentFile();
		String dirname = dir.getName();
		File dirdir = dir.getParentFile();
		Locale[] locales = locale.getCountry() != null
						 ? new Locale[] { locale, new Locale(locale.getLanguage()) }
						 : new Locale[] { locale };
		for (Locale loc : locales) {
			File locdir = new File(dirdir, dirname+"_"+loc);
			File locfile = new File(locdir, filename);
			if (locfile.exists()) {
				return locfile;
			}
		}
		return file;
	}
%><fmt:getLocale var='locale' scope='page' /><%
	PageContext pageContext = (PageContext)getJspContext();

	// generate cache-id
	String skin = (String)pageContext.findAttribute("skin");
	if (skin == null) skin = V_NO_SKIN;
	Locale locale = (Locale)pageContext.findAttribute("locale");
	if (locale == null) locale = Locale.US;
	String cacheId = skin + ":" + locale;

	// get image map
	Map<String,String> images = SKIN_IMAGES.get(cacheId);
	if (images == null) {
		images = Collections.synchronizedMap(new HashMap<String,String>());
		SKIN_IMAGES.put(cacheId, images);
	}

	// find image 
	String imageSrc = images.get(value);
	if (imageSrc == null) {
		String iconPath = null;
		if (!value.startsWith("/")) {
			iconPath = (String)pageContext.findAttribute("iconPath");
			if (iconPath == null) iconPath = "/img";
			if (iconPath.endsWith("/")) iconPath = iconPath.substring(0, iconPath.length() - 1);
		}
		imageSrc = iconPath != null ? iconPath+"/"+value : value;
		// find image
		ServletContext servletContext = pageContext.getServletContext();
		File basedir = new File(servletContext.getRealPath("/"));
		File imageFile = getImageSrc(basedir, imageSrc, locale);
		imageSrc = imageFile.getAbsolutePath().substring(basedir.getAbsolutePath().length());
		imageSrc = imageSrc.replace(File.separatorChar, '/');
		// save so we don't have to do this again
		images.put(value, imageSrc);
	}

	// store image path in page context
	pageContext.setAttribute("value", imageSrc, PageContext.PAGE_SCOPE);
%><c:url value="${zm:getImagePath(pageContext, value)}" />