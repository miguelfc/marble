'use strict';
angular.module('marbleCoreApp')
.controller('JobViewCtrl', ['$scope', '$state', '$stateParams', '$timeout', 'JobExtendedFactory',
function ($scope, $state, $stateParams, $timeout,  JobExtendedFactory) {
	var goBack = function() {
		$state.go('dashboard.topic.view', {
			'topicName' : $scope.job.topicName
		});
	}; 

    $scope.viewTopic = function () {
    	goBack();
    };
    
    $scope.viewChart = function() {
		$state.go('dashboard.chart.view', {
			'chartId' : $scope.job.chartId
		});
	};
	
	$scope.viewChart = function(chartId) {
	    $state.go('dashboard.chart.view', {
	        'chartId' : chartId
	    });
	};
    
    // TODO: Handle 404
    var refresh = function() {
    	if ($state.includes("dashboard.job.view")) {
    		JobExtendedFactory.show({id: $stateParams.jobId}).$promise.then(function(data) {
    			$scope.job = data;
    			$scope.job.jobParametersIdented = JSON.stringify($scope.job.jobParameters, undefined, 2);
    			$scope.updateDate = new Date();
    	    	if (!$scope.job.status.match(/Stopped|Aborted/)) {
    	    		setTimeout(refresh, 5000);
        		}
    		});
    	}
    }
    
    refresh();
}]);