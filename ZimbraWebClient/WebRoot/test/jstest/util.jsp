<!-- 
***** BEGIN LICENSE BLOCK *****
Zimbra Collaboration Suite Web Client
Copyright (C) 2004, 2005, 2006, 2007, 2010, 2012, 2013 Zimbra Software, LLC.

The contents of this file are subject to the Zimbra Public License
Version 1.4 ("License"); you may not use this file except in
compliance with the License.  You may obtain a copy of the License at
http://www.zimbra.com/license.

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
***** END LICENSE BLOCK *****
-->

<%@ page language="java" 
         import="java.lang.*, java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<c:if test="${inline != 'inline'}">
<script type="text/javascript" src="/ZimbraConsole/js/util/Vector.js"></script>
</c:if>

<c:if test="${inline == 'inline'}">
<script language="JavaScript>
<jsp:include page="/js/util/Vector.js"/>
</script>
</c:if>