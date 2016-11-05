<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="navbar-default sidebar" role="navigation">
	<div class="sidebar-nav navbar-collapse">
		<ul class="nav" id="side-menu">
			<li class="sidebar-search">
				<div class="input-group custom-search-form">
					<input type="text" class="form-control" placeholder="Search..." /> <span class="input-group-btn">
						<button class="btn btn-default" type="button">
							<i class="fa fa-search"></i>
						</button>
					</span>
				</div> <!-- /input-group -->
			</li>
			<li><a href="<c:url value="admin"/>"><i class="fa fa-cog fa-fw"></i> Administration Panel<span class="fa arrow"></span></a>
			<ul class="nav nav-second-level">
					<li><a href="<c:url value="admin"/>"><i class="fa fa-cog fa-fw"></i> Main</a>
					<li><a href="<c:url value="admin/keys/twitter"/>"><i class="fa fa-key fa-fw"></i> Twitter API Keys</a></li>
				</ul> <!-- /.nav-second-level --></li>
			<li><a href="<c:url value="topic"/>"><i class="fa fa-tags fa-fw"></i> Topics</a></li>
			<li><a href="<c:url value="validation"/>"><i class="fa fa-check-square-o fa-fw"></i> Validation</a></li> 
		</ul>
	</div>
	<!-- /.sidebar-collapse -->
</div>
<!-- /.navbar-static-side -->