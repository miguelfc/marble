'use strict';
angular.module('marbleCoreApp')
.controller('PostListByTopicCtrl', ['$scope', '$compile', '$state', '$stateParams', 'PostsByTopicNameFactory', 'PostTagFactory', 'Auth',
function ($scope, $compile, $state, $stateParams, PostsByTopicNameFactory, PostTagFactory, Auth) {
	
	$scope.tableState = {};
	$scope.topicName = $stateParams.topicName;
	$scope.tableState.name = $scope.topicName;
	$scope.tableState.size = "10";
	$scope.tableState.page = "0";
	$scope.tableState.sort = "";

	$scope.view = function(postId) {
		$state.go('dashboard.post.view', {
			'postId' : postId
		});
	};

	$scope.viewTopic = function(topicName) {
		$state.go('dashboard.topic.view', {
			'topicName' : $stateParams.topicName
		});
	};
	
	$scope.currentUser = "nobody";
	if (Auth.user != null) {
	    $scope.currentUser = Auth.user.name;
	}
	
	$scope.tag = function(id, polarity) {
	    var tagOperation = PostTagFactory.tag({id: id}, {
	        user:  $scope.currentUser,
	        polarity: polarity
	    });
	    
	    tagOperation.$promise.then(function(data) {
	        updateTable($scope);
	    });
	}

	$scope.gridOptions = {
		data : 'gridData',
		rowHeight : 40,
		enableFiltering : false,
		useExternalFiltering : true,
		paginationPageSizes : [ 10, 25, 50, 100 ],
		paginationPageSize : 10,
		useExternalPagination : true,
		useExternalSorting : true,
		columnDefs : [
				{
					field : 'text',
					displayName : 'Text',
					enableFiltering : false,
					cellTooltip: true
				},
				{
					field : 'createdAt',
					displayName : 'Date',
					enableFiltering : false,
					cellFilter: "date:'yyyy-MM-dd HH:mm:ss (Z)'"
				},
				{
					name : 'actions',
					displayName : 'Actions',
					enableFiltering : false,
					cellTemplate : '<div class="grid-action-cell"><a data-ng-click="grid.appScope.view(row.entity.id)" class="btn btn-default">'+
					'<i class="fa fa-info-circle"></i><span class="hidden-xs hidden-sm"> Details</span></a> '+
					
					'<a data-ng-click="grid.appScope.tag(row.entity.id, 1)" class="btn btn-default" ng-class="{\'btn-success\': row.entity.polarityTags[grid.appScope.currentUser] === 1}" ng-disabled="row.entity.polarityTags[grid.appScope.currentUser] === 1"><i class="fa fa-smile-o"></i></a>'+
					'<a data-ng-click="grid.appScope.tag(row.entity.id, 0)" class="btn btn-default" ng-class="{\'btn-warning\': row.entity.polarityTags[grid.appScope.currentUser] === 0}" ng-disabled="row.entity.polarityTags[grid.appScope.currentUser] === 0"><i class="fa fa-meh-o"></i></a>'+
					'<a data-ng-click="grid.appScope.tag(row.entity.id, -1)" class="btn btn-default" ng-class="{\'btn-danger\': row.entity.polarityTags[grid.appScope.currentUser] === -1}" ng-disabled="row.entity.polarityTags[grid.appScope.currentUser] === -1"><i class="fa fa-frown-o"></i></a>'+
					'</div>'
				} ],
		onRegisterApi : function(gridApi) {
			$scope.gridApi = gridApi;
			$scope.gridApi.core.on.sortChanged($scope, function(grid,
					sortColumns, pageSize) {
				if (sortColumns.length == 0) {
					$scope.tableState.sort = null;
				} else {
					$scope.tableState.sort = sortColumns[0].field + ","
							+ sortColumns[0].sort.direction;
				}
				updateTable($scope);
			});
			$scope.gridApi.pagination.on.paginationChanged($scope, function(
					newPage, pageSize) {
				$scope.tableState.size = pageSize;
				$scope.tableState.page = newPage - 1;
				updateTable($scope);
			});
		}
	};

	var updateTable = function($scope) {
		var postsList = PostsByTopicNameFactory.searchByName({
			name : $scope.tableState.name,
			size : $scope.tableState.size,
			page : $scope.tableState.page,
			sort : $scope.tableState.sort,
		});
		postsList.$promise.then(function(data) {
			$scope.gridData = data._embedded.posts;
			$scope.gridOptions.totalItems = data.page.totalElements;
		});
	};

	updateTable($scope);
	
}]);
