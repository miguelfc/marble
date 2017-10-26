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
					if ($scope.chart.type == "Google Chart") {
						$scope.googleChartObject = {};
						$scope.googleChartObject.type = $scope.chart.customType;
						$scope.googleChartObject.data = $scope.chart.data;
						$scope.googleChartObject.options = $scope.chart.options;
					}
					// Figure List
					else if ($scope.chart.type == "Figure List") {
						$scope.figureListObject = {};
						$scope.figureListObject.figures = $scope.chart.figures;
					}
					// Text Report
					else if ($scope.chart.type == "Report") {
						console.log("Aqui voy");
						$scope.reportObject = {};
						$scope.reportObject.data = $scope.chart.data;
						$scope.reportObject.figures = $scope.chart.figures;
					}
				}, function(error) {
					// TODO Handle Error 404
				});
			}

			refresh();
		});