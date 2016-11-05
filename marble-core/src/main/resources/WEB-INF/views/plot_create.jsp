<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<form:form commandName="executionModuleParameters" modelAttribute="executionModuleParameters">
	<div class="row">
		<div class="col-lg-12">
			<div class="panel panel-default">
				<div class="panel-body">
					<fieldset>
						<p class="help-block">
							<spring:message code="plot_create.form.informative_message" />
						</p>
						<div class="form-group">
							<div class="col-lg-offset-8 col-lg-4">
								<button type="submit" id="save" class="btn btn-primary pull-right">
									<i class="fa fa-floppy-o"></i>
									<spring:message code="plot_create.form.create" />
								</button>
								<a href="<c:url value="topic/${topic.id}/plot"/>" class="btn btn-default pull-right">
									<i class="fa fa-times"></i>
									<spring:message code="plot_create.form.cancel" />
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

	<div class="row">
		<div class="col-lg-8">
			<div class="panel panel-default">

				<!-- /.panel-heading -->

				<div class="panel-body">
					<form:errors path="*" cssClass="alert alert-danger" element="div" />
					<div class="form-group">
						<fieldset>
							<legend><spring:message code="plot_create.form.name" /></legend>
							<input id="plot-name" name="name" class="form-control" type="text">
							<p class="help-block"><spring:message code="plot_create.form.name.description" /></p>
						</fieldset>
					</div>

					<div class="form-group" id="modules-div">
						<fieldset>
							<legend><spring:message code="plot_create.form.module" /></legend>
							<select name="module" id="modules-select" class="form-control">
							</select>
						</fieldset>
					</div>
					<div class="form-group" id="operations-div"></div>
					<div id="parameters-div"></div>
				</div>
				<!-- .panel-body -->
			</div>
			<!-- /.panel -->
		</div>
		<!-- /.col-lg-12 -->
	</div>
	<!-- /.row -->
</form:form>