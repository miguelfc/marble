'use strict';
angular.module('marbleCoreApp')
.controller('TwitterApiKeyCreateCtrl', ['$scope', '$state', '$stateParams', '$timeout', 'TwitterApiKeysFactory', 
function ($scope, $state, $stateParams, $timeout, TwitterApiKeysFactory) {
	var goBack = function() {$state.go('dashboard.settings.twitterApiKey.list', {}, {reload: true})};
	
	$scope.create = function () {
		TwitterApiKeysFactory.create($scope.twitterApiKey);
		$timeout(function() {goBack()}, 200);
    };

    $scope.cancel = function () {
    	goBack();
    };
    
}]);