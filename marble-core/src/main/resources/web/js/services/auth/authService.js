angular.module('marbleCoreApp').factory('AuthService', function ($rootScope, $http, $state, $q) {
	
	var destroyUserData = function () {
		$rootScope.user = {};
	}
	return {
		isInitialized : function() {
			return ($rootScope.user === undefined ? false : true);
		},
		authenticate : function(credentials) {
	      return $q(function(resolve, reject) {
			$rootScope.user = [];
			var headers = credentials ? {
				authorization : "Basic "
						+ btoa(credentials.username + ":"
								+ credentials.password)
			} : {};
			$http.get('api/user', {
				headers : headers
			}).success(function(data) {
				if (data.name) {
					$rootScope.user.name = data.name;
					$rootScope.user.roles = [];
					$rootScope.authenticated = true;
					
					if (data.principal !== undefined && data.principal.authorities !== undefined) {
						for(var i = 0; i < data.principal.authorities.length; i++) {
							$rootScope.user.roles.push(data.principal.authorities[i].authority);
						}
					}
				} else {
					destroyUserData();
				}
				
				$rootScope.$emit('authenticated', ''); // going up!
				resolve();
				
				//callback && callback();
			}).error(function() {
				destroyUserData();
				resolve();
				//callback && callback();
			});
	      });
		},
		logout : function() {
			$http.post('logout', {}).success(function() {
				destroyUserData()
			  }).error(function(data) {
			    destroyUserData();
			  });
		},
		isLoggedIn : function() {
			return ($rootScope.user.name ? true : false);
		},
		currentUser : function() {
			return $rootScope.user.name;
		},
		isRole : function(role) {
			if ($rootScope.user !== undefined && $rootScope.user.roles !== undefined) {
				if ($rootScope.user.roles.indexOf(role) > -1) {
					return true;
				}
				else {
					return false;
				}
			}
			return false;
		},
		isAdmin : function() {
			var role = "ROLE_ADMIN";
			if ($rootScope.user !== undefined && $rootScope.user.roles !== undefined) {
				if ($rootScope.user.roles.indexOf(role) > -1) {
					return true;
				}
				else {
					return false;
				}
			}
			return false;
		},
		isOper : function(role) {
			var role = "ROLE_OPER";
			if ($rootScope.user !== undefined && $rootScope.user.roles !== undefined) {
				if ($rootScope.user.roles.indexOf(role) > -1) {
					return true;
				}
				else {
					return false;
				}
			}
			return false;
		},
		isGuest : function(role) {
			var role = "ROLE_GUEST";
			if ($rootScope.user !== undefined && $rootScope.user.roles !== undefined) {
				if ($rootScope.user.roles.indexOf(role) > -1) {
					return true;
				}
				else {
					return false;
				}
			}
			return false;
		}

	};
});