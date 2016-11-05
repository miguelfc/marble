'use strict';
angular.module('marbleCoreApp')
.controller('PostViewCtrl', ['$scope', '$state', '$stateParams', '$timeout', 'PostFactory',
function ($scope, $state, $stateParams, $timeout, PostFactory) {    
    
	$scope.viewTopic = function(topicName) {
		$state.go('dashboard.topic.view', {
			'topicName' : $scope.post.topicName
		});
	};
	
	// TODO: Handle 404
    $scope.post = PostFactory.show({id: $stateParams.postId});
}]);