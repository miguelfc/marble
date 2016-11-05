<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="row">
	<div class="col-lg-12">
		<div class="panel panel-default">
			<div class="panel-body">
				<fieldset>
					<p class="help-block">
						<spring:message code="executions_list.form.informative_message" arguments="${topic.name}" />
					</p>
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
				<spring:message code="executions_list.form.executions" />
			</div>
			<!-- .panel-heading -->
			<div class="panel-body">
				<c:if test="${not empty executions}">
					<div class="table-responsive">
						<table class="table data-table table-striped table-bordered table-hover">
							<thead>
								<tr>
									<th scope="col"><spring:message code="executions_list.form.id" /></th>
									<th scope="col"><spring:message code="executions_list.form.type" /></th>
									<th scope="col"><spring:message code="executions_list.form.status" /></th>
									<th scope="col"><spring:message code="executions_list.form.updatedAt" /></th>
									<th scope="col"><spring:message code="executions_list.form.actions" /></th>
								</tr>
							</thead>
							<c:forEach var="execution" items="${executions}">
								<tr>
									<td>${execution.id}</td>
									<td>${execution.type}</td>
									<td>${execution.status}</td>
									<td>${execution.updatedAt}</td>
									<td><a href="<c:url value="execution/${execution.id}"/>" class="btn btn-default"><i
											class="fa fa-pencil"></i> <spring:message code="executions_list.form.view" /></a></td>
								</tr>
							</c:forEach>
						</table>
					</div>
				</c:if>
				<c:if test="${empty executions}">
					<p>
						<spring:message code="executions_list.form.empty_message" />
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