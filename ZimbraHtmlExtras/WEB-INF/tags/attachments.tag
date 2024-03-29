<%--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2009, 2010, 2013, 2014 Zimbra, Inc.
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
--%>
<%@ tag body-content="empty" %>
<%@ attribute name="message" rtexprvalue="true" required="true" type="com.zimbra.cs.taglib.bean.ZMessageBean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlextras" %>

<div class="msgAttachContainer">
<c:forEach var="part" items="${message.attachments}">
	<c:if test="${part.isMssage}">
		<zm:getMessage var="partMessage" id="${message.id}" part="${part.partName}"/>
		<c:set var="body" value="${partMessage.body}"/>
		<c:if test="${body.contentType eq 'text/html'}">
			${body.content}
		</c:if>
		<c:if test="${!(body.contentType eq 'text/html')}">
			${body.textContentAsHtml}
		</c:if>
		<br><br>
		<app:attachments message="${partMessage}"/>
	</c:if>
</c:forEach>

<c:forEach var="part" items="${message.attachments}">
	<c:if test="${!part.isMssage}">
		<c:set var="pname" value="${part.displayName}"/>
		<c:if test="${empty pname}">
			<fmt:message key="unknownContentType" var="pname"><fmt:param value="${part.contentType}"/></fmt:message>
		</c:if>
		<c:set var="url" value="/home/~/?id=${message.id}&part=${part.partName}&auth=co"/>

		<table border=0 cellpadding=1 cellspacing=1>
		<tr>
			<td>
				<c:choose>
					<c:when test="${part.isImage}">
						<img class='msgAttachImage' src="${url}" alt="${part.displayName}"/>
					</c:when>
					<c:otherwise>
						<app:img src="${part.image}" alt="${part.displayName}" title="${part.contentType}"/>
					</c:otherwise>
				</c:choose>
			</td>
			<td width=7></td>
			<td>
				<b>${fn:escapeXml(pname)}</b><br />
				${part.displaySize}
				<a target="_blank" href="${url}&disp=i"><fmt:message key="view"/></a>
				<a href="${url}&disp=a"><fmt:message key="download"/></a>
			</td>
		</tr>
		</table>
	</c:if>
</c:forEach>
</div>
