'use strict';
angular.module('marbleCoreApp').controller(
		'PlotViewCtrl',
		function($scope, $state, $stateParams, $timeout, PlotFactory) {
			var goBack = function() {
				$state.go('dashboard.topic.view', {
					'topicName' : $scope.plot.topicName
				});
			};

			$scope.viewTopic = function() {
				goBack();
			};
			
			$scope.viewJob = function() {
			    $state.go('dashboard.job.view', {
                    'jobId' : $scope.plot.jobId
                });
            };

			// TODO: Handle 404
			var refresh = function() {
			    console.log($stateParams);
				PlotFactory.show({
					id : $stateParams.plotId
				}).$promise.then(function(data) {
					$scope.plot = data;
					console.log($scope.plot.data.rows);
					 for (var i in $scope.plot.data.rows) {
						 //console.log($scope.plot.data.rows[i].c[0].v);
						 $scope.plot.data.rows[i].c[0].v = new Date($scope.plot.data.rows[i].c[0].v);
						  }
					$scope.updateDate = new Date();
					
					// Google Chart
					$scope.chartObject = {};
				    
				    $scope.chartObject.type = $scope.plot.type;
				    
				    $scope.chartObject.data = $scope.plot.data;
				    
				    $scope.chartObject.options = $scope.plot.options;
				    
				}, function(error) {
					// TODO Handle Error 404
				});
			}

			refresh();
		});