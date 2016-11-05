<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<form action="<c:url value= "login/authenticate"></c:url>" method="post">
	<div class="row">
		<div class="col-md-6 col-md-offset-3">
			<div class="login-panel panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">Please enter your credentials</h3>
				</div>
				<!-- .panel-heading -->
				<div class="panel-body">
					<c:if test="${not empty error}">
						<div class="alert alert-danger">
							<spring:message code="LoginController.badCredentials" />
							<br />
						</div>
					</c:if>
					<c:if test="${not empty promote}">
						<div class="alert alert-info">
							<sec:authentication property="principal.username" var="loggedUser" />
							<spring:message code="LoginController.promotionNeeded" arguments="${loggedUser}" />
							<br />
						</div>
					</c:if>
					<fieldset>
						<div class="form-group">
							<input class="form-control" placeholder="Username" name="username" type="text" autofocus />
						</div>
						<div class="form-group">
							<input class="form-control" placeholder="Password" name="password" type="password" value="" />
						</div>
						<div class="checkbox">
							<label> <input name="_spring_security_remember_me" disabled="disabled" type="checkbox"
								value="Remember Me" />Remember Me
							</label>
						</div>
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input
							class="btn btn-lg btn-primary btn-block" type="submit" value="Login" />
					</fieldset>
				</div>
				<!-- .panel-body -->
			</div>
		</div>
		<!-- /.col-lg-12 -->
	</div>
	<!-- /.row -->
</form>
