'use strict';
angular.module('marbleCoreApp')
.controller('TwitterApiKeyListCtrl', ['$scope', '$compile', '$state', 'TwitterApiKeysFactory',
function ($scope, $compile, $state, TwitterApiKeysFactory) {

	$scope.tableState = {};
	$scope.tableState.size = "10";
	$scope.tableState.page = "0";
	$scope.tableState.sort = "";

	$scope.edit = function(twitterApiKeyName) {
		$state.go('dashboard.settings.twitterApiKey.view', {
			'twitterApiKeyName' : twitterApiKeyName
		});
	};

	$scope.create = function() {
		$state.go('dashboard.settings.twitterApiKey.create');
	};

	$scope.gridOptions = {
		data : 'gridData',
		rowHeight : 40,
		paginationPageSizes : [ 10, 25, 50, 100 ],
		paginationPageSize : 10,
		useExternalPagination : true,
		useExternalSorting : true,
		columnDefs : [
				{
					field : 'description',
					displayName : 'Description'
				},
				{
					field : 'consumerKey',
					displayName : 'Consumer Key'
				},
				{
					name : 'actions',
					displayName : 'Actions',
					cellTemplate : '<div class="grid-action-cell"><a data-ng-click="grid.appScope.edit(row.entity.id)" class="btn btn-default"><i class="fa fa-info-circle"></i><span class="hidden-xs hidden-sm"> Details</span></a></div>'
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
		var topicsList = TwitterApiKeysFactory.query({
			size : $scope.tableState.size,
			page : $scope.tableState.page,
			sort : $scope.tableState.sort,
		});
		topicsList.$promise.then(function(data) {
			$scope.gridData = data._embedded.twitterApiKeys;
			$scope.gridOptions.totalItems = data.page.totalElements;
		});
	};

	updateTable($scope);
}]);
