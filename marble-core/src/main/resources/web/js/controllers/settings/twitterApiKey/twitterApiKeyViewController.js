'use strict';
angular.module('marbleCoreApp')
.controller('TwitterApiKeyViewCtrl', ['$scope', '$state', '$stateParams', '$timeout', 'TwitterApiKeyFactory',
function ($scope, $state, $stateParams, $timeout, TwitterApiKeyFactory) {
	var goBack = function() {$state.go('dashboard.settings.twitterApiKey.list', {}, {reload: true})}; 
    
	$scope.update = function () {
    	TwitterApiKeyFactory.update({},$scope.twitterApiKey);
    	$timeout(function() {goBack()}, 200);
    };

    $scope.cancel = function () {
    	goBack();
    };
    
    $scope.delete = function () {
    	TwitterApiKeyFactory.delete({},$scope.twitterApiKey);
    	$timeout(function() {goBack()}, 200);
    };
    
    // TODO: Handle 404
    $scope.twitterApiKey = TwitterApiKeyFactory.show({name: $stateParams.twitterApiKeyName});
}]);