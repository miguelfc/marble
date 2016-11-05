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
						<spring:message code="validation.form.informative_message"/>
					</p>
					<div class="form-group">
						<div class="col-lg-offset-10 col-lg-2">
							<button type="submit" id="save" class="btn btn-primary pull-right">
								<i class="fa fa-rocket"></i>
								<spring:message code="validation.form.execute" />
							</button>
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
		<div class="col-lg-8">
			<div class="panel panel-default">

				<!-- /.panel-heading -->

				<div class="panel-body">
					<form:errors path="*" cssClass="alert alert-danger" element="div" />
					<div class="form-group">
						<fieldset>
							<legend><spring:message code="process_execute.form.name" /></legend>
							<input id="process-name" name="name" class="form-control" type="text">
							<p class="help-block"><spring:message code="process_execute.form.name.description" /></p>
						</fieldset>
					</div>

					<div class="form-group" id="modules-div">
						<fieldset>
							<legend><spring:message code="process_execute.form.module" /></legend>
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