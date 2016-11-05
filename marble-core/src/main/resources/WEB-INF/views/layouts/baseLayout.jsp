<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<!DOCTYPE html>
<html lang="en">

<head>

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content='<tiles:insertAttribute name="author"/>'>
<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>

<c:if test="${pageContext.request.contextPath == '/'}">
    <base href="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}" />
</c:if>
<c:if test="${pageContext.request.contextPath != '/'}">
	<base href="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/" />
</c:if>


<title>Marble - <tiles:insertAttribute name="title" /></title>

<!-- Google Open Sans Font -->
<link href='http://fonts.googleapis.com/css?family=Open+Sans:400,700' rel='stylesheet' type='text/css'>

<!-- Bootstrap Core CSS -->
<link href='<c:url value="resources/css/bootstrap/bootstrap.css"/>' rel="stylesheet">

<!-- MetisMenu CSS -->
<link href="<c:url value="resources/css/plugins/metisMenu/metisMenu.min.css"/>" rel="stylesheet">

<!-- Timeline CSS -->
<link href="<c:url value="resources/css/plugins/timeline.css"/>" rel="stylesheet">

<!-- Custom CSS -->
<link href="<c:url value="resources/css/sb-admin-2.css"/>" rel="stylesheet">

<!-- Morris Charts CSS -->
<!-- <link href="<c:url value="resources/css/plugins/morris.css"/>" rel="stylesheet">-->


<!-- Custom Fonts -->
<link href="<c:url value="resources/font-awesome-4.1.0/css/font-awesome.min.css"/>" rel="stylesheet" type="text/css">

<!-- Date Time Picker -->
<link href="<c:url value="resources/css/plugins/bootstrap-datetimepicker/bootstrap-datetimepicker.min.css"/>" rel="stylesheet"
	type="text/css">

<!-- Marble Custom CSS -->
<link href="<c:url value="resources/css/marble.css"/>" rel="stylesheet">

<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>

<body>

	<div id="wrapper">

		<!-- Navigation -->
		<nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
				</button>
				<a class="navbar-brand main-logo" href='<c:url value=""></c:url>'><span class="logo">M</span>arble <span class="logo narrow">I</span>nitiative </a>
			</div>
			<!-- /.navbar-header -->
			<tiles:insertAttribute name="topNavigation" />
			<tiles:insertAttribute name="leftNavigation" />
		</nav>

		<div id="page-wrapper">
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">
						<tiles:insertAttribute name="decoration" />
						<tiles:insertAttribute name="title" />
					</h1>
				</div>
				<!-- /.col-lg-12 -->
			</div>
			<!-- /.row -->
			<c:if test="${notificationMessage != null}">
				<div class="alert alert-${notificationLevel}">
					<i class='fa ${notificationIcon}'></i>
					<spring:message code="${notificationMessage}" />
				</div>
			</c:if>
			<tiles:insertAttribute name="content" />
		</div>
		<!-- /#page-wrapper -->



	</div>
	<!-- /#wrapper -->

	<!-- jQuery Version 1.11.0 -->
	<script src="<c:url value="resources/js/jquery-1.11.0.js"/>"></script>

	<!-- Bootstrap Core JavaScript -->
	<script src="<c:url value="resources/js/bootstrap.min.js"/>"></script>

	<!-- Metis Menu Plugin JavaScript -->
	<script src="<c:url value="resources/js/plugins/metisMenu/metisMenu.min.js"/>"></script>
	
	<!-- Flot Charts Javascript -->
	<script src="resources/js/plugins/flot/jquery.flot.min.js"></script>
	<script src="resources/js/plugins/flot/jquery.flot.resize.min.js"></script>
	<script src="resources/js/plugins/flot/jquery.flot.time.min.js"></script>

	<!-- Morris Charts JavaScript -->
	<!--
	<script src="<c:url value="resources/js/plugins/morris/raphael.min.js"/>"></script>
	<script src="<c:url value="resources/js/plugins/morris/morris.min.js"/>"></script>
	<script src="<c:url value="resources/js/plugins/morris/morris-data.js"/>"></script>
	-->

	<!-- Date Time Picker -->
	<script src="<c:url value="resources/js/plugins/bootstrap-datetimepicker/bootstrap-datetimepicker.min.js"/>"></script>

	<!-- Custom Theme JavaScript -->
	<script src="<c:url value="resources/js/sb-admin-2.js"/>"></script>

	<tiles:insertAttribute name="script" />
</body>

</html>
