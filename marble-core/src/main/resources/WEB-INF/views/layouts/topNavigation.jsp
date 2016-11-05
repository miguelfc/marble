<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<ul class="nav navbar-top-links navbar-right">
	<li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" href="#"> <i class="fa fa-user fa-fw"></i> <i
			class="fa fa-caret-down"></i>
	</a> <sec:authorize access="isAnonymous()">
			<ul class="dropdown-menu dropdown-user dropdown-login">
				<li><form action="<c:url value="login/authenticate" />" method="post">
						<fieldset>
							<div class="form-group">
								<input class="form-control" placeholder="Username" name="username" type="text" autofocus />
							</div>
							<div class="form-group">
								<input class="form-control" placeholder="Password" name="password" type="password" value="" />
							</div>
							<div class="checkbox">
								<label> <input name="_spring_security_remember_me" type="checkbox" disabled="disabled" value="Remember Me" />Remember Me
								</label>
							</div>
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /> <input class="btn btn-lg btn-primary btn-block"
								type="submit" value="Login" />
						</fieldset>
					</form></li>
			</ul>
			<!-- /.dropdown-user -->
		</sec:authorize> <sec:authorize access="isAuthenticated()">
			<ul class="dropdown-menu dropdown-user">
				<li><span><i class="fa fa-user fa-fw"></i> Hello <sec:authentication property="principal.username" />!</span></li>
				<li class="divider"></li>
				<li>
					<form action="<c:url value="logout" />" method="post">
						<fieldset>
							<input class="btn btn-lg btn-primary btn-block" type="submit" value="Logout" /> <input type="hidden" name="${_csrf.parameterName}"
								value="${_csrf.token}" />
						</fieldset>
					</form>
				</li>
			</ul>
			<!-- /.dropdown-user -->
		</sec:authorize></li>
	<!-- /.dropdown -->
</ul>
<!-- /.navbar-top-links -->