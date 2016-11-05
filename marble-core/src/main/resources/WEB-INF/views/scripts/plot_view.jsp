<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript">

$(function () {
	var data = <c:out value="${plot.data}"  escapeXml="false" />;
	var mainOptions = <c:out value="${plot.mainOptions}"  escapeXml="false" />;
	$("#plot-placeholder").plot(data, mainOptions);
});

</script>