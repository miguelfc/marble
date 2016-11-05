<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="row">

	<div class="col-lg-8">
		<div class="panel panel-default">
			<div class="panel-heading">Chart</div>
			<!-- /.panel-heading -->

			<div class="panel-body">
				<div id="plot-placeholder"></div>
			</div>
			<!-- .panel-body -->
		</div>
		<!-- /.panel -->
	</div>
	<!-- /.col-lg-8 -->

	<div class="col-lg-4">
		<div class="panel panel-default">
			<div class="panel-heading">Information</div>
			<!-- /.panel-heading -->

			<div class="panel-body">
				<div class="table-responsive">
					<table class="table table-hover">
						<thead>
							<tr>
								<th>Name</th>
								<th id="plot-name">${plot.name}</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<th>Created At</th>
								<td id="plot-created-at">${plot.createdAt}</td>
							</tr>
							<tr>
								<th>Module</th>
								<td id="plot-execution-module">${plot.execution.moduleParameters.simpleModule}</td>
							</tr>
							<tr>
								<th>Operation</th>
								<td id="plot-execution-module">${plot.execution.moduleParameters.operation}</td>
							</tr>
							<tr>
								<th>Topic</th>
								<td id="plot-execution-module">${plot.topic.name}</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
			<!-- .panel-body -->
		</div>
		<!-- /.panel -->

		<div class="panel panel-default">
			<div class="panel-heading">Actions</div>
			<!-- /.panel-heading -->

			<div class="panel-body">

				<div class="table-responsive">
					<table class="table">
						<tbody>
							<tr>
								<td><a href='<c:url value="topic/${plot.topic.id}/plot" />' class="btn btn-default btn-block">
										<i class="fa fa-bar-chart-o"></i> View Other Plots
									</a></td>
							</tr>
							<tr>
								<td><a href='<c:url value="topic/${plot.topic.id}" />' class="btn btn-default btn-block">
										<i class="fa fa-tags"></i> View Topic
									</a></td>
							</tr>
							<tr>
								<td><a href='<c:url value="execution/${plot.execution.id}" />' class="btn btn-default btn-block">
										<i class="fa fa-rocket"></i> View Execution
									</a></td>
							</tr>
							<tr>
								<td><a href='<c:url value="plot/delete/${plot.id}" />' class="btn btn-danger btn-block">
										<i class="fa fa-trash-o"></i> Delete
									</a></td>
							</tr>
						</tbody>
						<!-- <tbody>
							<tr>
								<td></td>
							</tr>
						</tbody>-->
					</table>
				</div>
			</div>
			<!-- .panel-body -->
		</div>
		<!-- /.panel -->

	</div>
	<!-- /.col-lg-4 -->

</div>
<!-- /.row -->