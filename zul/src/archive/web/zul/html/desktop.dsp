<%--
desktop.dsp

{{IS_NOTE
	Purpose:
		The desktop template for the zul language
	Description:
		
	History:
		Fri Jun 10 09:16:14     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="arg" value="${requestScope.arg}"/>
<c:set var="page" value="${arg.page}"/>
<%@ page contentType="${z:outContentType(page)}" %>
${z:outDocType(page)}
<html xmlns="http://www.w3.org/1999/xhtml"${z:outRootAttributes(page)}>
<head>
<title>${page.title}</title>
${z:outLangStyleSheets()}
<meta http-equiv="Cache-Control" content="no-cache,no-store,must-revalidate,max-age=0"/>
<c:set var="zk_htmlHeadRequired" value="true" scope="request"/><%-- ask page.dsp to generate </head><body> --%>
<c:include page="~./zul/html/page.dsp"/><%-- OC4J cannot handle relative page correctly --%>
</body>
</html>
