<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="row">
	<div class="col-lg-12">
		<div class="panel panel-default">
			<div class="panel-body">
				<fieldset>
					<p class="help-block">
						<spring:message code="plots_list.form.informative_message" arguments="${topic.name}" />
					</p>
					<div class="form-group">
						<div class="col-lg-offset-10 col-lg-2">
							<a href="<c:url value="topic/${topic.id}/plot/create"/>" class="btn btn-primary pull-right">
								<i class="fa fa-file-o"></i>
								<spring:message code="plots_list.form.create" />
							</a>
						</div>
					</div>
				</fieldset>
			</div>
			<!-- .panel-body -->
		</div>
		<!-- /.panel -->
	</div>
	<!-- /.col-lg-12 -->
</div>
<!-- /.row -->
<div class="row">
	<div class="col-lg-12">
		<div class="panel panel-default">
			<div class="panel-heading">
				<spring:message code="plots_list.form.plots" />
			</div>
			<!-- .panel-heading -->
			<div class="panel-body">
				<c:if test="${not empty plots}">
					<div class="table-responsive">
						<table class="table data-table table-striped table-bordered table-hover">
							<thead>
								<tr>
									<th scope="col"><spring:message code="plots_list.form.name" /></th>
									<th scope="col"><spring:message code="plots_list.form.created_at" /></th>
									<th scope="col"><spring:message code="plots_list.form.actions" /></th>
								</tr>
							</thead>
							<c:forEach var="plot" items="${plots}">
								<tr>
									<td>${plot.name}</td>
									<td>${plot.createdAt}</td>
									<td><a href="<c:url value="plot/${plot.id}" />" class="btn btn-default">
											<i class="fa fa-info-circle"></i><span class="hidden-xs hidden-sm"> <spring:message
													code="plots_list.form.view" /></span></a></td>
								</tr>
							</c:forEach>
						</table>
					</div>
				</c:if>
				<c:if test="${empty plots}">
					<p>
						<spring:message code="plots_list.form.empty_message" />
					</p>
				</c:if>
			</div>
			<!-- .panel-body -->
		</div>
		<!-- /.panel -->
	</div>
	<!-- /.col-lg-12 -->
</div>
<!-- /.row -->