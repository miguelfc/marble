angular.module('marbleCoreApp').constant('AuthLevels', {
    public: 'public',
    anon: 'anon',
    user: 'user',
    admin: 'admin',
  })
  .controller('LoginCtrl', ['$scope', '$rootScope', '$state', 'Auth', function($scope, $rootScope, $state, Auth) {
    $scope.credentials = {
      username: '',
      password: '',
    };

    var myself = this;
    this.alerts = [];
    this.addAlert = function(msg, type) {
      console.log(msg);
      myself.alerts.push({
        type: type,
        msg: msg
      });
    };
    this.closeAlert = function(index) {
      myself.alerts.splice(index, 1);
    };

    $scope.isAuthenticated = Auth.isAuthenticated();

    $scope.reason = $state.params.reason;

    this.currentUser = Auth.user;
    $scope.logout = function() {
      Auth.logout();
      this.currentUser = {};
      $state.go('home', {}, { reload: true });
    }
    
    $scope.auth = function(credentials) {
      Auth.auth(credentials).then(function(user) {

        if (Auth.isAuthenticated()) {
          if ($rootScope.returnToState) {
            $state.go($rootScope.returnToState.name, $rootScope.returnToStateParams);
          } else {
            $state.go('dashboard.home');
          }
        } else {
          console.log("User is not authenticated.");
        }
      }, function(res) {
        var message = "There was a problem while logging in. The error message is <" + res.statusText + ">";
        if (res.status == "401") {
          message = "The user credentials you provided are not valid, please check your data and try again.";
        }
        myself.addAlert(message, "danger");
      });
      $scope.isAuthenticated = Auth.isAuthenticated();
    };

  }])
  .factory('Session', function() {
    var user = {};
    return {
      user: user,
      setUser: function(u) {
        for (var i in u.authorities) {
          var authority = u.authorities[i].authority;
          if (authority == "ROLE_SERVICE") {
            u.isService = true;
            u.profile = "Service";
            break;
          } else if (authority == "ROLE_ADMIN") {
            u.isAdmin = true;
            u.profile = "Admin";
            break;
          } else if (authority == "ROLE_REGULAR") {
            u.isRegular = true;
            u.profile = "Regular";
            break;
          } else if (authority == "ROLE_VIEWER") {
            u.isViewer = true;
            u.profile = "Viewer";
            break;
          }
        }
        return angular.extend(user, u);
      },
      userSet: function() {
        return (Object.getOwnPropertyNames(user).length > 0);
      },
      clear: function() {
        if (user) {
          for (var k in user) {
            if (user.hasOwnProperty(k)) {
              delete user[k];
            }
          }
        } else {
          user = {};
        }
        return user;
      },
    };
  })
  .factory('Auth', ['$http', '$rootScope', '$state', '$q', 'Session', 'AuthLevels', function($http, $rootScope, $state, $q, Session, AuthLevels) {
    var Auth = {
      auth: function(credentials) {
        // Login operation
        var url = "user";
        var headers = credentials ? {
          authorization: "Basic " + btoa(credentials.username + ":" + credentials.password)
        } : {};
        return $http
          .get('/api/' + url, {
            headers: headers
          })
          .then(function(res) {
              return Session.setUser(res.data);
            }
            /*,
                      function() { return Session.clear(); }*/
          );
      },
      ping: function() {
        return $http
          .get('/api/user')
          .then(function(res) {
              return Session.user = (res.data);
            },
            function() {
              return Session.clear();
            });
      },
      logout: function() {
        return $http
          .post('/logout')
          .then(function(res) {
              return Session.clear();
            },
            function() {
              return Session.clear();
            });

      },

      identity: function(force) {
        var deferred = $q.defer();

        if (Session.userSet() && !force) {
          deferred.resolve(Session.user);
        } else {
          Auth.ping().then(
            function(data) {
              deferred.resolve(Session.user);
            },
            function() {
              deferred.resolve(Session.user);
            });
        }

        return deferred.promise;
      },
      authorize: function(toState, toStateParams) {
        return Auth.identity()
          .then(function() {
            if (toState && toState.data &&
              !Auth.isAuthorized(toState.data.access)) {
              if (Auth.isAuthenticated()) {
                $state.go('403'); // user is signed in but not authorized for desired state
              } else {
                // user is not authenticated. stow the state they wanted before you
                // send them to the signin state, so you can return them when you're done
                $rootScope.returnToState = toState;
                $rootScope.returnToStateParams = toStateParams;

                // now, send them to the signin state so they can log in
                $state.go('login');
              }
            }
          });
      },

      isInitialized: function() {
        return Session.userSet();
      },
      isAuthenticated: function() {
        return Session.userSet() && Session.user.name !== undefined;
      },

      isAuthorized: function(authLevel) {
        if (!Session.userSet()) {
          return false;
        }
        if (authLevel === null || authLevel === undefined) {
          return true;
        } else if (authLevel == AuthLevels.public) {
          return true;
        } else if (authLevel == AuthLevels.anon) {
          return (Session.user.id == 0);
        } else if (authLevel == AuthLevels.user) {
          return (Session.user.id > 0);
        } else if (authLevel == AuthLevels.admin) {
          return (Session.user.id > 0 && Session.user.admin);
        } else {
          return false;
        }
      },

      user: Session.user,


    };
    return Auth;
  }])
  .config(['$stateProvider', function($stateProvider) {
    // Need access to both state representations. Decorate any attribute to access private state object.
    $stateProvider.decorator('path', function(state, parentFn) {
      // Add a default empty resolve
      if (state.self.resolve === undefined) {
        state.self.resolve = {};
        state.resolve = state.self.resolve;
      }
      //Add an auth resolve
      state.resolve.authorize = function(Auth) {
        return Auth.authorize(state, {})
      }
      return parentFn(state);
    });
  }])
  .factory('getInterceptor', ['$q',  '$rootScope', '$location', '$state', '$stateParams', 'Session',
    function($q, $rootScope, $location, $state, $stateParams, Session) {
    // I'd like to use $state instead of $location, but that creates
    // a circular dependency.
    return {
      'responseError': function(response) {
        if (response.status === 401 || response.status === 419) {
          // probably timed out
          if ($state.current.name != "login") {
            $rootScope.returnToState = $state.current;
            var oldParams = {};
            angular.extend(oldParams, $stateParams);
            $rootScope.returnToStateParams = oldParams;
          }
          Session.clear();

          $location.path('/dashboard/login');

          // Another method using Include returnUrl (not implemented as we are using returnToState)
          // http://stackoverflow.com/questions/30125293/angularjs-location-path-not-working-for-returnurl
        }
        if (response.status === 403) {
          $location.path('/dashboard/login');
        }

        return $q.reject(response);
      }
    };
  }])
  .directive('access', ['ngIfDirective', 'Auth', function(ngIfDirective, Auth) {
    var ngIf = ngIfDirective[0];
    return {
      transclude: ngIf.transclude,
      priority: ngIf.priority,
      terminal: ngIf.terminal,
      restrict: ngIf.restrict,
      link: function($scope, $element, $attr) {
        $attr.ngIf = function() {
          return Auth.isAuthorized($attr['access']);
        };
        ngIf.link.apply(ngIf, arguments);
      }
    };
  }])
  .run(['Auth', function(Auth) {
    Auth.auth();
  }]);
