'use strict';
angular.module('marbleCoreApp').controller(
		'ChartViewCtrl',
		function($scope, $state, $stateParams, $timeout, ChartFactory) {
			var goBack = function() {
				$state.go('dashboard.topic.view', {
					'topicName' : $scope.chart.topicName
				});
			};

			$scope.viewTopic = function() {
				goBack();
			};
			
			$scope.viewJob = function() {
			    $state.go('dashboard.job.view', {
                    'jobId' : $scope.chart.jobId
                });
            };

			// TODO: Handle 404
			var refresh = function() {
			    console.log($stateParams);
				ChartFactory.show({
					id : $stateParams.chartId
				}).$promise.then(function(data) {
					$scope.chart = data;
					console.log($scope.chart.data.rows);
					 for (var i in $scope.chart.data.rows) {
						 //console.log($scope.chart.data.rows[i].c[0].v);
						 $scope.chart.data.rows[i].c[0].v = new Date($scope.chart.data.rows[i].c[0].v);
						  }
					$scope.updateDate = new Date();
					
					// Google Chart
					$scope.chartObject = {};
				    
				    $scope.chartObject.type = $scope.chart.type;
				    
				    $scope.chartObject.data = $scope.chart.data;
				    
				    $scope.chartObject.options = $scope.chart.options;
				    
				}, function(error) {
					// TODO Handle Error 404
				});
			}

			refresh();
		});