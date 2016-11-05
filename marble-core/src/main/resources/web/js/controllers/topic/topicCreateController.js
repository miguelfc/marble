'use strict';
angular.module('marbleCoreApp')
.controller('TopicCreateCtrl', ['$scope', '$state', '$stateParams', '$timeout', 'TopicsFactory', 
function ($scope, $state, $stateParams, $timeout, TopicsFactory) {
	var goBack = function() {$state.go('dashboard.topic.list', {}, {reload: true})}; 
	
	$scope.create = function () {
    	TopicsFactory.create($scope.topic);
    	$timeout(function() {goBack()}, 200);
    };

    $scope.cancel = function () {
    	goBack();
    };
    
}]);