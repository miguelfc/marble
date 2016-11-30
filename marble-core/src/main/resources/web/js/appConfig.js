'use strict';
angular.module('marbleCoreApp').config(['$stateProvider','$urlRouterProvider','$httpProvider', function ($stateProvider,$urlRouterProvider,$httpProvider) {
    
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
    
    $urlRouterProvider.otherwise('/dashboard/home');
    
    $stateProvider
      .state('dashboard', {
        url:'/dashboard',
        templateUrl: 'templates/views/dashboard/main.html',
    })
      .state('dashboard.home',{
        url:'/home',
        controller: 'MainCtrl',
        templateUrl:'templates/views/dashboard/home.html',
        resolve: {
          $title: function() { return 'Home'; }
        }
      })
      .state('dashboard.login',{
    	controller: 'LoginCtrl',
        templateUrl:'templates/views/pages/login.html',
        url:'/login',
        params: { 'reason': null,
        		  'origin': null
        		  },
        resolve : {
        	$reason: function($stateParams) { return $stateParams.reason; },
        	$origin: function($stateParams) { return $stateParams.origin; }
        }
    })
      .state('dashboard.topic',{
    	url:'/topics',
    	data : {requireGuest : true },
        templateUrl:'templates/views/topic/main.html'
        
    })
      .state('dashboard.topic.list',{
        templateUrl:'templates/views/topic/list.html',
        url:'/list',
        data : {requireGuest : true },
        controller:'TopicListCtrl',
        resolve: {
            $title: function() { return 'List of Topics'; }
        }
    })
      .state('dashboard.topic.view',{
        templateUrl:'templates/views/topic/view.html',
        url:'/view/{topicName}',
        data : {requireGuest : true },
        controller:'TopicViewCtrl',
        resolve: {
            $title: function($stateParams) { return 'Details for Topic ' + $stateParams.topicName; }
        }
    })
      .state('dashboard.topic.create',{
        templateUrl:'templates/views/topic/create.html',
        url:'/create',
        data : {requireOper : true },
        controller:'TopicCreateCtrl',
        resolve: {
            $title: function() { return 'Create a new Topic' }
        }
    })
    	.state('dashboard.settings',{
    	url:'/settings',
    	data : {requireAdmin : true },
        templateUrl:'templates/views/settings/main.html'
    })
      .state('dashboard.settings.generalProperty',{
    	url:'/generalProperties',
    	data : {requireAdmin : true },
        templateUrl:'templates/views/settings/generalProperty/main.html'
    })
      .state('dashboard.settings.generalProperty.list',{
        templateUrl:'templates/views/settings/generalProperty/list.html',
        url:'/list',
        data : {requireAdmin : true },
        controller:'GeneralPropertyListCtrl',
        resolve: {
            $title: function() { return 'General Properties'; }
        }
    })
      .state('dashboard.settings.generalProperty.view',{
        templateUrl:'templates/views/settings/generalProperty/view.html',
        url:'/view/{generalPropertyName}',
        data : {requireAdmin : true },
        controller:'GeneralPropertyViewCtrl',
        resolve: {
            $title: function() { return 'Edit Property'; }
        }
    })
      .state('dashboard.settings.generalProperty.create',{
        templateUrl:'templates/views/settings/generalProperty/create.html',
        url:'/create',
        data : {requireAdmin : true },
        controller:'GeneralPropertyCreateCtrl',
        resolve: {
            $title: function() { return 'Add a new property'; }
        }
    })
      .state('dashboard.settings.twitterApiKey',{
    	url:'/twitterApiKeys',
    	data : {requireAdmin : true },
        templateUrl:'templates/views/settings/twitterApiKey/main.html'
    })
      .state('dashboard.settings.twitterApiKey.list',{
        templateUrl:'templates/views/settings/twitterApiKey/list.html',
        url:'/list',
        data : {requireAdmin : true },
        controller:'TwitterApiKeyListCtrl',
        resolve: {
            $title: function() { return 'List of Twitter API keys'; }
        }
    })
      .state('dashboard.settings.twitterApiKey.view',{
        templateUrl:'templates/views/settings/twitterApiKey/view.html',
        url:'/view/{twitterApiKeyName}',
        data : {requireAdmin : true },
        controller:'TwitterApiKeyViewCtrl',
        resolve: {
            $title: function($stateParams) { return 'Details for Twitter API key ' + $stateParams.twitterApiKeyName; }
        }
    })
      .state('dashboard.settings.twitterApiKey.create',{
        templateUrl:'templates/views/settings/twitterApiKey/create.html',
        url:'/create',
        data : {requireAdmin : true },
        controller:'TwitterApiKeyCreateCtrl',
        resolve: {
            $title: function() { return 'Add a new Twitter API key' }
        }
    })
      .state('dashboard.settings.corpus',{
    	url:'/corpus',
    	data : {requireAdmin : true },
        templateUrl:'templates/views/settings/corpus/main.html',
        controller:'CorpusCtrl'
    })
      .state('dashboard.job',{
    	url:'/jobs',
    	data : {requireGuest : true },
        templateUrl:'templates/views/job/main.html'
    })
      .state('dashboard.job.list',{
        templateUrl:'templates/views/job/list.html',
        url:'/list',
        data : {requireGuest : true },
        controller:'JobListCtrl',
        resolve: {
            $title: function($stateParams) { return 'List of Jobs'; }
        }
    })
      .state('dashboard.job.listByTopic',{
        templateUrl:'templates/views/job/listByTopic.html',
        url:'/list/{topicName}',
        data : {requireGuest : true },
        controller:'JobListByTopicCtrl',
        resolve: {
            $title: function($stateParams) { return 'Jobs for Topic ' + $stateParams.topicName; }
        }
    })
      .state('dashboard.job.view',{
        templateUrl:'templates/views/job/view.html',
        url:'/view/{jobId}',
        data : {requireGuest : true },
        controller:'JobViewCtrl',
        resolve: {
            $title: function($stateParams) { return 'Details for Job ' + $stateParams.jobId; }
        }
    })
      .state('dashboard.chart',{
    	url:'/charts',
    	data : {requireGuest : true },
        templateUrl:'templates/views/chart/main.html'
    })
      .state('dashboard.chart.list',{
        templateUrl:'templates/views/chart/list.html',
        url:'/list',
        data : {requireGuest : true },
        controller:'ChartListCtrl',
        resolve: {
            $title: function($stateParams) { return 'List of Charts'; }
        }
    })
      .state('dashboard.chart.listByTopic',{
        templateUrl:'templates/views/chart/listByTopic.html',
        url:'/list/{topicName}',
        data : {requireGuest : true },
        controller:'ChartListByTopicCtrl',
        resolve: {
            $title: function($stateParams) { return 'Charts for Topic ' + $stateParams.topicName; }
        }
    })
      .state('dashboard.chart.view',{
        templateUrl:'templates/views/chart/view.html',
        url:'/view/{chartId}',
        data : {requireGuest : true },
        controller:'ChartViewCtrl',
        resolve: {
            $title: function($stateParams) { return 'Details for Chart ' + $stateParams.chartId; }
        }
    })
      .state('dashboard.post',{
    	url:'/posts',
    	data : {requireOper : true },
        templateUrl:'templates/views/post/main.html'
    })
      .state('dashboard.post.list',{
        templateUrl:'templates/views/post/list.html',
        url:'/list',
        data : {requireOper : true },
        controller:'PostListCtrl',
        resolve: {
            $title: function($stateParams) { return 'List of Posts'; }
        }
    })
      .state('dashboard.post.listByTopic',{
        templateUrl:'templates/views/post/listByTopic.html',
        url:'/list/{topicName}',
        data : {requireOper : true },
        controller:'PostListByTopicCtrl',
        resolve: {
            $title: function($stateParams) { return 'Posts for Topic ' + $stateParams.topicName; }
        }
    })
      .state('dashboard.post.view',{
        templateUrl:'templates/views/post/view.html',
        url:'/view/{postId}',
        data : {requireOper : true },
        controller:'PostViewCtrl',
        resolve: {
            $title: function($stateParams) { return 'Details for Post ' + $stateParams.postId; }
        }
    })
  }]);

app.run(function ($rootScope, $state, $location, AuthService) {
	$rootScope.$on('authenticated', function(event, toState, toParams, fromState) {
		  $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState) {
	      console.trace("Caught state change event from: " + fromState.name + " to: " + toState.name);
		  var shouldLogin = toState.data !== undefined
	                  && toState.data.requireLogin 
	                  && !AuthService.isLoggedIn() ;
	      
	      var requireAdmin = toState.data !== undefined
			          && toState.data.requireAdmin 
			          && !AuthService.isAdmin();
	      
	      var requireOper = toState.data !== undefined
			          && toState.data.requireOper 
			          && !AuthService.isOper();
	      
	      var requireGuest = toState.data !== undefined
			          && toState.data.requireGuest
			          && !AuthService.isGuest();
	      
	      // NOT authenticated - wants any private stuff
	      if(requireAdmin || requireOper || requireGuest)
	      {
	    	console.trace("User not authorized.");
	    	if (!AuthService.isLoggedIn()) {
	    		$state.go('dashboard.login', {
	    			reason: "You need to be logged in to access this area.",
	    			origin: toState.name
	    				});
	    	}
	    	else {
	    		$state.go('dashboard.login', {
	    			reason: "You need higher priviledges to access this area." +
	    				" Please login with an appropiated user.",
	    			origin: toState.name
		    			});
	    	}
	        event.preventDefault();
	        return;
	      }
	      
	      
	      // authenticated (previously) comming not to root main
	      if(AuthService.isLoggedIn()) 
	      {
	        var shouldGoToMain = fromState.name === ""
	                          && toState.name !== "dashboard.home" ;
	          
	        if (shouldGoToMain)
	        {
	            $state.go('dashboard.home');
	            event.preventDefault();
	        } 
	        return;
	      }
	      
	      // UNauthenticated (previously) comming not to root public 
	      var shouldGoToPublic = fromState.name === ""
	                        && toState.name !== "dashboard.home"
	                        && toState.name !== "dashboard.login" ;
	        
	      if(shouldGoToPublic)
	      {
	          $state.go('dashboard.home');
	          console.trace('Going public');
	          event.preventDefault();
	      } 
	      
	      // unmanaged
	    });
	});
	
});
    
