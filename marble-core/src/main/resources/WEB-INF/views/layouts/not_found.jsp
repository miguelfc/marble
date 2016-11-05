<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>



<tiles:insertTemplate template="/WEB-INF/views/layouts/baseLayout.jsp" flush="true">
	<tiles:putAttribute name="topNavigation" value="/WEB-INF/views/layouts/topNavigation.jsp" />
	<tiles:putAttribute name="leftNavigation" value="/WEB-INF/views/layouts/leftNavigation.jsp" />
	<tiles:putAttribute name="author" value="Miguel Fernandes" />
	<tiles:putAttribute name="title" value="We couldn't find it!" />
	<tiles:putAttribute name="content" value="/WEB-INF/views/not_found.jsp" />
	<tiles:putAttribute name="script" value="" />
	<tiles:putAttribute name="decoration" value="" />
</tiles:insertTemplate>
