'use strict';
angular.module('marbleCoreApp')
.controller('TopicViewCtrl', 
		function ($scope, 
				$state, 
				$window,
				$stateParams, 
				$timeout, 
				$uibModal,  
				TopicFactory, 
				TopicInfoFactory, 
				TopicExtractFactory,
				TopicStreamFactory,
				PostFactory, 
				JobFactory,
				ChartFactory,
				TopicProcessFactory, 
				TopicPlotFactory
				) {
	var goBack = function() {$state.go('dashboard.topic.list', {}, {reload: true})}; 

	$scope.update = function () {
		if ($scope.topic.streamerProcessParametersString) {			
			$scope.topic.streamerProcessParameters = JSON.parse($scope.topic.streamerProcessParametersString);
		}
		TopicFactory.update($scope.topic).$promise.then(function(data) {
			$scope.error = '';
			$scope.success = "The topic was updated successfully."
		}, function(error) {
			if (error.data.error == 'Forbidden') {
				$scope.error = "Oops! It seems like you don't have permission to do that.";
			}
			else {
				$scope.error = 'Oops! an error occurred (The server said something like "' + error.data.error + ': ' + error.data.message + '").';	
			}
			$scope.success = '';
		});
	};

	$scope.cancel = function () {
		goBack();
	};

	$scope.delete = function () {
		TopicFactory.delete($scope.topic);
		$timeout(function() {goBack()}, 200);
	};

	$scope.stats = function () {
		if ($scope.topicInfo) {
			$scope.topicInfo = null;
		}
		else { 
			TopicInfoFactory.show({name: $stateParams.topicName}).$promise.then(function(data) {
				$scope.topicInfo = data;
			});
		}
	};

	$scope.jobs = function () {
		$state.go('dashboard.job.listByTopic', {'topicName': $stateParams.topicName})
	};
	
	$scope.charts = function () {
		$state.go('dashboard.chart.listByTopic', {'topicName': $stateParams.topicName})
	};

	$scope.posts = function () {
		$state.go('dashboard.post.listByTopic', {'topicName': $stateParams.topicName})
	};

	$scope.extract = function () {
		TopicExtractFactory.extract({name: $stateParams.topicName}).$promise.then(function(data) {
			// Handle execution error
			var jobId = data.id;
			$state.go('dashboard.job.view', {
				'jobId' : jobId
			});
		});
	};

	$scope.stream = function () {
		TopicStreamFactory.stream({name: $stateParams.topicName}).$promise.then(function(data) {
			// Handle execution error
			var jobId = data.id;
			$state.go('dashboard.job.view', {
				'jobId' : jobId
			});
		});
	};
	
	$scope.stopStream = function () {
		TopicStreamFactory.stopStream({name: $stateParams.topicName}).$promise.then(function(data) {
			// Handle execution error
			var jobId = data.id;
			$state.go('dashboard.job.view', {
				'jobId' : jobId
			});
		});
	};
	
	$scope.process = function (options) {
		TopicProcessFactory.process({name: $stateParams.topicName}, options).$promise.then(function(data) {
			// Handle execution error
			var jobId = data.id;
			$scope.processModal = false;
			$state.go('dashboard.job.view', {
				'jobId' : jobId
			});
		});
	};

	$scope.plot = function (options) {
		TopicPlotFactory.plot({name: $stateParams.topicName}, options).$promise.then(function(data) {
			// Handle execution error
			var jobId = data.id;
			$scope.plotModal = false;
			$state.go('dashboard.job.view', {
				'jobId' : jobId
			});
		});
	};

	$scope.rebootExtraction = function () {
		$scope.topic.lowerLimit = $scope.topic.upperLimit;
		$scope.topic.upperLimit = "";
		$scope.update();
		$scope.extract();
	};

	$scope.deletePostsByTopic = function () {	
		var deleteResult = PostFactory.delete({
			topicName : $scope.topic.name
		});
		deleteResult.$promise.then(function(data) {
			if (typeof data.message === 'undefined' || data.message == null) {
				$scope.error = "An error ocurred while deleting the posts of this topic.";
				$scope.success = "";
			}
			else {
				$scope.error = "";
				$scope.success = data.message;
			}
		}, function(e) {
			$scope.error = "An error ocurred while deleting the posts of this topic.";
			$scope.success = "";
		});
	};

	$scope.downloadPosts = function () {
		$window.open("/api/posts/download/topic/" + $scope.topic.name);
	};
	
	$scope.downloadProcessedPosts = function () {
		$window.open("/api/processedPosts/download/topic/" + $scope.topic.name);
	};
	
	$scope.deleteJobsByTopic = function () {	
		var deleteResult = JobFactory.delete({
			topicName : $scope.topic.name
		});
		deleteResult.$promise.then(function(data) {
			if (typeof data.message === 'undefined' || data.message == null) {
				$scope.error = "An error ocurred while deleting the jobs of this topic.";
				$scope.success = "";
			}
			else {
				$scope.error = "";
				$scope.success = data.message;
			}
		}, function(e) {
			$scope.error = "An error ocurred while deleting the jobs of this topic.";
			$scope.success = "";
		});
	};

	$scope.deleteChartsByTopic = function () {	
		var deleteResult = ChartFactory.delete({
			topicName : $scope.topic.name
		});
		deleteResult.$promise.then(function(data) {
			if (typeof data.message === 'undefined' || data.message == null) {
				$scope.error = "An error ocurred while deleting the charts of this topic.";
				$scope.success = "";
			}
			else {
				$scope.error = "";
				$scope.success = data.message;
			}
		}, function(e) {
			$scope.error = "An error ocurred while deleting the charts of this topic.";
			$scope.success = "";
		});
	};
	
	$scope.hidDangerActions = true;
	$scope.showDangerActions = function () {
		$scope.hidDangerActions = false;
	}

	// TODO: Handle 404
	$scope.topic = TopicFactory.show({name: $stateParams.topicName}).$promise.then(function(data) {
		$scope.topic = data;
		if ($scope.topic.streamerProcessParameters) {			
			$scope.topic.streamerProcessParametersString = JSON.stringify($scope.topic.streamerProcessParameters, undefined, 2);
		}
	}, function(error) {
	});

});
