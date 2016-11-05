'use strict';
angular.module('marbleCoreApp')
.controller('GeneralPropertyViewCtrl', ['$scope', '$state', '$stateParams', '$timeout', 'GeneralPropertyFactory',
function ($scope, $state, $stateParams, $timeout, GeneralPropertyFactory) {
    
	var goBack = function() {$state.go('dashboard.settings.generalProperty.list', {}, {reload: true})}; 
    
	$scope.update = function () {
    	GeneralPropertyFactory.update({},$scope.generalProperty);
    	$timeout(function() {goBack()}, 200);
    };

    $scope.cancel = function () {
    	goBack();
    };
    
    $scope.delete = function () {
    	GeneralPropertyFactory.delete({},$scope.generalProperty);
    	$timeout(function() {goBack()}, 200);
    };
    
    
    // TODO: Handle 404
    $scope.generalProperty = GeneralPropertyFactory.show({name: $stateParams.generalPropertyName});
}]);