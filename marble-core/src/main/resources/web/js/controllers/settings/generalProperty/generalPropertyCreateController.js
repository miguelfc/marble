'use strict';
angular.module('marbleCoreApp')
.controller('GeneralPropertyCreateCtrl', ['$scope', '$state', '$stateParams', '$timeout', 'GeneralPropertiesFactory', 
function ($scope, $state, $stateParams, $timeout, GeneralPropertiesFactory) {
	var goBack = function() {$state.go('dashboard.settings.generalProperty.list', {}, {reload: true})}; 
    
	$scope.create = function () {
		GeneralPropertiesFactory.create($scope.generalProperty);
		$timeout(function() {goBack()}, 200);
    };

    $scope.cancel = function () {
    	goBack();
    };
    
}]);